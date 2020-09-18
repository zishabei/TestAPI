package com.example.testapi;

import androidx.ads.identifier.AdvertisingIdClient;
import androidx.ads.identifier.AdvertisingIdInfo;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActivityManager;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.app.usage.StorageStatsManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelUuid;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GetNewAPI";
    private ShowItemAdapter mAdapter;
    private MyBatteryBroadcastReciver myBatteryBroadcastReciver;
    TelephonyManager mTelephonyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        String[] requestPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this, requestPermission, 1000);
    }

    private void initView() {
        RecyclerView mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new ShowItemAdapter(new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        findViewById(R.id.startBtn).setOnClickListener(v -> start());
        findViewById(R.id.cleanBtn).setOnClickListener(v -> mAdapter.setNotifyData(new ArrayList<>()));
    }

    private void start() {
        //adid adTrackingEnable
        getAdid();
        //osType 固定値
        log("osType :", "Android");
        //buildVersion manufacturerName modelCode brandName
        getBuildInfo();
        //nfcEnable
        getNfcEnable();
        //memoryTotalSize availableRamSize totalInternalStorageSize freeInternalStorage totalExternalStorage freeExternalStorage
        getSize();
        //loadingAvgTimeBy1min loadingAvgTimeBy5min loadingAvgTimeBy15min
        getLoadingAvg();
        //cpuCoresNumber
        getCpuCoresNumber();
        //screenTimeoutTime brightnessLevelNumber
        getScreenInfo();
        //upTime wakeTime
        getTime();
        //pairedDevicesList
        getPairedDevicesList();
        //wallPagerID
        getWallPagerID();
        //batteryPresent batteryStatus batteryVoltage batteryTechnology batteryScale batteryHealth batteryTemperature
        getBatteryInfo();
        //userAgent
        getUserAgent();
        //wifiHotSpotEnable
        getWifiHotSpotEnable();
        //wifiFrequency
        getWifiFrequency();
        //applicationName
        getApplicationName();
        //cellConnectionStatus
        getCellConnectionStatus();
    }

    /**
     * 必要なdependency
     * dependencies {
     * //AdvertisingIdClient
     * implementation 'androidx.ads:ads-identifier:1.0.0-alpha01'
     * // Used for the calls to addCallback() in the snippets on this page.
     * implementation 'com.google.guava:guava:28.0-android'
     * }
     */
    private void getAdid() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            if (AdvertisingIdClient.isAdvertisingIdProviderAvailable(getApplicationContext())) {
                ListenableFuture<AdvertisingIdInfo> advertisingIdInfoListenableFuture =
                        AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                Futures.addCallback(advertisingIdInfoListenableFuture,
                        new FutureCallback<AdvertisingIdInfo>() {
                            @Override
                            public void onSuccess(AdvertisingIdInfo adInfo) {
                                String id = adInfo.getId();
                                boolean adTrackingEnable = adInfo.isLimitAdTrackingEnabled();
                                runOnUiThread(() -> {
                                    log("adid :", id);
                                    if (adTrackingEnable) {
                                        log("adTrackingEnable :", 1);
                                    } else {
                                        log("adTrackingEnable :", 0);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(@NonNull Throwable throwable) {
                                Log.e(TAG, "throwable:" + throwable.getMessage());
                            }
                        }, executorService);
            }
        });
    }

    private void getBuildInfo() {
        String buildVersion = Build.FINGERPRINT;
        log("buildVersion :", buildVersion);
        String manufacturerName = Build.MANUFACTURER;
        log("manufacturerName :", manufacturerName);
        String modelCode = Build.MODEL;
        log("modelCode :", modelCode);
        String brandName = Build.BRAND;
        log("brandName :", brandName);
    }

    private void getNfcEnable() {
        NfcAdapter nfcEnable = NfcAdapter.getDefaultAdapter(this);
        if (nfcEnable != null && nfcEnable.isEnabled()) {
            log("nfcEnable :", 1);
        } else {
            log("nfcEnable :", 0);
        }
    }

    private void getSize() {
        Map<String, String> map = new HashMap<>();
        try {
            Scanner s = new Scanner(new File("/proc/meminfo"));
            while (s.hasNextLine()) {
                String[] vals = s.nextLine().split(": ");
                if (vals.length > 1) map.put(vals[0].trim(), vals[1].trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        long swapTotal = 0;
        if (map.size() > 0 && map.containsKey("SwapTotal")) {
            String total = map.get("SwapTotal");
            if (total != null) {
                String substring = total.substring(0, total.indexOf(" "));
                swapTotal = Long.parseLong(substring) * 1024;
            }
        }
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        log("memoryRamSize :", (mi.totalMem + swapTotal));
        log("memoryTotalSize :", mi.totalMem);
        log("availableRamSize :", mi.availMem);

        StatFs statFsInternal = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        log("totalInternalStorageSize :", statFsInternal.getTotalBytes());
        log("freeInternalStorage :", statFsInternal.getAvailableBytes());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //API level 26
            StorageStatsManager storageStatsManager = (StorageStatsManager) getApplicationContext().getSystemService(Context.STORAGE_STATS_SERVICE);
            try {
                long totalExternalStorage = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT);
                long freeExternalStorage = storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT);
                log("totalExternalStorage :", totalExternalStorage);
                log("freeExternalStorage :", freeExternalStorage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            StatFs statFsExternal = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
            log("totalExternalStorage :", statFsExternal.getTotalBytes());
            log("freeExternalStorage :", statFsExternal.getAvailableBytes());
        }
    }

    private void getLoadingAvg() {
        try {
            Process exec = Runtime.getRuntime().exec("uptime");
            InputStream processInputStream = exec.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(processInputStream));
            String readStr = bufferedReader.readLine();
            String avgInfo = readStr.substring(readStr.indexOf("load average: ") + "load average: ".length());
            String[] avgArr = avgInfo.split(", ");
            if (avgArr.length == 3) {
                float loadingAvgTimeBy1min = Float.parseFloat(avgArr[0]);
                float loadingAvgTimeBy5min = Float.parseFloat(avgArr[1]);
                float loadingAvgTimeBy15min = Float.parseFloat(avgArr[2]);
                log("loadingAvgTimeBy1min :", loadingAvgTimeBy1min);
                log("loadingAvgTimeBy5min :", loadingAvgTimeBy5min);
                log("loadingAvgTimeBy15min :", loadingAvgTimeBy15min);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getCpuCoresNumber() {
        Runtime runtime = Runtime.getRuntime();
        int availableProcessors = runtime.availableProcessors();
        log("cpuCoresNumber :", availableProcessors);
    }

    private void getScreenInfo() {
        try {
            long screenTimeoutTime = Settings.System.getLong(getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
            log("screenTimeoutTime :", screenTimeoutTime);
            int brightnessLevelNumber = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
            log("brightnessLevelNumber :", brightnessLevelNumber);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void getTime() {
        long upTime = SystemClock.elapsedRealtime();
        log("upTime :", upTime);
        long wakeTime = SystemClock.uptimeMillis();
        log("wakeTime :", wakeTime);
        log("lastRestartedDateStr :", new Date(System.currentTimeMillis() - upTime));
    }

    /**
     * If Bluetooth state is not STATE_ON, this API will return an empty set.
     * Requires Manifest.permission.BLUETOOTH
     */
    private void getPairedDevicesList() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return;
        }
        boolean enabled = bluetoothAdapter.isEnabled();
        if (enabled) {
            Set<BluetoothDevice> pairedDeviceSet = bluetoothAdapter.getBondedDevices();
            JSONArray array = new JSONArray();
            JSONObject obj = new JSONObject();
            try {
                for (BluetoothDevice device : pairedDeviceSet) {
                    JSONObject object = new JSONObject();
                    object.put("name", device.getName());
                    object.put("type", device.getType());
                    object.put("address", device.getAddress());
                    object.put("bondState", device.getBondState());
                    ParcelUuid[] uuids = device.getUuids();
                    JSONArray arrayUuids = new JSONArray();
                    for (ParcelUuid uuid : uuids) {
                        JSONObject objectUuid = new JSONObject();
                        objectUuid.put("uuid", uuid.getUuid());
                        arrayUuids.put(objectUuid);
                    }
                    object.put("uuids", arrayUuids);
                    array.put(object);
                }
                obj.put("devicesList", array);
                log("pairedDevicesList :", obj.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getWallPagerID() {
        WallpaperManager instance = WallpaperManager.getInstance(this);
        WallpaperInfo wallpaperInfo = instance.getWallpaperInfo();
        if (wallpaperInfo == null) {
            log("wallPagerID :", 0);
        } else {
            log("wallPagerID :", 1);
        }
    }

    private void getBatteryInfo() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        myBatteryBroadcastReciver = new MyBatteryBroadcastReciver();
        registerReceiver(myBatteryBroadcastReciver, filter);
    }

    private class MyBatteryBroadcastReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                unregisterReceiver(myBatteryBroadcastReciver);
                boolean batteryPresent = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
                if (batteryPresent) {
                    log("batteryPresent :", 1);
                } else {
                    log("batteryPresent :", 0);
                }
                int batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                log("batteryStatus :", batteryStatus);
                int batteryVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                log("batteryVoltage :", batteryVoltage);

                BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
                int batteryNowCurrent = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                int batteryAvgCurrent = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);
                log("batteryNowCurrent :", batteryNowCurrent);
                log("batteryAvgCurrent :", batteryAvgCurrent);

                String batteryTechnology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
                log("batteryTechnology :", batteryTechnology);
                int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                log("batteryScale :", batteryScale);
                int batteryHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
                log("batteryHealth :", batteryHealth);
                int batteryTemperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                log("batteryTemperature :", batteryTemperature);
            }
        }
    }

    private void getUserAgent() {
        WebView mWebView = new WebView(this);
        String userAgent = mWebView.getSettings().getUserAgentString();
        log("userAgent :", userAgent);
    }

    private void getWifiHotSpotEnable() {
        WifiManager wifimanager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            Boolean inUse = (Boolean) method.invoke(wifimanager);
            if (inUse != null && inUse) {
                log("wifiHotSpotEnable :", 1);
            } else {
                log("wifiHotSpotEnable :", 0);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Requires Manifest.permission.ACCESS_WIFI_STATE
     */
    private void getWifiFrequency() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        float wifiFrequency = 0;
        int freq = wifiInfo.getFrequency();
        if (freq > 2400 && freq < 2500) {
            wifiFrequency = (float) 2.4;
        }
        if (freq > 4900 && freq < 5900) {
            wifiFrequency = (float) 5;
        }
        log("wifiFrequency :", wifiFrequency);
    }

    private void getApplicationName() {
        String applicationName = getPackageName();
        log("applicationName :", applicationName);
    }

    private void getCellConnectionStatus() {
        new Thread(() -> {
            mTelephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            List<CellInfo> allCellInfo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                CellInfoResultsCallback cellInfoResultsCallback = new CellInfoResultsCallback();
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                //request Manifest.permission.ACCESS_FINE_LOCATION
                mTelephonyManager.requestCellInfoUpdate(AsyncTask.THREAD_POOL_EXECUTOR, cellInfoResultsCallback);
                try {
                    cellInfoResultsCallback.waitCell();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                allCellInfo = cellInfoResultsCallback.cellInfo;
            } else {
                allCellInfo = mTelephonyManager.getAllCellInfo();
            }
            mTelephonyManager.getCellLocation();
            for (CellInfo cellInfo : allCellInfo) {
                if (cellInfo.isRegistered()) {
                    runOnUiThread(() -> {
                        // getCellConnectionStatus API level 28(P以降取得できる)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            log("cellConnectionStatus :", cellInfo.getCellConnectionStatus());
                        }
                    });
                }
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static class CellInfoResultsCallback extends TelephonyManager.CellInfoCallback {
        List<CellInfo> cellInfo;

        @Override
        public synchronized void onCellInfo(@NonNull List<CellInfo> cellInfo) {
            this.cellInfo = cellInfo;
            notifyAll();
        }

        synchronized void waitCell() throws InterruptedException {
            if (cellInfo == null) {
                super.wait(5000);
            }
        }
    }

    private void log(String key, Object value) {
        Log.i(TAG, key + value);
        mAdapter.addItem(key + "\n" + value);
    }
}