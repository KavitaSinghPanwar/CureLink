import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class AppointmentApp extends JFrame {
    private Connection conn;

    private JTextField tfPatient = new JTextField();
    private JComboBox<String> doctorBox = new JComboBox<>();
    private JDateChooser dateChooser = new JDateChooser();
    private JComboBox<String> slotBox = new JComboBox<>();

    private JTable table = new JTable();
    private DefaultTableModel model = new DefaultTableModel();
    private JButton btnBook = new JButton("Book Appointment");
    private JButton btnSlots = new JButton("View Slots");
    private JButton btnViewAll = new JButton("View All Appointments");
    private JButton btnPay = new JButton("Make Payment");

    private String currentUsername;
    private Runnable refreshCallback;

    public AppointmentApp(String username, Runnable refreshCallback) throws Exception {
        this.currentUsername = username;
        this.refreshCallback = refreshCallback;

        setTitle("CureLink - Appointment Booking");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(0, 120, 215));
        JLabel title = new JLabel("  🏥 Book Appointment");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titlePanel.add(title, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(8, 1, 10, 5));
        formPanel.setBorder(new TitledBorder("Appointment Details"));
        formPanel.setPreferredSize(new Dimension(330, 100));
        formPanel.add(new JLabel("Patient Name:"));
        formPanel.add(tfPatient);
        formPanel.add(new JLabel("Doctor:"));
        formPanel.add(doctorBox);
        formPanel.add(new JLabel("Appointment Date:"));
        formPanel.add(dateChooser);
        formPanel.add(new JLabel("Available Slots:"));
        formPanel.add(slotBox);
        add(formPanel, BorderLayout.WEST);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(new TitledBorder("Your Appointments"));
        model.addColumn("ID");
        model.addColumn("Patient");
        model.addColumn("Doctor");
        model.addColumn("Date");
        model.addColumn("Status");
        table.setModel(model);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.add(btnBook);
        bottomPanel.add(btnSlots);
        bottomPanel.add(btnViewAll);
        bottomPanel.add(btnPay);
        add(bottomPanel, BorderLayout.SOUTH);

        conn = DB.getConnection();

        // Load doctors into combo box
        PreparedStatement ps = conn.prepareStatement("SELECT id, first_name, last_name FROM doctors");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("first_name") + " " + rs.getString("last_name");
            doctorBox.addItem(id + " - " + name);
        }

        // Set patient's name
        ps = conn.prepareStatement("SELECT first_name, last_name FROM patients WHERE username = ?");
        ps.setString(1, currentUsername);
        rs = ps.executeQuery();
        if (rs.next()) {
            tfPatient.setText(rs.getString("first_name") + " " + rs.getString("last_name"));
        }

        btnSlots.addActionListener(_ -> {
            slotBox.removeAllItems();
            if (doctorBox.getSelectedItem() == null || dateChooser.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Please select doctor and date.");
                return;
            }

            int doctorId = Integer.parseInt(((String) doctorBox.getSelectedItem()).split(" - ")[0]);
            String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(dateChooser.getDate());

            try {
                PreparedStatement psSlot = conn.prepareStatement("SELECT id, slot_time FROM doctor_slots WHERE doctor_id = ?");
                psSlot.setInt(1, doctorId);
                ResultSet rsSlot = psSlot.executeQuery();
                while (rsSlot.next()) {
                    String slot = rsSlot.getString("slot_time");
                    slotBox.addItem(rsSlot.getInt("id") + " | " + dateStr + " " + slot);
                }
                if (slotBox.getItemCount() == 0) {
                    JOptionPane.showMessageDialog(this, "No slots available for this doctor.");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "❌ Error loading slots: " + ex.getMessage());
            }
        });

        btnBook.addActionListener(_ -> {
            String patient = tfPatient.getText().trim();
            if (doctorBox.getSelectedItem() == null || slotBox.getSelectedItem() == null || patient.isEmpty()) {
                JOptionPane.showMessageDialog(this, "❌ Fill all details and select a slot.");
                return;
            }

            int doctorId = Integer.parseInt(((String) doctorBox.getSelectedItem()).split(" - ")[0]);
            String selectedSlot = (String) slotBox.getSelectedItem();

            String fullDateTime = "";
            try {
                String[] parts = selectedSlot.split(" \\| ");
                String date = parts[1].split(" ")[0];  // yyyy-MM-dd
                String time = parts[1].substring(11);  // hh:mm AM

                SimpleDateFormat sdfInput = new SimpleDateFormat("hh:mm a");
                SimpleDateFormat sdfOutput = new SimpleDateFormat("HH:mm:ss");
                String time24 = sdfOutput.format(sdfInput.parse(time));

                fullDateTime = date + " " + time24;
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "❌ Failed to parse time: " + e.getMessage());
                return;
            }

            try {
                PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO appointments (patient_name, doctor_id, appointment_date, status) VALUES (?, ?, ?, 'Confirmed')");
                ps2.setString(1, patient);
                ps2.setInt(2, doctorId);
                ps2.setString(3, fullDateTime);
                ps2.executeUpdate();

                JOptionPane.showMessageDialog(this, "✅ Appointment booked.");
                if (refreshCallback != null) refreshCallback.run();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Booking Error: " + ex.getMessage());
            }
        });

        btnViewAll.addActionListener(_ -> {
            model.setRowCount(0);
            try {
                PreparedStatement psAll = conn.prepareStatement(
                    "SELECT a.id, a.patient_name, d.first_name, d.last_name, a.appointment_date, a.status " +
                    "FROM appointments a JOIN doctors d ON a.doctor_id = d.id WHERE a.patient_name = ?");
                psAll.setString(1, tfPatient.getText().trim());
                ResultSet rsAll = psAll.executeQuery();
                while (rsAll.next()) {
                    model.addRow(new Object[]{
                        rsAll.getInt("id"),
                        rsAll.getString("patient_name"),
                        rsAll.getString("first_name") + " " + rsAll.getString("last_name"),
                        rsAll.getString("appointment_date"),
                        rsAll.getString("status")
                    });
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "❌ Failed to load appointments.");
            }
        });

        btnPay.addActionListener(_ -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an appointment to pay for.");
                return;
            }

            String appointmentId = model.getValueAt(selectedRow, 0).toString();
            String status = model.getValueAt(selectedRow, 4).toString();

            if (status.equalsIgnoreCase("Paid")) {
                JOptionPane.showMessageDialog(this, "✅ Already Paid.");
                return;
            }

            JDialog payDialog = new JDialog(this, "Secure Payment", true);
            payDialog.setSize(450, 400);
            payDialog.setLayout(null);
            payDialog.setLocationRelativeTo(this);
            payDialog.getContentPane().setBackground(Color.WHITE);

            JLabel heading = new JLabel("💳 Pay ₹500 for Appointment");
            heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
            heading.setBounds(60, 10, 330, 30);
            payDialog.add(heading);

            JLabel modeLabel = new JLabel("Choose Payment Mode:");
            modeLabel.setBounds(40, 60, 200, 20);
            payDialog.add(modeLabel);

            JComboBox<String> modeBox = new JComboBox<>(new String[]{"UPI", "Credit Card", "Net Banking"});
            modeBox.setBounds(200, 60, 180, 25);
            payDialog.add(modeBox);

            JPanel inputPanel = new JPanel(null);
            inputPanel.setBounds(30, 100, 380, 160);
            inputPanel.setBorder(BorderFactory.createTitledBorder("Payment Details"));
            payDialog.add(inputPanel);

            JLabel upiLabel = new JLabel("Enter UPI ID:");
            JTextField upiField = new JTextField();

            JLabel cardLabel = new JLabel("Card No:");
            JTextField cardField = new JTextField();
            JLabel expiryLabel = new JLabel("Expiry:");
            JTextField expiryField = new JTextField("MM/YY");
            JLabel cvvLabel = new JLabel("CVV:");
            JPasswordField cvvField = new JPasswordField();

            JLabel bankLabel = new JLabel("Bank:");
            JComboBox<String> bankBox = new JComboBox<>(new String[]{"SBI", "ICICI", "HDFC", "Axis"});

            Runnable refreshFields = () -> {
                inputPanel.removeAll();
                switch (modeBox.getSelectedItem().toString()) {
                    case "UPI":
                        upiLabel.setBounds(20, 30, 100, 20);
                        upiField.setBounds(130, 30, 200, 25);
                        inputPanel.add(upiLabel);
                        inputPanel.add(upiField);
                        break;
                    case "Credit Card":
                        cardLabel.setBounds(20, 30, 100, 20);
                        cardField.setBounds(130, 30, 200, 25);
                        expiryLabel.setBounds(20, 65, 100, 20);
                        expiryField.setBounds(130, 65, 80, 25);
                        cvvLabel.setBounds(220, 65, 40, 20);
                        cvvField.setBounds(260, 65, 70, 25);
                        inputPanel.add(cardLabel);
                        inputPanel.add(cardField);
                        inputPanel.add(expiryLabel);
                        inputPanel.add(expiryField);
                        inputPanel.add(cvvLabel);
                        inputPanel.add(cvvField);
                        break;
                    case "Net Banking":
                        bankLabel.setBounds(20, 30, 100, 20);
                        bankBox.setBounds(130, 30, 200, 25);
                        inputPanel.add(bankLabel);
                        inputPanel.add(bankBox);
                        break;
                }
                inputPanel.repaint();
                inputPanel.revalidate();
            };

            refreshFields.run();
            modeBox.addActionListener(_ -> refreshFields.run());

            JButton payBtn = new JButton("Pay ₹500");
            payBtn.setBounds(150, 280, 130, 35);
            payDialog.add(payBtn);

            payBtn.addActionListener(_ -> {
                try {
                    PreparedStatement psPay = conn.prepareStatement("UPDATE appointments SET status = 'Paid' WHERE id = ?");
                    psPay.setString(1, appointmentId);
                    psPay.executeUpdate();
                    JOptionPane.showMessageDialog(payDialog, "✅ Payment Successful!");
                    payDialog.dispose();
                    btnViewAll.doClick();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(payDialog, "❌ Payment Error: " + ex.getMessage());
                }
            });

            payDialog.setVisible(true);
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            new AppointmentApp("KV01", null); // Replace with your actual patient username
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
