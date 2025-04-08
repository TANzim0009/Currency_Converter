import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import org.json.JSONObject;

public class CurrencyConverter {
    private static Map<String, Double> exchangeRates = new HashMap<>();
    private static Map<String, String> currencyToCountry = new HashMap<>();
    private static Map<String, String> currencyCodes = new HashMap<>();
    private static List<String> allCurrencyEntries = new ArrayList<>();
    private static JLabel exchangeRateLabel;

    public static void main(String[] args) {
        initializeCountryMapping();

        JFrame frame = new JFrame("Currency Converter");
        frame.setSize(700, 550);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel fromLabel = new JLabel("From Currency:");
        fromLabel.setBounds(100, 25, 100, 30);
        frame.add(fromLabel);

        JTextField fromSearchField = new JTextField();
        fromSearchField.setBounds(100, 55, 150, 25);
        fromSearchField.setToolTipText("Search currency");
        frame.add(fromSearchField);

        JComboBox<String> fromCurrency = new JComboBox<>();
        fromCurrency.setBounds(100, 85, 150, 25);
        frame.add(fromCurrency);

        JLabel toLabel = new JLabel("To Currency:");
        toLabel.setBounds(300, 25, 100, 30);
        frame.add(toLabel);

        JTextField toSearchField = new JTextField();
        toSearchField.setBounds(300, 55, 150, 25);
        toSearchField.setToolTipText("Search currency");
        frame.add(toSearchField);

        JComboBox<String> toCurrency = new JComboBox<>();
        toCurrency.setBounds(300, 85, 150, 25);
        frame.add(toCurrency);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setBounds(100, 120, 100, 30);
        frame.add(amountLabel);

        JTextField amountField = new JTextField();
        amountField.setBounds(100, 150, 150, 25);
        frame.add(amountField);

        JButton convert = new JButton("Convert");
        convert.setBounds(220, 200, 100, 30);
        frame.add(convert);

        JLabel convertedLabel = new JLabel("Converted Amount:");
        convertedLabel.setBounds(100, 240, 130, 30);
        frame.add(convertedLabel);

        JTextField convertedField = new JTextField();
        convertedField.setBounds(100, 270, 250, 25);
        convertedField.setEditable(false);
        convertedField.setBackground(Color.WHITE);
        frame.add(convertedField);

        exchangeRateLabel = new JLabel("Current exchange rate: -");
        exchangeRateLabel.setBounds(100, 310, 350, 25);
        frame.add(exchangeRateLabel);

        JLabel statusLabel = new JLabel("Fetching exchange rates...");
        statusLabel.setBounds(100, 340, 300, 25);
        frame.add(statusLabel);

        // Method to update the exchange rate display
        Runnable updateExchangeRate = () -> {
            try {
                String fromDisplay = (String) fromCurrency.getSelectedItem();
                String toDisplay = (String) toCurrency.getSelectedItem();

                if (fromDisplay != null && toDisplay != null &&
                        currencyCodes.containsKey(fromDisplay) && currencyCodes.containsKey(toDisplay)) {
                    String fromCode = currencyCodes.get(fromDisplay);
                    String toCode = currencyCodes.get(toDisplay);

                    double rate = exchangeRates.get(toCode) / exchangeRates.get(fromCode);
                    exchangeRateLabel.setText(String.format("Current exchange rate: 1 %s = %.6f %s",
                            fromCode, rate, toCode));
                }
            } catch (Exception ex) {
                exchangeRateLabel.setText("Current exchange rate: -");
            }
        };

        // Add item listeners to update exchange rate when selection changes
        fromCurrency.addItemListener(e -> updateExchangeRate.run());
        toCurrency.addItemListener(e -> updateExchangeRate.run());

        // Load exchange rates in a separate thread
        new Thread(() -> {
            fetchExchangeRates();
            SwingUtilities.invokeLater(() -> {
                String[] preferredCurrencies = {
                        "USD", "EUR", "GBP", "JPY", "CNY", "INR", "CAD", "AUD",
                        "CHF", "SGD", "NZD", "HKD", "KRW",
                        "BRL", "IDR", "PKR", "NGN", "BDT", "RUB", "MXN", "EGP", "VND", "TRY", "PHP"
                };

                allCurrencyEntries.clear();

                for (String currency : preferredCurrencies) {
                    if (exchangeRates.containsKey(currency)) {
                        String displayText = currency + " - " + currencyToCountry.get(currency);
                        allCurrencyEntries.add(displayText);
                        currencyCodes.put(displayText, currency);
                    }
                }

                Collections.sort(allCurrencyEntries);

                for (String entry : allCurrencyEntries) {
                    fromCurrency.addItem(entry);
                    toCurrency.addItem(entry);
                }

                fromCurrency.setSelectedItem("USD - United States Dollar");
                toCurrency.setSelectedItem("BDT - Bangladeshi Taka");
                statusLabel.setText("Exchange rates loaded successfully.");

                // Update exchange rate display after loading
                updateExchangeRate.run();
            });
        }).start();

        fromSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = fromSearchField.getText().toLowerCase();
                fromCurrency.removeAllItems();
                for (String entry : allCurrencyEntries) {
                    if (entry.toLowerCase().contains(searchText)) {
                        fromCurrency.addItem(entry);
                    }
                }
                if (fromCurrency.getItemCount() > 0) {
                    fromCurrency.setSelectedIndex(0);
                }
            }
        });

        toSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = toSearchField.getText().toLowerCase();
                toCurrency.removeAllItems();
                for (String entry : allCurrencyEntries) {
                    if (entry.toLowerCase().contains(searchText)) {
                        toCurrency.addItem(entry);
                    }
                }
                if (toCurrency.getItemCount() > 0) {
                    toCurrency.setSelectedIndex(0);
                }
            }
        });

        convert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String fromDisplay = (String) fromCurrency.getSelectedItem();
                    String toDisplay = (String) toCurrency.getSelectedItem();
                    String from = currencyCodes.get(fromDisplay);
                    String to = currencyCodes.get(toDisplay);

                    double amount = Double.parseDouble(amountField.getText());
                    double convertedAmount = amount * (exchangeRates.get(to) / exchangeRates.get(from));

                    convertedField.setText(String.format("%.2f %s", convertedAmount, to));

                    // Update exchange rate display after conversion
                    updateExchangeRate.run();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input. Please enter a valid number.");
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
            JOptionPane.showMessageDialog(null, "Failed to fetch exchange rates. Using default rates.");
            // Fallback rates in case API fails
            exchangeRates.put("USD", 1.0);
            exchangeRates.put("EUR", 0.85);
            exchangeRates.put("GBP", 0.73);
            exchangeRates.put("JPY", 110.0);
            exchangeRates.put("BDT", 85.0);
        }
    }

    private static void initializeCountryMapping() {
        currencyToCountry.put("USD", "United States Dollar");
        currencyToCountry.put("EUR", "Euro");
        currencyToCountry.put("GBP", "British Pound Sterling");
        currencyToCountry.put("JPY", "Japanese Yen");
        currencyToCountry.put("CNY", "Chinese Yuan");
        currencyToCountry.put("INR", "Indian Rupee");
        currencyToCountry.put("CAD", "Canadian Dollar");
        currencyToCountry.put("AUD", "Australian Dollar");
        currencyToCountry.put("CHF", "Swiss Franc");
        currencyToCountry.put("SGD", "Singapore Dollar");
        currencyToCountry.put("NZD", "New Zealand Dollar");
        currencyToCountry.put("HKD", "Hong Kong Dollar");
        currencyToCountry.put("KRW", "South Korean Won");
        currencyToCountry.put("BRL", "Brazilian Real");
        currencyToCountry.put("IDR", "Indonesian Rupiah");
        currencyToCountry.put("PKR", "Pakistani Rupee");
        currencyToCountry.put("NGN", "Nigerian Naira");
        currencyToCountry.put("BDT", "Bangladeshi Taka");
        currencyToCountry.put("RUB", "Russian Ruble");
        currencyToCountry.put("MXN", "Mexican Peso");
        currencyToCountry.put("EGP", "Egyptian Pound");
        currencyToCountry.put("VND", "Vietnamese Dong");
        currencyToCountry.put("TRY", "Turkish Lira");
        currencyToCountry.put("PHP", "Philippine Peso");
    }
}
