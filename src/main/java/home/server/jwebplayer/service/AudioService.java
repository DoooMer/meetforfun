package home.server.jwebplayer.service;

import home.server.jwebplayer.dto.DownloadedTrackDto;
import home.server.jwebplayer.entity.Track;
import home.server.jwebplayer.repository.TrackRepository;
import home.server.jwebplayer.service.playlist.PlaylistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class AudioService
{
    private final PlaylistService playlistService;

    private final TrackRepository trackRepository;

    @Value("${tracks.rootDirectory}")
    private String rootDirectory;

    @Autowired
    public AudioService(
            PlaylistService playlistService,
            TrackRepository trackRepository
    )
    {
        this.playlistService = playlistService;
        this.trackRepository = trackRepository;
    }

    @Nullable
    public Track getCurrentByHash(String hash) throws Exception
    {
        playlistService.select(hash);
        // TODO: catch exception
        // TODO: add fallback - regenerate playlist and try again
        String id = playlistService.current();

        if (id == null) {
            return null;
        }

        return trackRepository.findById(id).orElse(null);
    }

    @Nullable
    public Track getCurrent()
    {
        String id = playlistService.current();

        if (id == null) {
            return null;
        }

        Optional<Track> track = trackRepository.findById(id);

        return track.orElse(null);
    }

    public List<Track> getAllTracks()
    {
        return (List<Track>) trackRepository.findAll();
    }

    public DownloadedTrackDto downloadByHash(String hash) throws IOException
    {
        Track track = trackRepository.findById(hash).orElse(null);

        if (track == null) {
            throw new RuntimeException("Track is not found.");
        }

        File file = fileByPath(track.getPath());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file.getPath()));

        String name = file.getName().substring(file.getName().lastIndexOf("/") + 1);

        return new DownloadedTrackDto(resource, name, Files.size(file.toPath()), Files.probeContentType(file.toPath()));
    }

    public void next()
    {
        playlistService.next();
    }

    private File fileByPath(String path)
    {
        return new File(rootDirectory.strip() + (path != null ? "/" + path : ""));
    }
}
