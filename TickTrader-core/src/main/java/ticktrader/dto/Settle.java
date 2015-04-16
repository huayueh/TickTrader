package ticktrader.dto;

/**
 * Author: huayueh
 * Date: 2015/4/16
 */
public class Settle {
    private String contract;
    private double price;

    public Settle(String contract, double price) {
        this.contract = contract;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(contract).append(",");
        sb.append(price);
        return sb.toString();
    }
}
