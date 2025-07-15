import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {
    public DashboardFrame(String role, String username) {
        setTitle(role + " Dashboard");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Welcome, " + username + " (" + role + ")", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        add(label, BorderLayout.CENTER);

        setVisible(true);
    }
}
