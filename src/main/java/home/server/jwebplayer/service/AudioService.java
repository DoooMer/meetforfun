package home.server.jwebplayer.service;

import home.server.jwebplayer.dto.DownloadedTrackDto;
import home.server.jwebplayer.entity.Track;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class AudioService
{
    private final TrackStorage trackStorage;

    private boolean needScan = true;

    @Value("${tracks.rootDirectory}")
    private String rootDirectory;

    @Autowired
    public AudioService(TrackStorage trackStorage)
    {
        this.trackStorage = trackStorage;
    }

    public Track getCurrentByHash(String hash)
    {
        var track = trackStorage.getByHash(hash);

        if (track != null) {
            try {
                trackStorage.setCursor(track);
            } catch (ArrayIndexOutOfBoundsException e) {
                needScan = true;
            }
        }

        return track;
    }

    public boolean isNeedToScan()
    {
        return needScan;
    }

    public void scan() throws UnsupportedEncodingException
    {
        log.info("Scan " + rootDirectory);
        // set start directory (root) from config
        var directory = fileByPath(null);

        if (!directory.isDirectory() || !directory.canRead()) {
            return;
        }

        // search mp3 files recursively
        var tracksPath = readDirectory(directory);

        // and add each path to storage as Track
        for (String path : tracksPath) {
            trackStorage.add(path);
        }

        trackStorage.writeIndex();
        // log or return counter
        log.info("Scan completed, found " + tracksPath.size() + " tracks");
        needScan = false;
    }

    private Collection<String> readDirectory(File directory)
    {
        var files = directory.listFiles();
        var founded = new ArrayList<String>();

        if (files == null) {
            return founded;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                founded.addAll(readDirectory(file));
            } else {
                // walk by each founded mp3 file
                if (file.getName().endsWith(".mp3")) {
                    founded.add(file.getPath().substring(rootDirectory.strip().length() + 1));
                }
            }
        }

        return founded;
    }

    @Nullable
    public Track getCurrent()
    {
        return trackStorage.getCurrent();
    }

    public List<Track> getAllTracks()
    {
        return trackStorage.getAll();
    }

    public DownloadedTrackDto downloadByHash(String hash) throws IOException
    {
        Track track = trackStorage.getByHash(hash);

        if (track == null) {
            throw new RuntimeException("Current track is not found.");
        }

        File file = fileByPath(URLDecoder.decode(track.getPath(), StandardCharsets.UTF_8.toString()) + ".mp3");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file.getPath()));

        String name = file.getName().substring(file.getName().lastIndexOf("/") + 1);

        return new DownloadedTrackDto(resource, name, Files.size(file.toPath()), Files.probeContentType(file.toPath()));
    }

    public void next()
    {
        trackStorage.cursorToNext();
    }

    private File fileByPath(@Nullable String path)
    {
        return new File(rootDirectory.strip() + (path != null ? "/" + path : ""));
    }
}
