package com.example.dynamictrivial;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiceSensorActivity extends AppCompatActivity implements SensorEventListener {

    TextView currentPlayerTextView;
    ImageView diceImg;
    Button rollButton;
    Button nextButton;
    Boolean pressed;
    List<String> selectedPlayers;
    MediaPlayer mpClick;
    MediaPlayer mpDice;

    // Sensor variable declarations
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dice_sensor);
        diceImg = findViewById(R.id.imageDice);
        mpClick = MediaPlayer.create(this, R.raw.click_sound);
        mpDice = MediaPlayer.create(this, R.raw.dice_sound);
        rollDice();
        pressed = false;
        currentPlayerTextView = findViewById(R.id.current_player);
        Intent intent = getIntent();
        selectedPlayers = intent.getStringArrayListExtra("selectedPlayers");

        // Get current player name
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("jugadorActual").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentPlayerId = dataSnapshot.getValue(String.class);
                database.getReference("jugadores/" + currentPlayerId + "/nombre").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String currentPlayerName = dataSnapshot.getValue(String.class);
                        currentPlayerTextView.setText(currentPlayerName);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        rollButton = (Button) findViewById(R.id.roll_button);
        rollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpDice.start();
                if (!pressed) {
                    mpDice.start();
                    rollDice();
                    pressed = true;
                    nextButton.setVisibility(View.VISIBLE);
                    rollButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Button click
        nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setVisibility(View.INVISIBLE);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiceSensorActivity.this, DondeCaiActivity.class);
                intent.putExtra("selectedPlayers", (ArrayList<String>) selectedPlayers);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                // Sensor movement detected
                if (speed > SHAKE_THRESHOLD) {
                    if (!pressed) {
                        mpDice.start();
                        rollDice();
                        pressed = true;
                        nextButton.setVisibility(View.VISIBLE);
                        rollButton.setVisibility(View.INVISIBLE);
                    }
                }
                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void rollDice() {
        int diceValue = new Random().nextInt(6) + 1;
        int res = getResources().getIdentifier("dice" + diceValue, "drawable", "com.example.dynamictrivial");
        diceImg.setImageResource(res);
    }
}