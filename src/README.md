# Smart Campus REST API

A comprehensive REST API for managing campus infrastructure, including rooms and environmental sensors. Built with Jersey/Grizzly and featuring robust exception handling, resource linking, and real-time sensor readings.

---

## Overview of API Design

The Smart Campus API follows RESTful principles with the following architectural characteristics:

### Design Principles
- **Resource-Oriented**: Resources are represented as Rooms, Sensors, and Sensor Readings
- **Stateless Architecture**: Each request contains all information needed for processing
- **JSON Format**: All requests and responses use JSON (application/json)
- **HTTP Status Codes**: Proper status codes (201 Created, 404 Not Found, 204 No Content, etc.)
- **Linked Resources**: Rooms and Sensors maintain referential integrity

### API Layers
1. **Resource Layer** (`/resource`): REST endpoint handlers
2. **Model Layer** (`/model`): Domain objects (Room, Sensor, SensorReading)
3. **Data Layer** (`/data`): In-memory mock database
4. **Exception Layer** (`/exception`): Custom exception mappers and business rule validation

### Core Resources

#### Rooms
Represents physical spaces in the campus with capacity and sensor assignments.
- **Path:** `/api/v1/rooms`
- **Properties:** id, name, capacity, sensorIds[]

#### Sensors
Environmental monitoring devices assigned to rooms (CO2, Temperature, Humidity, etc.)
- **Path:** `/api/v1/sensors`
- **Properties:** id, type, status (ONLINE/OFFLINE/MAINTENANCE), currentValue, roomId

#### Sensor Readings
Historical data points from sensors with timestamps and values.
- **Path:** `/api/v1/sensors/{sensorId}/readings`
- **Properties:** id, timestamp, value

### Business Rules
- Rooms can only be deleted if they have no assigned sensors (RoomNotEmptyException)
- Sensors must reference an existing room (LinkedResourceNotFoundException)
- Sensor readings can only be added when the sensor is ONLINE (SensorUnavailableException)
- Sensor readings update the sensor's currentValue in real-time

---

## Build and Launch Instructions

### Prerequisites
- Java 8 or higher
- Maven 3.6 or higher
- Windows Command Prompt or PowerShell

### Step-by-Step Build Process

#### 1. Navigate to Project Directory
```cmd
cd "D:\New folder\SmartCampus"
```

#### 2. Clean Previous Build Artifacts
```cmd
mvn clean
```

#### 3. Compile and Package the Project
```cmd
mvn package
```
This generates a compiled JAR with all dependencies in the `target/` folder.

#### 4. Launch the Server
```cmd
mvn exec:java
```

#### 5. Verify Server is Running
You should see the output:
```
Smart Campus API started successfully!
Discovery Endpoint available at: http://localhost:8080/api/v1/
Press CTRL^C to shut down the server.
```

#### 6. Access the API
- **Base URL:** `http://localhost:8080/api/v1`
- **Discovery Endpoint:** `http://localhost:8080/api/v1/`

#### 7. Shut Down the Server
Press `CTRL+C` in the terminal where the server is running.

---

## Sample cURL Commands

Use the following cURL commands to test all major API functionality. Run these in PowerShell or Command Prompt.

### 1. Discovery Endpoint - Get API Metadata
Verify the API is running and fetch metadata.
```bash
curl -X GET "http://localhost:8080/api/v1/" -H "Accept: application/json"
```
**Expected Response (200 OK):**
```json
{
  "version": "1.0",
  "admin_contact": "admin@smartcampus.ac.uk",
  "links": {
    "rooms": "/api/v1/rooms",
    "sensors": "/api/v1/sensors"
  }
}
```

### 2. Create a Base Room
Create the initial physical space required before adding any sensors.
```bash
curl -X POST "http://localhost:8080/api/v1/rooms" ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"LIB-301\",\"name\":\"Library Quiet Study\",\"capacity\":50}"
```
**Expected Response (201 Created):**
```json
{
  "id": "LIB-301",
  "name": "Library Quiet Study",
  "capacity": 50,
  "sensorIds": []
}
```

### 3. Get All Rooms
Verify the room was created successfully.
```bash
curl -X GET "http://localhost:8080/api/v1/rooms" -H "Accept: application/json"
```
**Expected Response (200 OK):**
```json
[
  {
    "id": "LIB-301",
    "name": "Library Quiet Study",
    "capacity": 50,
    "sensorIds": []
  }
]
```

### 4. Create a Valid Sensor
Link a new active sensor to the existing room.
```bash
curl -X POST "http://localhost:8080/api/v1/sensors" ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"CO2-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":0.0,\"roomId\":\"LIB-301\"}"
```
**Expected Response (201 Created):**
```json
{
  "id": "CO2-001",
  "type": "CO2",
  "status": "ACTIVE",
  "currentValue": 0.0,
  "roomId": "LIB-301"
}
```

### 5. Create a Sensor in Maintenance Mode
Add a second sensor to test state constraints later.
```bash
curl -X POST "http://localhost:8080/api/v1/sensors" ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"OCC-001\",\"type\":\"CO2\",\"status\":\"MAINTENANCE\",\"currentValue\":0.0,\"roomId\":\"LIB-301\"}"
```
**Expected Response (201 Created):**
```json
{
  "id": "OCC-001",
  "type": "CO2",
  "status": "MAINTENANCE",
  "currentValue": 0.0,
  "roomId": "LIB-301"
}
```

