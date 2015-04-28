package ticktrader;

import ticktrader.dto.FutureType;
import ticktrader.dto.Topic;
import ticktrader.recorder.FileTickRecorder;
import ticktrader.recorder.PrintPositionRecorder;
import ticktrader.service.OpenTickService;
import ticktrader.service.OptionTickService;
import ticktrader.service.TickService;
import ticktrader.strategy.*;

import java.nio.file.Paths;

/**
 * Author: huayueh
 * Date: 2014/1/10
 */
public class Main {
    public static void main(String arg[]){
//        Strategy strategy = new RecordStrategy(new FileTickRecorder(Paths.get("E:", "tick.csv")));
//        TickService tickService = new OpenTickService("E:\\Tick\\Future_rpt\\2014", strategy);
//        tickService.addTopic(new Topic("TX", Topic.CURRENT, Topic.ANY_PRICE, FutureType.FUTURE));

        Strategy strategy = new OptionDayTradeStrategy(new PrintPositionRecorder());
        TickService tickService = new OptionTickService("E:\\Tick\\Option_rpt\\2014", strategy);
        tickService.addTopic(new Topic("TXO", Topic.ANY, Topic.ANY_PRICE, FutureType.PUT));
        tickService.addTopic(new Topic("TXO", Topic.ANY, Topic.ANY_PRICE, FutureType.CALL));

        Thread mkt = new Thread(tickService);
        mkt.setName("TickService");
        mkt.start();
    }
}
