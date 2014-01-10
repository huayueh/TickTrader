package com.nv.financial.chart.quote.provider;

import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.TimePeriod;
import com.nv.financial.chart.storage.CassandraStorage;
import com.nv.financial.chart.storage.IStorage;
import com.nv.financial.chart.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;

/**
 * User: Harvey
 * Date: 2013/10/31
 * Time: 下午 2:53
 */
public class CassandraQuoteProvider extends AbstractQuoteProvider {
    private static final Logger logger = LogManager.getLogger(CassandraQuoteProvider.class);
    protected IStorage storage;

    CassandraQuoteProvider(String provider, String product, TimePeriod period) {
        super(provider, product, period);
        storage = CassandraStorage.getInstance();
    }

    @Override
    public long getNextTime(long time) {
        List<Quote> qtList = storage.retrieveQuotes(2, time, this.contract, this.product, this.period);
        if (qtList.size() < 2)
            return Long.MAX_VALUE;

        if (qtList.get(0).getTime() == time){
            return qtList.get(1).getTime();
        } else {
            return qtList.get(0).getTime();
        }
    }

    @Override
    public Quote getQuote(long time) {
        return storage.retrieveQuote(time, this.contract, this.product, this.period);
    }

    @Override
    public Quote latestQuote() {
        Quote ret = null;
        long toTime = System.currentTimeMillis();
        List<Quote> qtListy = storage.retrieveQuotes(1, toTime, this.contract, this.product, this.period);
        if (!qtListy.isEmpty())
            ret = qtListy.get(0);
        return ret;
    }

    @Override
    public List<Quote> getQuotes(long fromTime, long toTime) {
        return storage.retrieveQuotes(fromTime, toTime, this.contract, this.product, this.period);
    }

    @Override
    public List<Quote> getQuotesBefore(int nQuote, long toTime) {
        List<Quote> ret = storage.retrieveQuotes(nQuote, toTime, this.contract, this.product, this.period);
        if (ret.size() != nQuote)
            logger.warn("Can't get " + nQuote + " " +this.period.name() + " quotes before " + Utils.formatTimeStamp(toTime));
        return ret;
    }

    @Override
    public List<Quote> getQuotesAfter(int nQuote, long fromTime) {
        return Collections.emptyList();
    }
}
