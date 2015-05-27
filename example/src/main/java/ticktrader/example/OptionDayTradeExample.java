package ticktrader.example;

import ticktrader.dto.FutureType;
import ticktrader.dto.Topic;
import ticktrader.provider.DaysFarContractProvider;
import ticktrader.provider.SettleContractProvider;
import ticktrader.recorder.ComposePositionRecorder;
import ticktrader.recorder.PrintPositionRecorder;
import ticktrader.service.OptionTickService;
import ticktrader.service.TickService;
import ticktrader.strategy.OptionDayTradeStrategy;
import ticktrader.strategy.Strategy;

import java.net.URISyntaxException;

/**
 * Author: huayueh
 * Date: 2015/4/29
 */
public class OptionDayTradeExample {
    public static void main(String arg[]) throws URISyntaxException {
        Strategy strategy = new OptionDayTradeStrategy(new PrintPositionRecorder(), new DaysFarContractProvider(10));
        TickService tickService = new OptionTickService("E:\\Tick\\Option_rpt\\2014", strategy);
        tickService.addTopic(new Topic("TXO", Topic.ANY, Topic.ANY_PRICE, FutureType.PUT));
        tickService.addTopic(new Topic("TXO", Topic.ANY, Topic.ANY_PRICE, FutureType.CALL));

        Thread mkt = new Thread(tickService);
        mkt.setName("TickService");
        mkt.start();
    }
}
