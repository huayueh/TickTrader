package com.nv.financial.chart.quote.provider;

import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.dto.Tick;
import com.nv.financial.chart.quote.TimePeriod;
import com.nv.financial.chart.storage.AdvCsvStorage;
import com.nv.financial.chart.storage.IStorage;
import com.nv.financial.chart.util.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 */
public class MemQuoteQuoteProvider extends AbstractQuoteProvider implements IMemQuoteProvider {
    private static final Logger logger = Logger.getLogger(MemQuoteQuoteProvider.class);
    protected ConcurrentNavigableMap<Long, Quote> his = new ConcurrentSkipListMap<Long, Quote>();
    protected IStorage storage;
    private int maxInMem = 0;
    private AtomicReference<Tick> preTickRef = new AtomicReference<Tick>();

    public MemQuoteQuoteProvider(String contract, String product, TimePeriod period) {
        super(contract, product, period);
//        storage = new CsvStorage(contract, product, period);
        storage = new AdvCsvStorage(contract, product, period);
        restoreFromStorage();
    }

    @Override
    public long getNextTime(long time) {
        long now = latestQuoteTime();
        long nextTime = period.getNextOn(time);
        Quote quote;

        do {
            quote = his.get(nextTime);
            nextTime = period.getNextOn(nextTime);
            logger.debug("keep getting next time");
        } while (quote == null && nextTime < now);

        if(quote != null)
            return quote.getTime();
        else
            return Long.MAX_VALUE;
    }

    @Override
    public void setQuotes(Tick tick) {
        storage.save(tick);
        if (tick.equals(preTickRef.get())){
            return;
        }
        preTickRef.set(tick);
        //is process
        if (!StringUtils.equals(tick.getContract(), this.contract) ||
                !StringUtils.equals(tick.getProductId(), this.product)) {
            return;
        }

        double price = tick.getPrice();
        long cur = tick.getTime();

        Quote qt;
        if (his.isEmpty()) {
            qt = new Quote(period.getStartOn(cur),
                    price, price, price, price, 0);
            qt.setContract(tick.getContract());
            qt.setProduct(tick.getProductId());
            qt.setPeriod(period);
            qt.cntAvgPrice(price);
            qt.setNew(true);
            his.put(qt.getTime(), qt);
//            toStorage(qt);
            logger.debug("tick time:" + Utils.formatTimeStamp(cur) +
                    " create first bar:(" + qt + ")" + period.name());
            return;
        }

        qt = latestQuote();

        long curBarStart = qt.getTime();
        long curBarEnd = period.getNextOn(curBarStart);

//        logger.debug("curTick:" + Utils.formatTimeStamp(tick.getTime()));
//        logger.debug("curBar:" + Utils.formatTimeStamp(curBarStart) + "~" + Utils.formatTimeStamp(curBarEnd));

        //still in this quote bar
        if (curBarStart < cur && cur < curBarEnd) {
            qt.setHigh(Math.max(qt.getHigh(), price));
            qt.setLow(Math.min(qt.getLow(), price));
            qt.setClose(price);
            qt.cntAvgPrice(price);
            qt.setNew(false);
            logger.debug("tick time:" + Utils.formatTimeStamp(cur) +
                    " update current bar:(" + qt + ")" + period.name());
        } else if(cur > curBarEnd){//new quote bar
            Quote nQt = new Quote(period.getStartOn(cur),
                    price, price, price, price, 0);
            nQt.setContract(tick.getContract());
            nQt.setProduct(tick.getProductId());
            nQt.setPeriod(period);
            nQt.cntAvgPrice(price);
            nQt.setNew(true);
            his.put(nQt.getTime(), nQt);
            toStorage(qt);
            logger.debug("create new bar:(" + nQt + ")" + period.name());
        } else {//cur < curBarStart
            //late tick, maybe cause by network
            //currently don't care
            logger.warn("late tick:" + tick);
        }
    }

    @Override
    public void toStorage(Quote qt) {
        storage.save(qt);
        if (his.size() > maxInMem) {
            Quote poll = his.pollFirstEntry().getValue();
//            storage.save(poll);
        }
    }

    @Override
    public void update(Observable o, final Object arg) {
        if (arg instanceof Tick){
            setQuotes((Tick) arg);
        }
    }

    @Override
    public void restoreFromStorage(){
        List<Quote> list = storage.retrieveQuotes(contract, product, period);
        for (Quote qt : list){
            his.put(qt.getTime(),qt);
        }
    }

    @Override
    public void setMaxInMem(int max) {
        this.maxInMem = max;
    }

    @Override
    public synchronized Quote latestQuote() {
        if(his.isEmpty()){
            return null;
        }
        return his.lastEntry().getValue();
    }

    @Override
    public Quote getQuote(long time) {
        if(his.isEmpty())
            return null;

        return his.get(time);
    }

    @Override
    public List<Quote> getQuotes(long fromTime, long toTime) {
        List<Quote> list = Collections.emptyList();
        if(his.isEmpty() || toTime == 0)
            return list;

        try {
            SortedMap<Long, Quote> map = his.subMap(fromTime, false, toTime, false);
            list = new ArrayList<Quote>(map.values());
        }catch (Exception ex){
            logger.warn("can't get quotes : " + this.getContract() + this.getProductName() + this.getTimePeriod()
                    + " from " + Utils.formatTimeStamp(fromTime) + " to " + Utils.formatTimeStamp(toTime));
        }

        return list;
    }

    @Override
    public List<Quote> getQuotesBefore(int nQuote, long toTime) {
        List<Quote> ret = new ArrayList<Quote>();

        if(his.isEmpty() || toTime == 0)
            return ret;

        try {
            SortedMap<Long, Quote> map = his.subMap(0L, false, toTime, false);
            List<Quote> list = new ArrayList<Quote>(map.values());

            //all data
            if (Integer.MAX_VALUE == nQuote || list.size() < nQuote) {
                ret = list;
            } else {
                //partial data
                int idx = list.size();
                for (int i = nQuote; i > 0; i--) {
                    ret.add(list.get(idx - i));
                }
            }
        } catch (Exception ex) {
            logger.warn("Can't get " + nQuote + " quotes before " + Utils.formatTimeStamp(toTime));
        }

        return ret;
    }

    @Override
    public List<Quote> getQuotesAfter(int nQuote, long fromTime) {
        List<Quote> ret = new ArrayList<Quote>();

        if(his.isEmpty() || fromTime == 0)
            return ret;

        try {
            SortedMap<Long, Quote> map = his.tailMap(fromTime);
            List<Quote> list = new ArrayList<Quote>(map.values());

            //all data
            if (Integer.MAX_VALUE == nQuote || list.size() < nQuote) {
                ret = list;
            } else {
                //partial data
                int idx = list.size();
                for (int i = nQuote; i > 0; i--) {
                    ret.add(list.get(idx - i));
                }
            }
        } catch (Exception ex) {
            logger.warn("Can't get " + nQuote + " quotes before " + Utils.formatTimeStamp(fromTime));
        }

        return ret;
    }
}
