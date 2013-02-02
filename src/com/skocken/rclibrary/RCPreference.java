/**
 * 
 */
package com.skocken.rclibrary;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.skocken.rclibrary.caller.HTTPCaller;
import com.skocken.rclibrary.parsing.ParserGSON;

/**
 * @author Stan Kocken (stan.kocken@gmail.com)
 * 
 */
public class RCPreference {
    /** Used locally to tag Logs */
    private static final String        TAG                                = RCPreference.class.getSimpleName();
    private static final String        SHARED_PREFERENCES_NAME            = "com.skocken.rclibrary.RCPreference";
    private static final String        SHARED_PREFERENCES_NAME_NEXTLAUNCH = "com.skocken.rclibrary.RCPreferenceNEXTLAUNCH";
    private static final String        SEPARATOR                          = "|-oRCo->|";
    private static final StringBuilder sStringBuilder                     = new StringBuilder();

    public static boolean              sDebugMode;

    private Context                    mContext;
    private SharedPreferences          mSharePreference;

    private RCEditor                   mEditor;

    private boolean                    mSaveInPending                     = false;

    /**
     * Constructor private
     */
    private RCPreference() {
    }

    /**
     * Get a RCPreference
     * 
     * @param context
     *            A context to retrieve the preference data
     * @return RCPreference for this context
     */
    public static RCPreference getRCPreference(Context context) {
        RCPreference pref = new RCPreference();
        pref.mContext = context;
        return pref;
    }

    /**
     * Set the debug mode to display the logs
     * 
     * @param debug
     *            set to true to display the logs
     */
    public static void setDebug(boolean debug) {
        sDebugMode = debug;
    }

    /**
     * Determine if the debug is set to true or not
     * 
     * @return true if the system should display the logs
     */
    public static boolean isDebug() {
        return sDebugMode;
    }

    /**
     * Download some preference by the URL given
     * 
     * @param context
     *            The context to retrieve the preference
     * @param url
     *            The URL of the file to load the JSON preference
     * @param loadAndWaitToApply
     *            set to false if you want the current value into preference as soon as possible. If you set this to true, you should use
     *            "loadPendingToPending(Context context)" when you want to apply the previous download from your URL.
     */
    public static void downloadFromUrl(final Context context, final String url, final boolean loadAndWaitToApply) {
        setRCPreferenceByURL(context, url, loadAndWaitToApply);
    }

    /**
     * Download some preference by the URL given
     * 
     * @param context
     *            The context to retrieve the preference
     * @param url
     *            The URL of the file to load the JSON preference
     * @param loadAndWaitToApply
     *            set to false if you want the current value into preference as soon as possible. If you set this to true, you should use
     *            "loadPendingToPending(Context context)" when you want to apply the previous download from your URL.
     */
    public static void setRCPreferenceByURL(final Context context, final String url, final boolean loadAndWaitToApply) {
        setRCPreferenceByURL(context, url, loadAndWaitToApply, null);
    }

    /**
     * Download some preference by the URL given
     * 
     * @param context
     *            The context to retrieve the preference
     * @param url
     *            The URL of the file to load the JSON preference
     * @param loadAndWaitToApply
     *            set to false if you want the current value into preference as soon as possible. If you set this to true, you should use
     *            "loadPendingToPending(Context context)" when you want to apply the previous download from your URL.
     * @param listener
     *            The listener to notify when it's over
     */
    public static void setRCPreferenceByURL(final Context context, final String url, final boolean loadAndWaitToApply, final RCPreferenceUpdateListener listener) {
        if (sDebugMode) {
            Log.v(TAG, "RCLibrary : downloadFromUrl (load and wait ? " + loadAndWaitToApply + ") : " + url);
        }
        (new Thread() {
            @Override
            public void run() {
                if (sDebugMode) {
                    Log.v(TAG, "RCLibrary : start download");
                }
                String json = HTTPCaller.loadFromUrl(url);
                if (sDebugMode) {
                    Log.v(TAG, "RCLibrary : end download : " + json);
                }
                setRCPreferenceByJSON(context, json, loadAndWaitToApply, listener);
            }
        }).start();
    }

    /**
     * Apply some preference from the JSON given
     * 
     * @param context
     *            The context to retrieve the preference
     * @param json
     *            The JSON to load into preference
     * @param loadAndWaitToApply
     *            set to false if you want the current value into preference as soon as possible. If you set this to true, you should use
     *            "loadPendingToPending(Context context)" when you want to apply the previous download from your URL.
     */
    public static void setRCPreferenceByJSON(final Context context, final String json, final boolean loadAndWaitToApply) {
        setRCPreferenceByJSON(context, json, loadAndWaitToApply, null);
    }

