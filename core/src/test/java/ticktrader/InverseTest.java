package ticktrader;

import org.junit.Test;
import org.mockito.Mock;
import ticktrader.dto.FutureType;
import ticktrader.dto.Order;
import ticktrader.dto.Position;
import ticktrader.dto.Tick;
import ticktrader.recorder.PrintPositionRecorder;
import ticktrader.strategy.AbstractStrategy;
import ticktrader.strategy.Strategy;

import java.time.LocalDateTime;
import java.util.Observable;

/**
 * Author: huayueh
 * Date: 2015/10/3
 */
public class InverseTest {
    private String symbol = "mockSymbol";
    private String contract = "mockContract";

    @Mock
    Observable observable;

    @Test
    public void testInverse() {
        Strategy strategy = new AbstractStrategy(new PrintPositionRecorder(), null) {
            private boolean inversed = false;

            @Override
            public void onTick(Tick tick) {
                if (!tradedToday()) {
                    Order order = new Order.Builder().
                            symbol(symbol).
                            contract(contract).
                            side(Order.Side.Sell).
                            price(tick.getPrice()).
                            qty(1).
                            putOrCall(FutureType.CALL).
                            exercisePrice(tick.getExPrice()).
                            build();
                    placePosition(new Position(order, tick.getTime()));
                }

                // inverse position
                if ((positions() != 0) && !inversed && (curPnl < -30)) {
                    FutureType inversType = FutureType.PUT;
                    Order order = new Order.Builder().
                            symbol(tick.getSymbol()).
                            contract(tick.getContract()).
                            side(Order.Side.Sell).
                            qty(1).
                            putOrCall(inversType).
                            exercisePrice(tick.getExPrice()).
                            build();
                    sendOrder(order);
                    inversed = true;
                }

                if ((positions() != 0) && inversed && (curPnl == 0)) {
                    settleAllPosition(tick);
                    //TODO: stop order
                }
            }

            @Override
            public void onFirstTick(Tick tick) {

            }
        };

        Tick tick1 = new Tick();
        tick1.setTime(LocalDateTime.now());
        tick1.setSymbol(symbol);
        tick1.setContract(contract);
        tick1.setFutureType(FutureType.CALL);
        tick1.setPrice(100);
        strategy.update(observable, tick1);

        Tick tick2 = new Tick();
        tick2.setTime(LocalDateTime.now());
        tick2.setSymbol(symbol);
        tick2.setContract(contract);
        tick2.setFutureType(FutureType.CALL);
        tick2.setPrice(130);
        strategy.update(observable, tick2);

        Tick tick3 = new Tick();
        tick3.setTime(LocalDateTime.now());
        tick3.setSymbol(symbol);
        tick3.setContract(contract);
        tick3.setFutureType(FutureType.PUT);
        tick3.setPrice(136);
        strategy.update(observable, tick3);

        Tick tick4 = new Tick();
        tick4.setTime(LocalDateTime.now());
        tick4.setSymbol(symbol);
        tick4.setContract(contract);
        tick4.setFutureType(FutureType.PUT);
        tick4.setPrice(100);
        strategy.update(observable, tick4);

        strategy.update(observable, tick2);
    }
}
