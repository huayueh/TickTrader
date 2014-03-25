package com.nv.financial.chart.service;

import com.nv.financial.chart.concurrent.JobExecutor;
import com.nv.financial.chart.dto.Tick;
import com.nv.financial.chart.quote.TimePeriod;
import com.nv.financial.chart.quote.provider.IMemQuoteProvider;
import com.nv.financial.chart.quote.provider.SettleProvider;
import com.nv.financial.chart.storage.AdvCsvStorage;
import com.nv.financial.chart.storage.IStorage;
import com.nv.financial.chart.util.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Get tick from feed server
 * Prime observers are EventService, IndicatorService, QuoteProvider
 */
public class FutureTickService extends Observable implements IMarketTickService, Runnable {
    private static final Logger logger = LogManager.getLogger(FutureTickService.class);
    private static final Logger tag = LogManager.getLogger("Tag");
    protected IQuoteService quoteService;
    private IStorage storage;
    protected SettleProvider stProvider = SettleProvider.getInstance();
    private Map<String, List> blockingMap = new BlockingMap<String, List>();
    private BlockingQueue<Tick> tickQueue = new LinkedBlockingQueue<Tick>(10000);
    private long start;
    private long end;
    private String baseFolder = "E:\\Tick\\Future_Tick\\";
    private AtomicBoolean allinQueue = new AtomicBoolean(false);

    private class TickCsvStorage extends AdvCsvStorage {
        ExecutorService exc = Executors.newSingleThreadExecutor();

        public TickCsvStorage(String contract, String product, TimePeriod period) {
            super(contract, product, period);
        }

        @Override
        public void save(final Tick tick) {
//            logger.debug("save " + tick);
            exc.execute(new Runnable() {
                @Override
                public void run() {
                    String line = tick.toString() + LINE_SEPARATOR;
                    byte[] in = line.getBytes();
                    if ((tickBuffer.limit() + in.length) > BUF_SIZE) {
                        try {
                            tickBuffer.flip();
                            tickFChannel.write(tickBuffer);
                            tickBuffer.clear();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    tickBuffer.put(in);
                }
            });
        }
    }

    private class BlockingMap<K, V> implements Map<K, V> {
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition condition = lock.newCondition();
        private final Map<K, V> map = new ConcurrentSkipListMap<K, V>();
        private final AtomicInteger count = new AtomicInteger(0);
        private final int capacity = 20;

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return map.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return map.containsValue(value);
        }

        @Override
        public V get(Object key) {
            throw new NotImplementedException();
        }

        @Override
        public V put(K key, V value) {
            try {
                lock.lockInterruptibly();
                while (count.get() == capacity) {
                    condition.await();
                }
                count.getAndIncrement();
                map.put(key, value);
                logger.debug("put " + key);
                condition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

            return value;
        }

        public V remove(Object key) {
            V x = null;

            try {
                lock.lockInterruptibly();
                while (count.get() == 0) {
                    condition.await();
                }
                while (!map.containsKey(key)) {
                    logger.debug("wait on " + key);
                    condition.await();
                }
                x = map.remove(key);
                count.getAndDecrement();
                condition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }

            return x;
        }

        @Override
        public void putAll(Map m) {
            throw new NotImplementedException();
        }

        @Override
        public void clear() {
            map.clear();
        }

        @Override
        public Set keySet() {
            return map.keySet();
        }

        @Override
        public Collection values() {
            return map.values();
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return map.entrySet();
        }
    }

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
            JobExecutor.close();
        }
    }

    public FutureTickService(long start, long end) {
        this.start = start;
        this.end = end;
        quoteService = QuoteService.getInstance();
        new QueueAdder("QueueAdder").start();
        new Ticker("Ticker").start();
        storage = new TickCsvStorage("CUR", "MTX", TimePeriod.DAY);
    }

    @Override
    public void onTick(final Tick tick) {
        logger.info(tick);
        storage.save(tick);
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
                JobExecutor.execute(new Runnable() {
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

                if (!quoteService.isExist(tick.getContract(), tick.getProductId())) {
                    quoteService.creatQuoteProvider(tick.getContract(), tick.getProductId());
                    for (IMemQuoteProvider provider : quoteService.getAllMemProvider()) {
                        this.addObserver(provider);
                    }
                }
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
