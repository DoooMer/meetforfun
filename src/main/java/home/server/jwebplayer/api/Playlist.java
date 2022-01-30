package home.server.jwebplayer.api;

import home.server.jwebplayer.dto.ApiTrackDto;
import lombok.Getter;

import java.util.List;

public class Playlist
{
    @Getter
    private final List<ApiTrackDto> tracks;

    @Getter
    private final int total;

    public Playlist(List<ApiTrackDto> tracks)
    {
        this.tracks = tracks;
        total = tracks.size();
    }
}
