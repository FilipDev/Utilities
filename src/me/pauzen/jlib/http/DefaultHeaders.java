package me.pauzen.jlib.http;

import java.util.HashMap;
import java.util.Map;

public enum DefaultHeaders {

    URLENCODED(new HashMap.SimpleEntry<>("Content-Type", "application/x-www-form-urlencoded")),
    USERAGENT(new HashMap.SimpleEntry<>("User-Agent", "Mozilla/5.0"));

    private Map.Entry type;

    DefaultHeaders(Map.Entry type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type.getKey() + ": " + this.type.getValue();
    }

}