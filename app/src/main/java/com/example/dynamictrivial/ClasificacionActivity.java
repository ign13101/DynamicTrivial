package com.example.dynamictrivial;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClasificacionActivity extends AppCompatActivity {

    Button nextButton;
    MediaPlayer mp;
    private ArrayList<String> selectedPlayers;
    private int currentPlayerIndex = 0;
    private ArrayList<String> currentPlayerData;
    private Boolean winCondition = false;
    private String winnningPlayer;
    Boolean dadoValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clasificacion);
        mp = MediaPlayer.create(this, R.raw.click_sound);
        Intent intent = getIntent();
        selectedPlayers = intent.getStringArrayListExtra("selectedPlayers");
        LinearLayout layoutResumen = findViewById(R.id.layout_clasificacion);
        DatabaseReference playersRef = FirebaseDatabase.getInstance().getReference().child("jugadores");
        Query playersOrdered = playersRef.orderByChild("turno");
        playersOrdered.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinearLayout playersLayout = findViewById(R.id.layout_muestraclasif);
                for (DataSnapshot playerSnapshot : dataSnapshot.getChildren()) {
                    String playerName = playerSnapshot.child("nombre").getValue(String.class);
                    int puntosArte = playerSnapshot.child("puntosArte").getValue(Integer.class);
                    int puntosDeporte = playerSnapshot.child("puntosDeporte").getValue(Integer.class);
                    int puntosEntretenimiento = playerSnapshot.child("puntosEntretenimiento").getValue(Integer.class);
                    int puntosGeografia = playerSnapshot.child("puntosGeografia").getValue(Integer.class);
                    int puntosHistoria = playerSnapshot.child("puntosHistoria").getValue(Integer.class);
                    // Crear el texto con el nombre en negrita
                    SpannableStringBuilder playerInfo = new SpannableStringBuilder();
                    playerInfo.append("NOMBRE: ");
                    SpannableString playerNameSpannable = new SpannableString(playerName);
                    playerNameSpannable.setSpan(new StyleSpan(Typeface.BOLD), 0, playerNameSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    playerInfo.append(playerNameSpannable);
                    playerInfo.append("\n");
                    playerInfo.append("Puntos de Arte: " + puntosArte + "\n");
                    playerInfo.append("Puntos de Deporte: " + puntosDeporte + "\n");
                    playerInfo.append("Puntos de Entretenimiento: " + puntosEntretenimiento + "\n");
                    playerInfo.append("Puntos de Geografía: " + puntosGeografia + "\n");
                    playerInfo.append("Puntos de Historia: " + puntosHistoria + "\n");
                    TextView playerTextView = new TextView(ClasificacionActivity.this);
                    playerTextView.setText(playerInfo);
                    playersLayout.addView(playerTextView);
                    if (puntosArte >= 1 && puntosDeporte >= 1 && puntosEntretenimiento >= 1 && puntosGeografia >= 1 && puntosHistoria >= 1) {
                        winCondition = true;
                        winnningPlayer = playerName;
                    }
                }
                ScrollView playersScrollView = findViewById(R.id.playersScrollView);
                playersScrollView.post(() -> playersScrollView.fullScroll(ScrollView.FOCUS_DOWN));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference currentPlayerRef = databaseRef.child("jugadorActual");
        currentPlayerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentPlayer = dataSnapshot.getValue(String.class);
                DatabaseReference orderRef = databaseRef.child("orden");
                orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<String> orderList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String jugador = snapshot.getValue(String.class);
                            orderList.add(jugador);
                        }
                        int currentIndex = orderList.indexOf(currentPlayer);
                        int nextIndex = (currentIndex + 1) % orderList.size();
                        String nextPlayer = orderList.get(nextIndex);
                        databaseRef.child("jugadorActual").setValue(nextPlayer);
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

        // Get value of dado in Firebase
        DatabaseReference dadoRef = FirebaseDatabase.getInstance().getReference().child("dado");
        dadoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dadoValue = dataSnapshot.getValue(Boolean.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        nextButton = findViewById(R.id.btn_continue);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                if (winCondition) {
                    Intent intent = new Intent(ClasificacionActivity.this, VictoryActivity.class);
                    intent.putExtra("winningPlayer", winnningPlayer);
                    startActivity(intent);
                    finish();
                } else {
                    // Check if sensor is chosen for the dice activity
                    if (dadoValue) {
                        Intent intent = new Intent(ClasificacionActivity.this, DiceSensorActivity.class);
                        intent.putStringArrayListExtra("selectedPlayers", selectedPlayers);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(ClasificacionActivity.this, DiceActivity.class);
                        intent.putStringArrayListExtra("selectedPlayers", selectedPlayers);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }
}
