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

import java.util.List;

/**
 * Price Oscillator. Base on
 * http://www.taindicators.com/2010/03/oscp-price-oscillator.html
 */
public class OSC extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(OSC.class);
    private final int shortLength;
    private final int longLength;

    {
        longName = "Price Oscillator";
        shortName = "OSC";
        description = "unknow";
    }

    public OSC(IQuoteProvider quoteProvider,int shortLength, int longLength){
        super(quoteProvider);
        this.shortLength = shortLength;
        this.longLength = longLength;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(longLength, time);
        int size = qtSer.size();
        double[] in = new double[size];
        double out[] = new double[size];
        MInteger outBegin = new MInteger();
        MInteger outNBElement = new MInteger();
        int idx = 0;
        for (Quote qt : qtSer){
            in[idx] = quotePrice.getPrice(qt);
            idx++;
        }
        RetCode retCode = talib.ppo(0, size - 1, in, shortLength, longLength, MAType.Sma, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double ppo = out[outNBElement.value-1];
            ret.put(shortName, ppo);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }
}
