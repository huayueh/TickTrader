package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Stochastic RSI. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:stochrsi
 * http://www.indicatorsmt4.com/indicators-wiki/momentum-indicators/stochastic-rsi/?lang=zh-tw
 */
public class StochRSI extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(StochRSI.class);
    private final int length;

    {
        longName = "Stochastic Relative Strength Index";
        shortName = "StochRSI";
        description = "unknow";
    }

    public StochRSI(IQuoteProvider quoteProvider,int length) {
        super(quoteProvider);
        this.length = length;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue();
        List<Double> serRSI = new ArrayList<Double>();
        List<Long> times = quoteProvider.getTimesBefore(length, time);
        double hiRsi = Double.MIN_VALUE;
        double loRsi = Double.MAX_VALUE;

        for(long now : times){
            IndicatorValue rsi = new RSI(quoteProvider,length).calculate(now, quotePrice);
            serRSI.add(rsi.getValue(0));
        }
        double nowRsi = serRSI.get(serRSI.size()-1);

        for(double rsi : serRSI){
            hiRsi = Math.max(hiRsi, rsi);
            loRsi = Math.min(loRsi, rsi);
        }
        double stochRSI = (nowRsi-loRsi)/(hiRsi-loRsi);
        ret.put(shortName,stochRSI);

        return ret;
    }
}
