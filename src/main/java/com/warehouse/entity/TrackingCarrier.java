package com.warehouse.entity;

public enum TrackingCarrier {

    FEDEX_EXPRESS,
    FEDEX_GROUND,
    UPS,
    OTHER;

    public static TrackingCarrier fromString(String value) {
        if (value == null) {
            return OTHER;
        }
        String v = value.trim().toUpperCase();
        switch (v) {
            case "FEDEX_EXPRESS":
            case "FEDEX-EXPRESS":
            case "FEDEXEXPRESS":
                return FEDEX_EXPRESS;
            case "FEDEX_GROUND":
            case "FEDEX-GROUND":
            case "FEDEXGROUND":
                return FEDEX_GROUND;
            case "UPS":
                return UPS;
            default:
                return OTHER;
        }
    }
}
