package smartcampus.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

public class ExceptionMappers {

    @Provider
    public static class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException> {
        @Override
        public Response toResponse(RoomNotEmptyException ex) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"" + ex.getMessage() + "\"}").build();
        }
    }

    @Provider
    public static class LinkedResourceNotFoundMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
        @Override
        public Response toResponse(LinkedResourceNotFoundException ex) {
            return Response.status(422)
                    .entity("{\"error\": \"" + ex.getMessage() + "\"}").build();
        }
    }

    @Provider
    public static class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {
        @Override
        public Response toResponse(SensorUnavailableException ex) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"error\": \"" + ex.getMessage() + "\"}").build();
        }
    }

    @Provider
    public static class GenericExceptionMapper implements ExceptionMapper<Throwable> {
        @Override
        public Response toResponse(Throwable ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"An unexpected internal server error occurred.\"}").build();
        }
    }
}