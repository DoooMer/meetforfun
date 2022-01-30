package home.server.jwebplayer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.InputStreamResource;

@AllArgsConstructor
public class DownloadedTrackDto
{
    @Getter
    private InputStreamResource in;

    @Getter
    private String path;

    @Getter
    private long size;

    @Getter
    private String contentType;
}
