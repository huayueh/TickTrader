package ticktrader.example;

import ticktrader.dto.FutureType;
import ticktrader.dto.Topic;
import ticktrader.recorder.FileTickRecorder;
import ticktrader.service.OpenTickService;
import ticktrader.service.TickService;
import ticktrader.strategy.RecordStrategy;
import ticktrader.strategy.Strategy;

import java.nio.file.Paths;

/**
 * Author: huayueh
 * Date: 2015/4/29
 */
public class OpenTickExample {
    public static void main(String arg[]){
        Strategy strategy = new RecordStrategy(new FileTickRecorder(Paths.get("E:", "open_tick.csv")));
        TickService tickService = new OpenTickService("E:\\Tick\\Future_rpt\\2014", strategy);
        tickService.addTopic(new Topic("TX", Topic.CURRENT, Topic.ANY_PRICE, FutureType.FUTURE));

        Thread mkt = new Thread(tickService);
        mkt.setName("TickService");
        mkt.start();
    }
}
