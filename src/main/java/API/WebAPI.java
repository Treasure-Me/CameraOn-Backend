package API;

import java.util.Map;

import Database.util.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class WebAPI {
    private final Javalin server;
    static final Logger logger = LoggerFactory.getLogger(WebAPI.class);

    public WebAPI() {
        server = Javalin.create(config -> {
                    config.bundledPlugins.enableCors(cors -> {
                        cors.addRule(it -> it.allowHost("https://cameraon.vercel.app"));
                        cors.addRule(it -> it.allowHost("http://localhost:63342"));
                        cors.addRule(it -> it.allowHost("http://localhost:5500", "http://127.0.0.1:5500"));
                    });
                })
                .before(ctx -> {
                    if (ctx.contentType() == null) {
                        ctx.contentType("application/json");
                    }
                })
                .after(ctx -> {
                    logger.info("Request: {} {}", ctx.method(), ctx.url());
                });

        this.server.post("/api/new-booking", WebAPIHandler::newBooking);
        this.server.post("/api/login", WebAPIHandler::login);
        this.server.post("/api/logout", WebAPIHandler::logout);
        this.server.post("/api/create-account", WebAPIHandler::createAccount);
        this.server.get("/api/user", WebAPIHandler::getUserLoggedIn);
        this.server.get("/api/places-search", WebAPIHandler::searchCustomStudio);
    }

    public static void main(String[] args) {
        DatabaseConfig.loadEnvironment();
        WebAPI server = new WebAPI();

        String portStr = System.getenv("PORT");
        int port = (portStr != null) ? Integer.parseInt(portStr) : 5000;

        System.out.println("Starting server on port: " + port);
        server.start(port);
    }

    public void start(int port) {
        this.server.start(port);
    }

    public void stop() {
        this.server.stop();
    }
}