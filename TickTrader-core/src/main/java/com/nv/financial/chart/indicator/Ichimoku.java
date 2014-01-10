package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ichimoku. Base on
 * http://www.ifcmarkets.com/zh_TW/ntx-indicators/ichimoku
 */
public class Ichimoku extends AbstractIndicator {
    public final static int Tenkan_Sen = 0;
    public final static int Kijun_Sen = 1;
    public final static int Senkou_Span_A = 2;
    public final static int Senkou_Span_B = 3;
    public final static int Chikou_Span = 4;
    private static final Logger logger = LogManager.getLogger(Ichimoku.class);
    private final int length1;
    private final int length2;
    private final int length3;

    {
        longName = "Ichimoku";
        shortName = "Ichimoku";
        description = "unknow";
    }

    public Ichimoku(IQuoteProvider quoteProvider,int length1, int length2, int length3){
        super(quoteProvider);
        this.length1 = length1;
        this.length2 = length2;
        this.length3 = length3;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        List<Quote> quotes = quoteProvider.getQuotesBefore(length3, time);
        int idx = 0;
        double len1High = Double.MIN_VALUE;
        double len1Low = Double.MAX_VALUE;
        double len2High = Double.MIN_VALUE;
        double len2Low = Double.MAX_VALUE;
        double len3High = Double.MIN_VALUE;
        double len3Low = Double.MAX_VALUE;
        double chikou_Span = 0;

        for (int i=quotes.size()-1; i > 0; i--){
            Quote quote = quotes.get(i);
            if(idx < length1){
                len1High = Math.max(quote.getHigh(),len1High);
                len1Low = Math.min(quote.getLow(),len1Low);
            }
            if(idx < length2){
                len2High = Math.max(quote.getHigh(),len2High);
                len2Low = Math.min(quote.getLow(),len2Low);
            }
            if(idx < length3){
                len3High = Math.max(quote.getHigh(),len3High);
                len3Low = Math.min(quote.getLow(),len3Low);
            }
            if(idx-1 == length2){
                chikou_Span = quote.getClose();
            }
            idx++;
        }

        double tenkan_Sen = (len1High + len1Low)/2;
        double kijun_Sen = (len2High + len2Low)/2;
        double senkou_Span_A = (tenkan_Sen + kijun_Sen)/2;
        double senkou_Span_B = (len3High + len3Low)/2;

        IndicatorValue ret = new IndicatorValue(time);
        ret.put("Tenkan_Sen",tenkan_Sen);
        ret.put("Kijun_Sen",kijun_Sen);
        ret.put("Senkou_Span_A",senkou_Span_A);
        ret.put("Senkou_Span_B",senkou_Span_B);
        ret.put("Chikou_Span",chikou_Span);

        return ret;
    }
}
