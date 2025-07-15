import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PatientProfile extends JFrame {
    private final String username;
    private Connection conn;

    public PatientProfile(String username) {
        this.username = username;
        setTitle("CURELINK - Patient Profile");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        try {
            conn = DB.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) throw new Exception("No patient found for username: " + username);

            String fullName = rs.getString("first_name") + " " + rs.getString("last_name");
            String dob = rs.getString("date_of_birth");
            String gender = rs.getString("gender");
            String bloodType = rs.getString("blood_type");

            boolean isProfileIncomplete = (dob == null || dob.isEmpty() || bloodType == null || bloodType.isEmpty());

            // === Profile Card ===
            JPanel profileCard = new JPanel(new BorderLayout(10, 10));
            profileCard.setBorder(BorderFactory.createTitledBorder("🧑‍⚕️ Profile"));
            profileCard.setBackground(Color.WHITE);

            ImageIcon avatarIcon = switch (gender != null ? gender.toLowerCase() : "") {
                case "female" -> new ImageIcon("female.png");
                case "male" -> new ImageIcon("male.png");
                default -> new ImageIcon("neutral_avatar.png");
            };
            Image scaled = avatarIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            JLabel avatarLabel = new JLabel(new ImageIcon(scaled));

            JPanel left = new JPanel();
            left.setBackground(Color.WHITE);
            left.add(avatarLabel);

            JPanel right = new JPanel();
            right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
            right.setBackground(Color.WHITE);
            right.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            Font infoFont = new Font("Segoe UI", Font.PLAIN, 15);
            right.add(new JLabel("👤 Name: " + fullName));
            right.add(new JLabel("📅 DOB: " + (dob != null ? dob : "N/A")));
            right.add(new JLabel("⚧ Gender: " + (gender != null ? gender : "N/A")));
            right.add(new JLabel("🧨 Blood Type: " + (bloodType != null ? bloodType : "N/A")));

            JButton editBtn = new JButton(isProfileIncomplete ? "⚠️ Complete Profile" : "✏️ Edit Profile");
            editBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            editBtn.addActionListener(e -> new EditProfileDialog(this, conn, username));
            right.add(Box.createVerticalStrut(10));
            right.add(editBtn);

            profileCard.add(left, BorderLayout.WEST);
            profileCard.add(right, BorderLayout.CENTER);

            add(profileCard, BorderLayout.CENTER);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Profile Error", JOptionPane.ERROR_MESSAGE);
        }

        setVisible(true);
    }

    class EditProfileDialog extends JDialog {
        public EditProfileDialog(JFrame parent, Connection conn, String username) {
            super(parent, "Edit Profile", true);
            setSize(400, 250);
            setLocationRelativeTo(parent);
            setLayout(new GridLayout(4, 2, 10, 10));

            JTextField dobField = new JTextField();
            JTextField genderField = new JTextField();
            JTextField bloodTypeField = new JTextField();
            JButton saveBtn = new JButton("💾 Save");

            add(new JLabel("DOB (YYYY-MM-DD):"));
            add(dobField);
            add(new JLabel("Gender (Male/Female):"));
            add(genderField);
            add(new JLabel("Blood Type:"));
            add(bloodTypeField);
            add(new JLabel());
            add(saveBtn);

            saveBtn.addActionListener(e -> {
                try {
                    PreparedStatement ps = conn.prepareStatement(
                        "UPDATE patients SET date_of_birth = ?, gender = ?, blood_type = ? WHERE username = ?"
                    );
                    ps.setString(1, dobField.getText().trim());
                    ps.setString(2, genderField.getText().trim());
                    ps.setString(3, bloodTypeField.getText().trim());
                    ps.setString(4, username);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                    dispose();
                    parent.dispose();
                    new PatientProfile(username);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage());
                }
            });

            setVisible(true);
        }
    }

    public static void main(String[] args) {
        new PatientProfile("KV01");
    }
}
