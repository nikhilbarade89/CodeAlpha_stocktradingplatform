package db;
import java.sql.*;

public class DatabaseManager{
    private static final String URL="jdbc:h2:./stockdb";
    private static final String USER="sa";
    private static final String PASS="";
    static{
        try{
            createTables();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL,USER,PASS);
    }
    private static void createTables() throws SQLException{
        try(Connection c=getConnection();
        Statement s=c.createStatement()){
            s.execute("""
CREATE TABLE IF NOT EXISTS users (id IDENTITY PRIMARY KEY,username VARCHAR(50) UNIQUE, balance DOUBLE)""");
            s.execute("""
CREATE TABLE IF NOT EXISTS stocks (symbol VARCHAR(10) PRIMARY KEY,company VARCHAR(100),price DOUBLE)""");
            s.execute("""
CREATE TABLE IF NOT EXISTS transactions (id IDENTITY PRIMARY KEY,user_id BIGINT,symbol VARCHAR(10),quantity INT,price DOUBLE,
    type VARCHAR(10),time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)""");
            s.execute("""
CREATE TABLE IF NOT EXISTS portfolio(user_id BIGINT, symbol VARCHAR(10),quantity INT,PRIMARY KEY(user_id,symbol))""");
            s.execute("""
MERGE INTO stocks KEY(symbol) VALUES('AAPL','Apple',175),('GOOG','Google',2800),('TSLA','Tesla',750)""");
        }
    }
}
