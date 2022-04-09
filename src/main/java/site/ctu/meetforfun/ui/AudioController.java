package site.ctu.meetforfun.ui;

import site.ctu.meetforfun.entity.Track;
import site.ctu.meetforfun.service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Controller
public class AudioController
{
    private final AudioService audioService;

    @Autowired
    public AudioController(AudioService audioService)
    {
        this.audioService = audioService;
    }

    @GetMapping("/")
    public String main(Model model) throws UnsupportedEncodingException
    {
        model.addAttribute("tracks", audioService.getAllTracks());
        model.addAttribute("total", audioService.getAllTracks().size());

        return "main";
    }

    @GetMapping("/controls")
    public String controls()
    {
        return "controls";
    }

    @GetMapping(value = "/download/{hash}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> download(@PathVariable String hash) throws IOException
    {
        var download = audioService.downloadByHash(hash);
        var headers = new HttpHeaders();
        headers.add("Accept-Ranges", "bytes");
        headers.setContentLength(download.getSize());
        headers.setContentType(MediaType.parseMediaType(download.getContentType()));

        return new ResponseEntity<>(download.getIn(), headers, HttpStatus.OK);
    }

    @GetMapping("/next")
    public String next()
    {
        audioService.next();
        Track track = audioService.getCurrent();

        return "redirect:" + (track == null ? "/" : "/audio/" + track.getId());
    }
}
