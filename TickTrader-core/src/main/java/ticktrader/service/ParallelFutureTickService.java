package ticktrader.service;

import ticktrader.dto.Tick;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Observer;
import java.util.stream.Stream;

/**
 * Author: huayueh
 * Date: 2015/4/17
 */
public class ParallelFutureTickService extends AbstractFutureTickService {

    public ParallelFutureTickService(long start, long end, Observer ob) {
        super(start, end, ob);
    }

    @Override public void run() {
        Path path = Paths.get(baseFolder+"\\2014");
        try {
            Files.list(path)
                .parallel()
                .forEach(p -> {
                    try (Stream<String> stream = Files.lines(p, Charset.defaultCharset())) {
                        stream.forEach(line -> {
                            Tick tick = wrapTick(line);
                            if (tick != null && "MTX".equals(tick.getProductId())) {
                                onTick(tick);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

