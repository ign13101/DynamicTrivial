package com.example.dynamictrivial;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    MediaPlayer mp;
    Button btnContinuar;
    private List<String> jugadores = new ArrayList<>();
    private LinearLayout linearLayout;
    private TextView[] textViews;
    private ArrayList<String> selectedPlayers;
    ArrayList<String> testSubjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = OrderActivity.this.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        int textColorPrimary = typedValue.data;
        btnContinuar = findViewById(R.id.btn_continue);
        btnContinuar.setVisibility(View.INVISIBLE);
        mp = MediaPlayer.create(this, R.raw.click_sound);
        Intent intent = getIntent();
        selectedPlayers = intent.getStringArrayListExtra("selectedPlayers");
        // Delete players from the database that haven't been chosen
        DatabaseReference jugadoresRef = FirebaseDatabase.getInstance().getReference().child("jugadores");
        jugadoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot jugadorSnapshot : dataSnapshot.getChildren()) {
                    String nombre = jugadorSnapshot.child("nombre").getValue(String.class);
                    if (!selectedPlayers.contains(nombre)) {
                        // If the player is missing from selectedPlayers, delete it
                        jugadorSnapshot.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        LinearLayout orderLayout = findViewById(R.id.order_layout);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference orderDatabase = FirebaseDatabase.getInstance().getReference().child("orden");
        orderDatabase.removeValue();
        mDatabase.child("jugadores").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jugadores = new ArrayList<>();
                for (DataSnapshot jugadorSnapshot : dataSnapshot.getChildren()) {
                    String jugadorId = jugadorSnapshot.getKey();
                    String nombre = jugadorSnapshot.child("nombre").getValue(String.class);
                    if (selectedPlayers.contains(nombre)) {
                        jugadores.add(jugadorId);
                        TextView nameView = new TextView(getApplicationContext());
                        nameView.setText(nombre);

                        nameView.setTextColor(textColorPrimary);

                        orderLayout.addView(nameView);
                    } else {
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        Button shuffleButton = (Button) findViewById(R.id.shuffle_button);
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                Collections.shuffle(jugadores);
                LinearLayout orderLayout = findViewById(R.id.order_layout);
                orderLayout.removeAllViews();
                for (int i = 0; i < jugadores.size(); i++) {
                    String jugadorId = jugadores.get(i);
                    int turno = i + 1;
                    orderDatabase.child(String.valueOf(i)).setValue(jugadorId);
                    mDatabase.child("jugadores").child(jugadorId).child("turno").setValue(turno);
                }
                for (int i = 0; i < jugadores.size(); i++) {
                    String jugadorId = jugadores.get(i);
                    TextView nameView = new TextView(OrderActivity.this);
                    DatabaseReference jugadorRef = mDatabase.child("jugadores").child(jugadorId);
                    jugadorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String nombre = dataSnapshot.child("nombre").getValue(String.class);
                            TextView nameView = new TextView(getApplicationContext());
                            nameView.setText(nombre);
                            nameView.setTextColor(textColorPrimary);
                            orderLayout.addView(nameView);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                btnContinuar.setVisibility(View.VISIBLE);
                shuffleButton.setVisibility(View.INVISIBLE);
            }
        });
        Button nextButton = (Button) findViewById(R.id.btn_continue);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                DatabaseReference jugadorActual = FirebaseDatabase.getInstance().getReference().child("jugadorActual");
                jugadorActual.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        jugadorActual.setValue(jugadores.get(0));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

                DatabaseReference dadoRef = FirebaseDatabase.getInstance().getReference().child("dado");
                dadoRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean dadoValue = dataSnapshot.getValue(Boolean.class);
                        if (dadoValue == true) {
                            Intent intent = new Intent(OrderActivity.this, DiceSensorActivity.class);
                            intent.putExtra("selectedPlayers", (ArrayList<String>) selectedPlayers);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(OrderActivity.this, DiceActivity.class);
                            intent.putExtra("selectedPlayers", (ArrayList<String>) selectedPlayers);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
    }
}