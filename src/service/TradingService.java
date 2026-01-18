package service;
import db.DatabaseManager;
import model.User;
import model.Stock;
import java.sql.*;
import java.util.*;
import model.PortfolioItem;
import model.Transaction;

public class TradingService {
    public User login(String username)throws Exception {
        try (Connection c = DatabaseManager.getConnection()) {
            PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getLong("id"), username, rs.getDouble("balance"));
            }
            ps = c.prepareStatement("INSERT INTO users(username,balance) VALUES (?,100000)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            rs.next();
            return new User(rs.getLong(1), username, 100000);
        }
    }
    public List<Stock> getMarketData() throws Exception{
        List<Stock> list=new ArrayList<>();
        try(Connection c= DatabaseManager.getConnection();
        Statement s=c.createStatement();
        ResultSet rs=s.executeQuery("SELECT * FROM stocks")){
            while(rs.next()){
                list.add(new Stock(rs.getString("symbol"),rs.getString("company"),rs.getDouble("price")));
            }
        }
        return list;
    }
    public List<PortfolioItem> getPortfolio(long userId) throws Exception {

        List<PortfolioItem> list = new ArrayList<>();

        try (Connection c = DatabaseManager.getConnection()) {

            PreparedStatement ps = c.prepareStatement("""
            SELECT  p.symbol,p.quantity,
                s.price AS current_price, COALESCE( (SELECT SUM(t.quantity * t.price)
                     FROM transactions t
                     WHERE t.user_id = ?
                       AND t.symbol = p.symbol
                       AND t.type = 'BUY'), 0
                )
                 - COALESCE(
                    (SELECT SUM(t.quantity * t.price)
                     FROM transactions t
                     WHERE t.user_id = ?
                       AND t.symbol = p.symbol
                       AND t.type = 'SELL'), 0
                ) AS invested

            FROM portfolio p
            JOIN stocks s ON p.symbol = s.symbol
            WHERE p.user_id = ?
        """);

            ps.setLong(1, userId);
            ps.setLong(2, userId);
            ps.setLong(3, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new PortfolioItem(
                        rs.getString("symbol"),
                        rs.getInt("quantity"),
                        rs.getDouble("current_price"),
                        rs.getDouble("invested")
                ));
            }
        }

        return list;

    }
    public List<Transaction> getTransactions(long userId) throws Exception {

        List<Transaction> list = new ArrayList<>();

        try (Connection c = DatabaseManager.getConnection()) {

            PreparedStatement ps = c.prepareStatement("""
            SELECT symbol, quantity, price, type, time
            FROM transactions
            WHERE user_id = ?
            ORDER BY time DESC
        """);

            ps.setLong(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Transaction(
                        rs.getString("symbol"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getString("type"),
                        rs.getTimestamp("time").toLocalDateTime()
                ));
            }
        }

        return list;
    }

    public void buy(User user, String symbol, int qty) throws Exception {

        try (Connection c = DatabaseManager.getConnection()) {
            c.setAutoCommit(false);


            PreparedStatement psPrice =
                    c.prepareStatement("SELECT price FROM stocks WHERE symbol=?");
            psPrice.setString(1, symbol);
            ResultSet rs = psPrice.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("Stock not found");
            }

            double price = rs.getDouble("price");
            double cost = price * qty;

            if (user.getBalance() < cost) {
                throw new RuntimeException("Insufficient balance");
            }


            PreparedStatement psCheck =
                    c.prepareStatement(
                            "SELECT quantity FROM portfolio WHERE user_id=? AND symbol=?"
                    );
            psCheck.setLong(1, user.getId());
            psCheck.setString(2, symbol);

            rs = psCheck.executeQuery();

            if (rs.next()) {

                PreparedStatement psUpdate =
                        c.prepareStatement(
                                "UPDATE portfolio SET quantity = quantity + ? WHERE user_id=? AND symbol=?"
                        );
                psUpdate.setInt(1, qty);
                psUpdate.setLong(2, user.getId());
                psUpdate.setString(3, symbol);
                psUpdate.executeUpdate();
            } else {

                PreparedStatement psInsert =
                        c.prepareStatement(
                                "INSERT INTO portfolio(user_id, symbol, quantity) VALUES (?,?,?)"
                        );
                psInsert.setLong(1, user.getId());
                psInsert.setString(2, symbol);
                psInsert.setInt(3, qty);
                psInsert.executeUpdate();
            }


            PreparedStatement psBalance =
                    c.prepareStatement(
                            "UPDATE users SET balance = balance - ? WHERE id=?"
                    );
            psBalance.setDouble(1, cost);
            psBalance.setLong(2, user.getId());
            psBalance.executeUpdate();


            PreparedStatement psTxn =
                    c.prepareStatement("""
                    INSERT INTO transactions(user_id, symbol, quantity, price, type)
                    VALUES (?,?,?,?, 'BUY')
                """);
            psTxn.setLong(1, user.getId());
            psTxn.setString(2, symbol);
            psTxn.setInt(3, qty);
            psTxn.setDouble(4, price);
            psTxn.executeUpdate();

            c.commit();


            user.setBalance(user.getBalance() - cost);
        }
    }
    public void sell(User user, String symbol, int qty) throws Exception {

        try (Connection c = DatabaseManager.getConnection()) {
            c.setAutoCommit(false);

            // 1️⃣ Check portfolio quantity
            PreparedStatement psCheck =
                    c.prepareStatement(
                            "SELECT quantity FROM portfolio WHERE user_id=? AND symbol=?"
                    );
            psCheck.setLong(1, user.getId());
            psCheck.setString(2, symbol);

            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("You do not own this stock");
            }

            int ownedQty = rs.getInt("quantity");
            if (qty > ownedQty) {
                throw new RuntimeException("Insufficient stock quantity");
            }

            // 2️⃣ Get current stock price
            PreparedStatement psPrice =
                    c.prepareStatement("SELECT price FROM stocks WHERE symbol=?");
            psPrice.setString(1, symbol);
            rs = psPrice.executeQuery();
            rs.next();

            double price = rs.getDouble("price");
            double proceeds = price * qty;

            // 3️⃣ Update portfolio
            if (qty == ownedQty) {
                // remove stock completely
                PreparedStatement psDelete =
                        c.prepareStatement(
                                "DELETE FROM portfolio WHERE user_id=? AND symbol=?"
                        );
                psDelete.setLong(1, user.getId());
                psDelete.setString(2, symbol);
                psDelete.executeUpdate();
            } else {
                // reduce quantity
                PreparedStatement psUpdate =
                        c.prepareStatement(
                                "UPDATE portfolio SET quantity = quantity - ? WHERE user_id=? AND symbol=?"
                        );
                psUpdate.setInt(1, qty);
                psUpdate.setLong(2, user.getId());
                psUpdate.setString(3, symbol);
                psUpdate.executeUpdate();
            }

            // 4️⃣ Update user balance
            PreparedStatement psBalance =
                    c.prepareStatement(
                            "UPDATE users SET balance = balance + ? WHERE id=?"
                    );
            psBalance.setDouble(1, proceeds);
            psBalance.setLong(2, user.getId());
            psBalance.executeUpdate();

            // 5️⃣ Record transaction
            PreparedStatement psTxn =
                    c.prepareStatement("""
                    INSERT INTO transactions(user_id, symbol, quantity, price, type)
                    VALUES (?,?,?,?, 'SELL')
                """);
            psTxn.setLong(1, user.getId());
            psTxn.setString(2, symbol);
            psTxn.setInt(3, qty);
            psTxn.setDouble(4, price);
            psTxn.executeUpdate();

            c.commit();

            // 6️⃣ Update in-memory balance
            user.setBalance(user.getBalance() + proceeds);
        }

    }}