package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Displaced Moving Average. Base on
 * http://tradingsim.com/blog/displaced-moving-average/
 */
public class DMA extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(DMA.class);
    private final int displacement;
    private final int length;

    {
        longName = "Displaced Moving Average";
        shortName = "DMA";
        description = "unknow";
    }

    public DMA(IQuoteProvider quoteProvider,int length, int displacement){
        super(quoteProvider);
        this.length = length;
        this.displacement = displacement;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        //SMA of n days ago is DMA
        IndicatorValue ret = new IndicatorValue(time);
        List<Long> times;
        SMA sma;
        IndicatorValue smaIv;

        if(displacement > 0){
            times = quoteProvider.getTimesBefore(displacement, time);
            sma = new SMA(quoteProvider,length);
            smaIv = sma.calculate(times.get(0),quotePrice);
            ret.put(shortName, smaIv.getValue(0));
        } else {
            times = quoteProvider.getTimesAfter(displacement*-1, time);
            sma = new SMA(quoteProvider,length);
            smaIv = sma.calculate(times.get(0),quotePrice);
            ret.put(shortName, smaIv.getValue(0));
        }

        return ret;
    }
}
