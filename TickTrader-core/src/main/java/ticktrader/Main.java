package ticktrader;

import ticktrader.dto.PutOrCall;
import ticktrader.dto.Topic;
import ticktrader.recorder.PrintRecorder;
import ticktrader.service.FutureTickService;
import ticktrader.service.OptionTickService;
import ticktrader.service.SettleContractProvider;
import ticktrader.service.TickService;
import ticktrader.strategy.DayTradeStrategy;
import ticktrader.strategy.PrintStrategy;
import ticktrader.strategy.Strategy;

/**
 * Author: huayueh
 * Date: 2014/1/10
 */
public class Main {
    public static void main(String arg[]){
        Strategy strategy = new DayTradeStrategy(new PrintRecorder());
        TickService tickService = new FutureTickService("E:\\Tick\\Future_rpt\\2014", strategy);
//        TickService tickService = new OptionTickService("E:\\Tick\\Option_rpt\\2014", strategy);
        tickService.addTopic(new Topic("MTX", Topic.ANY, 0, PutOrCall.NONE));
        Thread mkt = new Thread(tickService);
        mkt.setName("FutureTickService");
        mkt.start();
    }
}
