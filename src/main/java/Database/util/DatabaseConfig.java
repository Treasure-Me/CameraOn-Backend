package Database.util;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {

    // Final fallback for local development if no environment variables exist
    private static final String LOCAL_URL = "jdbc:postgresql://localhost:5432/bookings_local";

    public static void main(String[] args) {
        loadEnvironment();
        initializeDatabase();
    }

    /**
     * Loads .env variables into System properties for local development.
     * On Cloud platforms, ignoreIfMissing() ensures it uses the dashboard variables.
     */
    public static void loadEnvironment() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./Back-End")
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    public static void initializeDatabase() {
        try (Connection connection = getConnection()) {
            System.out.println("✅ Connected to: " + connection.getMetaData().getURL());
            createDefaultSchema(connection);
        } catch (SQLException e) {
            System.err.println("❌ Database fail: " + e.getMessage());
        }
    }

    private static void createDefaultSchema(Connection connection) throws SQLException {
        String schema = """
           CREATE TABLE IF NOT EXISTS Bookings (
               id SERIAL PRIMARY KEY,
               Name TEXT NOT NULL,
               Venue TEXT,
               Service TEXT,
               BOOKING_DATE TIMESTAMP,
               BOOKED_DATE TIMESTAMP DEFAULT (CURRENT_TIMESTAMP + INTERVAL '2 hours')
           );
           CREATE INDEX IF NOT EXISTS idx_venue ON Bookings(Venue);
           CREATE INDEX IF NOT EXISTS idx_booking_date ON Bookings(BOOKING_DATE);
        """;

        try (Statement statement = connection.createStatement()) {
            statement.execute(schema);
            System.out.println("✅ Schema verified.");
        }
    }

    /**
     * Core connection logic. Priority: System Property (.env) > Env Variable (Cloud) > Local Fallback.
     */
    public static Connection getConnection() throws SQLException {
        // Dynamic lookup ensures variables aren't 'null' if called after loadEnvironment()
        String url = System.getProperty("DATABASE_URL", System.getenv("DATABASE_URL"));
        String user = System.getProperty("DB_USER", System.getenv().getOrDefault("DB_USER", "postgres"));
        String pass = System.getProperty("DB_PASSWORD", System.getenv().getOrDefault("DB_PASSWORD", ""));

        if (url == null || url.isEmpty()) {
            url = LOCAL_URL;
        }

        // Professional Caution: Fix common protocol mismatch from cloud providers
        if (url.startsWith("postgres://")) {
            url = url.replace("postgres://", "jdbc:postgresql://");
        }

        return DriverManager.getConnection(url, user, pass);
    }
}