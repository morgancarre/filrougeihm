package com.example.filrouge_tp3;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This application has two activities:
 *
 *     MainActivity, which contains a frame-by-frame animation.
 *     ControlActivity, which contains two fragments:
 *         MenuFragment (static fragment)
 *         Screen1Fragment (dynamic fragment)
 *
 * @author F. Rallo - march 2025
 */
public class MainActivity extends AppCompatActivity {
    private final String TAG = "frallo "+getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView image = findViewById(R.id.imageView);
        image.setBackgroundResource(R.drawable.rotation_animation);
        AnimationDrawable animation = (AnimationDrawable)image.getBackground();
        animation.start();

        //default button
        findViewById(R.id.goDefault).setOnClickListener(clic -> {
            Intent intent = new Intent(getApplicationContext(), ControlActivity.class);
            intent.putExtra(getString(R.string.index), 0);
            startActivity(intent);
        });

        //option button
        findViewById(R.id.option).setOnClickListener(clic -> {
            String[] choix = {"Signalement rapide", "Signalement détaillé"};
            new AlertDialog.Builder(this)
                    .setTitle("Signaler")
                    .setItems(choix, (dialog, which) -> {
                        int index = (which == 0) ? 1 : 2;
                        Intent intent = new Intent(getApplicationContext(), ControlActivity.class);
                        intent.putExtra(getString(R.string.index), index);
                        startActivity(intent);
                    })
                    .show();
        });
    }

}