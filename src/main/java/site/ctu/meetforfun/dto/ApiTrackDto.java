package site.ctu.meetforfun.dto;

import site.ctu.meetforfun.entity.Track;
import lombok.Getter;

public class ApiTrackDto
{
    private final Track track;

    @Getter
    private final String downloadUrl;

    public ApiTrackDto(Track track, String downloadUrl)
    {
        this.track = track;
        this.downloadUrl = downloadUrl;
    }

    public String getId()
    {
        return track.getId();
    }

    public String getName()
    {
        return track.getName();
    }
}
