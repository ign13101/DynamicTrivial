package com.example.dynamictrivial;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResumenActivity extends AppCompatActivity {

    Button nextButton;
    MediaPlayer mp;
    private Jugador playerNext;
    List<String> selectedPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen);
        mp = MediaPlayer.create(this, R.raw.click_sound);
        LinearLayout layoutResumen = findViewById(R.id.layout_resumen); // find the existing linear layout
        Intent intent = getIntent();
        selectedPlayers = intent.getStringArrayListExtra("selectedPlayers");
        DatabaseReference currentPlayerObj = FirebaseDatabase.getInstance().getReference().child("jugadorActual");
        currentPlayerObj.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentPlayer = dataSnapshot.getValue(String.class);
                DatabaseReference players = FirebaseDatabase.getInstance().getReference().child("jugadores");
                DatabaseReference currentPlayerRef = players.child(currentPlayer);
                currentPlayerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> currentPlayerObj = (Map<String, Object>) dataSnapshot.getValue();
                        TextView playerName = findViewById(R.id.jugador);
                        playerName.setText(currentPlayerObj.get("nombre").toString());

                        TextView puntosArte = new TextView(ResumenActivity.this);
                        puntosArte.setText("Puntos de la categoría Arte: " + currentPlayerObj.get("puntosArte"));
                        layoutResumen.addView(puntosArte, 1);

                        TextView puntosDeporte = new TextView(ResumenActivity.this);
                        puntosDeporte.setText("Puntos de la categoría Deporte: " + currentPlayerObj.get("puntosDeporte"));
                        layoutResumen.addView(puntosDeporte, 2);

                        TextView puntosEntretenimiento = new TextView(ResumenActivity.this);
                        puntosEntretenimiento.setText("Puntos de la categoría Entretenimiento: " + currentPlayerObj.get("puntosEntretenimiento"));
                        layoutResumen.addView(puntosEntretenimiento, 3);

                        TextView puntosGeografia = new TextView(ResumenActivity.this);
                        puntosGeografia.setText("Puntos de la categoría Geografía: " + currentPlayerObj.get("puntosGeografia"));
                        layoutResumen.addView(puntosGeografia, 4);

                        TextView puntosHistoria = new TextView(ResumenActivity.this);
                        puntosHistoria.setText("Puntos de la categoría Historia: " + currentPlayerObj.get("puntosHistoria"));
                        layoutResumen.addView(puntosHistoria, 5);
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

        nextButton = findViewById(R.id.btn_continue);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                Intent intent = new Intent(ResumenActivity.this, ClasificacionActivity.class);
                intent.putExtra("selectedPlayers", (ArrayList<String>) selectedPlayers);
                startActivity(intent);
                finish();
            }
        });
    }
}
