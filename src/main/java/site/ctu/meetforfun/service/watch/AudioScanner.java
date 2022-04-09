package site.ctu.meetforfun.service.watch;

import site.ctu.meetforfun.entity.Track;
import site.ctu.meetforfun.repository.TrackRepository;
import site.ctu.meetforfun.service.playlist.PlaylistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Run initial scan root directory.
 */
@Component
@Slf4j
public class AudioScanner implements Runnable
{
    private final PlaylistService playlistService;

    private final TrackRepository trackRepository;

    @Value("${tracks.rootDirectory}")
    private String rootDirectory;

    @Autowired
    public AudioScanner(
            PlaylistService playlistService,
            TrackRepository trackRepository
    )
    {
        this.playlistService = playlistService;
        this.trackRepository = trackRepository;
    }

    @PostConstruct
    public void exec()
    {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run()
    {
        try {
            scan();
            generatePlaylist();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void scan() throws UnsupportedEncodingException
    {
        log.info("Scan " + rootDirectory);
        // set start directory (root) from config
        var directory = fileByPath();

        if (!directory.isDirectory() || !directory.canRead()) {
            return;
        }

        // search mp3 files recursively
        var tracksPath = readDirectory(directory);

        saveTracks(tracksPath);

        // log or return counter
        log.info("Scan completed, found " + tracksPath.size() + " tracks");
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

    private File fileByPath()
    {
        return new File(rootDirectory.strip());
    }

    private void saveTracks(Collection<String> tracksPath)
    {
        // and add each path to storage as Track
        for (String path : tracksPath) {
            String id = UUID.randomUUID().toString();
            trackRepository.save(new Track(id, path));
        }
    }

    private void generatePlaylist()
    {
        LinkedList<String> tracksIds = new LinkedList<>();

        for (Track track : trackRepository.findAll()) {
            tracksIds.add(track.getId());
        }

        playlistService.setQueue(tracksIds);

        log.info("Playlist generated");
    }
}
