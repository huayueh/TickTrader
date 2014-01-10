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
 * Aroon. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:aroon
 * http://www.chineseworldnet.com/na/stock/investToolGroup/stock_computer_choose/13
 */
public class Aroon extends AbstractIndicator {
    public final static int UP = 0;
    public final static int DOWN = 1;
    private static final Logger logger = LogManager.getLogger(Aroon.class);
    private final int length;

    {
        longName = "Aroon";
        shortName = "Aroon";
        description = "unknow";
    }

    public Aroon(IQuoteProvider quoteProvider,int length){
        super(quoteProvider);
        this.length = length;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(length*2, time);
        int size = qtSer.size();
        double[] high = new double[size];
        double[] low = new double[size];
        double[] outUp = new double[size];
        double[] outDown = new double[size];
        MInteger outBegin = new MInteger();
        MInteger outNBElement = new MInteger();
        int idx = 0;
        for (Quote qt : qtSer){
            high[idx] = qt.getHigh();
            low[idx] = qt.getLow();
            idx++;
        }
        RetCode retCode = talib.aroon(0, size - 1, high, low, length, outBegin, outNBElement, outDown, outUp);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double aroonUp = outUp[outNBElement.value-1];
            double aroonDn = outDown[outNBElement.value-1];
            ret.put("AroonUp",aroonUp);
            ret.put("AroonDown",aroonDn);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }
}
