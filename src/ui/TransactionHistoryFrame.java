package ui;

import model.Transaction;
import model.User;
import service.TradingService;
import ui.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionHistoryFrame extends JFrame {

    public TransactionHistoryFrame(User user) throws Exception {

        setTitle("Transaction History");
        setSize(800, 400);
        setLocationRelativeTo(null);

        TradingService service = new TradingService();
        List<Transaction> transactions = service.getTransactions(user.getId());

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Date & Time", "Symbol", "Type", "Qty", "Price", "Amount"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Transaction t : transactions) {
            double amount = t.getQuantity() * t.getPrice();
            model.addRow(new Object[]{
                    t.getTime().format(fmt),
                    t.getSymbol(),
                    t.getType(),
                    t.getQuantity(),
                    t.getPrice(),
                    amount
            });
        }

        JTable table = new JTable(model);
        UITheme.styleTable(table);
        add(new JScrollPane(table));
    }
}