package ticktrader.strategy;

import ticktrader.dto.Position;
import ticktrader.dto.Tick;
import ticktrader.provider.ContractProvider;
import ticktrader.recorder.Recorder;

import java.time.LocalDate;
import java.util.*;

/**
 * Author: huayueh
 * Date: 2015/4/21
 */
public abstract class AbstractStrategy implements Strategy {
    protected final Recorder recorder;
    protected ContractProvider contractProvider;
    protected double totalPnl = 0;
    protected LocalDate date;
    protected LocalDate lastTradedate;
    private Map<String, Queue<Position>> positions = new HashMap<>();
    private Set<LocalDate> tradedDate = new HashSet<>();

    public AbstractStrategy(Recorder recorder, ContractProvider contractProvider){
        this.recorder = recorder;
        this.contractProvider = contractProvider;
    }

    public AbstractStrategy(Recorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.getClass().isAssignableFrom(Tick.class)) {
            Tick tick = (Tick) arg;
            cntPnl(tick);
            if (date == null || !tick.getTime().toLocalDate().equals(date)) {
                lastTradedate = date;
                date = tick.getTime().toLocalDate();
                onFirstTick(tick);
            }
            onTick(tick);
        }
    }

    protected int positions(String symbol, String contract){
        Queue<Position> posQueue = positions.get(symbol + contract);
        if (posQueue != null)
            return posQueue.size();
        return 0;
    }

    protected int positions(){
        int cnt = 0;
        for(Map.Entry<String, Queue<Position>> entry : positions.entrySet()) {
            cnt += entry.getValue().size();
        }
        return cnt;
    }

    protected boolean tradedToday(){
        return tradedDate.contains(date);
    }

    protected void placePosition(Position position) {
        tradedDate.add(date);
        Queue<Position> posQueue = positions.get(position.getSymbol() + position.getContract());
        if (posQueue == null) {
            posQueue = new LinkedList<>();
        }
        posQueue.offer(position);
        positions.put(position.getSymbol() + position.getContract(), posQueue);
    }

    protected void settleAllPosition(Tick tick) {
        Queue<Position> posQueue = positions.get(tick.getSymbol() + tick.getContract());
        while (posQueue != null && !posQueue.isEmpty()) {
            Position position = posQueue.poll();
            position.fillAllQuantity(tick.getPrice(), tick.getTime());
            totalPnl += position.getPnl();
            recorder.record(position);
        }
        positions.clear();
    }

    protected void cntPnl(Tick tick) {
        String key = tick.getSymbol() + tick.getContract();
        Queue<Position> queue = positions.get(key);

        if (queue == null)
            return;

        for (Position pos : queue) {
            // concept: over 0 profit
            if (pos.getSide() == Position.Side.Buy) {
                pos.setPnl((tick.getPrice() - pos.getPrice()) * pos.getQty());
            } else {
                pos.setPnl((pos.getPrice() - tick.getPrice()) * pos.getQty());
            }
        }
        onTick(tick);
    }

    @Override
    public double getPnl() {
        return totalPnl;
    }
}
