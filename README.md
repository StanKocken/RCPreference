RCPreference
============

Allow your application to be configure by a remote server

What is RCPrefence ?
============

RCPreference is a library similar to SharePreference but where any value can be set by a remote server from a JSON.

From example you can set a default value to the key "my_test_key" but which can be automatically changed by a server side when you want.

This system support also a muli-level key.

How to use it ?
============

RCPreference is an Android Library but it can be used also as a .jar.

You can used it like the SharePreference. Becarefull, with the multi-level key, the order of your default value and the key is invert.

Example
============

An example of JSON can be found from this page : http://stankocken.com/json_test.php


    {
	date_time: "2013-01-06 16:39:06",
	tst_int: 88,
	tst_string: "I'm your test value",
	tst_boolean: true,
	tst_null: null,
	tst_tab: {
		tst_sub_string: "I'm a child",
		tst_sub_tab: {
			test_sub_sub_string: "I'm a child of your child"
		}
	},
	tst_float: 3.14
    }



From you're code you can get a value from : 


    // get a remote preference
    RCPreference rcp = RCPreference.getRCPreference(this);
    // get the value of time
    String dateTime1 = rcp.getString("No value", "date_time"); // "No value" is the default value
    // get the sub sub value of a string
    String test1 = rcp.getString("No value", "tst_tab", "tst_sub_tab", "test_sub_sub_string");



You just have to launch your download from your code by calling:


    RCPreference.downloadFromUrl(this, "http://stankocken.com/json_test.php", true);



With the last parameters, set to "true" in this example, you choose to apply the preferences downloaded when you call:


    RCPreference.loadPendingToCurrent(this);




