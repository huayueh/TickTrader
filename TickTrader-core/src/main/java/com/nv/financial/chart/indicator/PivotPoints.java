package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Pivot Points. Base on
 * http://stockcharts.com/help/doku.php?id=chart_school:technical_indicators:pivot_points
 */
public class PivotPoints extends AbstractIndicator {
    public final static int P = 0;
    public final static int S1 = 1;
    public final static int S2 = 2;
    public final static int R1 = 3;
    public final static int R2 = 4;

    {
        longName = "Pivot Points";
        shortName = "Pivot Points";
        description = "unknow";
    }

    public PivotPoints(IQuoteProvider quoteProvider) {
        super(quoteProvider);
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        IndicatorValue ret = new IndicatorValue();
        List<Quote> qtSer = quoteProvider.getQuotesBefore(32*2,time);
        List<Quote> preMonthSer = new ArrayList<Quote>();
        double high = Double.MIN_VALUE;
        double low = Double.MAX_VALUE;
        double close;

        //previous month
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.add(Calendar.MONTH,-1);

        for (Quote qt : qtSer){
            if (isOnSameMonth(qt.getTime(), cal.getTimeInMillis())){
                preMonthSer.add(qt);
            }
        }

        //find HLC
        for (Quote qt : preMonthSer){
            high = Math.max(high, qt.getHigh());
            low = Math.min(low, qt.getLow());
        }
        close = preMonthSer.get(preMonthSer.size()-1).getClose();

        double p = (high + low + close)/3;
        double s1 = p*2 - high;
        double s2 = p - (high-low);
        double r1 = p*2 - low;
        double r2 = p + (high-low);

        ret.put("P", p);
        ret.put("S1", s1);
        ret.put("S2", s2);
        ret.put("R1", r1);
        ret.put("R2", r2);

        return ret;
    }

    private static boolean isOnSameMonth(long time1, long time2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMM");
        String date1 = fmt.format(new Date(time1));
        String date2 = fmt.format(new Date(time2));
        if (date1.equals(date2))
            return true;
        else
            return false;
    }
}
