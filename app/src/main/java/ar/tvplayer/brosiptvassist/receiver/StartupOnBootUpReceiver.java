package ar.tvplayer.brosiptvassist.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import ar.tvplayer.brosiptvassist.MainActivity;

public class StartupOnBootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Toast.makeText(context.getApplicationContext(), "System has been booted up", Toast.LENGTH_LONG).show();
            Intent activityIntent = new Intent(context.getApplicationContext(), MainActivity.class);
            activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);
        }
    }
}
