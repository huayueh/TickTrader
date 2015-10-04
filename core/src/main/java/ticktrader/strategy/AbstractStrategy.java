package ticktrader.strategy;

import ticktrader.dto.Contract;
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
    protected double curPnl = 0;
    protected LocalDate date;
    protected LocalDate lastTradedate;
    private Map<Contract, Queue<Position>> positions = new HashMap<>();
    private Set<LocalDate> tradedDate = new HashSet<>();
    private int cost = 3;

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

    protected int positions(){
        int cnt = 0;
        for(Map.Entry<Contract, Queue<Position>> entry : positions.entrySet()) {
            cnt += entry.getValue().size();
        }
        return cnt;
    }

    protected boolean tradedToday(){
        return tradedDate.contains(date);
    }

    protected void placePosition(Position position) {
        tradedDate.add(date);
        Contract contract = Contract.get(position);
        Queue<Position> posQueue = positions.get(contract);
        if (posQueue == null) {
            posQueue = new LinkedList<>();
        }
        posQueue.offer(position);
        positions.put(contract, posQueue);
    }

    protected void settleAllPosition(Tick tick) {
        Queue<Position> posQueue = positions.get(Contract.get(tick));
        while (posQueue != null && !posQueue.isEmpty()) {
            Position position = posQueue.poll();
            position.fillAllQuantity(tick.getPrice(), tick.getTime());
            curPnl = 0;
            recorder.record(position);
        }

//        positions.clear();
    }

    protected void settleAllLosePosition(Tick tick) {
        Queue<Position> posQueue = positions.get(Contract.get(tick));
        while (posQueue.peek() != null && posQueue.peek().getPnl() <= 0) {
            Position position = posQueue.poll();
            position.fillAllQuantity(tick.getPrice(), tick.getTime());
            curPnl += position.getPnl();
            recorder.record(position);
        }
    }

    protected void settleAllWinPosition(Tick tick) {
        Queue<Position> posQueue = positions.get(Contract.get(tick));
        while (posQueue.peek() != null && posQueue.peek().getPnl() >= 0) {
            Position position = posQueue.poll();
            position.fillAllQuantity(tick.getPrice(), tick.getTime());
            curPnl += position.getPnl();
            recorder.record(position);
        }
    }

    protected void cntPnl(Tick tick) {
        Queue<Position> queue = positions.get(Contract.get(tick));

        if (queue == null)
            return;

        double totalPnl = 0;
        for (Position pos : queue) {
            // concept: over 0 profit
            double pnl;
            if (pos.getSide() == Position.Side.Buy) {
                pnl = (tick.getPrice() - pos.getPrice()) * pos.getQty();
            } else {
                pnl = (pos.getPrice() - tick.getPrice()) * pos.getQty();
            }
            totalPnl += (pnl - cost*pos.getQty());
            pos.setPnl(pnl - cost*pos.getQty());
            pos.setNetPnl(pnl);
        }
        curPnl = totalPnl;
    }

    @Override
    public void done() {
        recorder.done();
    }

    //TODO: partial settle
}
