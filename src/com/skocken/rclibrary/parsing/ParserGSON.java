/**
 * 
 */
package com.skocken.rclibrary.parsing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.skocken.rclibrary.RCEditor;
import com.skocken.rclibrary.RCPreference;

/**
 * @author Stan Kocken (stan.kocken@gmail.com)
 * 
 */
public class ParserGSON {
    /** Used locally to tag Logs */
    private static final String TAG = ParserGSON.class.getSimpleName();

    public static void parseAndSave(Context context, String json, boolean loadInPendingPreference) throws JSONException {
        if (json != null) {
            RCPreference rcPref = RCPreference.getRCPreference(context);
            rcPref.setPendingMode(loadInPendingPreference);
            RCEditor editor = rcPref.edit();
            parseObject(editor, new JSONObject(json), new ArrayList<String>());
            editor.apply();
            if (RCPreference.isDebug()) {
                Log.v(TAG, "RCLibrary : JSON saved");
            }
        } else if (RCPreference.isDebug()) {
            Log.w(TAG, "RCLibrary : No JSON to save");
        }
    }

    private static void parseObject(RCEditor editor, JSONObject jsonObject, List<String> keysBefore) throws JSONException {
        Iterator<?> iterator = jsonObject.keys();
        if (iterator != null) {
            while (iterator.hasNext()) {
                Object key = iterator.next();
                if (key instanceof String) {
                    List<String> newKeys = new ArrayList<String>();
                    newKeys.addAll(keysBefore);
                    newKeys.add((String) key);

                    Object value = jsonObject.opt((String) key);
                    if (RCPreference.isDebug()) {
                        Log.v(TAG, "RCLibrary : value (" + key + " : " + value.getClass().getSimpleName() + " : " + value + ")");
                    }
                    if (jsonObject.isNull((String) key)) {
                        editor.putObject((String) null, RCPreference.convertKey(newKeys));
                    } else if (value instanceof JSONArray) {
                        if (RCPreference.isDebug()) {
                            Log.e(TAG, "RCLibrary : We cannot unserialize an array into preference");
                        }
                    } else if (value instanceof JSONObject) {
                        parseObject(editor, (JSONObject) value, newKeys);
                    } else {
                        editor.putObject(value, RCPreference.convertKey(newKeys));
                    }
                }
            }
        }
    }
}
