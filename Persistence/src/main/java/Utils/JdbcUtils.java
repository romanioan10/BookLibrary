package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtils {
    private final Properties jdbcProps;

    public JdbcUtils(Properties props) {
        this.jdbcProps = props;
    }

    public Connection getConnection() {
        String url = jdbcProps.getProperty("jdbc.url");
        String user = jdbcProps.getProperty("jdbc.user");
        String pass = jdbcProps.getProperty("jdbc.pass");

        try {
            if (user != null && pass != null) {
                return DriverManager.getConnection(url, user, pass);
            } else {
                return DriverManager.getConnection(url);
            }
        } catch (SQLException e) {
            System.out.println("Error getting connection: " + e.getMessage());
            return null;
        }
    }
}
