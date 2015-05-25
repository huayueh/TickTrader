package ticktrader.example;

import ticktrader.dto.FutureType;
import ticktrader.dto.Topic;
import ticktrader.recorder.ComposePositionRecorder;
import ticktrader.service.FutureTickService;
import ticktrader.service.TickService;
import ticktrader.strategy.FutureDayTradeStrategy;
import ticktrader.strategy.Strategy;

import java.net.URISyntaxException;

/**
 * Author: huayueh
 * Date: 2015/5/25
 */
public class FutureDayTradeExample {
    public static void main(String arg[]) throws URISyntaxException {
        Strategy strategy = new FutureDayTradeStrategy(new ComposePositionRecorder());
        TickService tickService = new FutureTickService("E:\\Tick\\Future_rpt\\2014", strategy);
        tickService.addTopic(new Topic("MTX", Topic.CURRENT, Topic.ANY_PRICE, FutureType.FUTURE));

        Thread mkt = new Thread(tickService);
        mkt.setName("TickService");
        mkt.start();
    }
}
