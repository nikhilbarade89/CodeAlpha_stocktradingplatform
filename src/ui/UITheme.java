package ui;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UITheme {
    public static void styleButton(JButton button, Color bg){
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI",Font.PLAIN,13));
    }
    public static  void styleTable(JTable table){
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI",Font.PLAIN,13));
        table.setBackground(new Color(245,247,250));
        table.setSelectionBackground(new Color(184,207,229));
        table.setGridColor(new Color(220,220,220));
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(109, 140, 191));
        header.setFont(new Font("Segoe UI",Font.BOLD,13));
    }
}
