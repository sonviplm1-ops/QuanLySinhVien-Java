package vn.edu.quanlysinhvien.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private static final String DEFAULT_URL = "jdbc:sqlserver://localhost:1433;databaseName=QuanLySinhVien;encrypt=true;trustServerCertificate=true";
    private static volatile String sessionUser;
    private static volatile String sessionPassword;

    private Database() { }

    public static boolean authenticate(String user, String password) throws SQLException {
        String url = System.getenv().getOrDefault("DB_URL", DEFAULT_URL);
        try (Connection ignored = DriverManager.getConnection(url, user, password)) {
            sessionUser = user;
            sessionPassword = password;
            return true;
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getenv().getOrDefault("DB_URL", DEFAULT_URL);
        String user = sessionUser != null ? sessionUser : System.getenv().getOrDefault("DB_USER", "sa");
        String password = sessionPassword != null ? sessionPassword : System.getenv().getOrDefault("DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, password);
    }
}
