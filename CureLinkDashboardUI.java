import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;
import javax.swing.table.DefaultTableModel;

public class CureLinkDashboardUI {
    static final String[] HEALTH_TIPS = {
        "Stay hydrated by drinking 8 glasses of water daily.",
        "Get at least 7-8 hours of sleep every night.",
        "Exercise regularly to boost heart health.",
        "Eat a balanced diet rich in fruits and vegetables.",
        "Avoid excessive sugar and processed foods.",
        "Practice meditation to reduce stress.",
        "Avoid smoking and limit alcohol consumption.",
        "Maintain good hygiene and wash hands often.",
        "Get regular health check-ups.",
        "Stay socially connected for mental wellness."
    };

    private static final String DB_URL = "jdbc:mysql://localhost:3306/healthcare_system";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "ABCD1234";

    private static String fullName = "Patient";
    private static int patientId = -1;
    private static String username = "";
    private static JPanel appointmentsPanelRef;

    public CureLinkDashboardUI(String uname) {
        username = uname;
        SwingUtilities.invokeLater(() -> createUI(username));
    }

    public static void createUI(String uname) {
        username = uname;
        fetchPatientData();

        JFrame frame = new JFrame("CureLink Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);

        JPanel topNavBar = new JPanel();
        topNavBar.setBackground(new Color(51, 179, 255));
        topNavBar.setBounds(0, 0, 1000, 70);
        topNavBar.setLayout(null);

        JLabel logo = new JLabel("CureLink");
        logo.setFont(new Font("SansSerif", Font.BOLD, 28));
        logo.setForeground(Color.WHITE);
        logo.setBounds(30, 20, 200, 30);
        topNavBar.add(logo);

        JButton profileBtn = new JButton("Profile");
        profileBtn.setBounds(830, 15, 130, 40);
        profileBtn.setBackground(Color.WHITE);
        profileBtn.setForeground(new Color(51, 179, 255));
        profileBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        profileBtn.addActionListener(e -> openProfileWindow());
        topNavBar.add(profileBtn);
        frame.add(topNavBar);

        JPanel leftNav = new JPanel();
        leftNav.setBackground(new Color(245, 245, 245));
        leftNav.setBounds(0, 70, 120, 630);
        leftNav.setLayout(null);

        String[] btnLabels = {"Book Test", "Appointment", "Prescription"};
        String[] icons = {"\uD83E\uDDEA", "\uD83D\uDCC5", "\uD83D\uDC8A"};
        ActionListener[] actions = {
            e -> openBookTestWindow(),
            e -> openAppointmentWindow(),
            e -> openPrescriptionWindow()
        };

        for (int i = 0; i < btnLabels.length; i++) {
            String labelWithEmoji = "<html><div style='text-align:center; font-size:8.5px;'>" +
                    icons[i] + "<br>" + btnLabels[i] + "</div></html>";

            JButton btn = new JButton(labelWithEmoji);
            btn.setBounds(10, 20 + i * 110, 100, 90);
            btn.setBackground(new Color(102, 102, 102));
            btn.setForeground(Color.BLACK);
            btn.setFont(new Font("SansSerif", Font.BOLD, 12));
            btn.setHorizontalTextPosition(SwingConstants.CENTER);
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setMargin(new Insets(1, 1, 1, 1));
            btn.addActionListener(actions[i]);
            leftNav.add(btn);
        }

        frame.add(leftNav);

        JPanel mainPanel = new JPanel();
        mainPanel.setBounds(120, 70, 880, 630);
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(null);

        JLabel welcomeLabel = new JLabel("Welcome, " + fullName);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setBounds(30, 10, 500, 30);
        mainPanel.add(welcomeLabel);

        JPanel appointmentsPanel = new JPanel();
        appointmentsPanel.setLayout(null);
        appointmentsPanel.setBackground(Color.LIGHT_GRAY);
        appointmentsPanel.setBounds(30, 60, 400, 200);

        JLabel apptLabel = new JLabel("\uD83D\uDCC5 Today's Appointments");
        apptLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        apptLabel.setBounds(10, 10, 300, 30);
        appointmentsPanel.add(apptLabel);
        appointmentsPanelRef = appointmentsPanel;
        fetchAppointments(appointmentsPanel);
        mainPanel.add(appointmentsPanel);

        JPanel medicinePanel = new JPanel();
        medicinePanel.setLayout(null);
        medicinePanel.setBackground(Color.LIGHT_GRAY);
        medicinePanel.setBounds(30, 280, 400, 200);

        JLabel medLabel = new JLabel("\uD83D\uDC8A Medicine Reminder");
        medLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        medLabel.setBounds(10, 10, 300, 30);
        medicinePanel.add(medLabel);
        fetchMedicineReminders(medicinePanel);
        mainPanel.add(medicinePanel);

        JPanel tipPanel = new JPanel(new BorderLayout());
        tipPanel.setBackground(Color.LIGHT_GRAY);
        tipPanel.setBounds(450, 60, 400, 100);

        String randomTip = HEALTH_TIPS[new Random().nextInt(HEALTH_TIPS.length)];
        JLabel tipLabel = new JLabel("<html><div style='padding:10px;'>\uD83D\uDCA1 <b>Health Tip:</b> " + randomTip + "</div></html>", SwingConstants.CENTER);
        tipLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tipPanel.add(tipLabel, BorderLayout.CENTER);
        mainPanel.add(tipPanel);

        JPanel chatbotPanel = new JPanel();
        chatbotPanel.setLayout(new BorderLayout());
        chatbotPanel.setBounds(450, 170, 400, 430);
        chatbotPanel.setBorder(BorderFactory.createTitledBorder("\uD83E\uDD16 Ask CureLink"));
        chatbotPanel.add(new ChatBotApp().getChatPanel(), BorderLayout.CENTER);
        mainPanel.add(chatbotPanel);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static void fetchPatientData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT id, first_name, last_name FROM patients WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                patientId = rs.getInt("id");
                fullName = rs.getString("first_name") + " " + rs.getString("last_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fullName = "Unknown";
        }
    }

    private static void refreshAppointmentsPanel() {
        appointmentsPanelRef.removeAll();
        appointmentsPanelRef.setLayout(null);

        JLabel apptLabel = new JLabel("\uD83D\uDCC5 Today's Appointments");
        apptLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        apptLabel.setBounds(10, 10, 300, 30);
        appointmentsPanelRef.add(apptLabel);

        fetchAppointments(appointmentsPanelRef);
        appointmentsPanelRef.revalidate();
        appointmentsPanelRef.repaint();
    }

    private static void fetchAppointments(JPanel panel) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT d.first_name, d.last_name, s.slot_time " +
                 "FROM appointments a " +
                 "JOIN doctors d ON a.doctor_id = d.id " +
                 "JOIN doctor_slots s ON a.doctor_id = s.doctor_id AND TIME(a.appointment_date) = STR_TO_DATE(s.slot_time, '%h:%i %p') " +
                 "WHERE a.patient_name = ? AND DATE(a.appointment_date) = CURDATE()")) {

            stmt.setString(1, fullName);
            ResultSet rs = stmt.executeQuery();

            int y = 50;
            while (rs.next()) {
                String doctorName = rs.getString("first_name") + " " + rs.getString("last_name");
                String slotTime = rs.getString("slot_time");
                JLabel apptLabel = new JLabel("Dr. " + doctorName + " at " + slotTime);
                apptLabel.setBounds(20, y, 350, 20);
                apptLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                panel.add(apptLabel);
                y += 30;
            }

            if (y == 50) {
                JLabel noApptLabel = new JLabel("No appointments today.");
                noApptLabel.setBounds(20, y, 350, 20);
                noApptLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                panel.add(noApptLabel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void fetchMedicineReminders(JPanel medicinePanel) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT medicine_name, dosage, morning, afternoon, evening, night FROM prescriptions WHERE patient_id = ? ORDER BY prescribed_date DESC")) {
            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            int y = 50;
            boolean hasReminders = false;

            while (rs.next()) {
                String medicine = rs.getString("medicine_name");
                String dosage = rs.getString("dosage");
                boolean morning = rs.getBoolean("morning");
                boolean afternoon = rs.getBoolean("afternoon");
                boolean evening = rs.getBoolean("evening");
                boolean night = rs.getBoolean("night");

                StringBuilder reminder = new StringBuilder("<html><b>" + medicine + "</b> (" + dosage + "): ");
                if (morning) reminder.append("Morning ");
                if (afternoon) reminder.append("Afternoon ");
                if (evening) reminder.append("Evening ");
                if (night) reminder.append("Night ");
                reminder.append("</html>");

                JLabel reminderLabel = new JLabel(reminder.toString());
                reminderLabel.setBounds(20, y, 350, 20);
                reminderLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
                medicinePanel.add(reminderLabel);

                y += 30;
                hasReminders = true;
            }

            if (!hasReminders) {
                JLabel none = new JLabel("No active medicine reminders.");
                none.setBounds(20, 50, 350, 20);
                none.setFont(new Font("SansSerif", Font.PLAIN, 13));
                medicinePanel.add(none);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error fetching medicine reminders: " + e.getMessage());
        }
    }

    private static void openProfileWindow() {
        new PatientProfile(username);
    }

    private static void openBookTestWindow() {
        SwingUtilities.invokeLater(() -> new HealthTestBookingUI().createUI());
    }

    private static void openAppointmentWindow() {
        try {
            new AppointmentApp(username,  () -> refreshAppointmentsPanel());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Failed to open Appointment window:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private static void openPrescriptionWindow() {
        JFrame frame = new JFrame("\uD83D\uDCDD Your Prescriptions");
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);

        String[] columns = {"Medicine", "Dosage", "Diagnosis", "Morning", "Afternoon", "Evening", "Night", "Prescribed On"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement(
                 "SELECT medicine_name, dosage, diagnosis, morning, afternoon, evening, night, prescribed_date FROM prescriptions WHERE patient_id = ? ORDER BY prescribed_date DESC")) {

            ps.setInt(1, patientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("medicine_name"),
                        rs.getString("dosage"),
                        rs.getString("diagnosis"),
                        rs.getBoolean("morning") ? "✅" : "",
                        rs.getBoolean("afternoon") ? "✅" : "",
                        rs.getBoolean("evening") ? "✅" : "",
                        rs.getBoolean("night") ? "✅" : "",
                        rs.getString("prescribed_date")
                });
            }

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(frame, "No prescriptions found.");
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error loading prescriptions: " + e.getMessage());
            return;
        }

        frame.add(scrollPane);
        frame.setVisible(true);
    }
}
