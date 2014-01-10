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
 * Simple Moving Average. Base on
 * http://en.wikipedia.org/wiki/Moving_average#Simple_moving_average
 * http://www.prosticks.com.hk/tech_indicators.asp?page=13
 */
public class SMA extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(IndicatorValue.class);
	private final int length;

    {
        longName = "Simple Moving Average";
        shortName = "SMA";
        description = "unknow";
    }

	public SMA(IQuoteProvider quoteProvider,int length) {
        super(quoteProvider);
		this.length = length;
	}

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(length, time);
        int size = qtSer.size();
        double[] in = new double[size];
        double[] out = new double[size];
        MInteger outBegin = new MInteger();
        MInteger outNBElement = new MInteger();
        int idx = 0;
        for (Quote qt : qtSer){
            in[idx] = quotePrice.getPrice(qt);
            idx++;
        }
        RetCode retCode = talib.sma(0, size - 1, in, length, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double atr = out[outNBElement.value-1];
            ret.put(shortName, atr);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }

    /**
     * sma(t) = sma(t-1) + sma(t-2) + sma(t-3)....+ sma(t-n+1) / n
     */
    //TODO:remove
    public IndicatorValue calculate(List<Quote> quotes, QuotePrice quotePrice){
        IndicatorValue ret = new IndicatorValue();
        double sma;
        double sum = 0.0;

        if(quotes.size() != length){
            logger.error("Size of List is not equal to length");
        }
        for (Quote quote : quotes) {
            sum += quotePrice.getPrice(quote);
        }
        sma = sum/length;
        ret.put(shortName,sma);

        return ret;
    }
}
