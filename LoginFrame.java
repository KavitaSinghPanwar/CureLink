import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame implements ActionListener {
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginBtn, signupBtn;
    JLabel forgotPassword;
    JComboBox<String> roleDropdown;

    public LoginFrame() {
        setTitle("CureLink - Login");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        // LEFT PANEL
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(40, 120, 255));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("➕ CureLink");
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel welcome = new JLabel("Welcome to CureLink");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        welcome.setForeground(Color.WHITE);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(logo);
        leftPanel.add(Box.createVerticalStrut(30));
        leftPanel.add(welcome);
        leftPanel.add(Box.createVerticalGlue());

        // Image (optional)
        ImageIcon icon = new ImageIcon("health_icon_resized.jpeg");
        JLabel imageLabel = new JLabel(icon);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(imageLabel);
        leftPanel.add(Box.createVerticalStrut(10));

        // RIGHT PANEL
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(Box.createVerticalStrut(30));

        JLabel loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(loginLabel);
        rightPanel.add(Box.createVerticalStrut(50));

        roleDropdown = new JComboBox<>(new String[]{"Doctor", "Patient", "Receptionist"});
        roleDropdown.setMaximumSize(new Dimension(250, 30));
        roleDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(roleDropdown);
        rightPanel.add(Box.createVerticalStrut(15));

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(250, 30));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(usernameField);
        rightPanel.add(Box.createVerticalStrut(15));

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(250, 30));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(passwordField);
        rightPanel.add(Box.createVerticalStrut(10));

        forgotPassword = new JLabel("<HTML><U>Forgot password?</U></HTML>");
        forgotPassword.setForeground(Color.BLUE.darker());
        forgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPassword.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 0));
        forgotPanel.setOpaque(false);
        forgotPanel.add(forgotPassword);
        rightPanel.add(forgotPanel);
        rightPanel.add(Box.createVerticalStrut(30));

        loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(40, 120, 255));
        loginBtn.setForeground(Color.BLACK);
        loginBtn.setFocusPainted(false);
        loginBtn.setMaximumSize(new Dimension(250, 35));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(loginBtn);
        rightPanel.add(Box.createVerticalStrut(55));

        JPanel signUpPanel = new JPanel();
        signUpPanel.setBackground(Color.WHITE);
        JLabel noAcc = new JLabel("Don't have an account?");
        signupBtn = new JButton("Sign up");
        signupBtn.setBorderPainted(false);
        signupBtn.setBackground(Color.WHITE);
        signupBtn.setForeground(new Color(40, 120, 255));
        signupBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUpPanel.add(noAcc);
        signUpPanel.add(signupBtn);
        rightPanel.add(signUpPanel);
        rightPanel.add(Box.createVerticalGlue());

        // Add Panels
        add(leftPanel);
        add(rightPanel);

        // Button Listeners
        loginBtn.addActionListener(this);

        signupBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new RegisterFrame(); // open registration window
            }
        });

        forgotPassword.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new ForgotPasswordFrame();
            }
        });

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleDropdown.getSelectedItem();

        try {
            Connection conn = DB.getConnection(); // Your DB helper class
            String query = "";

            if (role.equalsIgnoreCase("Patient")) {
                query = "SELECT * FROM patients WHERE username=? AND password=?";
            } else if (role.equalsIgnoreCase("Doctor")) {
                query = "SELECT * FROM doctors WHERE username=? AND password=?";
            } else if (role.equalsIgnoreCase("Receptionist")) {
                query = "SELECT * FROM receptionists WHERE username=? AND password=?";
            }

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful");
                dispose();

                if (role.equalsIgnoreCase("Patient")) {
                    new CureLinkDashboardUI(username); // Open PatientDashboard
                }else if(role.equalsIgnoreCase("Doctor")){
                    new DoctorDashboard(username); // Doctor/Receptionist
                } 
                else {
                    new DashboardFrame(role, username); // Doctor/Receptionist
                }

            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials or role.");
            }

            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login failed due to error.");
        }
    }
}
