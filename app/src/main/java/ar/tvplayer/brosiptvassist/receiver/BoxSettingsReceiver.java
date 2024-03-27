package ar.tvplayer.brosiptvassist.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;

public class BoxSettingsReceiver extends BroadcastReceiver {

    private static final String CONFIGURATION_SEQUENCE = "1111111";
    private static final int CONFIGURATION_TRIGGER_COUNT = 7;
    private int sequenceCount = 0;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Runnable resetSequenceRunnable = () -> sequenceCount = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                int keyCode = event.getKeyCode();

                if (keyCode == KeyEvent.KEYCODE_1) {
                    timerHandler.removeCallbacks(resetSequenceRunnable);
                    timerHandler.postDelayed(resetSequenceRunnable, 2000);

                    if (CONFIGURATION_SEQUENCE.charAt(sequenceCount) == '1') {
                        sequenceCount++;
                        if (sequenceCount == CONFIGURATION_TRIGGER_COUNT) {
                            openDeviceSettings(context);
                            sequenceCount = 0;
                        }
                    } else {
                        sequenceCount = 0;
                    }
                } else {
                    sequenceCount = 0;
                }
            }
        }
    }

    private void openDeviceSettings(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
