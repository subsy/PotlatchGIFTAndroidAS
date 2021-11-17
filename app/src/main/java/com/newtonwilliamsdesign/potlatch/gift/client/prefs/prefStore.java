package com.newtonwilliamsdesign.potlatch.gift.client.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bwilliams on 16/11/2014.
 */
public class prefStore {
    private SharedPreferences sharedPreferences;

    public prefStore() {
        // Blank
    }

    private static SharedPreferences getPrefs(Context context, String prefName) {
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public static boolean getDisplayFlagged(Context context, String prefName) {
        return getPrefs(context, prefName).getBoolean("displayFlagged", false);
    }

    public static void setDisplayFlagged(Context context, String prefName, boolean showFlagged) {
        SharedPreferences.Editor editor = getPrefs(context, prefName).edit();
        editor.putBoolean("displayFlagged", showFlagged);
        editor.commit();
    }

    public static String getUpdateFreq(Context context, String prefName) {
        return getPrefs(context, prefName).getString("updateFreq", "-1");
    }

    public static void setUpdateFreq(Context context, String prefName, String updateFreq) {
        SharedPreferences.Editor editor = getPrefs(context, prefName).edit();
        editor.putString("updateFreq", updateFreq);
        editor.commit();
    }

    public static String getUsername(Context context, String prefName) {
        return getPrefs(context, prefName).getString("Username", "");
    }

    public static void setUsername(Context context, String prefName, String username) {
        SharedPreferences.Editor editor = getPrefs(context, prefName).edit();
        editor.putString("Username", username);
        editor.commit();
    }

    public static String getAccessToken(Context context, String prefName) {
        return getPrefs(context, prefName).getString("accessToken", "");
    }

    public static void setAccessToken(Context context, String prefName, String accessToken) {
        SharedPreferences.Editor editor = getPrefs(context, prefName).edit();
        editor.putString("accessToken", accessToken);
        editor.commit();
    }
}