### 6. Error Handling: Linked Resource Not Found
Attempt to create a sensor for a room that does not exist.
```bash
curl -X POST "http://localhost:8080/api/v1/sensors" ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"ERR-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":0.0,\"roomId\":\"FAKE-999\"}"
```
**Expected Response (422 Unprocessable Entity):**
```json
{
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Cannot create sensor: room 'FAKE-999' does not exist."
}
```

### 7. Get a Specific Sensor
Verify the state of the active sensor.
```bash
curl -X GET "http://localhost:8080/api/v1/sensors/CO2-001" -H "Accept: application/json"
```
**Expected Response (200 OK):**
```json
{
  "id": "CO2-001",
  "type": "CO2",
  "status": "ACTIVE",
  "currentValue": 0.0,
  "roomId": "LIB-301"
}
```

### 8. Get a Specific Room
Verify the room now accurately reflects the assigned sensor IDs.
```bash
curl -X GET "http://localhost:8080/api/v1/rooms/LIB-301" -H "Accept: application/json"
```
**Expected Response (200 OK):**
```json
{
  "id": "LIB-301",
  "name": "Library Quiet Study",
  "capacity": 50,
  "sensorIds": ["CO2-001", "OCC-001"]
}
```

### 9. Filter Sensors by Type
Test query parameters for filtering the sensor collection.
```bash
curl -X GET "http://localhost:8080/api/v1/sensors?type=CO2" -H "Accept: application/json"
```
**Expected Response (200 OK):**
```json
[
  {
    "id": "CO2-001",
    "type": "CO2",
    "status": "ACTIVE",
    "currentValue": 0.0,
    "roomId": "LIB-301"
  },
  {
    "id": "OCC-001",
    "type": "CO2",
    "status": "MAINTENANCE",
    "currentValue": 0.0,
    "roomId": "LIB-301"
  }
]
```

### 10. Add a Valid Sensor Reading
Submit data to the active sensor.
```bash
curl -X POST "http://localhost:8080/api/v1/sensors/CO2-001/readings" ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"READ-001\",\"timestamp\":1713950000,\"value\":415.5}"
```
**Expected Response (201 Created):**
```json
{
  "id": "READ-001",
  "timestamp": 1713950000,
  "value": 415.5
}
```

### 11. Get All Sensor Readings
Verify the reading was saved to the sensor's history.
```bash
curl -X GET "http://localhost:8080/api/v1/sensors/CO2-001/readings" -H "Accept: application/json"
```
**Expected Response (200 OK):**
```json
[
  {
    "id": "READ-001",
    "timestamp": 1713950000,
    "value": 415.5
  }
]
```

### 12. Error Handling: State Constraint Validation
Attempt to add a reading to a sensor that is not ACTIVE.
```bash
curl -X POST "http://localhost:8080/api/v1/sensors/OCC-001/readings" ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"READ-002\",\"timestamp\":1713950000,\"value\":415.5}"
```
**Expected Response (403 Forbidden):**
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Sensor 'OCC-001' is unavailable and cannot accept readings."
}
```

### 13. Error Handling: Delete Occupied Room
Attempt to delete a room that still has active sensors assigned to it.
```bash
curl -X DELETE "http://localhost:8080/api/v1/rooms/LIB-301"
```
**Expected Response (409 Conflict):**
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Room 'LIB-301' cannot be deleted: it still has sensors assigned."
}
```

### 14. Delete an Empty Room
Test the successful deletion of a room with no dependent resources.

**Step 14a: Create Empty Room**
```bash
curl -X POST "http://localhost:8080/api/v1/rooms" ^
  -H "Content-Type: application/json" ^
  -d "{\"id\":\"LIB-302\",\"name\":\"Library Quiet Study\",\"capacity\":50}"
```
**Expected Response (201 Created):**
```json
{
  "id": "LIB-302",
  "name": "Library Quiet Study",
  "capacity": 50,
  "sensorIds": []
}
```

**Step 14b: Delete Empty Room**
```bash
curl -X DELETE "http://localhost:8080/api/v1/rooms/LIB-302"
```
**Expected Response (204 No Content)**

---

## Error Handling

The API implements custom exception mappers to provide meaningful error responses:
- **LinkedResourceNotFoundException**: Triggered when creating a sensor with invalid room ID
- **RoomNotEmptyException**: Triggered when trying to delete a room with assigned sensors
- **SensorUnavailableException**: Triggered when adding readings to OFFLINE/MAINTENANCE sensors

---

## Architecture Summary

```
SmartCampus API (Jersey + Grizzly)
    ├── Resource Layer (REST handlers)
    │   ├── DiscoveryResource (GET /)
    │   ├── RoomResource (CRUD operations)
    │   ├── SensorResource (CRUD + filtering)
    │   └── SensorReadingResource (POST/GET readings)
    │
    ├── Model Layer (Domain objects)
    │   ├── Room
    │   ├── Sensor
    │   └── SensorReading
    │
    ├── Data Layer
    │   └── MockDatabase (in-memory storage)
    │
    └── Exception Layer
        ├── LinkedResourceNotFoundException
        ├── RoomNotEmptyException
        └── SensorUnavailableException
```
