/**
 * 
 */
package com.skocken.rclibrary;

import android.annotation.TargetApi;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.util.Log;

/**
 * Object used for modifying values in a {@link RCPreference} object. All changes you make in an editor are batched, and not copied
 * back to the original {@link RCPreference} until you call {@link #commit} or {@link #apply}
 */
public class RCEditor {
    /** Used locally to tag Logs */
    private static final String TAG = RCEditor.class.getSimpleName();

    private Editor              mEditor;

    public RCEditor(Editor editor) {
        mEditor = editor;
    }

    /**
     * Set a Object value in the preferences editor, to be written back once {@link #commit} or {@link #apply} are called.
     * 
     * @param value
     *            The new value for the preference. This object have to be an instance of Integer, String, Long, Boolean, Float or Double;
     * @param keys
     *            The name of the preference to retrieve. You can use a multilevel of keys.
     * 
     * @return Returns a reference to the same RCEditor object, so you can
     *         chain put calls together.
     * 
     * @throws ClassCastException
     */
    public RCEditor putObject(Object value, String... keys) {
        if(value == null) {
            putString(null, keys);
        } else if (value instanceof Integer) {
            putInt(((Integer) value).intValue(), keys);
        } else if (value instanceof String) {
            putString((String) value, keys);
        } else if (value instanceof Long) {
            putLong(((Long) value).longValue(), keys);
        } else if (value instanceof Boolean) {
            putBoolean(((Boolean) value).booleanValue(), keys);
        } else if (value instanceof Double) {
            putFloat(((Double) value).floatValue(), keys);
        } else if (value instanceof Float) {
            putFloat(((Float) value).floatValue(), keys);
        } else if (RCPreference.isDebug()) {
            Log.e(TAG, "RCLibrary : You try to store a value into RCPreference which is not an instance of Integer, String, Long, Boolean, Float or Double : "+value);
        }
        return this;
    }

    /**
     * Set a String value in the preferences editor, to be written back once {@link #commit} or {@link #apply} are called.
     * 
     * @param value
     *            The new value for the preference.
     * @param keys
     *            The name of the preference to retrieve. You can use a multilevel of keys.
     * 
     * @return Returns a reference to the same RCEditor object, so you can
     *         chain put calls together.
     */
    public RCEditor putString(String value, String... keys) {
        mEditor.putString(RCPreference.convertKey(keys), value);
        return this;
    }

    /**
     * Set an int value in the preferences editor, to be written back once {@link #commit} or {@link #apply} are called.
     * 
     * @param value
     *            The new value for the preference.
     * @param keys
     *            The name of the preference to retrieve. You can use a multilevel of keys.
     * 
     * @return Returns a reference to the same RCEditor object, so you can
     *         chain put calls together.
     */
    public RCEditor putInt(int value, String... keys) {
        mEditor.putInt(RCPreference.convertKey(keys), value);
        return this;
    }

    /**
     * Set a long value in the preferences editor, to be written back once {@link #commit} or {@link #apply} are called.
     * 
     * @param value
     *            The new value for the preference.
     * @param keys
     *            The name of the preference to retrieve. You can use a multilevel of keys.
     * 
     * @return Returns a reference to the same RCEditor object, so you can
     *         chain put calls together.
     */
    public RCEditor putLong(long value, String... keys) {
        mEditor.putLong(RCPreference.convertKey(keys), value);
        return this;
    }

    /**
     * Set a float value in the preferences editor, to be written back once {@link #commit} or {@link #apply} are called.
     * 
     * @param value
     *            The new value for the preference.
     * @param keys
     *            The name of the preference to retrieve. You can use a multilevel of keys.
     * 
     * @return Returns a reference to the same RCEditor object, so you can
     *         chain put calls together.
     */
    public RCEditor putFloat(float value, String... keys) {
        mEditor.putFloat(RCPreference.convertKey(keys), value);
        return this;
    }

