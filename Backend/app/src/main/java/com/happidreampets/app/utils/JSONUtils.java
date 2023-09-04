package com.happidreampets.app.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtils {
    public static String optString(JSONObject data, String key, String defaultValue) {
        if (data.has(key)) {
            return data.get(key).toString();
        } else {
            return defaultValue;
        }
    }

    public static Long optLong(JSONObject data, String key, Long defaultValue) {
        if (data.has(key)) {
            return Long.valueOf(data.get(key).toString());
        } else {
            return defaultValue;
        }
    }

    public static Integer optInteger(JSONObject data, String key, Integer defaultValue) {
        if (data.has(key)) {
            return Integer.valueOf(data.get(key).toString());
        } else {
            return defaultValue;
        }
    }

    public static <T extends Enum<T>> T optEnum(JSONObject data, String key, Class<T> enumClass) {
        if (data.has(key)) {
            String value = data.optString(key);
            try {
                return Enum.valueOf(enumClass, value);
            } catch (IllegalArgumentException e) {
                // Handle the case when the value does not match any enum constant
                return null;
            }
        } else {
            return null;
        }
    }

    public static JSONArray convertListToJSONArray(List<Map<String, Object>> data) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(data);
        return new JSONArray(json);
    }

    public static JSONObject convertMapToJSONObject(Map<String, Object> data) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(data);
        return new JSONObject(json);
    }

    public static List<Long> convertJSONToListLong(JSONObject data, String key) throws Exception {
        List<Long> returnData = new ArrayList<>();
        if (data.has(key)) {
            JSONArray values = data.getJSONArray(key);
            for (Object item : values) {
                returnData.add(Long.valueOf("" + item));
            }
        }
        return returnData;
    }
}
