package org.me.mobilesecurity.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {
    private final static String NAME = "mobilesafe";

    private static SharedPreferences preferences;

    private static SharedPreferences getPreferences(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(NAME,
                    Context.MODE_PRIVATE);
        }
        return preferences;
    }

    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    public static boolean getBoolean(Context context, String key,
                                     boolean defValue) {
        SharedPreferences preferences = getPreferences(context);
        return preferences.getBoolean(key, defValue);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences preferences = getPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
}
