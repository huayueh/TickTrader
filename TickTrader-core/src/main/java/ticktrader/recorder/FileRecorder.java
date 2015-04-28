package ticktrader.recorder;

import ticktrader.dto.Position;
import ticktrader.dto.Tick;
import ticktrader.dto.TimePeriod;

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
public class FileRecorder implements Recorder {
    private Path positionPath;
    private Path tickPath;

    public FileRecorder(Path position, Path tick) {
        this.positionPath = position;
        this.tickPath = tick;
        if (!Files.exists(tick)){
            try {
                Files.createFile(tick);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void record(Position position) {

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
