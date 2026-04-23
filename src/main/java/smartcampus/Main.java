package smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import java.net.URI;

public class Main {
    // Note: Grizzly handles the base URL, but we still respect the @ApplicationPath
    public static final String BASE_URI = "http://localhost:8080/api/v1/";

    public static void main(String[] args) {
        try {
            // Pass your RestApplication class to the server
            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
                    URI.create(BASE_URI), 
                    new RestApplication()
            );
            
            System.out.println("Smart Campus API started successfully!");
            System.out.println("Discovery Endpoint available at: " + BASE_URI);
            System.out.println("Press CTRL^C to shut down the server.");
            
            // Keep the server running
            Thread.currentThread().join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}