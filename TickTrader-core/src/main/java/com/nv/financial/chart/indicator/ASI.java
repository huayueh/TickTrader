package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Accumulation Swing Index. Base on
 * http://ta.mql4.com/indicators/trends/accumulation_swing
 * http://www.alltrading.info/technical-analysis/44/114-swing-index
 */
public class ASI extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(ASI.class);
    private final int L; //The limit move value.

    {
        longName = "Accumulation Swing Index";
        shortName = "ASI";
        description = "unknow";
    }

    public ASI(IQuoteProvider quoteProvider,int L){
        super(quoteProvider);
        this.L = L;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        List<Quote> quotes = quoteProvider.getQuotesBefore(Integer.MAX_VALUE,time);
        List<Double> serSI = new ArrayList<Double>();
        IndicatorValue ret = new IndicatorValue(time);

        /**
         * K = MAX of (H ??C n-1) and (L  ??C n-1)
         *
         * MAX (H ??C n-1) (L ??C n-1) (H ??C)
         * If the highest difference is between (H ??C n-1), then:
         * R = (H ??C n-1) + 0.5*(L ??C n-1) + 0.25*(C n-1 + O n-1)
         * If the highest difference is between (L ??C n-1), then:
         * R = (L ??C n-1) + 0.5*(H ??C n-1) + 0.25*(C n-1 + O n-1)
         * If the highest difference is between (H ??C), then:
         * R = (H ??L) + 0.25*(C n-1 + O n-1)
        */

        for(int idx=0; idx < quotes.size(); idx++){
            Quote quote = quotes.get(idx);
            Quote quotePre = quotes.get(idx-1);
            if(idx == 0){
                serSI.add(0.0);
                continue;
            }
            double K = Math.max(quote.getHigh() - quotePre.getClose(),
                    quote.getLow() - quotePre.getClose());
            double R = 0;
            double highestDif = NumberUtils.max(quote.getHigh() - quotePre.getClose(),
                    quote.getLow() - quotePre.getClose(),
                    quote.getHigh() - quote.getClose() );
            if(quote.getHigh() < highestDif && highestDif < quotePre.getClose()){
                R = quote.getHigh()-quotePre.getClose() + 0.5*(quote.getLow()-quotePre.getClose()) + 0.25*(quotePre.getClose()-quotePre.getOpen());
            }
            if(quote.getLow() < highestDif && highestDif < quotePre.getClose()){
                R = quote.getLow()-quotePre.getClose() + 0.5*(quote.getHigh()-quotePre.getClose()) + 0.25*(quotePre.getClose()+quotePre.getOpen());
            }
            if(quote.getHigh() < highestDif && highestDif < quote.getClose()){
                R = quote.getHigh()-quote.getClose() + 0.25*(quotePre.getClose()+quotePre.getOpen());
            }

            /**
             * Swing Index
             * SI(i) = {[ ((CLOSE(i-1) - CLOSE(i)) +
             * (0.5*((CLOSE(i-1) - OPEN(i-1))) +
             * (0.25*(CLOSE(i) - OPEN(i)))] / R}*(K / L)*50
            */
            double si =
            ( (quotePre.getClose() - quote.getClose()) +
                    0.5*(quotePre.getClose()-quotePre.getOpen()) +
                    0.25*(quote.getClose()-quote.getOpen())
            ) / R * K / L *50;
            serSI.add(si);
        }
        //accumulate
        double asi = 0;
        for (double si : serSI){
            asi += si;
        }
        ret.put(shortName,asi);
        return ret;
    }
}
