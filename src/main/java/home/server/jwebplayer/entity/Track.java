package home.server.jwebplayer.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Entity
@Table(name = "tracks")
@AllArgsConstructor
@NoArgsConstructor
public class Track
{
    @Id
    @Getter
    @Setter
    private String id; // hash

    @Column(unique = true)
    @Getter
    @Setter
    private String path; // relative path

    @Column
    @Getter
    @Setter
    private String name; // track name

    public Track(String hash, String path) //throws UnsupportedEncodingException
    {
        id = hash;
        name = path.substring(0, path.length() - 4);
        this.path = path;//URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
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
