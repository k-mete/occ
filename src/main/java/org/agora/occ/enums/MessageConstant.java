package org.agora.occ.enums;

/**
 * Central repository for all string message constants used throughout the
 * application.
 * Covers success/error messages for each domain and WebSocket/JSON field keys.
 */
public class MessageConstant {

    // Success Messages
    public static final String TELEMETRY_INGESTED_SUCCESS = "Telemetry data ingested successfully";
    public static final String WARNING_BROADCAST_SUCCESS = "Warning broadcasted successfully";
    public static final String TRIP_CREATED_SUCCESS = "Trip created successfully";
    public static final String TRIP_PROGRESS_RECORDED_SUCCESS = "Trip progress recorded successfully";
    public static final String DATA_FOUND = "Data found";
    public static final String DATA_RETRIEVED_SUCCESS = "Data retrieved successfully";
    public static final String DATA_CREATED_SUCCESS = "Data created successfully";
    public static final String DATA_UPDATED_SUCCESS = "Data updated successfully";
    public static final String DATA_DELETED_SUCCESS = "Data deleted successfully";
    public static final String REPORT_RETRIEVED_SUCCESS = "Report retrieved successfully";

    // Global Configuration Messages
    public static final String GLOBAL_CONFIG_NOT_FOUND = "Global configuration not found";

    // Error Messages
    public static final String TELEMETRY_INVALID_COORDINATES = "Invalid coordinates provided";
    public static final String DATA_NOT_FOUND = "Data not found";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error occurred";
    public static final String INVALID_LATITUDE = "Invalid Latitude";
    public static final String DOMAIN_ERROR = "Domain Error";
    public static final String NO_ACTIVE_TRIP = "No active trip found for train";
    public static final String INVALID_RADIUS = "Radius must be provided and greater than 0";

    // Trip Messages
    public static final String TRIP_STATUS_UPDATED_SUCCESS = "Trip status updated successfully";
    public static final String TRIP_NOT_FOUND = "Trip not found";
    public static final String TRIP_FOUND = "Trip found";
    public static final String TRIP_RETRIEVED_SUCCESS = "Trip retrieved successfully";

    // Train Messages
    public static final String TRAIN_CREATED_SUCCESS = "Train created successfully";
    public static final String TRAIN_UPDATED_SUCCESS = "Train updated successfully";
    public static final String TRAIN_DELETED_SUCCESS = "Train deleted successfully";
    public static final String TRAIN_RETRIEVED_SUCCESS = "Train retrieved successfully";
    public static final String TRAIN_NOT_FOUND = "Train not found";
    public static final String UPCOMING_LOCATIONS_RETRIEVED_SUCCESS = "Upcoming locations retrieved successfully";

    // Schedule Messages
    public static final String SCHEDULE_CREATED_SUCCESS = "Schedule created successfully";
    public static final String SCHEDULE_UPDATED_SUCCESS = "Schedule updated successfully";
    public static final String SCHEDULE_DELETED_SUCCESS = "Schedule deleted successfully";
    public static final String SCHEDULE_NOT_FOUND = "Schedule not found";

    // Station Messages
    public static final String STATION_NOT_FOUND = "Station not found";
    public static final String STATION_CREATED_SUCCESS = "Station created successfully";
    public static final String STATION_UPDATED_SUCCESS = "Station updated successfully";
    public static final String STATION_DELETED_SUCCESS = "Station deleted successfully";
    public static final String STATION_RETRIEVED_SUCCESS = "Station retrieved successfully";
    public static final String STATION_LIST_RETRIEVED_SUCCESS = "Station list retrieved successfully";
    public static final String STATION_DETAIL_RETRIEVED_SUCCESS = "Station detail retrieved successfully";
    public static final String STATION_CODE_ALREADY_EXISTS = "Station code already exists";
    public static final String STATION_LOCATION_REQUIRED = "Station location is required";
    public static final String STATIONS_NEARBY_RETRIEVED_SUCCESS = "Stations nearby retrieved successfully";

    // OCC Messages
    public static final String OCC_CREATED_SUCCESS = "OCC created successfully";
    public static final String OCC_UPDATED_SUCCESS = "OCC updated successfully";
    public static final String OCC_DELETED_SUCCESS = "OCC deleted successfully";
    public static final String OCC_RETRIEVED_SUCCESS = "OCC retrieved successfully";
    public static final String OCC_NOT_FOUND = "OCC not found";
    public static final String OCC_HAS_JPLS = "Cannot delete OCC with associated JPLs";

