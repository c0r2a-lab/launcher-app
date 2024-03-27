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

            if (!Helper.isTargetRunning(getApplicationContext(), getResources().getString(R.string.target_app))) {
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
