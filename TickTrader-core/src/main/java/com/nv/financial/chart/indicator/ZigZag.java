package com.nv.financial.chart.indicator;

import com.nv.financial.chart.dto.IndicatorValue;
import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.quote.QuotePrice;
import com.nv.financial.chart.quote.provider.IQuoteProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * ZigZag. Base on
 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:zigzag
 * http://ask.moneydj.com/question/140/xun-wen-kong-pan-zhe-cheng-ben-xian-yu-zigzag/
 */
public class ZigZag extends AbstractIndicator {
    private static final Logger logger = LogManager.getLogger(ZigZag.class);
    private final double percent;

    enum Trend {
        UP,
        DOWN,
        NO;
    }

    {
        longName = "ZigZag";
        shortName = "ZigZag";
        description = "unknow";
    }

    public ZigZag(IQuoteProvider quoteProvider, double percent) {
        super(quoteProvider);
        this.percent = percent;
    }

    @Override
    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
        List<Quote> quotes = quoteProvider.getQuotesBefore(91, time);
        double hiest = Double.MIN_VALUE;
        double loest = Double.MAX_VALUE;
        long prePoint = 0;
        Trend trend = Trend.NO;
        int idx = 0;

        //find invert point
        for(Quote quote : quotes){
            long qtTime = quote.getTime();

            if (prePoint == 0){
                hiest = quote.getClose();
                loest = quote.getClose();
//                IndicatorValue iv = new IndicatorValue(qtTime);
//                iv.put(shortName, 1);
//                cache.put(qtTime, iv);
                prePoint = qtTime;
                continue;
            }
            if (trend == Trend.NO){
                double up = 0;
                double down = 0;
                if(idx == 0){
                    up = quote.getHigh() * 1.0158;
                    down = up - 2 * 1.0158;
                }
                if (quote.getLow() < down){
                    trend = Trend.DOWN;
                }
                if (quote.getHigh() > up){
                    trend = Trend.UP;
                }
                continue;
            }
            if (trend == Trend.DOWN){
                double prec = (quote.getHigh() - loest)/quote.getHigh();
                if (prec > percent){
                    IndicatorValue iv = new IndicatorValue(prePoint);
                    iv.put(shortName, 1);
                    cache.put(prePoint, iv);
                    prePoint = qtTime;
                    trend = Trend.UP;
                }
            }
            if (trend == Trend.UP){
                double prec = (hiest - quote.getLow())/hiest;
                if (prec > percent){
                    IndicatorValue iv = new IndicatorValue(prePoint);
                    iv.put(shortName, 1);
                    cache.put(prePoint, iv);
                    prePoint = qtTime;
                    trend = Trend.DOWN;
                }
            }
            if (hiest == quote.getHigh() || loest == quote.getLow()){
                prePoint = quote.getTime();
            }
            idx++;
//            logger.debug(Utils.formatTimeStamp(qtTime) + " hi:" + hiest + "lo:" + loest + " " + iv.getValue(0));
        }
        IndicatorValue ret = cache.get(time);
        if(ret == null){
            ret = new IndicatorValue(time);
            ret.put(shortName, 0);
        }

        return ret;
    }

//    @Override
//    public IndicatorValue calculate(long time, QuotePrice quotePrice) {
//        List<Quote> points = new ArrayList<Quote>();
//        List<Quote> quotes;
//        double hiest;
//        double loest;
//
//        //never computed
//        if(points.isEmpty()){
//            quotes = quoteProvider.getQuotesBefore(91, time);
//            hiest = Double.MIN_VALUE;
//            loest = Double.MAX_VALUE;
//        }else{
//            Quote lastIvPoint = points.get(points.size()-1);
//            Quote secondIvPoint = points.get(points.size()-2);
//            quotes = quoteProvider.getQuotes(lastIvPoint.getTime(), time);
//            if(quotePrice.getPrice(lastIvPoint) > quotePrice.getPrice(secondIvPoint)){
//                hiest = quotePrice.getPrice(lastIvPoint);
//                loest = quotePrice.getPrice(secondIvPoint);
//            }else{
//                loest = quotePrice.getPrice(lastIvPoint);
//                hiest = quotePrice.getPrice(secondIvPoint);
//            }
//        }
//
//        //find invert point
//        for(Quote quote : quotes){
//            double price = quotePrice.getPrice(quote);
//            double downPercent = (price - hiest)/hiest;
//            double upPercent = (price - loest)/loest;
//            if(downPercent > percent || upPercent > percent){
//                points.add(quote);
//            }
//            hiest = Math.max(hiest, price);
//            loest = Math.min(loest, price);
//        }
//        Quote lastPoint = points.get(points.size()-1);
//        Quote now = quotes.get(quotes.size()-1);
//        IndicatorValue ret = new IndicatorValue();
//
//        //is now invert point
//        if(now.getTime() == lastPoint.getTime()){
//            ret.put(shortName,1);
//        }else{
//            ret.put(shortName,0);
//        }
//
//        return ret;
//    }
}
