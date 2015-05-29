package ticktrader.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrader.dto.Tick;
import ticktrader.dto.Topic;
import ticktrader.strategy.Strategy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.stream.Stream;

/**
 * Author: huayueh
 * Date: 2015/4/24
 */
public abstract class AbstractTickService extends Observable implements TickService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTickService.class);
    protected final Strategy strategy;
    protected List<Topic> topics = new ArrayList<>();
    private Path path;
    private int year;

    public AbstractTickService(String baseFolder, int year, Strategy ob) {
        //specify year or all files
        this.path = (year > 0) ? Paths.get(baseFolder + year) : Paths.get(baseFolder);
        this.year = year;
        this.strategy = ob;
        this.addObserver(ob);
    }

    public AbstractTickService(Strategy ob) {
        this.strategy = ob;
        this.addObserver(ob);
    }

    @Override
    public void addTopic(Topic topic) {
        topics.add(topic);
    }

    @Override
    public void removeTopic(Topic topic) {
        topics.remove(topic);
    }


    @Override
    public void onTick(final Tick tick) {
        if (topics.contains(Topic.get(tick))) {
            logger.debug("{}", tick);
            setChanged();
            notifyObservers(tick);
        }
    }

    @Override
    public void run() {
        try {
            Files.list(path).forEach(pOrf -> {
                if (year > 0) {
                    fileConsumer(pOrf);
                } else {
                    try {
                        Files.list(pOrf).forEach(p -> fileConsumer(p));
                    } catch (IOException e) {
                        logger.error("", e);
                    }
                }
            });
        } catch (IOException e) {
            logger.error("", e);
        }
        strategy.done();
    }

    protected void fileConsumer(Path path){
        try (Stream<String> stream = Files.lines(path, Charset.defaultCharset())) {
            stream.map(line -> wrapTick(line)).
                    filter(tick -> tick != null).
                    forEach(tick -> onTick(tick));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract Tick wrapTick(String line);
}
