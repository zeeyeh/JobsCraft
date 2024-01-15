package com.zeeyeh.devtoolkit.config;

import org.bukkit.util.NumberConversions;

import java.util.*;

public abstract class Configuration {
    private Map<String, Object> map;

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }


    public Object get(String path) {
        return get(path, this.map);
    }

    public Object get(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return get(remainingPath, (Map) value);
            } else {
                return value;
            }
        } else {
            return map.get(path);
        }
    }

    public List<Object> getList(String path) {
        return getList(path, this.map);
    }

    public List<Object> getList(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getList(remainingPath, (Map) value);
            } else {
                return (List) value;
            }
        } else {
            return (List) map.get(path);
        }
    }

    public Integer getInteger(String path) {
        return getInteger(path, this.map);
    }

    public Integer getInteger(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getInteger(remainingPath, (Map) value);
            } else {
                return Integer.parseInt(String.valueOf(NumberConversions.toInt(value)));
            }
        } else {
            return Integer.parseInt(String.valueOf(NumberConversions.toInt(map.get(path))));
        }
    }

    public int getIntValue(String path) {
        return getIntValue(path, this.map);
    }

    public int getIntValue(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getInteger(remainingPath, (Map) value);
            } else {
                return NumberConversions.toInt(value);
            }
        } else {
            return NumberConversions.toInt(map.get(path));
        }
    }

    public List<Integer> getIntegerList(String path) {
        return getIntegerList(path, this.map);
    }

    public List<Integer> getIntegerList(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getIntegerList(remainingPath, (Map) value);
            } else {
                return (List) value;
            }
        } else {
            return (List) map.get(path);
        }
    }

    public Float getFloat(String path) {
        return getFloat(path, this.map);
    }

    public Float getFloat(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getFloat(remainingPath, (Map) value);
            } else {
                return Float.parseFloat(String.valueOf(NumberConversions.toFloat(value)));
            }
        } else {
            return Float.parseFloat(String.valueOf(NumberConversions.toFloat(map.get(path))));
        }
    }

    public float getFloatValue(String path) {
        return getFloatValue(path, this.map);
    }

    public float getFloatValue(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getFloatValue(remainingPath, (Map) value);
            } else {
                return NumberConversions.toFloat(value);
            }
        } else {
            return NumberConversions.toFloat(map.get(path));
        }
    }

    public List<Float> getFloatList(String path) {
        return getFloatList(path, this.map);
    }

    public List<Float> getFloatList(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getFloatList(remainingPath, (Map) value);
            } else {
                return (List) value;
            }
        } else {
            return (List) map.get(path);
        }
    }

    public Double getDouble(String path) {
        return getDouble(path, this.map);
    }

    public Double getDouble(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getDouble(remainingPath, (Map) value);
            } else {
                return Double.parseDouble(String.valueOf(NumberConversions.toDouble(value)));
            }
        } else {
            return Double.parseDouble(String.valueOf(NumberConversions.toDouble(map.get(path))));
        }
    }

    public double getDoubleValue(String path) {
        return getDoubleValue(path, this.map);
    }

    public double getDoubleValue(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getDoubleValue(remainingPath, (Map) value);
            } else {
                return Double.parseDouble(String.valueOf(NumberConversions.toDouble(value)));
            }
        } else {
            return Double.parseDouble(String.valueOf(map.get(path)));
        }
    }

    public List<Double> getDoubleList(String path) {
        return getDoubleList(path, this.map);
    }

    public List<Double> getDoubleList(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getDoubleList(remainingPath, (Map) value);
            } else {
                return (List) value;
            }
        } else {
            return (List) map.get(path);
        }
    }

    public Boolean getBoolean(String path) {
        return getBoolean(path, this.map);
    }

    public Boolean getBoolean(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getBoolean(remainingPath, (Map) value);
            } else {
                return (Boolean) value;
            }
        } else {
            return (Boolean) map.get(path);
        }
    }

    public boolean getBooleanValue(String path) {
        return getBooleanValue(path, this.map);
    }

    public boolean getBooleanValue(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getBooleanValue(remainingPath, (Map) value);
            } else {
                return ((Boolean) value).booleanValue();
            }
        } else {
            return ((Boolean) map.get(path)).booleanValue();
        }
    }

    public List<Boolean> getBooleanList(String path) {
        return getBooleanList(path, this.map);
    }

    public List<Boolean> getBooleanList(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getBooleanList(remainingPath, (Map) value);
            } else {
                return (List) value;
            }
        } else {
            return (List) map.get(path);
        }
    }

    public Long getLong(String path) {
        return getLong(path, this.map);
    }

    public Long getLong(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getLong(remainingPath, (Map) value);
            } else {
                return Long.parseLong(String.valueOf(NumberConversions.toLong(value)));
            }
        } else {
            return Long.parseLong(String.valueOf(NumberConversions.toLong(map.get(path))));
        }
    }

    public long getLongValue(String path) {
        return getLongValue(path, this.map);
    }

    public long getLongValue(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getLongValue(remainingPath, (Map) value);
            } else {
                return NumberConversions.toLong(map.get(path));
            }
        } else {
            return NumberConversions.toLong(map.get(path));
        }
    }

    public List<Long> getLongList(String path) {
        return getLongList(path, this.map);
    }

    public List<Long> getLongList(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getLongList(remainingPath, (Map) value);
            } else {
                return (List) value;
            }
        } else {
            return (List) map.get(path);
        }
    }

    public Short getShort(String path) {
        return getShort(path, this.map);
    }

    public Short getShort(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getShort(remainingPath, (Map) value);
            } else {
                return Short.parseShort(String.valueOf(NumberConversions.toShort(value)));
            }
        } else {
            return Short.parseShort(String.valueOf(NumberConversions.toShort(map.get(path))));
        }
    }

    public short getShortValue(String path) {
        return getShortValue(path, this.map);
    }

    public short getShortValue(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getShortValue(remainingPath, (Map) value);
            } else {
                return NumberConversions.toShort(value);
            }
        } else {
            return NumberConversions.toShort(map.get(path));
        }
    }

    public List<Short> getShortList(String path) {
        return getShortList(path, this.map);
    }

    public List<Short> getShortList(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getShortList(remainingPath, (Map) value);
            } else {
                return (List) value;
            }
        } else {
            return (List) map.get(path);
        }
    }

    public String getString(String path) {
        return getString(path, this.map);
    }

    public String getString(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getString(remainingPath, (Map) value);
            } else {
                return String.valueOf(value);
            }
        } else {
            return String.valueOf(map.get(path));
        }
    }

    public List<String> getStringList(String path) {
        return getStringList(path, this.map);
    }

    public List<String> getStringList(String path, Map<String, Object> map) {
        if (path.contains(".")) {
            int index = path.indexOf(".");
            String currentKey = path.substring(0, index);
            String remainingPath = path.substring(index + 1);
            Object value = map.get(currentKey);
            if (value instanceof Map) {
                return getStringList(remainingPath, (Map) value);
            } else {
                return (List) value;
            }
        } else {
            return (List) map.get(path);
        }
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public Set<Map.Entry<String, Object>> entrySet() {
        return getMap().entrySet();
    }

    public Set<String> keySet() {
        return getMap().keySet();
    }

    public Collection<Object> values() {
        return getMap().values();
    }

    public Collection<Object> size() {
        return Collections.singleton(getMap().size());
    }
}
