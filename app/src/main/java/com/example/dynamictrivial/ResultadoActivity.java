package com.example.dynamictrivial;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultadoActivity extends AppCompatActivity {

    private Button btnContinuar;
    MediaPlayer mp;
    List<String> selectedPlayers;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);
        LinearLayout layout = findViewById(R.id.res_layout);
        btnContinuar = findViewById(R.id.btn_continue);
        mp = MediaPlayer.create(this, R.raw.click_sound);
        // Get the answer value and the category
        Intent intent = getIntent();
        selectedPlayers = intent.getStringArrayListExtra("selectedPlayers");
        String cat = intent.getStringExtra("cat");
        String categoriaMayus = cat.substring(0, 1).toUpperCase() + cat.substring(1);
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                Intent intent = new Intent(ResultadoActivity.this, ResumenActivity.class);
                intent.putExtra("selectedPlayers", (ArrayList<String>) selectedPlayers);
                startActivity(intent);
                finish();
            }
        });

        boolean answer = intent.getBooleanExtra("answer", false);
        if (answer) {
            TextView title = findViewById(R.id.tv_respuesta);
            title.setText("¡Respuesta Correcta!");
            TextView textView = new TextView(this);
            textView.setText("Acertaste la pregunta de la categoría " + categoriaMayus);
            layout.addView(textView, 2);
            // Increase points in category
            DatabaseReference currentPlayer = FirebaseDatabase.getInstance().getReference().child("jugadorActual");
            currentPlayer.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String playerName = dataSnapshot.getValue(String.class);
                    DatabaseReference jugadorActual = FirebaseDatabase.getInstance().getReference().child("jugadores").child(playerName);
                    jugadorActual.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Integer puntos = dataSnapshot.child("puntos" + categoriaMayus).getValue(Integer.class);
                            if (puntos != null) {
                                int puntosActual = puntos.intValue();
                                puntosActual++;
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put("puntos" + categoriaMayus, puntosActual);
                                jugadorActual.updateChildren(childUpdates);
                            } else {
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle possible errors
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        } else {
            TextView title = findViewById(R.id.tv_respuesta);
            title.setText("¡Respuesta Incorrecta!");
            TextView textView = new TextView(this);
            textView.setText("Fallaste la pregunta de la categoría " + categoriaMayus);
            layout.addView(textView, 2);
        }
    }
}

