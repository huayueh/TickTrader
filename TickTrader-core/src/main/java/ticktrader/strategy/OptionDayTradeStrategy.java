package ticktrader.strategy;

import ticktrader.dto.Position;
import ticktrader.dto.PutOrCall;
import ticktrader.dto.Tick;
import ticktrader.recorder.Recorder;
import ticktrader.service.FutureOpenPriceFinder;
import ticktrader.service.SettleContractProvider;

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
    private FutureOpenPriceFinder futureOpenPriceFinder = new FutureOpenPriceFinder("D:\\Tick\\Future_rpt");

    public OptionDayTradeStrategy(Recorder recorder) {
        super(recorder);
    }

    @Override
    public void onTick(Tick tick) {
        if (date == null || !tick.getTime().toLocalDate().equals(date)) {
            date = tick.getTime().toLocalDate();
            contract = SettleContractProvider.getInstance().closestContract(date);
            Optional<Tick> fTick = futureOpenPriceFinder.find(date, "TX", contract);

            if (fTick.isPresent()){
                double price = fTick.get().getPrice();
                exPrice = (int) (price / 100);
                exPrice *= 100;
            }
        }

        if (!tick.getContract().equals(contract) || !tick.getPutOrCall().equals(PutOrCall.CALL))
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
                    putOrCall(PutOrCall.CALL).
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
