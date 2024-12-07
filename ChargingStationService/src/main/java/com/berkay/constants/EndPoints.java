package com.berkay.constants;

public class EndPoints {
    private static final String VERSION = "/v1";
    private static final String DEV = "/dev";
    private static final String ROOT = DEV + VERSION;

    public static final String STATIONS = ROOT + "/stations";
    public static final String CREATE_STATION = "/create-stations";
    public static final String GET_STATION_BY_ID = "/get-station-by-id";
    public static final String GET_ALL_STATIONS = "/get-all-stations";

    public static final String LOCATIONS = ROOT + "/locations";
    public static final String CREATE_LOCATION = "/create-location";
    public static final String GET_ALL_LOCATIONS = "/get-all-locations";
}
