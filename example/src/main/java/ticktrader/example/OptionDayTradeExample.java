package ticktrader.example;

import ticktrader.dto.FutureType;
import ticktrader.dto.Position;
import ticktrader.dto.Topic;
import ticktrader.provider.DaysFarContractProvider;
import ticktrader.recorder.*;
import ticktrader.service.OptionTickService;
import ticktrader.service.TickService;
import ticktrader.strategy.OptionDayTradeStrategy;
import ticktrader.strategy.Strategy;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: huayueh
 * Date: 2015/4/29
 */
public class OptionDayTradeExample {
    public static void main(String arg[]) throws URISyntaxException {
        List<Recorder<Position>> recorders = new ArrayList<>();
        recorders.add(new PrintPositionRecorder());
        recorders.add(new FilePositionRecorder(Paths.get("E:", "positions.csv")));
        recorders.add(new FileReportRecorder(Paths.get("E:", "report.txt")));
        Recorder<Position> recorder = new ComposePositionRecorder(recorders);
        Strategy strategy = new OptionDayTradeStrategy(recorder, new DaysFarContractProvider(10));
        TickService tickService = new OptionTickService("E:\\Tick\\Option_rpt\\2014", strategy);
        tickService.addTopic(new Topic("TXO", Topic.ANY, Topic.ANY_PRICE, FutureType.PUT));
        tickService.addTopic(new Topic("TXO", Topic.ANY, Topic.ANY_PRICE, FutureType.CALL));

        Thread mkt = new Thread(tickService);
        mkt.setName("TickService");
        mkt.start();
    }
}
