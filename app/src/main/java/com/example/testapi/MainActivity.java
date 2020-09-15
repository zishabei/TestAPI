package com.example.testapi;

import androidx.ads.identifier.AdvertisingIdClient;
import androidx.ads.identifier.AdvertisingIdInfo;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Test";


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] requestPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        ActivityCompat.requestPermissions(this,
                requestPermission,
                123);

        //adid adTrackingEnable
        getAdid();
        //osType 固定値
        Log.i(TAG, "osType:" + "Android");
        //buildVersion manufacturerName modelCode brandName
        getBuildInfo();
        //nfcEnable
        getNfcEnable();
        //memoryTotalSize availableRamSize totalInternalStorageSize freeInternalStorage totalExternalStorage freeExternalStorage
        getSize();
        //cpuCoresNumber
        getCpuCoresNumber();
        //screenTimeoutTime brightnessLevelNumber
        getScreenInfo();
        //upTime wakeTime
        getTime();
        // TODO: 2020/09/15 再確認
        getWallPagerID();
        //batteryPresent batteryStatus batteryVoltage batteryTechnology batteryScale batteryHealth batteryTemperature
        getBatteryInfo();
        //wifiFrequency
        getWifiFrequency();
        //applicationName
        getApplicationName();
        //cellConnectionStatus
        getCellConnectionStatus();
    }

    private void getAdid() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (AdvertisingIdClient.isAdvertisingIdProviderAvailable(getApplicationContext())) {
                    ListenableFuture<AdvertisingIdInfo> advertisingIdInfoListenableFuture =
                            AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                    Futures.addCallback(advertisingIdInfoListenableFuture,
                            new FutureCallback<AdvertisingIdInfo>() {
                                @Override
                                public void onSuccess(AdvertisingIdInfo adInfo) {
                                    String id = adInfo.getId();
                                    Log.i(TAG, "adid:" + id);
                                    boolean adTrackingEnable = adInfo.isLimitAdTrackingEnabled();
                                    Log.i(TAG, "adTrackingEnable:" + adTrackingEnable);
                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                }
                            }, executorService);
                }
            }
        });
    }

    private void getBuildInfo() {
        String buildVersion = Build.FINGERPRINT;
        Log.i(TAG, "buildVersion:" + buildVersion);
        String manufacturerName = Build.MANUFACTURER;
        Log.i(TAG, "manufacturerName:" + manufacturerName);
        String modelCode = Build.MODEL;
        Log.i(TAG, "modelCode:" + modelCode);
        String brandName = Build.BRAND;
        Log.i(TAG, "brandName:" + brandName);
    }

    private void getNfcEnable() {
        NfcAdapter nfcEnable = NfcAdapter.getDefaultAdapter(this);
        Log.i(TAG, "nfcEnable:" + nfcEnable.isEnabled());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getSize() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        // TODO: 2020/09/15 memoryRamSize 再確認必要あり
        Log.i(TAG, "memoryTotalSize:" + mi.totalMem);
        Log.i(TAG, "availableRamSize:" + mi.availMem);

        StatFs statFsInternal = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        Log.i(TAG, "totalInternalStorageSize:" + statFsInternal.getTotalBytes());
        Log.i(TAG, "freeInternalStorage:" + statFsInternal.getAvailableBytes());

        StorageStatsManager storageStatsManager = (StorageStatsManager) getApplicationContext().getSystemService(Context.STORAGE_STATS_SERVICE);
        try {
            long totalExternalStorage = storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT);
            long freeExternalStorage = storageStatsManager.getFreeBytes(StorageManager.UUID_DEFAULT);
            Log.d(TAG, "totalExternalStorage:" + totalExternalStorage);
            Log.d(TAG, "freeExternalStorage:" + freeExternalStorage);
        } catch (IOException e) {
        }
    }

    private void getCpuCoresNumber() {
        Runtime runtime = Runtime.getRuntime();
        int availableProcessors = runtime.availableProcessors();
        Log.i(TAG, "cpuCoresNumber:" + availableProcessors);
    }

    private void getScreenInfo() {
        try {
            long screenTimeoutTime = Settings.System.getLong(getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
            Log.i(TAG, "screenTimeoutTime:" + screenTimeoutTime);
            int brightnessLevelNumber = Settings.System.getInt(getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
            Log.i(TAG, "brightnessLevelNumber:" + brightnessLevelNumber);
        } catch (Settings.SettingNotFoundException e) {
        }
    }

    private void getTime() {
        long upTime = SystemClock.elapsedRealtime();
        Log.i(TAG, "upTime:" + upTime);
        long wakeTime = SystemClock.uptimeMillis();
        Log.i(TAG, "wakeTime:" + wakeTime);
    }

    private void getBatteryInfo() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(new MyBatteryBroadcastReciver(), filter);
    }

    private class MyBatteryBroadcastReciver extends BroadcastReceiver {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED == intent.getAction()) {

                boolean batteryPresent = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);
                if (batteryPresent) {
                    Log.i(TAG, "batteryPresent:" + 1);
                } else {
                    Log.i(TAG, "batteryPresent:" + 0);
                }
                int batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                Log.i(TAG, "batteryStatus:" + batteryStatus);
                int batteryVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                Log.i(TAG, "batteryVoltage:" + batteryVoltage);


                String batteryTechnology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
                Log.i(TAG, "batteryTechnology:" + batteryTechnology);
                //电池的总电量
                int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                Log.i(TAG, "batteryScale:" + batteryScale);
                int batteryHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
                Log.i(TAG, "batteryHealth:" + batteryHealth);
                int batteryTemperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                Log.i(TAG, "batteryTemperature:" + batteryTemperature);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        Log.i(TAG, "wifiFrequency:" + wifiFrequency);
    }

    private void getApplicationName() {
        String applicationName = getPackageName();
        Log.i(TAG, "applicationName:" + applicationName);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void getCellConnectionStatus() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<CellInfo> allCellInfo = telephonyManager.getAllCellInfo();
        for (CellInfo info : allCellInfo) {
            Log.i(TAG, "cellConnectionStatus:" + info.getCellConnectionStatus());
        }
    }

    public Map<String, String> getCPUInfo() {
        try {
            String[] DATA = {"cat", "/proc/cpuinfo"};
            ProcessBuilder processBuilder = new ProcessBuilder(DATA);
            Process process = processBuilder.start();
            InputStream inputStream = process.getInputStream();
            byte[] byteArry = new byte[1024];
            String output = "";
            while (inputStream.read(byteArry) != -1) {
                output = output + new String(byteArry);
            }
            inputStream.close();
            Log.d(TAG, "CPU_INFO" + output);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void getUserAgent() {
        WebView mWebView = new WebView(this);
        String userAgent = mWebView.getSettings().getUserAgentString();
        Log.i(TAG, "userAgent:" + userAgent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getWallPagerID() {
        WallpaperManager instance = WallpaperManager.getInstance(this);
        WallpaperInfo wallpaperInfo = instance.getWallpaperInfo();
        if (wallpaperInfo == null) {
            Log.i(TAG, "wallPagerID:" + 0);
        } else {
            Log.i(TAG, "wallPagerID:" + 1);
        }
    }

    private void getPairedDevicesList() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDeviceSet = bluetoothAdapter.getBondedDevices();
        Log.i(TAG, "pairedDevicesList:" + pairedDeviceSet);
    }
}