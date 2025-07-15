import java.sql.Connection;
import java.sql.DriverManager;

public class DB {
    public static Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/healthcare_system", // ✅ your correct DB
            "root",  // replace with your username
            "ABCD1234" // replace with your password
        );
    }
}
