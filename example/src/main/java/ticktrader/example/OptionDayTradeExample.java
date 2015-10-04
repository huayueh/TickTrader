package ticktrader.example;

import ticktrader.dto.Contract;
import ticktrader.dto.FutureType;
import ticktrader.dto.Position;
import ticktrader.provider.DaysFarContractProvider;
import ticktrader.recorder.*;
import ticktrader.service.OptionTickService;
import ticktrader.service.TickService;
import ticktrader.strategy.MyOptionDayTradeStrategy;
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
        int year =  2014;
        //Recorder
        List<Recorder<Position>> recorders = new ArrayList<>();
        recorders.add(new PrintPositionRecorder());
        recorders.add(new FilePositionRecorder(Paths.get("D:", year + "_positions.csv")));
        recorders.add(new FileReportRecorder(Paths.get("D:", year + "_report.txt")));
        Recorder<Position> recorder = new ComposePositionRecorder(recorders);

        //strategy
        Strategy strategy = new MyOptionDayTradeStrategy(recorder, new DaysFarContractProvider(3), year);
        TickService tickService = new OptionTickService("D:/Tick/Option_rpt/", year, strategy);
        tickService.addContract(new Contract("TXO", Contract.ANY, Contract.ANY_PRICE, FutureType.PUT));
        tickService.addContract(new Contract("TXO", Contract.ANY, Contract.ANY_PRICE, FutureType.CALL));

        Thread mkt = new Thread(tickService);
        mkt.setName("TickService");
        mkt.start();
    }
}
