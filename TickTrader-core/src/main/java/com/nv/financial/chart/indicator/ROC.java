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
 * Rate of Price Change. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:rate_of_change_roc_a
 */
public class ROC extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(ROC.class);
    private final int length;

    {
        longName = "Rate of Price Change";
        shortName = "ROC";
        description = "unknow";
    }

	public ROC(IQuoteProvider quoteProvider,int length) {
        super(quoteProvider);
		this.length = length;
	}

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(length*2, time);
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
        RetCode retCode = talib.roc(0, size - 1, in, length, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double roc = out[outNBElement.value-1];
            ret.put(shortName, roc);

        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }
}
