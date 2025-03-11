import javax.swing.*;
import java.awt.*;
public class CurrencyConverter {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Currency Converter");
        frame.setSize(300, 200);
        frame.setLayout(new GridLayout(2,2,5,5));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField bdtField = new JTextField(10);
        JTextField dollarField = new JTextField(10);

        frame.add(new JLabel("BDT:"));
        frame.add(bdtField);
        frame.add(new JLabel("Dollars:"));
        frame.add(dollarField);
        
        frame.setVisible(true);
    }
}
