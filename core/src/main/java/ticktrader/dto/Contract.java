package ticktrader.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import ticktrader.provider.ContractProvider;
import ticktrader.provider.SettleContractProvider;

/**
 * Author: huayueh
 * Date: 2015/4/27
 */
public class Contract {
    public static final String ANY = "ANY";
    public static final String CURRENT = "CURRENT";
    public static final int ANY_PRICE = 0;
    //TODO: DI
    private static final ContractProvider contractProvider = SettleContractProvider.getInstance();
    private final String symbol;
    private final String contract;
    private final int exPrice;
    private final FutureType futureType;

    public Contract(String symbol, String contract, int exPrice, FutureType futureType) {
        this.symbol = symbol;
        this.contract = contract;
        this.exPrice = exPrice;
        this.futureType = futureType;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getContract() {
        return contract;
    }

    public int getExPrice() {
        return exPrice;
    }

    public FutureType getFutureType() {
        return futureType;
    }

    public static Contract getCurrent(Tick tick) {
        String contract = contractProvider.closestContract(tick.getTime().toLocalDate());
        if (tick.getContract().equals(contract)){
            return new Contract(tick.getSymbol(), CURRENT, tick.getExPrice(), tick.getFutureType());
        }
        return new Contract(tick.getSymbol(), tick.getContract(), tick.getExPrice(), tick.getFutureType());
    }

    public static Contract get(Tick tick) {
        return new Contract(tick.getSymbol(), tick.getContract(), tick.getExPrice(), tick.getFutureType());
    }

    public static Contract get(Position position) {
        return new Contract(position.getSymbol(), position.getContract(), position.getExPrice(), position.getFutureType());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                append(symbol).
                append(contract).
                append(exPrice).
                append(futureType).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Contract))
            return false;

        Contract contract = (Contract) obj;
        EqualsBuilder builder = new EqualsBuilder().
                append(futureType, contract.futureType);

        if (exPrice != ANY_PRICE && contract.exPrice != ANY_PRICE)
            builder.append(exPrice, contract.exPrice);

        if (!symbol.equals(ANY) && !contract.symbol.equals(ANY))
            builder.append(symbol, contract.symbol);

        if (!this.contract.equals(ANY) && !contract.contract.equals(ANY))
            builder.append(this.contract, contract.contract);

        return builder.build();
    }
}
