package home.server.jwebplayer.service.watch;

import home.server.jwebplayer.entity.Track;
import home.server.jwebplayer.repository.TrackRepository;
import home.server.jwebplayer.service.playlist.PlaylistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EventListener;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
// TODO: only add track to DB
public class TrackListener implements EventListener
{
    private final TrackRepository trackRepository;

    private final PlaylistService playlistService;

    @Autowired
    public TrackListener(
            TrackRepository trackRepository,
            PlaylistService playlistService
    )
    {
        this.trackRepository = trackRepository;
        this.playlistService = playlistService;
    }

    public void onCreated(String path)
    {
        Track track = findTrack(path).orElse(new Track(UUID.randomUUID().toString(), path));
        trackRepository.save(track);
        // TODO: move to PlaylistListener
        playlistService.add(track);
        afterChange();
    }

    public void onDeleted(String path)
    {
        Optional<Track> track = findTrack(path);

        if (track.isPresent()) {
            trackRepository.deleteById(track.get().getId());
            // TODO: move to PlaylistListener
            playlistService.delete(track.get());
        }

        afterChange();
    }

    private Optional<Track> findTrack(String path)
    {
        return trackRepository.findFirstByPath(path);
    }

    private void afterChange()
    {
        log.info("Tracks list updated");
    }
}
