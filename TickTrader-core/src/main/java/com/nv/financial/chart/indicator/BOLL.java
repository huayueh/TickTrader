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
 * Bollinger Bands. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:bollinger_bands
 * http://en.wikipedia.org/wiki/Bollinger_Bands
 * http://zh.wikipedia.org/wiki/%E6%A8%99%E6%BA%96%E5%B7%AE
 */
public class BOLL extends AbstractIndicator {
    public final static int BB_DN = 2;
    public final static int BB_MB = 0;
    public final static int BB_UP = 1;

    private static final Logger logger = LogManager.getLogger(IndicatorValue.class);
	private final int length;
	private final double deviations;

	public BOLL(IQuoteProvider quoteProvider,int length, double deviations) {
        super(quoteProvider);
		this.length = length;
		this.deviations = deviations;
	}

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(length, time);
        int size = qtSer.size();
        double[] in = new double[size];
        double[] outUp = new double[size];
        double[] outMid = new double[size];
        double[] outLow = new double[size];
        MInteger outBegin = new MInteger();
        MInteger outNBElement = new MInteger();
        int idx = 0;
        for (Quote qt : qtSer){
            in[idx] = quotePrice.getPrice(qt);
            idx++;
        }
        RetCode retCode = talib.bbands(0, size - 1, in, length, deviations, deviations, MAType.Sma, outBegin, outNBElement, outUp, outMid, outLow);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            ret.put("BB_MB",outMid[outNBElement.value-1]);
            ret.put("BB_UP",outUp[outNBElement.value-1]);
            ret.put("BB_DN",outLow[outNBElement.value-1]);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }
}
