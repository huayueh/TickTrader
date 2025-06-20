package ticktrader.example;

import java.net.URISyntaxException;
import java.nio.file.Paths;

import ticktrader.dto.Contract;
import ticktrader.dto.FutureType;
import ticktrader.provider.FixContractProvider;
import ticktrader.recorder.FileTickRecorder;
import ticktrader.service.FutureTickService;
import ticktrader.service.TickService;
import ticktrader.strategy.FutureDayTradeStrategy;
import ticktrader.strategy.Strategy;

/**
 * Author: huayueh
 * Date: 2015/5/25
 */
public class FutureDayTradeExample {
    public static void main(String arg[]) throws URISyntaxException {
        int year = 2021;
        Strategy strategy = new FutureDayTradeStrategy(new FileTickRecorder(Paths.get("2021-12-02.csv")), new FixContractProvider("202112"));
        TickService tickService = new FutureTickService("/Users/harvey/Downloads/Tick/future/", year, strategy);
        tickService.addContract(new Contract("TX", Contract.ANY, Contract.ANY_PRICE, FutureType.FUTURE));

        Thread mkt = new Thread(tickService);
        mkt.setName("TickService");
        mkt.start();
    }
}
