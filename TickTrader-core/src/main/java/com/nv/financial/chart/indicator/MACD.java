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
 * Moving Average Convergence/Divergence. Base on
 * http://zh.wikipedia.org/wiki/MACD
 * http://www.prosticks.com.hk/tech_indicators.asp?page=10
 */
public class MACD extends AbstractIndicator {
    public final static int MACD = 0;
    public final static int SIGNAL = 1;
    public final static int Hist = 2;
    private static final Logger logger = LogManager.getLogger(MACD.class);
    //The weight omitted by stopping after k
    private final int k;
	private final int fastLength;
    private final int slowLength;
    private final int signalLine;

    {
        longName = "Moving Average Convergence/Divergence";
        shortName = "MACD";
        description = "unknow";
    }

	public MACD(IQuoteProvider quoteProvider,int fastLength, int slowLength, int signalLine) {
        super(quoteProvider);
		this.fastLength = fastLength;
		this.slowLength = slowLength;
        this.signalLine = signalLine;
//        this.k = (int)Math.ceil(3.45 * (signalLine + 1));
        this.k = Integer.MAX_VALUE;
	}

	@Override
	public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(k, time);
        int size = qtSer.size();
        double[] in = new double[size];
        double outMACD[] = new double[size];
        double outMACDSignal[] = new double[size];
        double outMACDHist[] = new double[size];
        MInteger outBegin = new MInteger();
        MInteger outNBElement = new MInteger();
        int idx = 0;
        for (Quote qt : qtSer){
            in[idx] = quotePrice.getPrice(qt);
            idx++;
        }
        RetCode retCode = talib.macd(0, size - 1, in, fastLength, slowLength, signalLine, outBegin, outNBElement, outMACD, outMACDSignal, outMACDHist);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double macd = outMACD[outNBElement.value-1];
            double macdSig = outMACDSignal[outNBElement.value-1];
            double macdHi = outMACDHist[outNBElement.value-1];
            ret.put("MACD", macd);
            ret.put("MACD Signal", macdSig);
            ret.put("MACD Hist", macdHi);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
	}
}
