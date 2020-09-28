
package com.example.testapi.sp;

import android.content.Context;
public class NetworkLibPreference {
    private static final String TAG = NetworkLibPreference.class.getSimpleName();
    private static final String PREF_TRAFFIC_MOBILE_UP = "pref_key_traffic_mobileup";
    private static final String PREF_TRAFFIC_MOBILE_DOWN = "pref_key_traffic_mobiledown";
    private static final String PREF_TRAFFIC_WIFI_DOWN = "pref_key_traffic_wifidown";
    private static final String PREF_TRAFFIC_WIFI_UP = "pref_key_traffic_wifiup";


    public static void setMobileTrafficDown(final Context context, long mobileTrafficDown) {
        NetworkLibPreferenceManager pref = new NetworkLibPreferenceManager(context);
        pref.putLong(PREF_TRAFFIC_MOBILE_DOWN, mobileTrafficDown);
    }

    public static long getMobileTrafficDown(final Context context) {
        NetworkLibPreferenceManager pref = new NetworkLibPreferenceManager(context);
        return pref.getLong(PREF_TRAFFIC_MOBILE_DOWN, 0);
    }

    public static void setMobileTrafficUp(final Context context, long mobileTrafficUp) {
        NetworkLibPreferenceManager pref = new NetworkLibPreferenceManager(context);
        pref.putLong(PREF_TRAFFIC_MOBILE_UP, mobileTrafficUp);
    }

    public static long getMobileTrafficUp(final Context context) {
        NetworkLibPreferenceManager pref = new NetworkLibPreferenceManager(context);
        return pref.getLong(PREF_TRAFFIC_MOBILE_UP, 0);
    }

    public static void setWifiTrafficDown(final Context context, long wifiTrafficDown) {
        NetworkLibPreferenceManager pref = new NetworkLibPreferenceManager(context);
        pref.putLong(PREF_TRAFFIC_WIFI_DOWN, wifiTrafficDown);
    }

    public static long getWifiTrafficDown(final Context context) {
        NetworkLibPreferenceManager pref = new NetworkLibPreferenceManager(context);
        return pref.getLong(PREF_TRAFFIC_WIFI_DOWN, 0);
    }

    public static void setWifiTrafficUp(final Context context, long wifiTrafficUp) {
        NetworkLibPreferenceManager pref = new NetworkLibPreferenceManager(context);
        pref.putLong(PREF_TRAFFIC_WIFI_UP, wifiTrafficUp);
    }

    public static long getWifiTrafficUp(final Context context) {
        NetworkLibPreferenceManager pref = new NetworkLibPreferenceManager(context);
        return pref.getLong(PREF_TRAFFIC_WIFI_UP, 0);
    }
}
