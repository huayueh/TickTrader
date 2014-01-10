package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.nv.financial.chart.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;

/**
 * Detrended Price Oscillator. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:detrended_price_osci
 * http://www.moneydj.com/kmdj/wiki/wikiviewer.aspx?keyid=358679c7-b45c-48d5-8ef5-07c8e0fef8f8
 */
public class DPO extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(DPO.class);
    private final int length;

    {
        longName = "Detrended Price Oscillator";
        shortName = "DPO";
        description = "unknow";
    }

    public DPO(IQuoteProvider quoteProvider,int length){
        super(quoteProvider);
        this.length = length;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        Quote qt = quoteProvider.getQuote(time);
        int displace = length/2+1;
        DMA dma = new DMA(quoteProvider,length,displace*1);
        IndicatorValue dmaIv = dma.calculate(time, quotePrice);
        IndicatorValue ret = new IndicatorValue(time);
        if(dmaIv.getValue(0) == 0)
            ret.put(shortName, 0);
        else
            ret.put(shortName, quotePrice.getPrice(qt) - dmaIv.getValue(0));

        return ret;
    }
}
