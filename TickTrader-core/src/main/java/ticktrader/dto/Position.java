package ticktrader.dto;

/**
 * Author: huayueh
 * Date: 2015/4/21
 */
public class Position {
    private final String symbol;
    private final String contract;
    private final double price;
    private double pnl;

    public Position(String symbol, String contract, double price) {
        this.symbol = symbol;
        this.contract = contract;
        this.price = price;
    }
}
