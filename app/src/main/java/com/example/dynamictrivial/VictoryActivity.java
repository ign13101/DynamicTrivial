package com.example.dynamictrivial;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class VictoryActivity extends AppCompatActivity {

    Button nextButton;
    MediaPlayer mp;
    private String winningPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victory);
        mp = MediaPlayer.create(this, R.raw.click_sound);
        Intent intent = getIntent();
        winningPlayer = intent.getExtras().getString("winningPlayer");
        LinearLayout layoutVictoria = findViewById(R.id.layout_victory);
        TextView winner = new TextView(VictoryActivity.this);
        winner.setText(winningPlayer);
        winner.setGravity(Gravity.CENTER);
        winner.setTextSize(28);
        layoutVictoria.addView(winner, 1);

        nextButton = findViewById(R.id.btn_continue);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                Intent intent = new Intent(VictoryActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
