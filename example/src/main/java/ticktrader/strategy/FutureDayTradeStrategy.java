package ticktrader.strategy;

import ticktrader.dto.Position;
import ticktrader.dto.Tick;
import ticktrader.provider.ContractProvider;
import ticktrader.recorder.Recorder;
import ticktrader.provider.SettleContractProvider;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Author: huayueh
 * Date: 2015/4/22
 */
public class FutureDayTradeStrategy extends AbstractStrategy {
    private LocalDate date;
    private String contract;
    private String symbol = "MTX";

    public FutureDayTradeStrategy(Recorder recorder, ContractProvider contractProvider) {
        super(recorder, contractProvider);
    }


    @Override
    public void onFirstTick(Tick tick) {
        assert (contractProvider != null) : "contractProvider is expected.";
        date = tick.getTime().toLocalDate();
        contract = contractProvider.closestContract(date);
    }

    @Override
    public void onTick(Tick tick) {
        // filter
        if (!symbol.equals(tick.getSymbol()) || !tick.getContract().equals(contract))
            return;

        // right tick for strategy
        LocalTime tickTime = tick.getTime().toLocalTime();

        if (tickTime.isAfter(LocalTime.of(8, 45, 00)) && tickTime.isBefore(LocalTime.of(13, 44, 00)) && positions() == 0) {
            Position position = new Position.Builder().
                    symbol(tick.getSymbol()).
                    contract(tick.getContract()).
                    side(Position.Side.Sell).
                    price(tick.getPrice()).
                    qty(1).
                    openTime(tick.getTime()).
                    build();
            placePosition(position);
        }

        if (tickTime.isAfter(LocalTime.of(13, 44, 00)) && positions() != 0) {
            settleAllPosition(tick);
        }
    }

}
