package ticktrader.recorder;

import ticktrader.dto.Tick;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Author: huayueh
 * Date: 2015/4/27
 */
public class FileTickRecorder implements Recorder<Tick> {
    private Path tickPath;

    public FileTickRecorder(Path path) {
        this.tickPath = path;
        if (!Files.exists(path)){
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void record(Tick position) {
        try (BufferedWriter writer = Files.newBufferedWriter(tickPath, Charset.defaultCharset(), StandardOpenOption.APPEND)){
            String s = position.toString();
            writer.write(s, 0, s.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }
}
