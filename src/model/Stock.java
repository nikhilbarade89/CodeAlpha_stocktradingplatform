package model;

public class Stock {
    private String symbol;
    private String company;
    private double price;

    public Stock(String symbol,String company,double price){
        this.symbol=symbol;
        this.company=company;
        this.price=price;
    }
    public String getSymbol(){return symbol;}
    public String getCompany(){return company;}
    public double getPrice(){
        return price;
    }
}
