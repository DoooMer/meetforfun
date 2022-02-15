package home.server.jwebplayer.service.watch;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

/**
 * Watcher of root directory.
 * Trigger for add or remove track info into DB.
 */
@Slf4j
public class DirectoryWatcher implements Runnable
{
    private final TrackListener listener;

    private final Path directory;

    public DirectoryWatcher(Path directory, TrackListener listener)
    {
        this.directory = directory;
        this.listener = listener;
    }

    public void watch()
    {
        if (!directory.toFile().exists()) {
            return;
        }

        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();

    }

    @Override
    public void run()
    {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            watchDirectory(directory, watchService);
            log.info("Watch started ...");
            listen(watchService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void watchDirectory(Path path, WatchService watchService) throws IOException
    {
        path.register(watchService, ENTRY_CREATE, ENTRY_DELETE);

        var files = path.toFile().listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {

            if (file.isDirectory()) {
                watchDirectory(file.toPath(), watchService);
            }

        }

    }

    private void listen(WatchService watchService)
    {
        boolean poll = true;


        while (poll) {
            WatchKey key;

            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            Path path = (Path) key.watchable();

            for (WatchEvent<?> event : key.pollEvents()) {
                String filePath = path
                        .resolve((Path) event.context())
                        .toFile()
                        .toString()
                        .substring(directory.toString().strip().length() + 1);

                if (event.kind().equals(ENTRY_DELETE)) {
                    // ...
                    listener.onDeleted(filePath);
                } else if (event.kind().equals(ENTRY_CREATE)) {
                    // ...
                    listener.onCreated(filePath);
                }

            }

            poll = key.reset();
        }
    }
}