    /**
     * Set a boolean value in the preferences editor, to be written back
     * once {@link #commit} or {@link #apply} are called.
     * 
     * @param value
     *            The new value for the preference.
     * @param keys
     *            The name of the preference to retrieve. You can use a multilevel of keys.
     * 
     * @return Returns a reference to the same RCEditor object, so you can
     *         chain put calls together.
     */
    public RCEditor putBoolean(boolean value, String... keys) {
        mEditor.putBoolean(RCPreference.convertKey(keys), value);
        return this;
    }

    /**
     * Mark in the editor that a preference value should be removed, which
     * will be done in the actual preferences once {@link #commit} is
     * called.
     * 
     * <p>
     * Note that when committing back to the preferences, all removals are done first, regardless of whether you called remove before or
     * after put methods on this editor.
     * 
     * @param keys
     *            The name of the preference to retrieve. You can use a multilevel of keys.
     * 
     * @return Returns a reference to the same RCEditor object, so you can
     *         chain put calls together.
     */
    public RCEditor remove(String... keys) {
        mEditor.remove(RCPreference.convertKey(keys));
        return this;
    }

    /**
     * Mark in the editor to remove <em>all</em> values from the
     * preferences. Once commit is called, the only remaining preferences
     * will be any that you have defined in this editor.
     * 
     * <p>
     * Note that when committing back to the preferences, the clear is done first, regardless of whether you called clear before or after
     * put methods on this editor.
     * 
     * @return Returns a reference to the same RCEditor object, so you can
     *         chain put calls together.
     */
    public RCEditor clear() {
        mEditor.clear();
        return this;
    }

    /**
     * Commit your preferences changes back from this RCEditor to the {@link RCPreference} object it is editing. This atomically
     * performs the requested modifications, replacing whatever is currently
     * in the RCPreference.
     * 
     * <p>
     * Note that when two editors are modifying preferences at the same time, the last one to call commit wins.
     * 
     * <p>
     * If you don't care about the return value and you're using this from your application's main thread, consider using {@link #apply}
     * instead.
     * 
     * @return Returns true if the new values were successfully written
     *         to persistent storage.
     */
    public boolean commit() {
        return mEditor.commit();
    }

    /**
     * Commit your preferences changes back from this RCEditor to the {@link RCPreference} object it is editing. This atomically
     * performs the requested modifications, replacing whatever is currently
     * in the RCPreference.
     * 
     * <p>
     * Note that when two editors are modifying preferences at the same time, the last one to call apply wins.
     * 
     * <p>
     * Unlike {@link #commit}, which writes its preferences out to persistent storage synchronously, {@link #apply} commits its changes to
     * the in-memory {@link RCPreference} immediately but starts an asynchronous commit to disk and you won't be notified of any failures.
     * If another editor on this {@link RCPreference} does a regular {@link #commit} while a {@link #apply} is still outstanding, the
     * {@link #commit} will block until all async commits are completed as well as the commit itself.
     * 
     * <p>
     * As {@link RCPreference} instances are singletons within a process, it's safe to replace any instance of {@link #commit} with
     * {@link #apply} if you were already ignoring the return value.
     * 
     * <p>
     * You don't need to worry about Android component lifecycles and their interaction with <code>apply()</code> writing to disk. The
     * framework makes sure in-flight disk writes from <code>apply()</code> complete before switching states.
     * 
     * <p class='note'>
     * The RCPreference.RCEditor interface isn't expected to be implemented directly. However, if you previously did implement it and are
     * now getting errors about missing <code>apply()</code>, you can simply call {@link #commit} from <code>apply()</code>.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void apply() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            if (RCPreference.isDebug()) {
                Log.w(TAG, "RCLibrary : You're using the method \"apply()\" on a device pre-gimgerbread, instead of this, we have to call the method commit().");
            }
            commit();
        } else {
            mEditor.apply();
        }
    }
}
