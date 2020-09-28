
package com.example.testapi;

import android.content.Context;
import android.net.TrafficStats;
import android.util.Log;

import com.example.testapi.sp.NetworkLibPreference;

import java.util.Map;

public class InfoNetworkTask implements Runnable {
    private static final String TAG = InfoNetworkTask.class.getSimpleName();
    private final Map<String, Long> mLogResult;
    private final Context mContext;

    public InfoNetworkTask(Context ctx,Map<String, Long> logResult) {
        mContext = ctx;
        mLogResult = logResult;
    }

    @Override
    public void run() {
        saveData(mLogResult,mContext);
    }

    private void saveData(Map<String, Long> logResult, Context ctx) {
        // traffic info
        Long mobileDownBytes = null;
        Long mobileUpBytes = null;
        Long wifiDownBytes = null;
        Long wifiUpBytes = null;
        long[] trafficStats = getTrafficStats(ctx);
        if (trafficStats != null && (trafficStats.length == 4)) {
            mobileDownBytes = trafficStats[0];
            mobileUpBytes = trafficStats[1];
            wifiDownBytes = trafficStats[2];
            wifiUpBytes = trafficStats[3];
            Log.d(TAG, "mobileDownBytes:" + String.valueOf(trafficStats[0]));
            Log.d(TAG, "mobileUpBytes:" + String.valueOf(trafficStats[1]));
            Log.d(TAG, "wifiDownBytes:" + String.valueOf(trafficStats[2]));
            Log.d(TAG, "wifiUpBytes:" + String.valueOf(trafficStats[3]));
        }
        logResult.put("mobileDownBytes", mobileDownBytes);
        logResult.put("mobileUpBytes", mobileUpBytes);
        logResult.put("wifiDownBytes", wifiDownBytes);
        logResult.put("wifiUpBytes", wifiUpBytes);
    }

    private long[] getTrafficStats(Context ctx) {
        long rxBytes = TrafficStats.getTotalRxBytes();
        long txBytes = TrafficStats.getTotalTxBytes();
        if ((rxBytes == -1L) || (txBytes == -1L)) {
            return null;
        }
        long mobileUpload = TrafficStats.getMobileTxBytes();
        long mobileDown = TrafficStats.getMobileRxBytes();
        if ((mobileUpload == -1L) || (mobileDown == -1L)) {
            return null;
        }

        long wifiUpload = txBytes - mobileUpload;
        if (wifiUpload < 0) {
            wifiUpload = 0L;
        }
        long wifiDown = rxBytes - mobileDown;
        if (wifiDown < 0) {
            wifiDown = 0L;
        }

        Log.d(TAG, "srcmobileUpload:" + String.valueOf(mobileUpload));
        Log.d(TAG, "srcmobileDownload:" + String.valueOf(mobileDown));
        Log.d(TAG, "srcwifiUpload:" + String.valueOf(wifiUpload));
        Log.d(TAG, "srcwifiDownload:" + String.valueOf(wifiDown));

        //Last time
        long lastMobileUpload = NetworkLibPreference.getMobileTrafficUp(ctx);
        long lastMobileDown = NetworkLibPreference.getMobileTrafficDown(ctx);
        long lastWifiUpload = NetworkLibPreference.getWifiTrafficUp(ctx);
        long lastWifiDown = NetworkLibPreference.getWifiTrafficDown(ctx);

        Log.d(TAG, "lastmobileUpload:" + String.valueOf(lastMobileUpload));
        Log.d(TAG, "lastmobileDownload:" + String.valueOf(lastMobileDown));
        Log.d(TAG, "lastwifiUpload:" + String.valueOf(lastWifiUpload));
        Log.d(TAG, "lastwifiDownload:" + String.valueOf(lastWifiDown));

        long diffMobileDown = mobileDown - lastMobileDown;
        if (diffMobileDown < 0) {
            diffMobileDown = mobileDown;
        }

        long diffMobileUpload = mobileUpload - lastMobileUpload;
        if (diffMobileUpload < 0) {
            diffMobileUpload = mobileUpload;
        }

        long diffWifiDown = wifiDown - lastWifiDown;
        if (diffWifiDown < 0) {
            diffWifiDown = wifiDown;
        }

        long diffWifiUpload = wifiUpload - lastWifiUpload;
        if (diffMobileUpload < 0) {
            diffMobileUpload = wifiUpload;
        }

        NetworkLibPreference.setMobileTrafficUp(ctx, mobileUpload);
        NetworkLibPreference.setMobileTrafficDown(ctx, mobileDown);
        NetworkLibPreference.setWifiTrafficUp(ctx, wifiUpload);
        NetworkLibPreference.setWifiTrafficDown(ctx, wifiDown);

        return new long[]{diffMobileDown, diffMobileUpload, diffWifiDown, diffWifiUpload};
    }
}
