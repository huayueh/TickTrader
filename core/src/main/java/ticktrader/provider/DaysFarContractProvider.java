package ticktrader.provider;

import ticktrader.dto.Settle;

import java.time.LocalDate;
import java.time.Period;

/**
 * Author: huayueh
 * Date: 2015/5/27
 */
public class DaysFarContractProvider implements ContractProvider {
    private final SettleContractProvider settleContractProvider = SettleContractProvider.getInstance();
    private final int daysFar;

    public DaysFarContractProvider(int days){
        this.daysFar = days;
    }

    @Override
    public String closestContract(LocalDate time) {
        Settle settle;
        LocalDate ceilingkey = settleContractProvider.closestDate(time);
        LocalDate nextCeilingKey = settleContractProvider.closestDate(ceilingkey.plusDays(1));
        long days = Period.between(time, ceilingkey).getDays();
        if (days > daysFar){
            return settleContractProvider.closestContract(ceilingkey);
        } else {
            return settleContractProvider.closestContract(nextCeilingKey);
        }
    }
}
