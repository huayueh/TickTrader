package ticktrader.strategy;

import ticktrader.dto.Position;
import ticktrader.dto.Tick;
import ticktrader.recorder.Recorder;
import ticktrader.service.SettleContractProvider;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Author: huayueh
 * Date: 2015/4/22
 */
public class DayTradeStrategy extends AbstractStrategy {
    private LocalDate date;
    private String contract;
    private boolean traded = false;

    public DayTradeStrategy(Recorder recorder) {
        super(recorder);
    }

    @Override
    public void onTick(Tick tick) {
        if (date == null || !tick.getTime().toLocalDate().equals(date)) {
            date = tick.getTime().toLocalDate();
            contract = SettleContractProvider.getInstance().closestContract(date);
        }

        if (!"MTX".equals(tick.getSymbol()) || !tick.getContract().equals(contract))
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
                    build();
            placePosition(position);
            traded = true;
        }

        //TODO: settle partial qty
        //TODO: settle recorder to storage
        if (tickTime.isAfter(LocalTime.of(13, 44, 00)) && traded) {
            settleAllPosition(tick);
            traded = false;
        }
    }
}
