import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONObject;

public class CurrencyConverter {
    private static Map<String, Double> exchangeRates = new HashMap<>();

    public static void main(String[] args) {
        JFrame frame = new JFrame("Universal Currency Converter");
        frame.setSize(700, 500);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel fromLabel = new JLabel("From Currency:");
        fromLabel.setBounds(5, 10, 100, 20);
        frame.add(fromLabel);

        JComboBox<String> fromCurrency = new JComboBox<>();
        fromCurrency.setBounds(120, 10, 150, 20);
        frame.add(fromCurrency);

        JLabel toLabel = new JLabel("To Currency:");
        toLabel.setBounds(5, 40, 100, 20);
        frame.add(toLabel);

        JComboBox<String> toCurrency = new JComboBox<>();
        toCurrency.setBounds(120, 40, 150, 20);
        frame.add(toCurrency);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(5, 70, 100, 20);
        frame.add(amountLabel);

        JTextField amountField = new JTextField();
        amountField.setBounds(120, 70, 150, 20);
        frame.add(amountField);

        JButton convert = new JButton("Convert");
        convert.setBounds(100, 100, 100, 30);
        frame.add(convert);

        JLabel resultLabel = new JLabel("Converted amount will appear here.");
        resultLabel.setBounds(5, 140, 400, 20);
        frame.add(resultLabel);

        JLabel rateLabel = new JLabel("Fetching exchange rates...");
        rateLabel.setBounds(5, 170, 400, 20);
        frame.add(rateLabel);

        new Thread(() -> {
            fetchExchangeRates();
            SwingUtilities.invokeLater(() -> {
                for (String currency : exchangeRates.keySet()) {
                    fromCurrency.addItem(currency);
                    toCurrency.addItem(currency);
                }
                fromCurrency.setSelectedItem("USD");
                toCurrency.setSelectedItem("BDT");
                rateLabel.setText("Exchange rates loaded.");
            });
        }).start();

        convert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String from = (String) fromCurrency.getSelectedItem();
                    String to = (String) toCurrency.getSelectedItem();
                    double amount = Double.parseDouble(amountField.getText());
                    double fromRate = exchangeRates.get(from);
                    double toRate = exchangeRates.get(to);
                    double convertedAmount = (amount / fromRate) * toRate;
                    resultLabel.setText("Converted Amount: " + String.format("%.2f", convertedAmount) + " " + to);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input .");
                }
            }
        });

        frame.setVisible(true);
    }

    private static void fetchExchangeRates() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.exchangerate-api.com/v4/latest/USD").openConnection();
            conn.setRequestMethod("GET");
            Scanner scanner = new Scanner(new InputStreamReader(conn.getInputStream()));
            String response = scanner.useDelimiter("\\A").next();
            scanner.close();

            JSONObject rates = new JSONObject(response).getJSONObject("rates");
            for (String key : rates.keySet()) {
                exchangeRates.put(key, rates.getDouble(key));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to fetch exchange rates.");
        }
    }
}
