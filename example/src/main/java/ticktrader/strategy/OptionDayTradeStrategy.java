package ticktrader.strategy;

import ticktrader.dto.FutureType;
import ticktrader.dto.Order;
import ticktrader.dto.Position;
import ticktrader.dto.Tick;
import ticktrader.provider.ContractProvider;
import ticktrader.recorder.Recorder;
import ticktrader.service.FuturePriceFinder;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Author: huayueh
 * Date: 2015/4/27
 */
public class OptionDayTradeStrategy extends AbstractStrategy {
    protected String contract;
    protected int exPrice;
    protected FutureType type = FutureType.CALL;
    protected FuturePriceFinder futurePriceFinder;

    public OptionDayTradeStrategy(Recorder recorder, ContractProvider contractProvider, int year) throws URISyntaxException {
        super(recorder, contractProvider);
        URI uriOpen = getClass().getResource(String.format("/%d_open_tick.csv", year)).toURI();
        URI uriClose = getClass().getResource(String.format("/%d_last_tick.csv", year)).toURI();
        futurePriceFinder = new FuturePriceFinder(Paths.get(uriOpen), Paths.get(uriClose));
    }

    @Override
    public void onFirstTick(Tick tick) {
        contract = contractProvider.closestContract(date);
        Optional<Tick> openTick = futurePriceFinder.find(date, "TX", FuturePriceFinder.Type.OPEN);

        if (openTick.isPresent()){
            double price = openTick.get().getPrice();
            exPrice = (int) (price / 100);
            exPrice *= 100;
        }
    }

    @Override
    public void onTick(Tick tick) {
        // filter
        if (!tick.getContract().equals(contract) || !tick.getFutureType().equals(type))
            return;

        if (tick.getExPrice() != exPrice)
            return;

        // right tick for strategy
        LocalTime tickTime = tick.getTime().toLocalTime();

        if (tickTime.isAfter(LocalTime.of(8, 44, 59)) && tickTime.isBefore(LocalTime.of(13, 44, 00)) && positions() == 0) {
            // day trade strategy
            if (tradedToday())
                return;

            Order order = new Order.Builder().
                    symbol(tick.getSymbol()).
                    contract(tick.getContract()).
                    side(Order.Side.Sell).
                    price(tick.getPrice()).
                    qty(1).
                    putOrCall(type).
                    exercisePrice(tick.getExPrice()).
                    build();
            placePosition(new Position(order, tick.getTime()));
        }

        if (tickTime.isAfter(LocalTime.of(13, 40, 00)) && positions()!=0 && tick.getExPrice() == exPrice) {
            settleAllPosition(tick);
        }
    }
}
