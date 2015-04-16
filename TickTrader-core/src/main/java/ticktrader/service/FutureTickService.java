package ticktrader.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ticktrader.dto.Tick;
import ticktrader.util.BlockingMap;
import ticktrader.util.Utils;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author: huayueh
 * Date: 2015/4/16
 */
public class FutureTickService extends Observable implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(FutureTickService.class);
    private static final Logger tag = LoggerFactory.getLogger("Tag");
    protected Observer quoteService;
    protected SettleProvider stProvider = SettleProvider.getInstance();
    private Map<String, List> blockingMap = new BlockingMap<String, List>();
    private BlockingQueue<Tick> tickQueue = new LinkedBlockingQueue<Tick>(10000);
    private ExecutorService jobExecutor;
    private long start;
    private long end;
    private String baseFolder = "E:\\Tick\\Future_Tick\\";
    private AtomicBoolean allinQueue = new AtomicBoolean(false);

    private class Ticker extends Thread {
        Ticker(String name) {
            super.setName(name);
        }

        @Override
        public void run() {
            tag.info("Ticker start");
            while (!allinQueue.get() || !tickQueue.isEmpty()) {
                Tick tick = null;
                try {
                    tick = tickQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                onTick(tick);
            }
            tag.info("Ticker end");
        }
    }

    private class QueueAdder extends Thread {
        QueueAdder(String name) {
            super.setName(name);
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
                    List<Tick> ticks = blockingMap.remove(fileName);
                    for (Tick tick : ticks) {
                        try {
                            tickQueue.put(tick);
                        } catch (InterruptedException e) {
                            logger.error("", e);
                        }
                    }
                }
                calStart.add(Calendar.DATE, 1);
                start = calStart.getTimeInMillis();
            }
            logger.info("QueueAdder allinQueue");
            allinQueue.set(true);
            jobExecutor.shutdown();
        }
    }

    public FutureTickService(long start, long end, Observer ob) {
        this.start = start;
        this.end = end;
        quoteService = ob;
        new QueueAdder("QueueAdder").start();
        new Ticker("Ticker").start();
    }

    public void onTick(final Tick tick) {
        logger.info("{}", tick);
        setChanged();
        notifyObservers(tick);
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
                jobExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        parse(file);
//                        parseRand(file);
//                        mem(file);
                    }
                });
                logger.info(file.getName());
            }
            calStart.add(Calendar.DATE, 1);
            start = calStart.getTimeInMillis();
        }
        logger.info("allinQueue");
    }

    private void parse(File file) {
        String line;
        String fileName = file.getName();
        LineIterator it = null;
        List<Tick> ticks = new ArrayList<Tick>();

        try {
            it = FileUtils.lineIterator(file, "Big5");
            while (it.hasNext()) {
                line = it.nextLine();
                Tick tick = wrapTick(line);
                if (tick == null || !"MTX".equals(tick.getProductId()))
                    continue;
                ticks.add(tick);

                this.addObserver(quoteService);
            }
            blockingMap.put(fileName, ticks);
            logger.info(file.getName() + " finish");
        } catch (Exception ex) {
            logger.error("", ex);
        } finally {
            LineIterator.closeQuietly(it);
        }
    }

    /**
     * use RandomAccessFile
     */
    private void parseRand(File file) {
        String line;
        String fileName = file.getName();
        RandomAccessFile raf = null;
        List<Tick> ticks = new ArrayList<Tick>();

        try {
            raf = new RandomAccessFile(file, "r");
            while (raf.getFilePointer() < raf.length()) {
                line = raf.readLine();
                line = new String(line.getBytes("8859_1"), "Big5");//solved chinese encoding
                Tick tick = wrapTick(line);
                if (tick == null || !"MTX".equals(tick.getProductId()))
                    continue;
                ticks.add(tick);
            }
            blockingMap.put(fileName, ticks);
            logger.info(file.getName() + " finish");
        } catch (Exception ex) {
            logger.error("", ex);
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Tick wrapTick(String line) {
        Tick tick = null;

        String[] ary = StringUtils.split(line, ",");
        //交易日期,商品代號,交割年月,成交時間,成交價格,成交數量(B+S)
        if (ary.length > 5 && !line.startsWith("交易日期")) {
            String date = ary[0].trim();
            String product = ary[1].trim();
            String contract = ary[2].trim();
            String time = ary[3].trim();
            time = time.substring(0, 6);
            String price = ary[4].trim();
            String qty = ary[5].trim();
            long ltime = Utils.formatTimeStamp(date + time);

            tick = new Tick();
            tick.setTime(ltime);
            tick.setPrice(NumberUtils.toDouble(price));
            if (product.equals("FIMTX") || product.equals("MXF")) {
                product = "MTX";
            }
            tick.setProductId(product);
            tick.setContract(contract);
            tick.setQty(NumberUtils.toInt(qty));
        }
        return tick;
    }

    public void mem(File file) {
        String line;
        String fileName = file.getName();
        FileChannel fc = null;
        List<Tick> ticks = new ArrayList<Tick>();

        try {
            fc = new FileInputStream(file).getChannel();
            MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            Charset charset = Charset.forName("Big5");
            CharsetDecoder decoder = charset.newDecoder();
            CharBuffer charBuffer = decoder.decode(byteBuffer);
            Scanner sc = new Scanner(charBuffer).useDelimiter(System.getProperty("line.separator"));
            while (sc.hasNext()) {
                line = sc.next();
                Tick tick = wrapTick(line);
                if (tick == null || !"MTX".equals(tick.getProductId()))
                    continue;
                ticks.add(tick);
            }
            fc.close();
            blockingMap.put(fileName, ticks);
            logger.info(file.getName() + " finish");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CharacterCodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
