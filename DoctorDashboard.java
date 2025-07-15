import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DoctorDashboard extends JFrame {
    private Connection conn;
    private JTable appointmentTable;
    private DefaultTableModel model;

    private JTextField searchField;
    private JButton updateStatusBtn, addDiagnosisBtn, viewProfileBtn, viewPatientBtn;
    private JComboBox<String> statusBox;
    private JTextArea diagnosisArea;
    private JTextField medNameField, dosageField;
    private JCheckBox morningBox, afternoonBox, eveningBox, nightBox;

    private int doctorId;
    private String doctorFullName;

    public DoctorDashboard(String username) {
        setTitle("CureLink - Doctor Dashboard");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel(" Doctor Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(30, 144, 255));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setPreferredSize(new Dimension(100, 60));
        add(titleLabel, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new Object[]{"ID", "Patient Name", "Date", "Status"});
        appointmentTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("\uD83D\uDCCB Appointments List"));

        JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(350, 0));
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel searchLabel = new JLabel("\uD83D\uDD0D Search Patient:");
        searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        searchField.addActionListener(e -> searchAppointments());

        JLabel statusLabel = new JLabel("\uD83D\uDCCC Update Status:");
        statusBox = new JComboBox<>(new String[]{"Confirmed", "Completed", "Cancelled"});
        statusBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        updateStatusBtn = new JButton("Update Status");
        updateStatusBtn.addActionListener(e -> updateAppointmentStatus());

        JLabel diagLabel = new JLabel("\uD83D\uDCDD Diagnosis:");
        diagnosisArea = new JTextArea(3, 20);
        diagnosisArea.setLineWrap(true);
        diagnosisArea.setWrapStyleWord(true);
        JScrollPane diagScroll = new JScrollPane(diagnosisArea);

        JLabel medLabel = new JLabel("\uD83D\uDC8A Medicine:");
        medNameField = new JTextField();
        medNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel doseLabel = new JLabel("Dosage:");
        dosageField = new JTextField();
        dosageField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel timeLabel = new JLabel("Time Slots:");
        morningBox = new JCheckBox("Morning");
        afternoonBox = new JCheckBox("Afternoon");
        eveningBox = new JCheckBox("Evening");
        nightBox = new JCheckBox("Night");

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timePanel.add(morningBox);
        timePanel.add(afternoonBox);
        timePanel.add(eveningBox);
        timePanel.add(nightBox);

        addDiagnosisBtn = new JButton("\uD83D\uDCBE Save Prescription");
        addDiagnosisBtn.addActionListener(e -> addDiagnosis());

        viewProfileBtn = new JButton("\uD83D\uDC64 View Doctor Profile");
        viewProfileBtn.addActionListener(e -> showDoctorProfile());

        viewPatientBtn = new JButton("\uD83D\uDCC1 View Patient Profile");
        viewPatientBtn.addActionListener(e -> showPatientProfile());

        rightPanel.add(searchLabel);
        rightPanel.add(searchField);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(statusLabel);
        rightPanel.add(statusBox);
        rightPanel.add(updateStatusBtn);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(diagLabel);
        rightPanel.add(diagScroll);
        rightPanel.add(medLabel);
        rightPanel.add(medNameField);
        rightPanel.add(doseLabel);
        rightPanel.add(dosageField);
        rightPanel.add(timeLabel);
        rightPanel.add(timePanel);
        rightPanel.add(addDiagnosisBtn);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(viewProfileBtn);
        rightPanel.add(viewPatientBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        try {
            conn = DB.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT id, first_name, last_name FROM doctors WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                doctorId = rs.getInt("id");
                doctorFullName = rs.getString("first_name") + " " + rs.getString("last_name");
            } else {
                JOptionPane.showMessageDialog(this, "Doctor not found.");
                return;
            }
            loadAppointments();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }

        setVisible(true);
    }

    private void loadAppointments() {
        try {
            model.setRowCount(0);
            PreparedStatement ps = conn.prepareStatement("SELECT id, patient_name, appointment_date, status FROM appointments WHERE doctor_id = ?");
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getString("appointment_date"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + e.getMessage());
        }
    }

    private void searchAppointments() {
        String query = searchField.getText().trim();
        try {
            model.setRowCount(0);
            PreparedStatement ps = conn.prepareStatement("SELECT id, patient_name, appointment_date, status FROM appointments WHERE doctor_id = ? AND patient_name LIKE ?");
            ps.setInt(1, doctorId);
            ps.setString(2, "%" + query + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getString("appointment_date"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search Error: " + e.getMessage());
        }
    }

    private void updateAppointmentStatus() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment.");
            return;
        }
        String status = (String) statusBox.getSelectedItem();
        int id = (int) model.getValueAt(selectedRow, 0);
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE appointments SET status = ? WHERE id = ?");
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
            loadAppointments();
            JOptionPane.showMessageDialog(this, "Status updated.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Status update failed: " + e.getMessage());
        }
    }

    private void addDiagnosis() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment.");
            return;
        }

        String diagnosis = diagnosisArea.getText().trim();
        String medicine = medNameField.getText().trim();
        String dosage = dosageField.getText().trim();

        if (diagnosis.isEmpty() || medicine.isEmpty() || dosage.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all prescription fields.");
            return;
        }

        int appointmentId = (int) model.getValueAt(selectedRow, 0);
        String patientName = (String) model.getValueAt(selectedRow, 1);
        int patientId = getPatientIdFromName(patientName);

        try {
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO prescriptions (appointment_id, patient_id, doctor_id, medicine_name, dosage, diagnosis, morning, afternoon, evening, night) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setInt(1, appointmentId);
            ps.setInt(2, patientId);
            ps.setInt(3, doctorId);
            ps.setString(4, medicine);
            ps.setString(5, dosage);
            ps.setString(6, diagnosis);
            ps.setBoolean(7, morningBox.isSelected());
            ps.setBoolean(8, afternoonBox.isSelected());
            ps.setBoolean(9, eveningBox.isSelected());
            ps.setBoolean(10, nightBox.isSelected());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Prescription saved.");

            diagnosisArea.setText("");
            medNameField.setText("");
            dosageField.setText("");
            morningBox.setSelected(false);
            afternoonBox.setSelected(false);
            eveningBox.setSelected(false);
            nightBox.setSelected(false);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving prescription: " + e.getMessage());
        }
    }

    private int getPatientIdFromName(String fullName) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT id FROM patients WHERE CONCAT(first_name, ' ', last_name) = ?");
            ps.setString(1, fullName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void showDoctorProfile() {
        JOptionPane.showMessageDialog(this, "\uD83D\uDC68\u200D\u2695\uFE0F Doctor Profile:\nName: " + doctorFullName);
    }

    private void showPatientProfile() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select an appointment to view patient profile.");
            return;
        }
        String patientName = model.getValueAt(selectedRow, 1).toString();
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM patients WHERE CONCAT(first_name, ' ', last_name) = ?");
            ps.setString(1, patientName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String profile = "\uD83D\uDC64 Patient Profile:\n"
                        + "Name: " + rs.getString("first_name") + " " + rs.getString("last_name") + "\n"
                        + "Username: " + rs.getString("username") + "\n"
                        + "Gender: " + rs.getString("gender") + "\n"
                        + "DOB: " + rs.getString("date_of_birth") + "\n"
                        + "Blood Type: " + rs.getString("blood_type");
                JOptionPane.showMessageDialog(this, profile);
            } else {
                JOptionPane.showMessageDialog(this, "Patient not found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving profile: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DoctorDashboard("Disha12"));
    }
}
