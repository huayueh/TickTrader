package ticktrader.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrader.dto.Contract;
import ticktrader.dto.Tick;
import ticktrader.strategy.Strategy;

/**
 * Author: huayueh
 * Date: 2015/4/24
 */
public abstract class AbstractTickService extends Observable implements TickService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTickService.class);

    protected final Strategy strategy;

    protected List<Contract> contracts = new ArrayList<>();

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
    public void addContract(Contract contract) {
        contracts.add(contract);
    }

    @Override
    public void removeContract(Contract contract) {
        contracts.remove(contract);
    }


    @Override
    public void onTick(final Tick tick) {
        logger.debug("{}", tick);
        setChanged();
        notifyObservers(tick);
    }

    @Override
    public void run() {
        try {
            Files.list(path).sorted().forEach(pOrf -> {
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

    protected void fileConsumer(Path path) {
        logger.info("start file: {}", path);
        try (Stream<String> stream = readLinesFromZip(path.toFile()).stream()) {
            stream.map(line -> {
                        Tick tick = null;
                        try {
                            tick = wrapTick(line);
                        } catch (Exception e) {
                            logger.warn("can't turn line {} to tick", line);
                        }
                        return tick;
                    })
                    .filter(tick -> tick != null)
                    .sorted((t1, t2) -> t1.getTime().compareTo(t2.getTime())).
                    forEach(tick -> onTick(tick));
        }

    }

    private List<String> readLinesFromZip(File file) {
        List<String> lines = new ArrayList<>();

        try (BufferedInputStream bf = new BufferedInputStream(new FileInputStream(file));
             ZipInputStream zip = new ZipInputStream(bf, Charset.forName("Big5"))) {
            if (zip.getNextEntry() != null) {
                String line;
                try (BufferedReader br = new BufferedReader(new InputStreamReader(zip, Charset.forName("Big5")))) {
                    while ((line = br.readLine()) != null) {
                        lines.add(line);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("{}", e);
        }
        return lines;
    }

    protected abstract Tick wrapTick(String line);
}
