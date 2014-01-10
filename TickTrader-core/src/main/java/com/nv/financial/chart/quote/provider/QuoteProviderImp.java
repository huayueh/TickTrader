package com.nv.financial.chart.quote.provider;

import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.TimePeriod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Harvey
 * Date: 2013/11/4
 * Time: 上午 11:19
 *
 * Access through QuoteProviderFactory.
 * Look up from memory and than cassandra storage.
 */
public class QuoteProviderImp extends AbstractQuoteProvider {
    private static final Logger logger = LogManager.getLogger(QuoteProviderImp.class);
    private IQuoteProvider mem;
    private IQuoteProvider storage;

    QuoteProviderImp(String provider, String product, TimePeriod period) {
        super(provider, product, period);
    }

    public void setMemProvider(IQuoteProvider mem){
        this.mem = mem;
    }

    public void setStorageProvider(IQuoteProvider storage){
        this.storage = storage;
    }

    @Override
    public long getNextTime(long time) {
        long nxtime = mem.getNextTime(time);
        if (nxtime == Long.MAX_VALUE){
            nxtime = storage.getNextTime(time);
        }
        return nxtime;
    }

    @Override
    public Quote getQuote(long time) {
        Quote qt = mem.getQuote(time);
        if (qt == null)
            qt = storage.getQuote(time);
        return qt;
    }

    @Override
    public Quote latestQuote() {
        return mem.latestQuote();
    }

    /**
     * possible data range. best case all in memory
     *        +---------------------+------------------------+
     *        |         db          |         memory         |
     *        +---------------------+------------------------+
     * case 1    <- from to ->
     * case 2                <-  from to  ->
     * case 3                              <-  from to  ->
     * */
    @Override
    public List<Quote> getQuotes(long fromTime, long toTime) {
        List<Quote> ret = new ArrayList<Quote>();
        List<Quote> memList = mem.getQuotes(fromTime, toTime);
        //partial in memory
        if (!memList.isEmpty()){
            long memf = memList.get(0).getTime();
            List<Quote> storFromList = Collections.emptyList();

            if (memf > fromTime){
                //case 2: partial in storage
                storFromList = storage.getQuotes(fromTime, memf);
            }

            if (!storFromList.isEmpty()){
                ret.addAll(storFromList);
            }

            ret.addAll(memList);
        }else {
        //case 1: all in storage
            ret = storage.getQuotes(fromTime, toTime);
        }
        return ret;
    }

    /**
     * possible data range. best case all in memory
     *        +---------------------+------------------------+
     *        |         db          |         memory         |
     *        +---------------------+------------------------+
     * case 1 <- nQuote ->
     * case 2          <- lackBars -><- nQuote-lackBars ->
     * case 3                                   <-  nQuote  ->
     * */
    @Override
    public List<Quote> getQuotesBefore(int nQuote, long toTime) {
        List<Quote> ret = new ArrayList<Quote>();
        List<Quote> memList = mem.getQuotesBefore(nQuote, toTime);
        //partial in storage
        int lackBars = nQuote - memList.size();
        if (lackBars > 0){
            long memf = toTime;
            if (!memList.isEmpty()){
                memf = memList.get(0).getTime();
            }
            List<Quote> storFromList = storage.getQuotesBefore(lackBars, memf);

            if (!storFromList.isEmpty()){
                ret.addAll(storFromList);
            }

            ret.addAll(memList);
        } else {
            //case 3: all in memory
            ret = memList;
        }

        return ret;
    }

    @Override
    public List<Quote> getQuotesAfter(int nQuote, long fromTime) {
        return mem.getQuotesAfter(nQuote, fromTime);
    }
}
