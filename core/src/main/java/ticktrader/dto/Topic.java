package ticktrader.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import ticktrader.provider.ContractProvider;
import ticktrader.provider.SettleContractProvider;

/**
 * Author: huayueh
 * Date: 2015/4/27
 */
public class Topic {
    public static final String ANY = "ANY";
    public static final String CURRENT = "CURRENT";
    public static final int ANY_PRICE = 0;
    //TODO: DI
    private static final ContractProvider contractProvider = SettleContractProvider.getInstance();
    private final String symbol;
    private final String contract;
    private final int exPrice;
    private final FutureType futureType;

    public Topic(String symbol, String contract, int exPrice, FutureType futureType) {
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

    public static Topic get(Tick tick) {
        String contract = contractProvider.closestContract(tick.getTime().toLocalDate());
        if (tick.getContract().equals(contract)){
            return new Topic(tick.getSymbol(), CURRENT, tick.getExPrice(), tick.getFutureType());
        }
        return new Topic(tick.getSymbol(), tick.getContract(), tick.getExPrice(), tick.getFutureType());
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
        if (!(obj instanceof Topic))
            return false;

        Topic topic = (Topic) obj;
        EqualsBuilder builder = new EqualsBuilder().
                append(futureType, topic.futureType);

        if (exPrice != ANY_PRICE && topic.exPrice != ANY_PRICE)
            builder.append(exPrice, topic.exPrice);

        if (!symbol.equals(ANY) && !topic.symbol.equals(ANY))
            builder.append(symbol, topic.symbol);

        if (!contract.equals(ANY) && !topic.contract.equals(ANY))
            builder.append(contract, topic.contract);

        return builder.build();
    }
}
