package home.server.jwebplayer.service;

import home.server.jwebplayer.entity.Track;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.*;

@Component
class TrackStorage
{
    private int cursor = 0;

    private final LinkedList<Track> tracks = new LinkedList<>();

    private final HashMap<String, Integer> index = new HashMap<>();

    public void add(String path) throws UnsupportedEncodingException
    {
        String hash = UUID.randomUUID().toString();
        Track track = new Track(hash, path);
        tracks.add(track);
    }

    public void writeIndex()
    {
        int i = 0;

        if (tracks.size() > 0) {
            cursor = 0;
        }

        for (Track track : tracks) {
            index.put(track.getId(), i++);
        }
    }

    @Nullable
    public Track getByHash(String hash)
    {
        return index.containsKey(hash) ? tracks.get(index.get(hash)) : null;
    }

    public void setCursor(Track track) throws ArrayIndexOutOfBoundsException
    {

        if (!index.containsKey(track.getId())) {
            throw new ArrayIndexOutOfBoundsException("Index not synchronized.");
        }

        cursor = index.get(track.getId());
    }

    public Track getCurrent()
    {
        return tracks.get(cursor);
    }

    public List<Track> getAll()
    {
        return tracks;
    }

    public void cursorToNext()
    {
        cursor++;

        if (cursor >= tracks.size()) {
            cursor = 0;
        }

    }
}
