package com.nv.financial.chart;

import com.nv.financial.chart.quote.TimePeriod;
import com.nv.financial.chart.quote.provider.IMemQuoteProvider;
import com.nv.financial.chart.quote.provider.MemQuoteQuoteProvider;
import com.nv.financial.chart.quote.provider.SettleProvider;
import com.nv.financial.chart.service.FutureTickService;
import com.nv.financial.chart.util.Utils;

/**
 * User: Harvey
 * Date: 2014/1/10
 * Time: 上午 10:39
 */
public class Main {
    public static void main(String arg[]){
        long start = Utils.formatDate("2007-01-01").getTime();
        long end = Utils.formatDate("2007-02-04").getTime();

        SettleProvider stProvider = SettleProvider.getInstance();

        FutureTickService futureTickService = new FutureTickService(start, end);
//        IMemQuoteProvider test = new MemQuoteQuoteProvider("200701", "MTX", TimePeriod.DAY);
//        futureTickService.addObserver(test);

        Thread mkt = new Thread(futureTickService);
        mkt.setName("FutureTickService");
        mkt.start();
    }
}
