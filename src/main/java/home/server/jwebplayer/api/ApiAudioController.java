package home.server.jwebplayer.api;

import home.server.jwebplayer.dto.ApiTrackDto;
import home.server.jwebplayer.entity.Track;
import home.server.jwebplayer.service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ApiAudioController
{
    private final AudioService audioService;

    @Autowired
    public ApiAudioController(AudioService audioService)
    {
        this.audioService = audioService;
    }

    @GetMapping("/api/tracks")
    public ResponseEntity<Playlist> playlist()
    {
        List<ApiTrackDto> tracks = audioService.getAllTracks()
                .stream()
                .map(track -> new ApiTrackDto(track, "/download/" + track.getId()))
                .toList();

        return ResponseEntity.ok(new Playlist(tracks));
    }

    @GetMapping("/api/playback/current")
    public ResponseEntity<ApiTrackDto> current()
    {
        Track track = audioService.getCurrent();

        if (track == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new ApiTrackDto(track, "/download/" + track.getId()));
    }

    @GetMapping("/api/playback/next")
    public ResponseEntity<ApiTrackDto> next()
    {
        audioService.next();

        return current();
    }

    @GetMapping("/api/playback/{hash}")
    public ResponseEntity<ApiTrackDto> play(@PathVariable String hash)
    {
        Track track = audioService.getCurrentByHash(hash);

        if (track == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new ApiTrackDto(track, "/download/" + track.getId()));
    }
}
