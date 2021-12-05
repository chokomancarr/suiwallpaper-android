package com.example.suiwallpaper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = new Intent();

        i.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);

        String p = MyLWPService.class.getPackage().getName();
        String c = MyLWPService.class.getCanonicalName();
        i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(p, c));
        startActivityForResult(i, 0);
        MainActivity.this.finish();
    }
}