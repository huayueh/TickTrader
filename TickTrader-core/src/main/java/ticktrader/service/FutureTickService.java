package ticktrader.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrader.dto.Tick;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Observer;
import java.util.stream.Stream;

/**
 * Author: huayueh
 * Date: 2015/4/16
 */
public class FutureTickService extends AbstractFutureTickService {
    private static final Logger logger = LoggerFactory.getLogger(FutureTickService.class);

    public FutureTickService(long start, long end, Observer ob) {
        super(start, end, ob);
    }

    @Override
    public void run() {
        Calendar calStart = Calendar.getInstance();
        calStart.setTimeInMillis(start);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTimeInMillis(end);

        while (start < end) {
            //Daily_2013_01_02.rpt
            int year = calStart.get(Calendar.YEAR);
            String month = "" + (calStart.get(Calendar.MONTH) + 1);
            String day = "" + calStart.get(Calendar.DAY_OF_MONTH);
            String path = baseFolder + year + File.separator;
            month = (month.length() == 1) ? "0" + month : month;
            day = (day.length() == 1) ? "0" + day : day;
            String fileName = "Daily_" + year + "_" + month + "_" + day + ".rpt";
            path += fileName;
            final File file = new File(path);
            if (file.exists()) {
                try (Stream<String> stream = Files.lines(file.toPath(), Charset.defaultCharset())) {
                    stream.forEach(line -> {
                        Tick tick = wrapTick(line);
                        if (tick != null && "MTX".equals(tick.getSymbol())){
                            onTick(tick);
                        }
                    });
                } catch (IOException e) {
                    logger.error("{}", e);
                }
            }
            calStart.add(Calendar.DATE, 1);
            start = calStart.getTimeInMillis();
        }
    }

}
