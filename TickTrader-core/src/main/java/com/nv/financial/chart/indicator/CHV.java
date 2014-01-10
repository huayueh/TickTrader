package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Chaikin Volatility. Base on
 * http://www.barchart.com/education/std_studies.php?what=std_chaikvol
 */
public class CHV extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(CHV.class);
    //The weight omitted by stopping after k
    private final int k;
    private final int lookBackLength;

    {
        longName = "Chaikin Volatility";
        shortName = "CHV";
        description = "unknow";
    }

    public CHV(IQuoteProvider quoteProvider,int lookBackLength) {
        super(quoteProvider);
        this.lookBackLength = lookBackLength;
        this.k = (int)Math.ceil(3.45 * (lookBackLength + 1));
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> serEma= new ArrayList<Quote>();
        List<Long> timeSer = quoteProvider.getTimesBefore(k, time);
        double dif = 0;
        double dem = 0;

        for(Long serTime : timeSer){
            IndicatorValue ema = new EMA(quoteProvider,lookBackLength).calculate(serTime, QuotePrice.HL_DIF);
            dif = ema.getValue(0);
            Quote difQuote = new Quote(0);
            difQuote.setAmplitude(dif);
            serEma.add(difQuote);
        }
        double lookBackEma = serEma.get(0).getAmplitude();
        double chv = (serEma.get(serEma.size()).getAmplitude() - lookBackEma) / lookBackEma;
        ret.put(shortName,chv);

        return ret;
    }
}
