package model;

public class User {
    private long id;
    private String username;
    private double balance;
    public User(long id,String username,double balance){
        this.id=id;
        this.username=username;
        this.balance=balance;
    }
    public long getId(){
        return id;
    }
    public String getUsername(){
        return username;
    }
    public double getBalance(){
        return balance;
    }
    public void setBalance(double balance){
        this.balance=balance;
    }
}
