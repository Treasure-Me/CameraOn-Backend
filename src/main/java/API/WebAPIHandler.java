package API;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import Database.DatabaseEditor;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class WebAPIHandler {

    public static void newBooking(@NotNull Context ctx) throws SQLException, IOException {
        BookingRequest req = ctx.bodyAsClass(BookingRequest.class);
        LocalDateTime dt = LocalDateTime.parse(req.date);
        Date bookingDate = Date.valueOf(dt.toLocalDate());

        DatabaseEditor.addIntoBookings(req.name, req.venue, req.service, bookingDate);
        ctx.json(Map.of("success", true));
    }



    public static void login(@NotNull Context context) {
    }

    public static void logout(@NotNull Context context) {
    }

    public static void createAccount(@NotNull Context context) {
    }

    public static void getUserLoggedIn(@NotNull Context context) {
    }

    public static void loadLoginPage(@NotNull Context context) {
    }

    public static void bookingPage(@NotNull Context context){
        context.redirect("/Book.html");
    }

    public static void mainPage(@NotNull Context context) {
        context.redirect("Front-End/index.html");
    }

    public static class BookingRequest {
        public String name;
        public String venue;
        public String service;
        public String date;
    }
}
