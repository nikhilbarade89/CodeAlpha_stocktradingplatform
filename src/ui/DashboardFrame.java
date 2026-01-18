package ui;

import model.Stock;
import model.User;
import service.TradingService;
import ui.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private JTable stockTable;
    private DefaultTableModel tableModel;
    private TradingService service;
    private User user;
    private JLabel balanceLabel;

    public DashboardFrame(User user) throws Exception {
        this.user = user;
        this.service = new TradingService();

        setTitle("Stock Trading Dashboard - " + user.getUsername());
        setSize(800, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🔵 TOP PANEL
        balanceLabel = new JLabel(
                "Available Balance: ₹ " + String.format("%.2f", user.getBalance())
        );
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 16));
        balanceLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(balanceLabel, BorderLayout.NORTH);

        // 📊 TABLE
        tableModel = new DefaultTableModel(
                new Object[]{"Symbol", "Company", "Price"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        stockTable = new JTable(tableModel);
        loadMarketData();


        add(new JScrollPane(stockTable), BorderLayout.CENTER);
        UITheme.styleTable(stockTable);

        // 🔘 BUTTONS
        JButton buyButton = new JButton("Buy");
        JButton sellButton = new JButton("Sell");
        JButton portfolioButton = new JButton("View Portfolio");
        JButton historybutton= new JButton("Transaction History");
        UITheme.styleButton(buyButton,new Color(0,153,76));
        UITheme.styleButton(sellButton, new Color(204,0,0));
        UITheme.styleButton(portfolioButton,new Color(0,102,204));
        UITheme.styleButton(historybutton, new Color(64,64,64));

        buyButton.addActionListener(e -> buyStock());
        sellButton.addActionListener(e -> sellStock());
        portfolioButton.addActionListener(e -> viewPortfolio());
        historybutton.addActionListener(e-> {
            try{
                new TransactionHistoryFrame(user).setVisible(true);
            }catch (Exception ex){
                JOptionPane.showMessageDialog(this,ex.getMessage()
                );
            }
        });

        JPanel bottom = new JPanel();
        bottom.add(buyButton);
        bottom.add(sellButton);
        bottom.add(portfolioButton);
        bottom.add(historybutton);

        add(bottom, BorderLayout.SOUTH);
    }

    // 📈 Load Market Data
    private void loadMarketData() throws Exception {
        tableModel.setRowCount(0);
        List<Stock> stocks = service.getMarketData();

        for (Stock s : stocks) {
            tableModel.addRow(new Object[]{
                    s.getSymbol(),
                    s.getCompany(),
                    s.getPrice()
            });
        }
    }

    // 🟢 BUY
    private void buyStock() {
        int row = stockTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a stock first");
            return;
        }

        String symbol = tableModel.getValueAt(row, 0).toString();
        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity to BUY:");

        if (qtyStr == null || qtyStr.isEmpty()) return;

        try {
            int qty = Integer.parseInt(qtyStr);
            service.buy(user, symbol, qty);

            JOptionPane.showMessageDialog(this, "Stock purchased successfully");
            refreshBalance();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // 🔴 SELL
    private void sellStock() {
        int row = stockTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a stock first");
            return;
        }

        String symbol = tableModel.getValueAt(row, 0).toString();
        String qtyStr = JOptionPane.showInputDialog(this, "Enter quantity to SELL:");

        if (qtyStr == null || qtyStr.isEmpty()) return;

        try {
            int qty = Integer.parseInt(qtyStr);
            service.sell(user, symbol, qty);

            JOptionPane.showMessageDialog(this, "Stock sold successfully");
            refreshBalance();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    // 📂 Portfolio
    private void viewPortfolio() {
        try {
            new PortfolioFrame(user).setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void refreshBalance() {
        balanceLabel.setText(
                "Available Balance: ₹ " + String.format("%.2f", user.getBalance())
        );
    }
}