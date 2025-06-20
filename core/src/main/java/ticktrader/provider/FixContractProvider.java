package ticktrader.provider;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Author: huayueh
 * Date: 2015/5/27
 */
public class FixContractProvider implements ContractProvider {
    private final String contract;

    public FixContractProvider(String contract){
        this.contract = contract;
    }

    @Override
    public String closestContract(LocalDate time) {
       return contract;
    }
}
