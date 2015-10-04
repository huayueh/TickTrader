package ticktrader.example;

import ticktrader.dto.FutureType;
import ticktrader.dto.Contract;
import ticktrader.provider.SettleContractProvider;
import ticktrader.recorder.PrintPositionRecorder;
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
        int year = 0;
        Strategy strategy = new FutureDayTradeStrategy(new PrintPositionRecorder(), SettleContractProvider.getInstance());
        TickService tickService = new FutureTickService("E:/Tick/Future_rpt/", year, strategy);
        tickService.addContract(new Contract("TX", Contract.ANY, Contract.ANY_PRICE, FutureType.FUTURE));

        Thread mkt = new Thread(tickService);
        mkt.setName("TickService");
        mkt.start();
    }
}
