package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Stochastic Momentum Index. Base on
 * http://support.motivewave.com/studies/stochastic-momentum-index/
 * http://www.blastchart.com/Community/IndicatorGuide/Indicators/StochasticMomentumIndex.aspx
 *
 * getPeriodHiLo()
 * HH = highest(index, hlPeriod, HIGH);
 * LL = lowest(index, hlPeriod, LOW);
 *
 * calculateMAValues()
 * M = (HH + LL)/2;
 * D = getClose(index) - M;
 * HL = HH - LL;
 * D_MA = ma(method, index, maPeriod, D);
 * HL_MA = ma(method, index, maPeriod, HL);
 *
 * calculateSMI()
 * D_SMOOTH = ma(method, index, smoothPeriod, D_MA);
 * HL_SMOOTH = ma(method, index, smoothPeriod, HL_MA);
 * HL2 = HL_SMOOTH/2;
 * SMI = 0;
 * SMI = 100 * (D_SMOOTH/HL2);
 *
 * calculate()
 * SIGNAL = ma(method, index, signalPeriod, SMI);
 *
 * Need to calculate from button up.
 * call trace:
 * calculate -> calculateSMI -> calculateMAValues -> getPeriodHiLo
 */
public class SMI extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(SMI.class);
    private final int maPeriod;
    private final int smoothPeriod;
    private final int hiloPeriod;
    private final int signalPeriod;
    //The weight omitted by stopping after k
    private final int k;

    {
        longName = "Stochastic Momentum Index";
        shortName = "SMI";
        description = "unknow";
    }

    public SMI(IQuoteProvider quoteProvider,int hiloPeriod, int maPeriod, int smoothPeriod, int signalPeriod){
        super(quoteProvider);
        this.hiloPeriod = hiloPeriod;
        this.maPeriod = maPeriod;
        this.smoothPeriod = smoothPeriod;
        this.signalPeriod = signalPeriod;
        this.k = (int)Math.ceil(3.45 * (this.hiloPeriod + 1));
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        List<Long> times = quoteProvider.getTimesBefore(signalPeriod,time);
        List<Quote> serSMI = new ArrayList<Quote>();
        double smi = 0;
        for (long now : times){
            smi = calculateSMI(time);
            Quote quote = new Quote(now);
            quote.setAmplitude(smi);
            serSMI.add(quote);
        }
        double signal = new EMA(quoteProvider,signalPeriod).calculate(serSMI, QuotePrice.CUST_PRICE).getValue(0);
        IndicatorValue ret = new IndicatorValue(time);
        ret.put("SMI", smi);
        ret.put("SIGNAL", signal);

        return ret;
    }

    private double calculateSMI(long time) {
        List<Long> times = quoteProvider.getTimesBefore(smoothPeriod,time);
        List<Quote> serDsmooth = new ArrayList<Quote>();
        List<Quote> serHLsmooth = new ArrayList<Quote>();

        for (long now : times){
            double dMA = calculateMAValues(now).get(0);
            double hlMA = calculateMAValues(now).get(1);
            Quote quoteD = new Quote(now);
            quoteD.setAmplitude(dMA);
            serDsmooth.add(quoteD);
            Quote quoteHL = new Quote(now);
            quoteHL.setAmplitude(hlMA);
            serHLsmooth.add(quoteHL);
        }
        double dSmooth = new EMA(quoteProvider,smoothPeriod).calculate(serDsmooth, QuotePrice.CUST_PRICE).getValue(0);
        double hlSmooth = new EMA(quoteProvider,smoothPeriod).calculate(serHLsmooth, QuotePrice.CUST_PRICE).getValue(0);
        double hl2 = hlSmooth/2;
        double smi = 100* dSmooth/hl2;
        return smi;
    }

    private List<Double> calculateMAValues(long time) {
        List<Quote> quotes = quoteProvider.getQuotesBefore(k, time);
        List<Quote> periodHiLo = getPeriodHiLo(k, time, quoteProvider);
        List<Quote> serD = new ArrayList<Quote>();
        List<Quote> serHL = new ArrayList<Quote>();
        List<Double> ret = new ArrayList<Double>();

        //make seril for ema caculation
        for(int idx=0; idx < quotes.size(); idx++){
            Quote now = quotes.get(quotes.size()-1);
            double hiest = periodHiLo.get(idx).getHigh();
            double loest = periodHiLo.get(idx).getLow();
            double m = (hiest+loest)/2;
            double d = now.getClose() - m;
            double hl = hiest - loest;
            Quote quoteD = new Quote(now.getTime());
            quoteD.setAmplitude(d);
            serD.add(quoteD);
            Quote quoteHL = new Quote(now.getTime());
            quoteHL.setAmplitude(hl);
            serHL.add(quoteHL);
        }
        IndicatorValue dMa = new EMA(quoteProvider,maPeriod).calculate(serD, QuotePrice.CUST_PRICE);
        IndicatorValue hlMa = new EMA(quoteProvider,maPeriod).calculate(serHL, QuotePrice.CUST_PRICE);

        ret.add(dMa.getValue(0));
        ret.add(hlMa.getValue(0));

        return ret;
    }


}
