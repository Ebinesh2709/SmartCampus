package smartcampus.resource;

import smartcampus.data.MockDatabase;
import smartcampus.exception.SensorUnavailableException;
import smartcampus.model.Sensor;
import smartcampus.model.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private String parentSensorId;

    public SensorReadingResource(String parentSensorId) {
        this.parentSensorId = parentSensorId;
    }

    @GET
    public Response getReadings() {
        return Response.ok(MockDatabase.sensorReadings.getOrDefault(parentSensorId, new ArrayList<>())).build();
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = MockDatabase.sensors.get(parentSensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (sensor.getStatus().equalsIgnoreCase("MAINTENANCE") || sensor.getStatus().equalsIgnoreCase("OFFLINE")) {
            throw new SensorUnavailableException("Sensor is disconnected or in maintenance.");
        }

        MockDatabase.sensorReadings.putIfAbsent(parentSensorId, new ArrayList<>());
        MockDatabase.sensorReadings.get(parentSensorId).add(reading);

        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}