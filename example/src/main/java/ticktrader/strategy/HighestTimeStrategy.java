package ticktrader.strategy;

import ticktrader.dto.Tick;
import ticktrader.provider.ContractProvider;
import ticktrader.provider.TickPriceContractProvider;
import ticktrader.recorder.Recorder;

public class HighestTimeStrategy extends AbstractStrategy {
    private Tick putTick;

    private Tick callTick;

    private TickPriceContractProvider contractProvider;

    public HighestTimeStrategy(Recorder recorder, ContractProvider contractProvider) {
        super(recorder, contractProvider);
        this.contractProvider = (TickPriceContractProvider) contractProvider;
    }

    @Override
    public void onFirstTick(Tick tick) {
        this.putTick = contractProvider.getPutTick(tick.getTime().toLocalDate());
        this.callTick = contractProvider.getCallTick(tick.getTime().toLocalDate());
    }

    @Override
    public void onTick(Tick tick) {
        if (putTick != null && tick.samePrice(putTick)) {
            recorder.record(tick);
        }

        if (callTick != null && tick.samePrice(callTick)) {
            recorder.record(tick);
        }
    }

}
