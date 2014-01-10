package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Relative Strength Index. Base on
 * http://en.wikipedia.org/wiki/Relative_strength
 */
public class RSI extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(RSI.class);
	private final int periodLength;//The weight omitted by stopping after k
    private final int k;

    {
        longName = "Relative Strength Index";
        shortName = "RSI";
        description = "unknow";
    }

	public RSI(IQuoteProvider quoteProvider,int periodLength) {
        super(quoteProvider);
		this.periodLength = periodLength;
//        this.k = (int)Math.ceil(3.45 * (periodLength + 1));
        this.k = Integer.MAX_VALUE;
	}

	@Override
	public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(k, time);
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
        RetCode retCode = talib.rsi(0, size - 1, in, periodLength, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double roc = out[outNBElement.value-1];
            ret.put(shortName, roc);

        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
	}
}
