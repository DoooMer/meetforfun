package home.server.jwebplayer.dto;

import lombok.Getter;

import java.util.List;

public class PlaylistDto
{
    @Getter
    private final List<ApiTrackDto> tracks;

    @Getter
    private final int total;

    public PlaylistDto(List<ApiTrackDto> tracks)
    {
        this.tracks = tracks;
        total = tracks.size();
    }
}
