package ticktrader;

import ticktrader.service.ParallelFutureTickService;
import ticktrader.service.SettleProvider;
import ticktrader.util.Utils;

import java.util.Observable;
import java.util.Observer;

/**
 * User: Harvey
 * Date: 2014/1/10
 * Time: 上午 10:39
 */
public class Main {
    public static void main(String arg[]){
        long start = Utils.formatDate("2014-01-01").getTime();
        long end = Utils.formatDate("2014-12-31").getTime();

        SettleProvider stProvider = SettleProvider.getInstance();

        ParallelFutureTickService futureTickService = new ParallelFutureTickService(start, end, new Observer() {
            @Override public void update(Observable o, Object arg) {
                System.out.println(arg);
            }
        });
        Thread mkt = new Thread(futureTickService);
        mkt.setName("FutureTickService");
        mkt.start();
    }
}
