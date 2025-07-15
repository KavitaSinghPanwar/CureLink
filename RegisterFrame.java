import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterFrame extends JFrame implements ActionListener {
    JTextField firstNameField, lastNameField, usernameField;
    JPasswordField passwordField, confirmPasswordField;
    JComboBox<String> roleDropdown;
    JButton registerBtn;

    public RegisterFrame() {
        setTitle("CureLink - Register");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(Box.createVerticalStrut(15));
        add(title);
        add(Box.createVerticalStrut(20));

        firstNameField = new JTextField();
        firstNameField.setMaximumSize(new Dimension(250, 30));
        firstNameField.setBorder(BorderFactory.createTitledBorder("First Name"));
        firstNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(firstNameField);
        add(Box.createVerticalStrut(10));

        lastNameField = new JTextField();
        lastNameField.setMaximumSize(new Dimension(250, 30));
        lastNameField.setBorder(BorderFactory.createTitledBorder("Last Name"));
        lastNameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lastNameField);
        add(Box.createVerticalStrut(10));

        usernameField = new JTextField();
        usernameField.setMaximumSize(new Dimension(250, 30));
        usernameField.setBorder(BorderFactory.createTitledBorder("Username"));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(usernameField);
        add(Box.createVerticalStrut(10));

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(250, 30));
        passwordField.setBorder(BorderFactory.createTitledBorder("Password"));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(passwordField);
        add(Box.createVerticalStrut(10));

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setMaximumSize(new Dimension(250, 30));
        confirmPasswordField.setBorder(BorderFactory.createTitledBorder("Confirm Password"));
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(confirmPasswordField);
        add(Box.createVerticalStrut(10));

        roleDropdown = new JComboBox<>(new String[]{"Doctor", "Patient","Receptionist"});
        roleDropdown.setMaximumSize(new Dimension(250, 30));
        roleDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(roleDropdown);
        add(Box.createVerticalStrut(15));
        
        

        registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(40, 120, 255));
        registerBtn.setForeground(Color.BLACK);
        registerBtn.setFocusPainted(false);
        registerBtn.setMaximumSize(new Dimension(150, 30));
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerBtn.addActionListener(this);
        add(registerBtn);

        setVisible(true);
    }
    


    public void actionPerformed(ActionEvent e) {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String role = (String) roleDropdown.getSelectedItem();

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        

        try {
            Connection conn = DB.getConnection();
            String insertQuery = "";
            if (role.equalsIgnoreCase("Patient")) {
                insertQuery = "INSERT INTO patients (first_name, last_name, username, password) VALUES (?, ?, ?, ?)";
            } else if (role.equalsIgnoreCase("Doctor")) {
                insertQuery = "INSERT INTO doctors (first_name, last_name, username, password) VALUES (?, ?, ?, ?)";
            } else if (role.equalsIgnoreCase("Receptionist")) {
                insertQuery = "INSERT INTO receptionists (first_name, last_name, username, password) VALUES (?, ?, ?, ?)";
            }
            PreparedStatement ps = conn.prepareStatement(insertQuery);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, username);
            ps.setString(4, password);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                dispose();
                new LoginFrame();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed.");
            }

            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
