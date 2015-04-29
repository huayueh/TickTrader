package ticktrader.service;

import ticktrader.dto.Tick;
import ticktrader.dto.Topic;
import ticktrader.strategy.Strategy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Author: huayueh
 * Date: 2015/4/28
 */
public class OpenTickService extends FutureTickService {

    public OpenTickService(String baseFolder, Strategy strategy) {
        super(baseFolder, strategy);
    }

    @Override
    public void run() {
        Path path = Paths.get(baseFolder);
        try {
            Files.list(path)
                    .forEach(p -> {
                        try (Stream<String> stream = Files.lines(p, Charset.defaultCharset())) {
                            Optional<Tick> opTick = stream.map(line -> wrapTick(line)).
                                    filter(tick -> {
                                        if (tick != null) {
                                            return topics.contains(Topic.get(tick));
                                        }
                                        return false;
                                    }).
                                    findFirst();
                            onTick(opTick.get());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
