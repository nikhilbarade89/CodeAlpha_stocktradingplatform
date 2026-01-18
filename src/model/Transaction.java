package model;

import java.time.LocalDateTime;

public class Transaction {

    private String symbol;
    private int quantity;
    private double price;
    private String type;
    private LocalDateTime time;

    public Transaction(String symbol, int quantity, double price, String type, LocalDateTime time) {
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.time = time;
    }

    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getType() { return type; }
    public LocalDateTime getTime() { return time; }
}