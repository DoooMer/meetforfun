package home.server.jwebplayer.service.watch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Run directory watcher.
 */
@Component
public class DirectoryWatchService
{
    private final DirectoryWatcher directoryWatcher;

    @Autowired
    public DirectoryWatchService(DirectoryWatcher directoryWatcher)
    {
        this.directoryWatcher = directoryWatcher;
    }

    @PostConstruct
    public void run()
    {
        directoryWatcher.watch();
    }
}
