package ui;

import model.PortfolioItem;
import model.User;
import service.TradingService;
import ui.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class PortfolioFrame extends JFrame {

    public PortfolioFrame(User user) throws Exception {
        setTitle("Portfolio Performance");
        setSize(700,400);

        TradingService service = new TradingService();
        List<PortfolioItem> items = service.getPortfolio(user.getId());

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Symbol","Qty","Price","Invested","Value","P/L"},0
        );

        double totalPL = 0;

        for (PortfolioItem p : items) {
            double pl = p.getProfitLoss();
            totalPL += pl;

            model.addRow(new Object[]{
                    p.getSymbol(),
                    p.getQuantity(),
                    p.getCurrentPrice(),
                    p.getInvested(),
                    p.getMarketValue(),
                    pl
            });
        }

        JTable table = new JTable(model);
        UITheme.styleTable(table);

        JLabel summary = new JLabel(
                "Total Profit / Loss: " + String.format("%.2f", totalPL)
        );

        add(new JScrollPane(table));
        add(summary,"South");
    }
}