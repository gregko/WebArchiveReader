package com.hyperionics.war_test;

/**
 * Created by Greg Kochaniak, http://www.hyperionics.com
 * License: public domain.
 */
import android.util.Log;

public class Lt {
    private static String myTag = "war_test";
    private Lt() {}
    static void setTag(String tag) { myTag = tag; }
    public static void d(String msg) {
        // Uncomment line below to turn on debug output
        Log.d(myTag, msg == null ? "(null)" : msg);
    }
    public static void df(String msg) {
        // Forced output, do not comment out - for exceptions etc.
        Log.d(myTag, msg == null ? "(null)" : msg);
    }
}