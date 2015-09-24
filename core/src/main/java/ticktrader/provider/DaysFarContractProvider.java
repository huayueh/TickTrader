package ticktrader.provider;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

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
        LocalDate ceilingkey = settleContractProvider.closestDate(time);
        LocalDate nextCeilingKey = settleContractProvider.closestDate(ceilingkey.plusDays(1));
//        int days = Period.between(time, ceilingkey).getDays();
        long days = ChronoUnit.DAYS.between(time, ceilingkey);
        if (days > daysFar){
            return settleContractProvider.exactlyContractDay(ceilingkey);
        } else {
            return settleContractProvider.exactlyContractDay(nextCeilingKey);
        }
    }
}
