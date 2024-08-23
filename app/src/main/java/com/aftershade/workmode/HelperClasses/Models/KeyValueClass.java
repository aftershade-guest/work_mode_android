package com.aftershade.workmode.HelperClasses.Models;

public class KeyValueClass {

    private String key, value;

    public KeyValueClass() {
    }

    public KeyValueClass(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
