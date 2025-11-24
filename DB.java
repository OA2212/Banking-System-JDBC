import java.sql.*;

public class DB {
    private static final String URL  = "jdbc:postgresql://localhost:5432/YOUR NAME";
    private static final String USER = "postgres";
    private static final String PASS = "YOUR PASSWORD";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
