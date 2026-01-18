package ui;
import service.TradingService;
import model.User;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    public LoginFrame(){
        setTitle("STOCK TRADING PLATFORM");
        setSize(450,175);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10,10));
        JTextField userField = new JTextField();
        userField.setFont(new Font("Segoe UI",Font.PLAIN,14));
        userField.setBorder(BorderFactory.createTitledBorder("Username"));
        JButton login = new JButton("Login");
        UITheme.styleButton(login,new Color(2, 16, 41));
        login.setPreferredSize(new Dimension(200,32));
        login.setFont(new Font("Segoe UI",Font.BOLD,12));
        login.addActionListener(e ->{
            try{
                TradingService service = new TradingService();
                User user = service.login(userField.getText());
                new DashboardFrame(user).setVisible(true);
                dispose();
            } catch (Exception ex){
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }
        });
        JPanel centre = new JPanel(new BorderLayout());
        centre.setBorder(BorderFactory.createEmptyBorder(20,40,10,40));
        centre.add(userField,BorderLayout.CENTER);
        JPanel btnpanel= new JPanel();
        btnpanel.add(login);
        add(centre,BorderLayout.CENTER);
        add(btnpanel,BorderLayout.SOUTH);
    }
}
