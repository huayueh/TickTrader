package com.nv.financial.chart.storage;

import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.dto.Tick;
import com.nv.financial.chart.quote.TimePeriod;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.nv.financial.chart.util.Utils.FILE_DELIMITER;

/**
 * User: Harvey
 * Date: 2013/10/25
 * Time: 下午 2:52
 *
 * For test.
 * Output quote to csv file split by TimePeriod.
 */
public class CsvStorage implements IStorage{
    private static final Logger logger = LogManager.getLogger(CsvStorage.class);
    private File file;

    public CsvStorage(String provider, String product, TimePeriod period){
        String strPath = "Quote" + File.separator + period.name() + File.separator +
                provider + FILE_DELIMITER + product + FILE_DELIMITER + period.name() + ".csv";
        file = new File(strPath);
    }


    @Override
    public void save(Tick tick) {
    }

    @Override
    public void save(Quote quote) {
        try {
            FileUtils.writeStringToFile(file, quote.toString(), true);
            FileUtils.writeStringToFile(file, "\n", true);
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    @Override
    public Quote retrieveQuote(long time, String provider, String product, TimePeriod period) {
        return null;
    }

    /**
     * @deprecated
     */
    @Override
    public List<Quote> retrieveQuotes(long fromTime, long toTime, String provider, String prd, TimePeriod period) {
        return Collections.emptyList();
    }

    @Override
    public List<Quote> retrieveQuotes(int nBars, long toTime, String provider, String product, TimePeriod period) {
        return Collections.emptyList();
    }

    @Override
    public List<Quote> retrieveQuotes(String provider, String product, TimePeriod period) {
        return Collections.emptyList();
    }


//    @Override
//    public List<Quote> retrieveQuotes(long fromTime, long toTime, String contract, String prd, TimePeriod period) {
//        List<Quote> ret = new ArrayList<Quote>();
//        Scanner scan;
//        String line;
//        try {
//            if(!file.exists())
//                return ret;
//            scan = new Scanner(file);
//            int idx = 0;
//            while (scan.hasNext()) {
//                line = scan.next();
//                String[] ary = StringUtils.split(line, ",");
//                //Feed,Product,TimePeriod,Time,Open,High,Low,Close,Volume
//                if (ary.length == 9) {
//                    String quoteProvider = ary[0].trim();
//                    String product = ary[1].trim();
//                    String timePeriod = ary[2].trim();
//                    String time = ary[3].trim();
//                    String open = ary[4].trim();
//                    String high = ary[5].trim();
//                    String low = ary[6].trim();
//                    String close = ary[7].trim();
//                    String vol = ary[8].trim();
//
//                    long lTime = Utils.formatTimeStamp(time);
//                    Quote quote = new Quote(lTime);
//                    quote.setContract(quoteProvider);
//                    quote.setProduct(product);
//                    quote.setPeriod(TimePeriod.valueOf(timePeriod));
//                    quote.setOpen(NumberUtils.toDouble(open));
//                    quote.setHigh(NumberUtils.toDouble(high));
//                    quote.setLow(NumberUtils.toDouble(low));
//                    quote.setClose(NumberUtils.toDouble(close));
//                    quote.setVolume(NumberUtils.toLong(vol));
//                    ret.add(quote);
//
//                    idx++;
////                    logger.debug(quote);
//                }
//            }
//            logger.info("restore " + contract + FILE_DELIMITER + prd + FILE_DELIMITER + period.name() + "　success");
//        } catch (Exception ex) {
//            logger.error("", ex);
//        }
////        int size = ret.size();
////        return ret.subList(size - ChartSetting.getMaxInMem(), size-1);
//        return ret;
//    }
}
