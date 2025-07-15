import java.sql.*;
import java.util.List;
import javax.swing.*;

public class BookingHandler {
    public static void saveBooking(List<Test> cart, int totalAmount, String customerName, String phone) {
        String url = "jdbc:mysql://localhost:3306/healthcare_system";
        String user = "root";
        String password = "ABCD1234";

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            String sql = "INSERT INTO bookings (customer_name, phone, test_name, price, booking_time) VALUES (?, ?, ?, ?, NOW())";
            PreparedStatement ps = con.prepareStatement(sql);

            for (Test t : cart) {
                ps.setString(1, customerName);
                ps.setString(2, phone);
                ps.setString(3, t.name);
                ps.setInt(4, t.price);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}


