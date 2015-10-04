package ticktrader.service;

import ticktrader.dto.Contract;
import ticktrader.dto.Tick;

/**
 * Author: huayueh
 * Date: 2015/4/24
 */
public interface TickService extends Runnable {
    void addContract(Contract contract);
    void removeContract(Contract contract);
    void onTick(Tick tick);
}
