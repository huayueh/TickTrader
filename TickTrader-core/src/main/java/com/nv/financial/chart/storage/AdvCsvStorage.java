package com.nv.financial.chart.storage;

import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.dto.Tick;
import com.nv.financial.chart.quote.TimePeriod;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.List;

import static com.nv.financial.chart.util.Utils.FILE_DELIMITER;

/**
 * User: Harvey
 * Date: 2014/2/5
 * Time: 下午 4:15
 */
public class AdvCsvStorage implements IStorage{
    private static final Logger logger = LogManager.getLogger(CsvStorage.class);
    protected final int BUF_SIZE = 2048;
    public static String LINE_SEPARATOR = System.getProperty("line.separator");
    protected FileChannel qtFChannel = null;
    protected FileChannel tickFChannel = null;
    protected ByteBuffer qtBuffer;
    protected ByteBuffer tickBuffer;
    protected File qtFile;
    protected File tickFile;

    public AdvCsvStorage(String contract, String product, TimePeriod period){
        String strPath = "Quote" + File.separator + period.name() + File.separator +
                contract + File.separator + product + ".csv";
        qtFile = new File(strPath);
        strPath = "Tick" + File.separator + product + ".csv";
        tickFile = new File(strPath);

        try {
            qtFChannel = FileUtils.openOutputStream(qtFile,true).getChannel();
            qtBuffer = ByteBuffer.allocate(BUF_SIZE);
            tickFChannel = FileUtils.openOutputStream(tickFile,true).getChannel();
            tickBuffer = ByteBuffer.allocate(BUF_SIZE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void save(Tick tick) {
//        String line = tick.toString() + LINE_SEPARATOR;
//        byte[] in = line.getBytes();
//        if ((tickBuffer.limit() + in.length) > BUF_SIZE) {
//            try {
//                tickBuffer.flip();
//                tickFChannel.write(tickBuffer);
//                tickBuffer.clear();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        tickBuffer.put(in);
    }

    @Override
    public void save(Quote quote) {
//        logger.debug("save " + quote);
        String line = quote.toString() + LINE_SEPARATOR;
        byte[] in = line.getBytes();
        if ((qtBuffer.limit() + in.length) > BUF_SIZE) {
            try {
                qtBuffer.flip();
                qtFChannel.write(qtBuffer);
                qtBuffer.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        qtBuffer.put(in);
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
}
