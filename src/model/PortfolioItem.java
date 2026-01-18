package model;

public class PortfolioItem {
    private String symbol;
    private int quantity;
    private double currentPrice;
    private double invested;
    public PortfolioItem(String symbol,int quantity,double currentPrice,double invested){
        this.symbol=symbol;
        this.quantity=quantity;
        this.currentPrice=currentPrice;
        this.invested=invested;
    }

    public PortfolioItem(String symbol, int quantity, double invested) {
        this.symbol=symbol;
        this.quantity=quantity;
        this.invested=invested;
    }

    public String getSymbol(){
        return symbol;
    }
    public int getQuantity(){return quantity;}
    public double getCurrentPrice(){
        return currentPrice;
    }
    public double getInvested(){
        return invested;
    }
    public double getMarketValue(){
        return quantity*currentPrice;
    }
    public double getProfitLoss(){
        return getMarketValue()-invested;
    }
}
