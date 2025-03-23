import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        JRadioButton bdtToDollar = new JRadioButton("BDT to Dollar", true);
        bdtToDollar.setBounds(20, 60, 120, 25);
        frame.add(bdtToDollar);

        JRadioButton dollarToBdt = new JRadioButton("Dollar to BDT");
        dollarToBdt.setBounds(150, 60, 120, 25);
        frame.add(dollarToBdt);

        ButtonGroup conversionGroup = new ButtonGroup();
        conversionGroup.add(bdtToDollar);
        conversionGroup.add(dollarToBdt);

        JButton convert = new JButton("Convert");
        convert.setBounds(100, 90, 100, 30);
        frame.add(convert);

        convert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (bdtToDollar.isSelected()) {
                        double bdt = Double.parseDouble(bdtField.getText());
                        double dollars = bdt / 124.0; // Assuming 1 USD = 110 BDT
                        dollarField.setText(String.format("%.2f", dollars));
                    } else if (dollarToBdt.isSelected()) {
                        double dollars = Double.parseDouble(dollarField.getText());
                        double bdt = dollars * 124.0;
                        bdtField.setText(String.format("%.2f", bdt));
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input! Enter a valid number.");
                }
            }
        });


        frame.setVisible(true);
    }
}
