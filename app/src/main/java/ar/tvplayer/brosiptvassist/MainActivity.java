package ar.tvplayer.brosiptvassist;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ar.tvplayer.brosiptvassist.service.StayService;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        startForegroundService(new Intent(this.getApplicationContext(), StayService.class));
    }
}
