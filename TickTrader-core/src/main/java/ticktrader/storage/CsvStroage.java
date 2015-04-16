package ticktrader.storage;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrader.dto.Quote;
import ticktrader.dto.Tick;
import ticktrader.dto.TimePeriod;
import ticktrader.util.Utils;

import java.io.File;
import java.io.IOException;


/**
 * Author: huayueh
 * Date: 2015/4/16
 *
 * For test.
 * Output quote to csv qtFile split by TimePeriod.
 */
public class CsvStroage implements Stroage {
    private static final Logger logger = LoggerFactory.getLogger(CsvStroage.class);
    private File qtFile;
    private File tickFile;

    public CsvStroage(String provider, String product, TimePeriod period){
        String strPath = "Quote" + File.separator + period.name() + File.separator +
                provider + Utils.FILE_DELIMITER + product + Utils.FILE_DELIMITER + period.name() + ".csv";
        qtFile = new File(strPath);
        strPath = "Tick" + File.separator + product + ".csv";
        tickFile = new File(strPath);
    }

    @Override
    public void save(Tick tick) {
        try {
            FileUtils.writeStringToFile(tickFile, tick.toString(), true);
            FileUtils.writeStringToFile(tickFile, "\n", true);
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    @Override
    public void save(Quote quote) {
        try {
            FileUtils.writeStringToFile(qtFile, quote.toString(), true);
            FileUtils.writeStringToFile(qtFile, "\n", true);
        } catch (IOException e) {
            logger.error("", e);
        }
    }

}
