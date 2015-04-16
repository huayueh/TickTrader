package ticktrader.storage;

import org.apache.commons.io.FileUtils;
import ticktrader.dto.Quote;
import ticktrader.dto.Tick;
import ticktrader.dto.TimePeriod;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


/**
 * User: Harvey
 * Date: 2014/2/5
 */
public class AdvCsvStroage implements Stroage {

    protected final int BUF_SIZE = 2048;
    public static String LINE_SEPARATOR = System.getProperty("line.separator");
    protected FileChannel qtFChannel = null;
    protected FileChannel tickFChannel = null;
    protected ByteBuffer qtBuffer;
    protected ByteBuffer tickBuffer;
    protected File qtFile;
    protected File tickFile;

    public AdvCsvStroage(String contract, String product, TimePeriod period){
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

}
