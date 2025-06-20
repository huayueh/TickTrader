package ticktrader.example;

import java.nio.file.Paths;

import ticktrader.dto.Contract;
import ticktrader.dto.FutureType;
import ticktrader.provider.TickPriceContractProvider;
import ticktrader.recorder.FileTickRecorder;
import ticktrader.service.OptionTickService;
import ticktrader.service.TickService;
import ticktrader.strategy.HighestTimeStrategy;
import ticktrader.strategy.Strategy;

/**
 * Author: huayueh
 * Date: 2021/11/8
 */
public class OptionDailyHigh {
    public static void main(String arg[]) {
        int year = 2018;

        //strategy
        Strategy strategy = new HighestTimeStrategy(new FileTickRecorder(Paths.get("highest-match-2018.txt")), new TickPriceContractProvider("highest.csv"));
        TickService tickService = new OptionTickService("/Users/harvey/Documents/Tick/option/", year, strategy);
        tickService.addContract(new Contract("TXO", Contract.ANY, Contract.ANY_PRICE, FutureType.PUT));
        tickService.addContract(new Contract("TXO", Contract.ANY, Contract.ANY_PRICE, FutureType.CALL));

        Thread mkt = new Thread(tickService);
        mkt.setName("TickService");
        mkt.start();
    }
}
