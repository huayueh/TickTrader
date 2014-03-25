package com.nv.financial.chart.quote.provider;

import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.TimePeriod;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class AbstractQuoteProvider implements IQuoteProvider{
    protected String contract;
    protected String product;
    protected TimePeriod period;

    public AbstractQuoteProvider(String contract, String product, TimePeriod period) {
        this.contract = contract;
        this.product = product;
        this.period = period;
    }

    @Override
    public List<Long> getTimesBefore(int nTime, long toTime) {
        List<Long> ret = new ArrayList<Long>();

        List<Quote> qtList = getQuotesBefore(nTime, toTime);
        for (Quote qt : qtList){
            ret.add(qt.getTime());
        }

        return ret;
    }

    @Override
    public List<Long> getTimesAfter(int nTime, long fromTime) {
        List<Long> ret = new ArrayList<Long>();

        List<Quote> qtList = getQuotesAfter(nTime, fromTime);
        for (Quote qt : qtList){
            ret.add(qt.getTime());
        }

        return ret;
    }

    @Override
    public long latestQuoteTime(){
        Quote quote = latestQuote();
        if(quote == null)
            return Long.MAX_VALUE;
        return quote.getTime();
    }

    @Override
    public TimePeriod getTimePeriod() {
        return period;
    }

    @Override
    public String getProductName() {
        return this.product;
    }

    @Override
    public String getContract() {
        return this.contract;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(" contract: ").append(contract).append(",");
        sb.append(" product: ").append(product).append(",");
        sb.append(" period: ").append(period).append(",");

        return sb.toString();
    }
}
