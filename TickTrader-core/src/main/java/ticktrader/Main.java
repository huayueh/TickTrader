package ticktrader;

import ticktrader.service.FutureTickService;
import ticktrader.service.SettleProvider;
import ticktrader.strategy.DayTradeStrategy;
import ticktrader.util.Utils;

/**
 * Author: huayueh
 * Date: 2014/1/10
 */
public class Main {
    public static void main(String arg[]){
        long start = Utils.formatDate("2014-01-01").getTime();
        long end = Utils.formatDate("2014-12-31").getTime();

        SettleProvider stProvider = SettleProvider.getInstance();
        DayTradeStrategy strategy = new DayTradeStrategy();
        FutureTickService futureTickService = new FutureTickService(start, end, strategy);
        Thread mkt = new Thread(futureTickService);
        mkt.setName("FutureTickService");
        mkt.start();
    }
}
