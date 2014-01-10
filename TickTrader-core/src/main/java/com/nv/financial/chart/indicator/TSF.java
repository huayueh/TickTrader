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
 * Time Series Forecasts. Base on
 * https://extra.agea.com/zh/education/types-of-technical-indicators/time-series-forecasts-tsf
 * http://www.familycomputerclub.com/excel/forecast.swf.html
 * http://ctdn.com/algos/indicators/show/39
 */
public class TSF extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(TSF.class);
    private final int forecastPeriod;

    {
        longName = "Time Series Forecasts";
        shortName = "TSF";
        description = "The Time Series Forecast indicator is also known as the Moving Linear Regression, or Regression Oscillator";
    }

    public TSF(IQuoteProvider quoteProvider, int forecastPeriod) {
        super(quoteProvider);
        this.forecastPeriod = forecastPeriod;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue(time);
        List<Quote> qtSer = quoteProvider.getQuotesBefore(forecastPeriod, time);
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
        RetCode retCode = talib.tsf(0, size - 1, in, forecastPeriod, outBegin, outNBElement, out);
        if(retCode == RetCode.Success && outNBElement.value > 0){
            double trix = out[outNBElement.value-1];
            ret.put(shortName, trix);
        } else {
            logger.error("TaLib error:" + retCode);
        }

        return ret;
    }
}
