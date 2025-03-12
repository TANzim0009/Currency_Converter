import javax.swing.*;
import java.awt.*;
public class CurrencyConverter {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Currency Converter");
        frame.setSize(700, 500);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel label1 = new JLabel("BDT:");
        label1.setBounds(5, 10, 50, 20);
        frame.add(label1);

        JTextField bdtField = new JTextField();
        bdtField.setBounds(85, 10, 150, 20);
        frame.add(bdtField);

        JLabel label2 = new JLabel("DOLLAR:");
        label2.setBounds(5, 35, 80, 20);
        frame.add(label2);

        JTextField dollarField = new JTextField();
        dollarField.setBounds(85, 35, 150, 20);
        frame.add(dollarField);
        
        frame.setVisible(true);
    }
}
