package com.ktrack.morti.ktrack.utils;

public enum MessageContext {
    trackingStart("trackingStart"),
    trackingEnd("trackingEnd"),
    normalTracking("normalTracking"),
    emergency("Emergency");

    private final String code;

    MessageContext(final String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
