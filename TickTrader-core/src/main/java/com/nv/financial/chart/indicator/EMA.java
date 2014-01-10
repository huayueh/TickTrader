package com.nv.financial.chart.indicator;

import java.util.ArrayList;
import java.util.List;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import com.nv.financial.chart.util.Utils;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Exponential Moving Average. Base on
 * http://en.wikipedia.org/wiki/Moving_average#Exponential_moving_average
 */
public class EMA extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(EMA.class);
    //The weight omitted by stopping after k
    private final int k;
    //alpha value
    private final double a;
	private final int length;

    {
        longName = "Exponential Moving Average";
        shortName = "EMA";
        description = "unknow";
    }

    public EMA(IQuoteProvider quoteProvider,int length){
        super(quoteProvider);
        this.length = length;
//        this.k = (int)Math.ceil(3.45 * (length + 1));
        this.k = Integer.MAX_VALUE;
        this.a = 2.0 / (length + 1);
    }

    @Override
	public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(k, time);
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
        RetCode retCode = talib.ema(0, size - 1, in, length, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double atr = out[outNBElement.value-1];
            ret.put(shortName, atr);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
	}

    /**
     * a = 2 / N + 1
     * ema(t) = ema(t-1) + a * ( price - ema(t-1) )
     */
    //TODO:remove
    public IndicatorValue calculate(List<Quote> quotes, QuotePrice quotePrice){
        List<IndicatorValue> serEma = new ArrayList<IndicatorValue>();
        int idx = 0;
        double ema;
        double emat;

        for (Quote quote : quotes) {
            IndicatorValue iv = new IndicatorValue();
            if(idx == 0){
                iv.put(shortName, quotePrice.getPrice(quote));
                serEma.add(iv);
                idx++;
                continue;
            }
            emat = serEma.get(idx-1).getValue(0);
            ema = emat + a * (quotePrice.getPrice(quote) - emat);
            iv.put(shortName,ema);
            iv.setIdcId(shortName + Utils.INDICATOR_DELIMITER + length);
            serEma.add(iv);
            idx++;
        }
        if(serEma.isEmpty()){
            return new IndicatorValue();
        }
        return serEma.get(serEma.size()-1);
    }
}
