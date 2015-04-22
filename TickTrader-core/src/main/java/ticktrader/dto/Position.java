package ticktrader.dto;

/**
 * Author: huayueh
 * Date: 2015/4/21
 */
public class Position {
    private final String symbol;
    private final String contract;
    private final double price;
    private final double qty;
    private final Side side;
    private final long openTime;
    private long closeTime;
    private double pnl;

    public enum Side {
        Buy,
        Sell;
    }

    public Position(String symbol, String contract, Side side, double price, double qty, long openTime) {
        this.symbol = symbol;
        this.contract = contract;
        this.side = side;
        this.price = price;
        this.qty = qty;
        this.openTime = openTime;
    }

    public double getPrice() {
        return price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getContract() {
        return contract;
    }

    public Side getSide() {
        return side;
    }

    public double getPnl() {
        return pnl;
    }

    public void setPnl(double pnl) {
        this.pnl = pnl;
    }

    public double getQty() {
        return qty;
    }

//    public void fillQuantity(double qty) {
//        qtyRemain -= qty;
//    }
//
//    public void fillAllQuantity(double price, long time) {
//        qtyRemain = 0;
//        closePrice = price;
//        closeTime = time;
//    }
}
