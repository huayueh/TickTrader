package ticktrader;

import ticktrader.dto.PutOrCall;
import ticktrader.dto.Topic;
import ticktrader.recorder.PrintRecorder;
import ticktrader.service.OptionTickService;
import ticktrader.service.TickService;
import ticktrader.strategy.OptionDayTradeStrategy;
import ticktrader.strategy.Strategy;

/**
 * Author: huayueh
 * Date: 2014/1/10
 */
public class Main {
    public static void main(String arg[]){
        Strategy strategy = new OptionDayTradeStrategy(new PrintRecorder());
//        TickService tickService = new FutureTickService("D:\\Tick\\Future_rpt\\2014", strategy);
        TickService tickService = new OptionTickService("D:\\Tick\\Option_rpt\\2014", strategy);
        tickService.addTopic(new Topic("TXO", Topic.ANY, Topic.ANY_PRICE, PutOrCall.PUT));
        tickService.addTopic(new Topic("TXO", Topic.ANY, Topic.ANY_PRICE, PutOrCall.CALL));
        Thread mkt = new Thread(tickService);
        mkt.setName("TickService");
        mkt.start();
    }
}
