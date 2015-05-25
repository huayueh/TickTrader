package ticktrader.recorder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Author: huayueh
 * Date: 2015/5/25
 */
public abstract class AbstractFileRecorder<T> implements Recorder<T> {
    private Path savePath;

    public AbstractFileRecorder(Path path) {
        this.savePath = path;
        if (!Files.exists(path)){
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void write(String s){
        try (BufferedWriter writer = Files.newBufferedWriter(savePath, Charset.defaultCharset(), StandardOpenOption.APPEND)){
            writer.write(s, 0, s.length());
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
    }
}
