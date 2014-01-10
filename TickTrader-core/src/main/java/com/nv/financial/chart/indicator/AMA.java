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
 * Adaptive Moving Average. Base on
 * http://etfhq.com/blog/2011/11/07/adaptive-moving-average-ama-aka-kaufman-adaptive-moving-average-kama/
 * http://etfhq.com/blog/2011/02/07/kaufmans-efficiency-ratio/
 */
public class AMA extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(AMA.class);
    //The weight omitted by stopping after k
    private final int k;
    private final int length;
    private final double sc;
    private final double fc;

    {
        longName = "Adaptive Moving Average";
        shortName = "AMA";
        description = "unknow";
    }

    public AMA(IQuoteProvider quoteProvider, int length, int sn, int fn){
        super(quoteProvider);
        this.length = length;
        this.k = (int)Math.ceil(3.45 * (length + 1));
        this.sc = 2.0 / (sn + 1);
        this.fc = 2.0 / (fn + 1);
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        List<Quote> quotes = quoteProvider.getQuotesBefore(k,time);
        IndicatorValue ret = calculate(quotes, quotePrice);
        ret.setTime(time);
        return ret;
    }

    private IndicatorValue getVI(long time, QuotePrice quotePrice){
        //VI=ER(3)
        List<Quote> quotesER = quoteProvider.getQuotesBefore(3+1,time);
        Quote now = quotesER.get(quotesER.size()-1);
        IndicatorValue ret = new IndicatorValue(time);
        double sum = 0;

        for (int idx=1; idx < quotesER.size(); idx++){
            Quote quote = quotesER.get(idx);
            Quote quotePre = quotesER.get(idx-1);
            sum += Math.abs(quotePrice.getPrice(quote) - quotePrice.getPrice(quotePre));
        }
        double abs = Math.abs(quotePrice.getPrice(now) - quotePrice.getPrice(quotesER.get(0)));
        ret.put("ER",abs/sum);

        return ret;
    }

    /**
     * a = [(VI * (FC ??SC)) + SC]²
     * ama(t) = ama(t-1) + a * ( price - ama(t-1) )
     */
    public IndicatorValue calculate(List<Quote> quotes, QuotePrice quotePrice){
        List<IndicatorValue> serAma = new ArrayList<IndicatorValue>();
        int idx = 0;
        double ama;
        double amat;

        for (Quote quote : quotes) {
            IndicatorValue iv = new IndicatorValue();
            if(idx == 0){
                iv.put(shortName, quotePrice.getPrice(quote));
                serAma.add(iv);
                idx++;
                continue;
            }
            double vi = getVI(quote.getTime(),quotePrice).getValue(0);
            double a = Math.pow((vi*(fc-sc))+sc,2);
            amat = serAma.get(idx-1).getValue(0);
            ama = amat + a * (quotePrice.getPrice(quote) - amat);
            iv.put(shortName,ama);
            serAma.add(iv);
            idx++;
        }
        return serAma.get(serAma.size()-1);
    }
}
