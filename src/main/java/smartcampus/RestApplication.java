package smartcampus;

import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api/v1")
public class RestApplication extends ResourceConfig {
    public RestApplication() {
        // Tells Jersey to scan this package for your @Path classes
        packages("smartcampus");
    }
}