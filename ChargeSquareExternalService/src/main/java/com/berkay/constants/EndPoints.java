package com.berkay.constants;

public class EndPoints {
    private static final String VERSION = "/v1";
    private static final String DEV = "/dev";
    private static final String ROOT = DEV + VERSION;

    public static final String EXTERNALAPI = ROOT + "/externalapi";
    public static final String FETCH_AND_SEND_STATIONS = ROOT + "/fetch-and-send-stations";

}
