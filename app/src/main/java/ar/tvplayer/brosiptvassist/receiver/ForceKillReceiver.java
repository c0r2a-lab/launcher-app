package ar.tvplayer.brosiptvassist.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;

import ar.tvplayer.brosiptvassist.MainActivity;

public class ForceKillReceiver extends BroadcastReceiver {

    private StringBuilder inputSequence = new StringBuilder();

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private final Runnable timeoutRunnable = () -> inputSequence = new StringBuilder();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event != null && event.getAction() == KeyEvent.ACTION_DOWN) {
                int keyCode = event.getKeyCode();

                inputSequence.append(keyCode - KeyEvent.KEYCODE_0);

                timerHandler.removeCallbacks(timeoutRunnable);
                timerHandler.postDelayed(timeoutRunnable, 2000);

                if (inputSequence.toString().equals("13579")) {
                    finishApplication(context);

                    inputSequence = new StringBuilder();
                }
            }
        }
    }

    private void finishApplication(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        context.startActivity(intent);
    }
}