package com.dtn.lightningalert.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FlashType {

    CLOUD_TO_GROUND(0),
    CLOUD_TO_CLOUD(1),
    HEARTBEAT(9);

    private int type;

    FlashType(int type){
        this.type = type;
    }

    //Added due to problem serializing enum
    @JsonCreator
    public static FlashType getNameByValue(final int value) {
        for (final FlashType f: FlashType.values()) {
            if (f.type == value) {
                return f;
            }
        }
        return null;
    }

    @JsonValue
    public int getType() {
        return type;
    }
}