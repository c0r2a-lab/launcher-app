package ar.tvplayer.brosiptvassist.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import android.os.Process;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ar.tvplayer.brosiptvassist.CacheManager;
import ar.tvplayer.brosiptvassist.DeleteDirectoryContents;
import ar.tvplayer.brosiptvassist.Helper;
import ar.tvplayer.brosiptvassist.R;

public class StayService extends Service {

    private Looper scheduleMonitorLooper = null;
    private ScheduleMonitor scheduleMonitor = null;

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ScheduleMonitorStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        scheduleMonitorLooper = thread.getLooper();
        scheduleMonitor = new ScheduleMonitor(scheduleMonitorLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = scheduleMonitor.obtainMessage();
        msg.arg1 = startId;
        scheduleMonitor.sendMessage(msg);

        return START_STICKY;
    }

    //gets current date as string
    private String getCurrentDateString() {
        return Instant.now().toString();
    }

    //gets date integer
    //if some problem, it will return -1
    private int getCurrentDateInt(String date_str) {
        String pattern = getString(R.string.regexp_extract_dayint);  // 2023-11-01T05:21:17.789Z
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(date_str);
        if(m.find()) {
            return Integer.parseInt(Objects.requireNonNull(m.group(1)));
        }
        return -1;
    }

    //gets time string as format "hh:mm:ss"
    private String getCurrentTimeString(String date_str) {
        String pattern = getString(R.string.regexp_extract_timestr);
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(date_str);
        if(m.find()) {
            return m.group(1);
        }
        return null;
    }

    //clears memory cache
    private void clearMemoryCache() {
//        Path directoryPath = null;
//        directoryPath = Paths.get(getResources().getString(R.string.tivimate_folder_path));
//
//        try {
//            // Delete the contents of the directory, but not the directory itself
//            DeleteDirectoryContents.emptyDirectory(directoryPath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        CacheManager.clearCacheForApp(getApplicationContext(), getResources().getString(R.string.target_app));
        CacheManager.clearCacheForApp(getApplicationContext(), getResources().getString(R.string.origin_app));
    }

    //restarts target app
    public void restartTargetApp() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getResources().getString(R.string.target_app));

        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(launchIntent);
            } catch (Exception e) {
                showToast(getResources().getString(R.string.package_launch_failure));
            }
        } else {
            showToast(getResources().getString(R.string.no_found_package));
        }
    }

    private final class ScheduleMonitor extends Handler {
        public ScheduleMonitor(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (getCurrentDateInt(getCurrentDateString()) % 3 == 0) {
                if (Objects.equals(getCurrentTimeString(getCurrentDateString()), "00:00:00") ||
                        Objects.equals(getCurrentTimeString(getCurrentDateString()), "00:00:01")) {
                    clearMemoryCache();
                    showToast(getResources().getString(R.string.memory_cached));
                }
            }

            if (!Helper.isTargetRunning(getApplicationContext(), getResources().getString(R.string.target_app))) {
                clearMemoryCache();
                restartTargetApp();
            } else {
                showToast(getResources().getString(R.string.already_running));
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException ie) {
                showToast(getResources().getString(R.string.unable_delay));
            }

            handleMessage(msg);
        }
    }



//    //checks if target app is running in real-time
//    //reopens target app if it has been turned off
//    //clear memory cache every 3 days
//    private void scheduleMonitoringCheck() {
//        Handler handler = new Handler();
//        handler.postDelayed(() -> {
//
//            if (getCurrentDateInt(getCurrentDateString()) % 3 == 0) {
//                if (Objects.equals(getCurrentTimeString(getCurrentDateString()), "00:00:00") ||
//                        Objects.equals(getCurrentTimeString(getCurrentDateString()), "00:00:01")) {
//                    clearMemoryCache();
//                    showToast(getResources().getString(R.string.memory_cached));
//                }
//            }
//
//            if (!Helper.isTargetRunning(getApplicationContext(), getResources().getString(R.string.target_app))) {
//                clearMemoryCache();
//                // The target app is not running, initiate restart
//                restartTargetApp();
//            } else {
//                showToast(getResources().getString(R.string.already_running));
//            }
//            // Schedule the next monitoring check after a short delay
//            scheduleMonitoringCheck();
//        }, 3000); // Delay in milliseconds (e.g., 3 seconds)
//    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
