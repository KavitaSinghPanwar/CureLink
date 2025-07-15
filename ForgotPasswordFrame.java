import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class ForgotPasswordFrame extends JFrame {
    JTextField usernameField;
    JButton resetButton, backButton;

    public ForgotPasswordFrame() {
        setTitle("Reset Password - CureLink");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Forgot Password");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel info = new JLabel("Enter your username to reset password.");
        info.setFont(new Font("SansSerif", Font.PLAIN, 14));
        info.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(250, 30));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        resetButton = new JButton("Send Reset Link");
        resetButton.setMaximumSize(new Dimension(200, 35));
        resetButton.setBackground(new Color(40, 120, 255));
        resetButton.setForeground(Color.BLACK);
        resetButton.setFocusPainted(false);
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        backButton = new JButton("Back to Login");
        backButton.setMaximumSize(new Dimension(200, 30));
        backButton.setFocusPainted(false);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalStrut(20));
        add(title);
        add(Box.createVerticalStrut(10));
        add(info);
        add(Box.createVerticalStrut(20));
        add(usernameField);
        add(Box.createVerticalStrut(15));
        add(resetButton);
        add(Box.createVerticalStrut(10));
        add(backButton);

       resetButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(ForgotPasswordFrame.this, "Please enter your username.");
        } else {
            // You can replace this logic later with email/OTP
            JOptionPane.showMessageDialog(ForgotPasswordFrame.this, "Password reset link sent to registered email (mock).");
            dispose();
            new LoginFrame();
        }
    }
});

backButton.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
        dispose();
        new LoginFrame();
    }
});


        setVisible(true);
    }
}
