package ticktrader.service;

import ticktrader.dto.Contract;
import ticktrader.dto.Tick;
import ticktrader.strategy.Strategy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Author: huayueh
 * Date: 2015/4/28
 */
public class OpenTickService extends FutureTickService {

    public OpenTickService(String baseFolder, int year, Strategy ob) {
        super(baseFolder, year, ob);
    }

    @Override
    protected void fileConsumer(Path path){
        try (Stream<String> stream = Files.lines(path, Charset.defaultCharset())) {
            Optional<Tick> opTick = stream.map(line -> wrapTick(line)).
                    filter(tick -> {
                        if (tick != null) {
                            return contracts.contains(Contract.getCurrent(tick));
                        }
                        return false;
                    }).
                    findFirst();
            onTick(opTick.get());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
