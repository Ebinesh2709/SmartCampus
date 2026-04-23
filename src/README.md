# Smart Campus API - Postman Testing Guide

This document outlines the step-by-step testing procedure for the Smart Campus API using Postman. This sequence ensures all endpoints, business logic constraints, and custom exception mappers are thoroughly validated.

## Prerequisites
Ensure the server is running locally on port 8080.
**Base URL:** `http://localhost:8080/api/v1`

---

## Phase 1: Setup & Discovery (Part 1)

### 1. Get Discovery Metadata
* **Method:** `GET`
* **URL:** `/`
* **Expected Status:** `200 OK`
* **Expected Response:**
```json
{
    "version": "1.0",
    "admin_contact": "admin@smartcampus.ac.uk",
    "links": {
        "rooms": "/api/v1/rooms",
        "sensors": "/api/v1/sensors"
    }
}

## Phase 2: room creation
url : http://localhost:8080/api/v1/rooms
method : POST
body :
{
    "id": "LIB-301",
    "name": "Library Quiet Study",
    "capacity": 50
}
expected status : 201

## phase 3 : fetch specific room
url : http://localhost:8080/api/v1/rooms/LIB-301
method : GET
expected status : 201

#phase 4 : BAD room ID validation
url : http://localhost:8080/api/v1/sensors
method : POST
body :
{
    "id": "CO2-001",
    "type": "CO2",
    "status": "ACTIVE",
    "currentValue": 0.0,
    "roomId": "FAKE-999"
}
expected status : 422

## phase 4 Valid sensor
url : http://localhost:8080/api/v1/sensors
method : POST
body :
{
    "id": "CO2-001",
    "type": "CO2",
    "status": "ACTIVE",
    "currentValue": 0.0,
    "roomId": "LIB-301"
}
expected status : 201

#phase 5 
url : http://localhost:8080/api/v1/sensors
method : POST
body :
{
    "id": "OCC-001",
    "type": "CO2",
    "status": "MAINTENANCE",
    "currentValue": 0.0,
    "roomId": "LIB-301"
}
expected status : 201

#phase 6 Test Sensor Filtering
url : http://localhost:8080/api/v1/sensors?type=CO2
method : GET
expected status : 200

# phase 7 Sensor Reading
url : http://localhost:8080/api/v1/sensors/CO2-001/readings
method : POST
body :
{
    "id": "READ-001",
    "timestamp": 1713950000,
    "value": 415.5
}
expected status : 201

#phase  8 
url : http://localhost:8080/api/v1/sensors/CO2-001
method : GET
expected status : 200

#Phase 8 Error handling  403 state constraint
url : http://localhost:8080/api/v1/sensors/OCC-001/readings
method : POST
body :
{
    "id": "READ-001",
    "timestamp": 1713950000,
    "value": 415.5
}
expected status : 403

#phase 9 Deleting a occupied room
URL: http://localhost:8080/api/v1/rooms/LIB-301
method : DELETE

expected status : 409

#phase 10 delete an empty room
step 1: 
url : http://localhost:8080/api/v1/rooms
method : POST
body :
{
    "id": "LIB-302",
    "name": "Library Quiet Study",
    "capacity": 50
}
expected status : 201

step 2:
URL: http://localhost:8080/api/v1/rooms/LIB-302
method : DELETE

expected status : 204

