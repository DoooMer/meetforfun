package site.ctu.meetforfun.service.playlist;

import site.ctu.meetforfun.entity.Track;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

@Component
public class PlaylistService
{
    private LinkedList<String> queue = new LinkedList<>();

    private Integer cursor = 0;

    public void setQueue(LinkedList<String> queue)
    {
        this.queue = queue;
    }

    public void add(Track track)
    {
        queue.add(track.getId());
    }

    public String current()
    {
        return queue.size() > 0 ? queue.get(cursor) : null;
    }

    public void select(String id) throws Exception
    {
        int index = queue.indexOf(id);

        if (index < 0) {
            // TODO: custom exception and cursor+1
            throw new Exception("Track " + id + " not found.");
        }

        cursor = index;
    }

    public void next()
    {
        int size = queue.size();

        if (size < 1) {
            cursor = 0;
            return;
        }

        cursor++;

        if (cursor >= size) {
            cursor = 0;
        }
    }

    public void delete(Track track)
    {
        int index = queue.indexOf(track.getId());

        if (index < 0) {
            return;
        }

        if (index <= cursor) {
            cursor--;
        }

        queue.remove(index);
    }
}
