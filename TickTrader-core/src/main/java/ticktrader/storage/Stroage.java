package ticktrader.storage;

import ticktrader.dto.Quote;
import ticktrader.dto.Tick;

/**
 * Author: huayueh
 * Date: 2015/4/16
 */
interface Stroage {
    void save(Tick tick);
    void save(Quote quote);
}