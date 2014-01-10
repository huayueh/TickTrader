package com.nv.financial.chart.indicator;

import com.nv.financial.chart.ChartSetting;
import com.nv.financial.chart.concurrent.JobExecutor;
import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.nv.financial.chart.util.Utils;
import com.tictactec.ta.lib.Core;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.*;

/**
 * Keep calculated values in cache.
 * Search cache rather than calculate directly.
 */
public abstract class AbstractIndicator implements IIndicator{
    private static final Logger logger = LogManager.getLogger(AbstractIndicator.class);
    protected ConcurrentNavigableMap<Long, IndicatorValue> cache = new ConcurrentSkipListMap<Long, IndicatorValue>();
    protected final IQuoteProvider quoteProvider;
    protected String longName = "unknow";
    protected String shortName = "unknow";
    protected String description = "unknow";
    protected Core talib = TaLib.getCore();
    private int maxInMem = ChartSetting.getMaxInMem();

    class CalculateTask implements Callable {
        private long time;
        private QuotePrice quotePrice;

        public CalculateTask(long time, QuotePrice quotePrice){
            this.time = time;
            this.quotePrice = quotePrice;
        }

        @Override
        public IndicatorValue call() throws Exception {
            IndicatorValue iv = AbstractIndicator.this.calculate(time, quotePrice);
            return iv;
        }
    }

    public AbstractIndicator(IQuoteProvider quoteProvider){
        this.quoteProvider = quoteProvider;
    }

    public String getDescription() {
        return description;
    }

    public String getLongName() {
        return longName;
    }

    public String getShortName() {
        return shortName;
    }

    @Override
    public List<IndicatorValue> calculate(int nQuote, long toTime, QuotePrice quotePrice) {
        List<IndicatorValue> ret = new ArrayList<IndicatorValue>();
        List<IndicatorValue> memList = getFromCache(nQuote, toTime);
        int lackBars = nQuote - memList.size();
        //partial in cache
        if (lackBars > 0){
            long memf = toTime;
            if (!memList.isEmpty()){
                memf = memList.get(0).getTime();
            }
            List<IndicatorValue> calList = calculateDirectly(lackBars, memf, quotePrice);

            if (!calList.isEmpty()){
                ret.addAll(calList);
            }

            ret.addAll(memList);
        } else {
            //all in cache
            ret = memList;
        }
        return ret;
    }

    public List<IndicatorValue> calculateDirectly(int nQuote, long toTime, QuotePrice quotePrice) {
        List<IndicatorValue> ret = new ArrayList<IndicatorValue>();
        List<Long> times = quoteProvider.getTimesBefore(nQuote, toTime);
        Future<IndicatorValue> fuAry[] = new Future[times.size()];
        int idx = 0;

        //split task
        for (Long time : times){
            Future<IndicatorValue> fuIv = JobExecutor.submit(new CalculateTask(time, quotePrice));
            fuAry[idx] = fuIv;
            idx++;
        }

        //collect result
        for (Future<IndicatorValue> fuIv : fuAry){
            IndicatorValue iv = null;
            try {
                iv = fuIv.get();
                if(iv != null){
                    iv.setContract(quoteProvider.getContract());
                    iv.setProduct(quoteProvider.getProductName());
                    iv.setTime(iv.getTime());
                    iv.setPeriod(quoteProvider.getTimePeriod());
                    ret.add(iv);
                    //put calculated to cache
                    cache.put(iv.getTime(), iv);
                    if (cache.size() > maxInMem) {
                        cache.pollFirstEntry().getValue();
                    }
                }
            } catch (InterruptedException e) {
                logger.error("", e);
            } catch (ExecutionException e) {
                logger.error("", e);
            }

        }
        return ret;
    }

    private List<IndicatorValue> getFromCache(int nQuote, long toTime) {
        List<IndicatorValue> ret = new ArrayList<IndicatorValue>();

        try {
            SortedMap<Long, IndicatorValue> map = cache.subMap(0L, false, toTime, false);
            List<IndicatorValue> list = new ArrayList<IndicatorValue>(map.values());

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
            logger.error("Can't get " + nQuote + " indicator value before " + Utils.formatTimeStamp(toTime));
        }

        return ret;
    }

    @Override
    public IndicatorValue calculateLatest(){
        IndicatorValue iv = calculate(quoteProvider.latestQuoteTime(), QuotePrice.CLOSE);
        if(iv != null){
            iv.setContract(quoteProvider.getContract());
            iv.setProduct(quoteProvider.getProductName());
            iv.setTime(quoteProvider.latestQuoteTime());
            iv.setPeriod(quoteProvider.getTimePeriod());
        }
        return iv;
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(" long name: ").append(longName);
        sb.append(" short name: ").append(shortName);
        sb.append(" description: ").append(description);
		return sb.toString();
	}

    protected List<Quote> getPeriodHiLo(int length, long time, IQuoteProvider quoteProvider) {
        List<Quote> quotes = quoteProvider.getQuotesBefore(length*2, time);
        List<Quote> ret = new ArrayList<Quote>();
        double hiest = Double.MIN_VALUE;
        double loest = Double.MAX_VALUE;
        int idx = 0;

        for(Quote quote : quotes){
            idx++;
            hiest = Math.max(hiest, quote.getHigh());
            loest = Math.min(loest, quote.getLow());
            if(idx > length){
                Quote quoteHiLo = new Quote(quote.getTime());
                quoteHiLo.setHigh(hiest);
                quoteHiLo.setLow(loest);
                ret.add(quoteHiLo);
            }
        }
        return ret;
    }

    public abstract IndicatorValue calculate(long time, QuotePrice qp);
}
