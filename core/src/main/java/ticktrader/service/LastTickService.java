package ticktrader.service;

import ticktrader.dto.Tick;
import ticktrader.dto.Topic;
import ticktrader.strategy.Strategy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Author: huayueh
 * Date: 2015/5/25
 */
public class LastTickService extends FutureTickService {

    public LastTickService(String baseFolder, int year, Strategy ob) {
        super(baseFolder, year, ob);
    }

    @Override
    protected void fileConsumer(Path path){
        try (Stream<String> stream = Files.lines(path, Charset.defaultCharset())) {
            Optional<Tick> opTick = stream.map(line -> wrapTick(line)).
                    filter(tick -> {
                        if (tick != null) {
                            return topics.contains(Topic.get(tick));
                        }
                        return false;
                    }).reduce((previous, current) -> current);
            onTick(opTick.get());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
