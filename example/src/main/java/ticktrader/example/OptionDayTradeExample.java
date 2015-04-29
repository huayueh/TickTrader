package ticktrader.example;

import ticktrader.dto.FutureType;
import ticktrader.dto.Topic;
import ticktrader.recorder.PrintPositionRecorder;
import ticktrader.service.OptionTickService;
import ticktrader.service.TickService;
import ticktrader.strategy.OptionDayTradeStrategy;
import ticktrader.strategy.Strategy;

/**
 * Author: huayueh
 * Date: 2015/4/29
 */
public class OptionDayTradeExample {
    public static void main(String arg[]){
        Strategy strategy = new OptionDayTradeStrategy(new PrintPositionRecorder());
        TickService tickService = new OptionTickService("E:\\Tick\\Option_rpt\\2014", strategy);
        tickService.addTopic(new Topic("TXO", Topic.ANY, Topic.ANY_PRICE, FutureType.PUT));
        tickService.addTopic(new Topic("TXO", Topic.ANY, Topic.ANY_PRICE, FutureType.CALL));

        Thread mkt = new Thread(tickService);
        mkt.setName("TickService");
        mkt.start();
    }
}
