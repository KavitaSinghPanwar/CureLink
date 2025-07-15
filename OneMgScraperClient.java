import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class OneMgScraperClient extends JFrame {
    private JTextField medicineField;
    private JComboBox<String> cityComboBox;
    private JTextArea resultArea;
    private JButton searchButton;

    public OneMgScraperClient() {
        setTitle("1mg Best Deal Finder");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("Medicine Name:"));
        medicineField = new JTextField();
        inputPanel.add(medicineField);

        inputPanel.add(new JLabel("Select City:"));
        String[] cities = {"delhi", "mumbai", "bangalore", "chennai"};
        cityComboBox = new JComboBox<>(cities);
        inputPanel.add(cityComboBox);

        searchButton = new JButton("Search Best Deal");
        inputPanel.add(new JLabel());
        inputPanel.add(searchButton);

        panel.add(inputPanel, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Event Listener
        searchButton.addActionListener(this::handleSearch);
        add(panel);
    }

    private void handleSearch(ActionEvent event) {
        String medicine = medicineField.getText().trim();
        String city = (String) cityComboBox.getSelectedItem();

        if (medicine.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a medicine name.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        resultArea.setText("Searching for \"" + medicine + "\" in " + city + "...\n");

        new Thread(() -> {
            try {
                String apiUrl = "https://scrapyapi-bxvl.onrender.com/api/1mg-deal?medicine=" +
                        java.net.URLEncoder.encode(medicine, "UTF-8") +
                        "&city=" + java.net.URLEncoder.encode(city, "UTF-8");

                URL url = new URL(apiUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject json = new JSONObject(response.toString());

                SwingUtilities.invokeLater(() -> {
                    StringBuilder sb = new StringBuilder();
                    json.keySet().forEach(key -> sb.append(key).append(": ").append(json.getString(key)).append("\n"));
                    resultArea.setText(sb.toString());
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> resultArea.setText("Error: " + e.getMessage()));
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OneMgScraperClient().setVisible(true));
    }
}