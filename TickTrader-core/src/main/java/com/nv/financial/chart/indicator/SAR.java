package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Stop And Reverse. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:parabolic_sar
 */
public class SAR extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(SAR.class);
    private final double increment;
    private final double max;
    private boolean trendUp;

    {
        longName = "Stop And Reverse";
        shortName = "SAR";
        description = "unknow";
    }

    public SAR(IQuoteProvider quoteProvider,double increment, double max){
        super(quoteProvider);
        this.max = max;
        this.increment = increment;
        this.trendUp = true;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(Integer.MAX_VALUE, time);
        int size = qtSer.size();
        double[] high = new double[size];
        double[] low = new double[size];
        double out[] = new double[size];
        MInteger outBegin = new MInteger();
        MInteger outNBElement = new MInteger();
        int idx = 0;
        for (Quote qt : qtSer){
            high[idx] = qt.getHigh();
            low[idx] = qt.getLow();
            idx++;
        }
        RetCode retCode = talib.sar(0, size - 1, high, low, increment, max, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double roc = out[outNBElement.value-1];
            ret.put(shortName, roc);

        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }
}
