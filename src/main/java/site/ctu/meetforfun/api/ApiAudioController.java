package site.ctu.meetforfun.api;

import site.ctu.meetforfun.dto.ApiTrackDto;
import site.ctu.meetforfun.dto.PlaylistDto;
import site.ctu.meetforfun.entity.Track;
import site.ctu.meetforfun.service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
    public ResponseEntity<PlaylistDto> playlist()
    {
        List<ApiTrackDto> tracks = audioService.getAllTracks()
                .stream()
                .map(this::transformTrackToDto)
                .toList();

        return ResponseEntity.ok(new PlaylistDto(tracks));
    }

    @GetMapping("/api/playback/current")
    public ResponseEntity<ApiTrackDto> current()
    {
        Track track = audioService.getCurrent();

        if (track == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(transformTrackToDto(track));
    }

    @GetMapping("/api/playback/next")
    public ResponseEntity<ApiTrackDto> next()
    {
        audioService.next();

        return current();
    }

    @GetMapping("/api/playback/{hash}")
    public ResponseEntity<ApiTrackDto> play(@PathVariable String hash) throws Exception
    {
        Track track = audioService.getCurrentByHash(hash);

        if (track == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(transformTrackToDto(track));
    }

    private ApiTrackDto transformTrackToDto(Track track)
    {
        return new ApiTrackDto(track, "/download/" + track.getId());
    }
}
