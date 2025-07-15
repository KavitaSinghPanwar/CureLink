import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class HealthTestBookingUI {
    private List<Test> allTests = new ArrayList<>();
    private List<Test> cart = new ArrayList<>();
    private JPanel centerPanel;
    private JTextField searchField;
    private JComboBox<String> priceFilter, paramFilter;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HealthTestBookingUI().createUI());
    }

    public void createUI() {
        JFrame frame = new JFrame("CureLink - Book Your Health Tests");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 900);
        frame.setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(59, 89, 152));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("🩺 CureLink Diagnostics");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        topBar.add(title, BorderLayout.WEST);

        JButton cartButton = new JButton("🛒 View Cart");
        cartButton.addActionListener(e -> showCartWindow());
        topBar.add(cartButton, BorderLayout.EAST);

        frame.add(topBar, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> filterTests());

        priceFilter = new JComboBox<>(new String[] {"All Prices", "< ₹200", "₹200 - ₹500", "> ₹500"});
        paramFilter = new JComboBox<>(new String[] {"All Parameters", "1-5", "6-15", "> 15"});

        searchPanel.add(new JLabel("Search Test:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(new JLabel("Price:"));
        searchPanel.add(priceFilter);
        searchPanel.add(new JLabel("Parameters:"));
        searchPanel.add(paramFilter);

        frame.add(searchPanel, BorderLayout.SOUTH);

        centerPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        frame.add(new JScrollPane(centerPanel), BorderLayout.CENTER);

        loadStaticTests();
        renderTests(allTests);

        frame.setVisible(true);
    }

    private void loadStaticTests() {
        allTests.add(new Test(1, "Thyroid Profile", "10 hours", 3, 299, 650, 54, "Diagnose thyroid issues."));
        allTests.add(new Test(2, "Glucose Fasting", "9 hours", 1, 99, 160, 38, "Fasting sugar level test."));
        allTests.add(new Test(3, "HBA1C Test", "10 hours", 2, 299, 440, 32, "Avg. sugar levels in 3 months."));
        allTests.add(new Test(4, "CBC Test", "9 hours", 26, 299, 450, 33, "Blood component check."));
        allTests.add(new Test(5, "Liver Function Test", "10 hours", 12, 399, 800, 50, "Check liver health."));
        // Add more tests similarly...
    }

    private void renderTests(List<Test> tests) {
        centerPanel.removeAll();
        for (Test test : tests) {
            centerPanel.add(createTestCard(test));
        }
        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private JPanel createTestCard(Test test) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel name = new JLabel(test.name);
        name.setFont(new Font("SansSerif", Font.BOLD, 16));
        name.setForeground(new Color(40, 40, 90));

        JLabel report = new JLabel("Reports in: " + test.reportTime);
        JLabel params = new JLabel("Parameters: " + test.parameters);

        JTextArea desc = new JTextArea(test.description);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setEditable(false);
        desc.setBackground(Color.WHITE);
        desc.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel deal = new JLabel("₹" + test.price + "  (₹" + test.oldPrice + ")  " + test.discount + "% Off");
        deal.setForeground(Color.RED);

        JButton book = new JButton("Book Now");
        book.setBackground(new Color(0, 123, 255));
        book.setForeground(Color.BLACK);
        book.setFocusPainted(false);
        book.addActionListener(e -> {
            cart.add(test);
            JOptionPane.showMessageDialog(null, test.name + " added to cart.");
        });

        card.add(name);
        card.add(report);
        card.add(params);
        card.add(desc);
        card.add(deal);
        card.add(book);
        return card;
    }

    private void filterTests() {
        String keyword = searchField.getText().toLowerCase();
        String priceRange = priceFilter.getSelectedItem().toString();
        String paramRange = paramFilter.getSelectedItem().toString();

        List<Test> filtered = allTests.stream().filter(t ->
            t.name.toLowerCase().contains(keyword) &&
            (priceRange.equals("All Prices") ||
             (priceRange.equals("< ₹200") && t.price < 200) ||
             (priceRange.equals("₹200 - ₹500") && t.price >= 200 && t.price <= 500) ||
             (priceRange.equals("> ₹500") && t.price > 500)) &&
            (paramRange.equals("All Parameters") ||
             (paramRange.equals("1-5") && t.parameters <= 5) ||
             (paramRange.equals("6-15") && t.parameters >= 6 && t.parameters <= 15) ||
             (paramRange.equals("> 15") && t.parameters > 15))
        ).collect(Collectors.toList());

        renderTests(filtered);
    }

    private void showCartWindow() {
        JFrame cartFrame = new JFrame("Your Cart");
        cartFrame.setSize(400, 500);
        cartFrame.setLayout(new BorderLayout());

        JPanel cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));

        int total = 0;
        for (Test t : cart) {
            cartPanel.add(new JLabel(t.name + " - ₹" + t.price));
            total += t.price;
        }

        JLabel totalLabel = new JLabel("Total Amount: ₹" + total);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));

        JButton payButton = new JButton("Make Payment");
        final int finalTotal = total;
        payButton.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField phoneField = new JTextField();
            Object[] fields = {
                "Enter your name:", nameField,
                "Enter your phone:", phoneField
            };
            int option = JOptionPane.showConfirmDialog(cartFrame, fields, "Enter Details", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String name = nameField.getText();
                String phone = phoneField.getText();

                BookingHandler.saveBooking(cart, finalTotal, name, phone);

                JOptionPane.showMessageDialog(cartFrame, "Payment Successful! Booking Confirmed.\nTotal: ₹" + finalTotal);
                cart.clear();
                cartFrame.dispose();
            }
        });

        cartFrame.add(new JScrollPane(cartPanel), BorderLayout.CENTER);
        cartFrame.add(totalLabel, BorderLayout.NORTH);
        cartFrame.add(payButton, BorderLayout.SOUTH);

        cartFrame.setVisible(true);
    }
}