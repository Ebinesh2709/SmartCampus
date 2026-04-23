package smartcampus.data;

import smartcampus.model.Room;
import smartcampus.model.Sensor;
import smartcampus.model.SensorReading;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockDatabase {
    public static Map<String, Room> rooms = new ConcurrentHashMap<>();
    public static Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    public static Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();
}