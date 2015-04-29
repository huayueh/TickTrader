package ticktrader.service;

import java.time.LocalDate;

/**
 * Author: huayueh
 * Date: 2015/4/24
 */
public interface ContractProvider {
    String closestContract(LocalDate time);
}
