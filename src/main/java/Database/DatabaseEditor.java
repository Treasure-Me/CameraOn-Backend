package Database;

import Database.util.DatabaseConfig;
import java.sql.*;

public class DatabaseEditor {

    // Accept both bookingStart and bookingEnd
    public static void addIntoBookings(String name, String venue, String service, Timestamp bookingStart, Timestamp bookingEnd) throws SQLException {
        // Update the SQL to match the new schema columns
        String sql = "INSERT INTO Bookings (Name, Venue, Service, BOOKING_START, BOOKING_END) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, venue);
            pstmt.setString(3, service);

            // Set both timestamps
            pstmt.setTimestamp(4, bookingStart);
            pstmt.setTimestamp(5, bookingEnd);

            pstmt.executeUpdate();
        }
    }
}