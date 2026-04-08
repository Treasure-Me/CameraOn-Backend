package Database;

import Database.util.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

public class DatabaseEditor {

    public static void addIntoBookings(String name, String venue, String service, Date bookedDate) throws SQLException {
        String sql = "INSERT INTO Bookings (Name, Venue, Service, BOOKING_DATE) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, venue);
            pstmt.setString(3, service);
            pstmt.setDate(4, bookedDate);

            pstmt.executeUpdate();
        }
    }
}