    // JPL Messages
    public static final String JPL_CREATED_SUCCESS = "JPL created successfully";
    public static final String JPL_UPDATED_SUCCESS = "JPL updated successfully";
    public static final String JPL_DELETED_SUCCESS = "JPL deleted successfully";
    public static final String JPL_RETRIEVED_SUCCESS = "JPL retrieved successfully";
    public static final String JPL_NOT_FOUND = "JPL not found";
    public static final String JPL_HAS_SCHEDULES = "Cannot delete JPL with associated schedules";
    public static final String JPL_HAS_TELEMETRY = "Cannot delete JPL with associated telemetry";
    public static final String JPL_HAS_WARNINGS = "Cannot delete JPL with associated warnings";
    public static final String JPL_HAS_SCHEDULE_PLANS = "Cannot delete JPL with associated schedule plans";
    public static final String JPL_ID_REQUIRED = "JPL ID is required";

    // Schedule Plan Messages
    public static final String SCHEDULE_PLAN_CREATED_SUCCESS = "Schedule plan created successfully";
    public static final String SCHEDULE_PLAN_UPDATED_SUCCESS = "Schedule plan updated successfully";
    public static final String SCHEDULE_PLAN_DELETED_SUCCESS = "Schedule plan deleted successfully";
    public static final String SCHEDULE_PLAN_RETRIEVED_SUCCESS = "Schedule plan retrieved successfully";
    public static final String SCHEDULE_PLAN_NOT_FOUND = "Schedule plan not found";

    // Route Messages
    public static final String ROUTE_CREATED_SUCCESS = "Route created successfully";
    public static final String ROUTE_UPDATED_SUCCESS = "Route updated successfully";
    public static final String ROUTE_DELETED_SUCCESS = "Route deleted successfully";
    public static final String ROUTE_RETRIEVED_SUCCESS = "Route retrieved successfully";
    public static final String ROUTE_NOT_FOUND = "Route not found";
    public static final String ROUTE_DETAIL_RETRIEVED_SUCCESS = "Route detail retrieved successfully";
    public static final String ROUTE_LIST_RETRIEVED_SUCCESS = "Route list retrieved successfully";
    public static final String ROUTE_CODE_IS_REQUIRED = "Route code is required";

    // Route Segment Messages
    public static final String ROUTE_SEGMENT_CREATED_SUCCESS = "Route segment created successfully";
    public static final String ROUTE_SEGMENT_UPDATED_SUCCESS = "Route segment updated successfully";
    public static final String ROUTE_SEGMENT_DELETED_SUCCESS = "Route segment deleted successfully";
    public static final String ROUTE_SEGMENT_RETRIEVED_SUCCESS = "Route segment retrieved successfully";
    public static final String ROUTE_SEGMENT_NOT_FOUND = "Route segment not found";

    // WebSocket & JSON Type Constants
    public static final String TYPE_WARNING = "WARNING";
    public static final String TYPE_TELEMETRY = "TELEMETRY";

    // WebSocket & JSON Key Constants
    public static final String KEY_TYPE = "type";
    public static final String KEY_JPL_ID = "jplId";
    public static final String KEY_LEVEL = "level";
    public static final String KEY_TRAIN_ID = "trainId";
    public static final String KEY_TIMESTAMP = "timeStamp";
    public static final String KEY_ERROR = "error";
    public static final String KEY_MESSAGE = "message";

    // Telemetry Keys
    public static final String KEY_TRAIN_LATITUDE = "trainLatitude";
    public static final String KEY_TRAIN_LONGITUDE = "trainLongitude";
    public static final String KEY_SPEED = "speed";
    public static final String KEY_HEADING = "heading";
    public static final String KEY_TRAIN_CODE = "trainCode";

    // Warning Broadcast Keys
    public static final String KEY_ALERT_ID = "alertId";
    public static final String KEY_CAMERA_ID = "cameraId";
    public static final String KEY_JPL_CODE = "jplCode";
    public static final String KEY_JPL_NAME = "jplName";
    public static final String KEY_TRAIN_NAME = "trainName";
    public static final String KEY_CROWD_LEVEL = "crowdLevel";
    public static final String KEY_WARNING_LEVEL = "warningLevel";
    public static final String KEY_DISTANCE_KM = "distanceKm";
    public static final String KEY_SPEED_KMH = "speedKmh";
    public static final String KEY_OBJECT_DETECTED = "objectDetected";
    public static final String KEY_ALERT_TIMESTAMP = "alertTimestamp";
    public static final String KEY_ACTION_REQUIRED = "actionRequired";
    public static final String KEY_COLOR_INDICATOR = "colorIndicator";
    public static final String KEY_IS_HEALTH = "isHealth";
    public static final String KEY_IS_SIREN_ON = "isSirenOn";
    public static final String KEY_IS_GATE_OPEN = "isGateOpen";
    public static final String KEY_IS_ANY_OBSTACLE = "isAnyObstacle";
    public static final String KEY_IS_INSTALLED = "isInstalled";
    public static final String KEY_CAMERA_STREAM = "cameraStream";

    // Stream Messages
    public static final String STREAM_STARTED = "Stream started for %s";
    public static final String STREAM_STOPPED = "Stream stopped for %s";

    private MessageConstant() {
        // Prevent instantiation
    }
}
