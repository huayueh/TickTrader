package ticktrader.strategy;

import ticktrader.dto.FutureType;
import ticktrader.dto.Position;
import ticktrader.dto.Tick;
import ticktrader.recorder.Recorder;
import ticktrader.service.FuturePriceFinder;
import ticktrader.service.SettleContractProvider;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Author: huayueh
 * Date: 2015/4/27
 */
public class OptionDayTradeStrategy extends AbstractStrategy {
    private LocalDate date;
    private String contract;
    private boolean traded = false;
    private int exPrice;
    private FutureType type = FutureType.CALL;
    private FuturePriceFinder futurePriceFinder;

    public OptionDayTradeStrategy(Recorder<Position> recorder) throws URISyntaxException {
        super(recorder);
        URI uriOpen = getClass().getResource("/2014_open_tick.csv").toURI();
        URI uriClose = getClass().getResource("/2014_last_tick.csv").toURI();
        futurePriceFinder = new FuturePriceFinder(Paths.get(uriOpen), Paths.get(uriClose));
    }

    private int roundUpExPrice(double price){
        int i = (int) (price % 100);
        if (i > 50) {
            return ((int)(price / 100) * 100 + 100);
        } else {
            return ((int)(price / 100) * 100);
        }
    }

    @Override
    public void onTick(Tick tick) {
        if (date == null || !tick.getTime().toLocalDate().equals(date)) {
            Optional<Tick> closeTick = futurePriceFinder.find(date, "TX", FuturePriceFinder.Type.CLOSE);
            date = tick.getTime().toLocalDate();
            contract = SettleContractProvider.getInstance().closestContract(date);
            Optional<Tick> openTick = futurePriceFinder.find(date, "TX", FuturePriceFinder.Type.OPEN);

            if (openTick.isPresent()){
                double price = openTick.get().getPrice();
                exPrice = roundUpExPrice(price);

                if (closeTick.isPresent()){
                    // go up at open
                    if (openTick.get().getPrice() > closeTick.get().getPrice()){
                        type = FutureType.CALL;
                    } else {// go down at open
                        type = FutureType.PUT;
                    }
                }
            }
        }

        if (!tick.getContract().equals(contract) || !tick.getFutureType().equals(type))
            return;

        if (tick.getExPrice() != exPrice)
            return;

        LocalTime tickTime = tick.getTime().toLocalTime();

        //TODO: position qty
        if (tickTime.isAfter(LocalTime.of(8, 45, 00)) && tickTime.isBefore(LocalTime.of(13, 44, 00)) && !traded) {
            Position position = new Position.Builder().
                    symbol(tick.getSymbol()).
                    contract(tick.getContract()).
                    side(Position.Side.Sell).
                    price(tick.getPrice()).
                    qty(1).
                    openTime(tick.getTime()).
                    putOrCall(type).
                    exercisePrice(tick.getExPrice()).
                    build();
            placePosition(position);
            traded = true;
        }

        //TODO: settle partial qty
        //TODO: settle recorder to storage
        if (tickTime.isAfter(LocalTime.of(13, 44, 00)) && traded && tick.getExPrice() == exPrice) {
            settleAllPosition(tick);
            traded = false;
        }
    }
}
