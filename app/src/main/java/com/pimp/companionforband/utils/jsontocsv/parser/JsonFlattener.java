package com.pimp.companionforband.utils.jsontocsv.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonFlattener {
    public LinkedHashMap<String, String> parse(JSONObject jsonObject) {
        LinkedHashMap<String, String> flatJson = new LinkedHashMap<>();
        flatten(jsonObject, flatJson, "");
        return flatJson;
    }

    public List<LinkedHashMap<String, String>> parse(JSONArray jsonArray) {
        List<LinkedHashMap<String, String>> flatJson = new ArrayList<>();
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                LinkedHashMap<String, String> stringMap = parse(jsonObject);
                flatJson.add(stringMap);
            } catch (Exception e) {
                Log.e("parseJson", e.toString());
            }
        }
        return flatJson;
    }

    public List<LinkedHashMap<String, String>> parseJson(String json) throws Exception {
        List<LinkedHashMap<String, String>> flatJson;
        try {
            JSONObject jsonObject = new JSONObject(json);
            flatJson = new ArrayList<>();
            flatJson.add(parse(jsonObject));
        } catch (JSONException je) {
            flatJson = handleAsArray(json);
        }
        return flatJson;
    }

    private List<LinkedHashMap<String, String>> handleAsArray(String json) throws Exception {
        List<LinkedHashMap<String, String>> flatJson;
        try {
            JSONArray jsonArray = new JSONArray(json);
            flatJson = parse(jsonArray);
        } catch (Exception e) {
            throw new Exception("Json might be malformed");
        }
        return flatJson;
    }

    private void flatten(JSONArray obj, Map<String, String> flatJson, String prefix) {
        int length = obj.length();
        for (int i = 0; i < length; i++) {
            try {
                if (obj.get(i).getClass() == JSONArray.class) {
                    JSONArray jsonArray = (JSONArray) obj.get(i);
                    if (jsonArray.length() < 1) continue;
                    flatten(jsonArray, flatJson, prefix + i);
                } else if (obj.get(i).getClass() == JSONObject.class) {
                    JSONObject jsonObject = (JSONObject) obj.get(i);
                    flatten(jsonObject, flatJson, prefix + (i + 1));
                } else {
                    String value = obj.getString(i);
                    if (value != null)
                        flatJson.put(prefix + (i + 1), value);
                }
            } catch (Exception e) {
                Log.e("flattenJsonArray", e.toString());
            }
        }
    }

    private void flatten(JSONObject obj, Map<String, String> flatJson, String prefix) {
        Iterator iterator = obj.keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            try {
                if (obj.get(key).getClass() == JSONObject.class) {
                    JSONObject jsonObject = (JSONObject) obj.get(key);
                    flatten(jsonObject, flatJson, prefix);
                } else if (obj.get(key).getClass() == JSONArray.class) {
                    JSONArray jsonArray = (JSONArray) obj.get(key);
                    if (jsonArray.length() < 1) continue;
                    flatten(jsonArray, flatJson, key);
                } else {
                    String value = obj.getString(key);
                    if (value != null && !value.equals("null"))
                        flatJson.put(prefix + key, value);
                }
            } catch (Exception e) {
                Log.e("flattenJson", e.toString());
            }
        }
    }
}

