package ticktrader.storage;

import com.nv.financial.chart.dto.Quote;
import com.nv.financial.chart.storage.IStorage;
import ticktrader.dto.Tick;
import com.nv.financial.chart.quote.TimePeriod;

import java.util.List;

/**
 * User: Harvey
 * Date: 2014/7/21
 * Time: 下午 05:53
 */
public class EmptyStorage implements IStorage {

    @Override
    public void save(com.nv.financial.chart.dto.Tick tick) {

    }

    @Override
    public void save(Quote quote) {

    }

    @Override
    public Quote retrieveQuote(long time, String provider, String product, TimePeriod period) {
        return null;
    }

    @Override
    public List<Quote> retrieveQuotes(long fromTime, long toTime, String provider, String product, TimePeriod period) {
        return null;
    }

    @Override
    public List<Quote> retrieveQuotes(int nBars, long toTime, String provider, String product, TimePeriod period) {
        return null;
    }

    @Override
    public List<com.nv.financial.chart.dto.Tick> retrieveTicks(int nTicks, long toTime, String provider, String product) {
        return null;
    }

}
