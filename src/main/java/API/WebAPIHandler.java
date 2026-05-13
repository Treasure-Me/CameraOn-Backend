package API;

import io.javalin.http.Context;
import io.javalin.http.util.NaiveRateLimit;
import org.jetbrains.annotations.NotNull;
import Database.DatabaseEditor;

import java.util.concurrent.TimeUnit;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import io.javalin.http.util.RateLimiter;
import com.google.maps.*;

public class WebAPIHandler {

    private static final RateLimiter searchLimiter = new RateLimiter(TimeUnit.MINUTES);

    public static void newBooking(Context ctx) {
        try {
            BookingRequest req = ctx.bodyAsClass(BookingRequest.class);

            LocalDateTime startDt = LocalDateTime.parse(req.startDate);
            LocalDateTime endDt = LocalDateTime.parse(req.endDate);
            Timestamp bookingStart = Timestamp.valueOf(startDt);
            Timestamp bookingEnd = Timestamp.valueOf(endDt);

            DatabaseEditor.addIntoBookings(req.name, req.venue, req.service, bookingStart, bookingEnd);

            ctx.json(Map.of("success", true));

        } catch (SQLException e) {
            if ("P0001".equals(e.getSQLState())) {
                ctx.status(409).json(Map.of(
                        "success", false,
                        "message", e.getMessage()
                ));
            } else {
                e.printStackTrace();
                ctx.status(500).json(Map.of("success", false, "message", "A database error occurred."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(400).json(Map.of("success", false, "message", "Invalid booking data received."));
        }
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

    public static void searchCustomStudio(Context ctx) {
        searchLimiter.incrementCounter(ctx, 10);
        String userInput = ctx.queryParam("location");

        if (userInput == null || userInput.isBlank()) {
            ctx.status(400).json("{\"error\": \"Location query is required\"}");
            return;
        }

        String apiKey = System.getProperty("GOOGLE_MAPS_API_KEY");

        try {
            String encodedQuery = URLEncoder.encode(userInput, StandardCharsets.UTF_8);
            String googleUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + encodedQuery + "&key=" + apiKey;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(googleUrl))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ctx.contentType("application/json");
            ctx.status(response.statusCode()).result(response.body());

        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).json("{\"error\": \"Failed to contact mapping service\"}");
        }
    }

    public static class BookingRequest {
        public String name;
        public String venue;
        public String service;
        public String startDate;
        public String endDate;
    }
}
