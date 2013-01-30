/**
 * 
 */
package com.skocken.rclibrary;

import android.content.Context;

/**
 * A listener on changed on RCPreference
 * 
 * @author Stan Kocken (stan.kocken@gmail.com)
 * 
 */
public interface RCPreferenceUpdateListener {

    /**
     * Method called when the RCPreference has been changed
     * 
     * @param context
     *            The current context of this preference
     * @param rcPreference
     *            The RCPreference which receive this change
     * @param isStillPending
     *            true if changes was not already put into the current RCPreference. In this case we will wait a call on
     *            "loadPendingToCurrent()" from RCPreference
     */
    public void onRCPreferenceUpdated(Context context, RCPreference rcPreference, boolean isStillPending);
}