    /**
     * Apply some preference from the JSON given
     * 
     * @param context
     *            The context to retrieve the preference
     * @param json
     *            The JSON to load into preference
     * @param loadAndWaitToApply
     *            set to false if you want the current value into preference as soon as possible. If you set this to true, you should use
     *            "loadPendingToPending(Context context)" when you want to apply the previous download from your URL.
     * @param listener
     *            The listener to notify when it's over
     */
    public static void setRCPreferenceByJSON(final Context context, final String json, final boolean loadAndWaitToApply, RCPreferenceUpdateListener listener) {
        try {
            ParserGSON.parseAndSave(context, json, loadAndWaitToApply, listener);
        } catch (JSONException e) {
            if (sDebugMode) {
                Log.v(TAG, "RCLibrary : ", e);
            }
        }
    }

    /**
     * Load pending data to store it on current data
     * 
     * @param context
     *            A context to retrieve the preference data
     * @return true if some data has been retrieve and store to the current preference
     */
    public static boolean loadPendingToCurrent(Context context) {
        // retrieve the preference pending
        RCPreference prefPending = getRCPreference(context);
        prefPending.setPendingMode(true);

        // retrieve all data from the pending preference
        Map<String, ?> mapPrefPending = prefPending.getSP().getAll();
        if (mapPrefPending != null && mapPrefPending.size() > 0) {
            // the list of keys
            Set<String> set = mapPrefPending.keySet();

            RCEditor editorPrefNow = getRCPreference(context).edit();
            for (String key : set) {
                // for each key, save the value from pending preference to the current preference
                editorPrefNow.putObject(mapPrefPending.get(key), key);
            }

            // save the current preference
            editorPrefNow.apply();
            // clear the pending preference
            prefPending.edit().clear().apply();

            // we find some data to save
            return true;
        }
        // no pending data
        return false;
    }

    /**
     * Set if you want to use the pending cache or the current one
     * 
     * @param pendingMode
     *            true if you want to use the pending cache
     */
    public void setPendingMode(boolean pendingMode) {
        if (mSaveInPending ^ pendingMode) {
            // the current value is different than the new value, reset the current share preference
            mSharePreference = null;
            mSaveInPending = pendingMode;
        }
    }

    /**
     * Get the SharedPreference associate with this RCPreference
     * 
     * @return SharedPreference
     */
    private SharedPreferences getSP() {
        if (mSharePreference == null && mContext != null) {
            String prefName;
            if (mSaveInPending) {
                prefName = SHARED_PREFERENCES_NAME_NEXTLAUNCH;
            } else {
                prefName = SHARED_PREFERENCES_NAME;
            }
            mSharePreference = mContext.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        }
        return mSharePreference;
    }

    /**
     * Checks whether the preferences contains a preference.
     * 
     * @param keys
     *            The name of the preference to retrieve. You can use a multi-
     *            level of keys.
     * @return Returns true if the preference exists in the preferences,
     *         otherwise false.
     */
    public boolean contains(String... keys) {
        return getSP().contains(convertKey(keys));
    }

    /**
     * Retrieve a boolean value from the preferences.
     * 
     * @param defValue
     *            Value to return if this preference does not exist.
     * @param keys
     *            The name of the preference to retrieve. You can use a multi-
     *            level of keys.
     * 
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that is not
     *         a boolean.
     * 
     * @throws ClassCastException
     */
    public boolean getBoolean(boolean defValue, String... keys) {
        return getSP().getBoolean(convertKey(keys), defValue);
    }

    /**
     * Retrieve a float value from the preferences.
     * 
     * @param defValue
     *            Value to return if this preference does not exist.
     * @param keys
     *            The name of the preference to retrieve. You can use a multi-
     *            level of keys.
     * 
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that is not
     *         a float.
     * 
     * @throws ClassCastException
     */
    public float getFloat(float defValue, String... keys) {
        return getSP().getFloat(convertKey(keys), defValue);
    }

    /**
     * Retrieve an int value from the preferences.
     * 
     * @param defValue
     *            Value to return if this preference does not exist.
     * @param keys
     *            The name of the preference to retrieve. You can use a multi-
     *            level of keys.
     * 
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that is not
     *         an int.
     * 
     * @throws ClassCastException
     */
    public int getInt(int defValue, String... keys) {
        return getSP().getInt(convertKey(keys), defValue);
    }

