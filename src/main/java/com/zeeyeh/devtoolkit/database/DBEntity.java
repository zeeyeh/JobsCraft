package com.zeeyeh.devtoolkit.database;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DBEntity extends LinkedHashMap<String, Object> {

    public DBEntity set(String attribute, Object value) {
        this.put(attribute, value);
        return this;
    }

    public DBEntity setIgnoreNullValue(String attribute, Object value) {
        if (value == null) {
            return this;
        }
        this.put(attribute, value);
        return this;
    }

    public List<String> getKeys() {
        return new ArrayList<>(this.keySet());
    }

    public List<Object> getValues() {
        List<Object> values = new ArrayList<>();
        for (String key : getKeys()) {
            values.add(this.get(key));
        }
        return values;
    }

    public Long getLong(String key) {
        return Long.parseLong(String.valueOf(this.get(key)));
    }

    public String getString(String key) {
        return String.valueOf(this.get(key));
    }
}
