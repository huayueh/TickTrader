package ticktrader;

import org.junit.Test;
import ticktrader.dto.FutureType;
import ticktrader.dto.Position;
import ticktrader.dto.Tick;
import ticktrader.strategy.AbstractStrategy;
import ticktrader.strategy.Strategy;

/**
 * Author: huayueh
 * Date: 2015/10/3
 */
public class InverseTest {
    private String symbol = "mockSymbol";
    private String contract = "mockContract";
    private FutureType type = FutureType.CALL;

    @Test
    public void testInverse() {
        Strategy strategy = new AbstractStrategy(null, null) {
            private boolean inversed = false;

            @Override
            public void onTick(Tick tick) {
                cntPnl(tick);
                if (!tradedToday()){
                    Position position = new Position.Builder().
                            symbol(symbol).
                            contract(contract).
                            side(Position.Side.Sell).
                            price(tick.getPrice()).
                            qty(1).
                            openTime(tick.getTime()).
                            putOrCall(type).
                            exercisePrice(tick.getExPrice()).
                            build();
                    placePosition(position);
                }

                // inverse position
                if ((positions() != 0) && !inversed && (curPnl < -30)) {
                    FutureType inversType = FutureType.CALL.equals(type)?FutureType.PUT:FutureType.CALL;
                    Position position = new Position.Builder().
                            symbol(tick.getSymbol()).
                            contract(tick.getContract()).
                            side(Position.Side.Sell).
                            price(tick.getPrice()).
                            qty(1).
                            openTime(tick.getTime()).
                            putOrCall(inversType).
                            exercisePrice(tick.getExPrice()).
                            build();
                    placePosition(position);
                    inversed = true;
                }

                if ((positions() != 0) && inversed && (curPnl == 0)) {
                    settleAllPosition(tick);
                }
            }

            @Override
            public void onFirstTick(Tick tick) {

            }
        };

        Tick tick1 = new Tick();
        tick1.setSymbol(symbol);
        tick1.setContract(contract);
        tick1.setFutureType(type);
        tick1.setPrice(100);
        strategy.onTick(tick1);

        Tick tick2 = new Tick();
        tick2.setSymbol(symbol);
        tick2.setContract(contract);
        tick2.setFutureType(type);
        tick2.setPrice(130);
        strategy.onTick(tick2);

    }
}
