package ticktrader;

import ticktrader.dto.PutOrCall;
import ticktrader.dto.Topic;
import ticktrader.recorder.FileRecorder;
import ticktrader.recorder.PrintRecorder;
import ticktrader.service.OpenTickService;
import ticktrader.service.OptionTickService;
import ticktrader.service.TickService;
import ticktrader.strategy.OptionDayTradeStrategy;
import ticktrader.strategy.PrintStrategy;
import ticktrader.strategy.RecordStrategy;
import ticktrader.strategy.Strategy;

import java.io.File;
import java.nio.file.Paths;

/**
 * Author: huayueh
 * Date: 2014/1/10
 */
public class Main {
    public static void main(String arg[]){
        Strategy strategy = new RecordStrategy(new FileRecorder(null, new File("Tick/tick.csv").toPath()));
        TickService tickService = new OpenTickService("E:\\Tick\\Future_rpt\\2014", strategy);
        tickService.addTopic(new Topic("TX", Topic.CURRENT, Topic.ANY_PRICE, PutOrCall.NONE));
//        TickService tickService = new OptionTickService("D:\\Tick\\Option_rpt\\2014", strategy);
//        tickService.addTopic(new Topic("TXO", Topic.ANY, Topic.ANY_PRICE, PutOrCall.PUT));
//        tickService.addTopic(new Topic("TXO", Topic.ANY, Topic.ANY_PRICE, PutOrCall.CALL));
        Thread mkt = new Thread(tickService);
        mkt.setName("TickService");
        mkt.start();
    }
}
