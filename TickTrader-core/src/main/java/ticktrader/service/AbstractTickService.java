package ticktrader.service;

import com.pretty_tools.dde.DDEException;
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
    protected final Strategy observer;
    protected String baseFolder;
    private List<Topic> topics = new ArrayList<>();

    public AbstractTickService(String baseFolder, Strategy ob) {
        this.baseFolder = baseFolder;
        this.observer = ob;
        this.addObserver(ob);
    }

    public AbstractTickService(Strategy ob) {
        this.observer = ob;
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
        if (topics.contains(tick.getTopic())) {
            logger.debug("{}", tick);
            setChanged();
            notifyObservers(tick);
        }
    }

    @Override
    public void run() {
        Path path = Paths.get(baseFolder);
        try {
            Files.list(path)
                    .parallel()
                    .forEach(p -> {
                        try (Stream<String> stream = Files.lines(p, Charset.defaultCharset())) {
                            stream.map(line -> wrapTick(line)).
                                    filter(tick -> tick != null).
                                    forEach(tick -> onTick(tick));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract Tick wrapTick(String line);
}
