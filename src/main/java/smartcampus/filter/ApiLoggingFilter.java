package smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
public class ApiLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
    private static final Logger logger = Logger.getLogger(ApiLoggingFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext reqContext) throws IOException {
        logger.info("Incoming Request: " + reqContext.getMethod() + " " + reqContext.getUriInfo().getRequestUri());
    }

    @Override
    public void filter(ContainerRequestContext reqContext, ContainerResponseContext resContext) throws IOException {
        logger.info("Outgoing Response Status: " + resContext.getStatus());
    }
}