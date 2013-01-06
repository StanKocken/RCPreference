package com.example.testrcp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.skocken.rclibrary.RCPreference;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textview = (TextView) findViewById(R.id.textview);

        // set the debug mode to true to enable the log
        RCPreference.setDebug(BuildConfig.DEBUG);

        // get a remote preference
        RCPreference rcp = RCPreference.getRCPreference(this);
        // get the value of time
        String dateTime1 = rcp.getString("No value", "date_time");
        // get the sub sub value of a string
        String test1 = rcp.getString("No value", "tst_tab", "tst_sub_tab", "test_sub_sub_string");

        // load the pending preference to the current (previously download by the method "downloadFromUrl")
        RCPreference.loadPendingToCurrent(this);
        // load some preferences from an URL (from a test website)
        RCPreference.downloadFromUrl(this, "http://stankocken.com/json_test.php", true);

        // get the same value than previously but after a load from pending
        String test2 = rcp.getString("No value", "tst_tab", "tst_sub_tab", "test_sub_sub_string");
        String dateTime2 = rcp.getString("No value", "date_time");

        // display the result
        textview.setText(String.format("dataTime1 : %s \n val : %s \n \n \n dataTime2 : %s \n val : %s", dateTime1, test1, dateTime2, test2));
    }

}