    /**
     * Retrieve a long value from the preferences.
     * 
     * @param defValue
     *            Value to return if this preference does not exist.
     * @param keys
     *            The name of the preference to retrieve. You can use a multi-
     *            level of keys.
     * 
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that is not
     *         a long.
     * 
     * @throws ClassCastException
     */
    public long getLong(long defValue, String... keys) {
        return getSP().getLong(convertKey(keys), defValue);
    }

    /**
     * Retrieve a String value from the preferences.
     * 
     * @param defValue
     *            Value to return if this preference does not exist.
     * @param keys
     *            The name of the preference to retrieve. You can use a multi-
     *            level of keys.
     * 
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that is not
     *         a String.
     * 
     * @throws ClassCastException
     */
    public String getString(String defValue, String... keys) {
        return getSP().getString(convertKey(keys), defValue);
    }

    /**
     * Retrieve a String value from the preferences.
     * 
     * @param defResIdValue
     *            Id of the String Resources to return if this preference does not exist.
     * @param keys
     *            The name of the preference to retrieve. You can use a multi-
     *            level of keys.
     * 
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that is not
     *         a String.
     * 
     * @throws ClassCastException
     */
    public String getString(int defResIdValue, String... keys) {
        String result = getString(null, keys);
        if (result == null) {
            return mContext.getString(defResIdValue);
        }
        return result;
    }

    /**
     * Retrieve a JSONObject value from the preferences.
     * 
     * @param defValue
     *            Value to return if this preference does not exist.
     * @param keys
     *            The name of the preference to retrieve. You can use a multi-
     *            level of keys.
     * 
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that is not
     *         a String.
     * 
     * @throws ClassCastException
     */
    public JSONArray getJSONArray(JSONArray defValue, String... keys) {
        String json = getSP().getString(convertKey(keys), null);
        if (json == null) {
            return defValue;
        }
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            if (RCPreference.isDebug()) {
                Log.w(TAG, e);
            }
            jsonArray = defValue;
        }
        return jsonArray;
    }

    /**
     * Retrieve a JSONObject value from the preferences.
     * 
     * @param defJSONValue
     *            JSON of the JSONArray to return if this preference does not exist.
     * @param keys
     *            The name of the preference to retrieve. You can use a multi-
     *            level of keys.
     * 
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that is not
     *         a String.
     * 
     * @throws ClassCastException
     */
    public JSONArray getJSONArray(String defJSONValue, String... keys) {
        String json = getSP().getString(convertKey(keys), null);
        if (json == null) {
            json = defJSONValue;
        }
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(json);
        } catch (JSONException e) {
            if (RCPreference.isDebug()) {
                Log.w(TAG, e);
            }
            jsonArray = null;
        }
        return jsonArray;
    }

    /**
     * Create a new RCEditor for these preferences, through which you can make
     * modifications to the data in the preferences and atomically commit those
     * changes back to the SharedPreferences object.
     * 
     * <p>
     * Note that you <em>must</em> call {@link RCEditor#apply} to have any changes you perform in the RCEditor actually show up in the
     * SharedPreferences.
     * 
     * @return Returns a new instance of the {@link RCEditor} interface, allowing
     *         you to modify the values in this RCPreference object.
     */
    public RCEditor edit() {
        if (mEditor == null) {
            mEditor = new RCEditor(getSP().edit());
        }
        return mEditor;
    }

    /**
     * Convert a list of keys and sub key to an unique key
     * 
     * @param keys
     *            list of key
     * @return the unique key associate
     */
    public static String convertKey(List<String> keys) {
        if (keys != null && keys.size() > 0) {
            if (keys.size() == 1) {
                return keys.get(0);
            } else {
                synchronized (sStringBuilder) {
                    sStringBuilder.setLength(0);
                    for (String key : keys) {
                        if (sStringBuilder.length() > 0) {
                            sStringBuilder.append(SEPARATOR);
                        }
                        sStringBuilder.append(key);
                    }
                    return sStringBuilder.toString();
                }
            }
        }
        return null;
    }

    /**
     * Convert a list of keys and sub key to an unique key
     * 
     * @param keys
     *            list of key
     * @return the unique key associate
     */
    public static String convertKey(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                return keys[0];
            } else {
                synchronized (sStringBuilder) {
                    sStringBuilder.setLength(0);
                    for (String key : keys) {
                        if (sStringBuilder.length() > 0) {
                            sStringBuilder.append(SEPARATOR);
                        }
                        sStringBuilder.append(key);
                    }
                    return sStringBuilder.toString();
                }
            }
        }
        return null;
    }
}
