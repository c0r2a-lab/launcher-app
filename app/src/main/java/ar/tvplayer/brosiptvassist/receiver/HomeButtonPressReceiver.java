package ar.tvplayer.brosiptvassist.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import ar.tvplayer.brosiptvassist.MainActivity;

public class HomeButtonPressReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context.getApplicationContext(), "home button has been pushed by user", Toast.LENGTH_SHORT).show();
        if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
            String reason = intent.getStringExtra("reason");
            if ("homekey".equals(reason)) {
                Intent activityIntent = new Intent(context, MainActivity.class);
                activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(activityIntent);
            }
        }
    }
}
