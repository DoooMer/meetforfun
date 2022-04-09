package site.ctu.meetforfun.repository;

import site.ctu.meetforfun.entity.Track;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrackRepository extends CrudRepository<Track, String>
{
    Optional<Track> findFirstByPath(String path);

    void deleteByPath(String path);
}
