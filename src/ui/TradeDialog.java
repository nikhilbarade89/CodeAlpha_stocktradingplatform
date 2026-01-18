package ui;
import model.User;
import service.TradingService;
import javax.swing.*;

public class TradeDialog extends JDialog {
    public TradeDialog(JFrame parent,User user, TradingService service,String symbol){
        super(parent,"Buy"+symbol,true);
        setSize(300,200);
        JTextField qty= new JTextField();
        JButton buy= new JButton("Confirm Buy");
        buy.addActionListener(e ->{
            try{
                service.buy(user,symbol,Integer.parseInt(qty.getText()));
                JOptionPane.showMessageDialog(this,"Trade successfull!");
                dispose();
            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,ex.getMessage());
            }
        });
        add(qty,"North");
        add(buy,"South");
        setVisible(true);
    }
}
