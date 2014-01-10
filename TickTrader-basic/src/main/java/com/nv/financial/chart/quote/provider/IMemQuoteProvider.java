package com.nv.financial.chart.quote.provider;

import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.dto.Tick;

import java.util.Observer;

/**
 * User: Harvey
 * Date: 2013/11/4
 * Time: 上午 11:46
 */
public interface IMemQuoteProvider extends IQuoteProvider, Observer {
    public void toStorage(Quote qt);
    public void restoreFromStorage();
    public void setQuotes(Tick tick);
    public void setMaxInMem(int max);
}
