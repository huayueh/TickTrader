package ticktrader;

import ticktrader.service.SettleProvider;
import ticktrader.service.FutureTickService;
import ticktrader.service.SingleFutureTickService;
import com.nv.financial.chart.util.Utils;

/**
 * User: Harvey
 * Date: 2014/1/10
 * Time: 上午 10:39
 */
public class Main {
    public static void main(String arg[]){
        long start = Utils.formatDate("2007-01-01").getTime();
        long end = Utils.formatDate("2014-01-27").getTime();

        SettleProvider stProvider = SettleProvider.getInstance();

        FutureTickService futureTickService = new SingleFutureTickService(start, end);
        Thread mkt = new Thread(futureTickService);
        mkt.setName("FutureTickService");
        mkt.start();
    }
}
