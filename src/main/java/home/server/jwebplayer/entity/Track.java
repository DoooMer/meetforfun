package home.server.jwebplayer.entity;

import lombok.Getter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Track
{
    @Getter
    private final String id; // hash

    @Getter
    private final String path; // relative path

    @Getter
    private final String name; // track name

    public Track(String hash, String path) throws UnsupportedEncodingException
    {
        id = hash;
        name = path.substring(0, path.length() - 4);
        this.path = URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
    }

    @Override
    public boolean equals(Object obj)
    {

        if (obj instanceof Track) {
            return ((Track) obj).getId().equals(id);
        }

        return super.equals(obj);
    }
}
