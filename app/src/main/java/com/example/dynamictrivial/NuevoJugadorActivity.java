package com.example.dynamictrivial;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public class NuevoJugadorActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    MediaPlayer mp;
    Button btnContinuar;
    Button btnAdd;
    private List<String> jugadores = new ArrayList<>();
    private ArrayList<String> selectedPlayers;

    private Integer numUltimo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevojugador);

        btnContinuar = findViewById(R.id.btn_continue);
        btnAdd = findViewById(R.id.btn_add);
        mp = MediaPlayer.create(this, R.raw.click_sound);

        Intent intent = getIntent();
        selectedPlayers = intent.getStringArrayListExtra("selectedPlayers");

        LinearLayout orderLayout = findViewById(R.id.newplayer_layout);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Obtener la referencia a la base de datos de Firebase
        DatabaseReference jugadoresRef = FirebaseDatabase.getInstance().getReference().child("jugadores");

        //añadir jugador a la bbd para que settings lo pille al volver. Para ello crear el jugador con sus puntos a 0 y añadirlo a la BBDD y su nombre a selectedPlayers
        Button addButton = (Button) findViewById(R.id.btn_add);
        // Obtener la referencia del EditText
        EditText nuevoPlayerEditText = findViewById(R.id.nuevoplayer);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();

                // Obtén el texto del EditText
                String nuevoPlayerNombre = nuevoPlayerEditText.getText().toString();
                // Verifica si el texto no está vacío
                if (!nuevoPlayerNombre.isEmpty()) {
                    // Obtiene la clave (key) del último elemento en la lista de jugadores
                    //String ultimo = jugadoresRef.push().getKey();
                    Query queryUltimo = jugadoresRef.orderByKey().limitToLast(1);

                    queryUltimo.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                DataSnapshot ultimoSnapshot = dataSnapshot.getChildren().iterator().next();
                                String ultimoKey = ultimoSnapshot.getKey();
                                if (ultimoKey != null) {
                                    // Obtén el número del último jugador y conviértelo a entero
                                    int numUltimo = Integer.parseInt(ultimoKey.replace("jugador", ""));

                                    // Incrementa en 1 el número del último jugador
                                    int nuevoNumero = numUltimo + 1;

                                    // Crea un identificador personalizado para el jugador
                                    String jugadorId = "jugador" + nuevoNumero;

                                    // Crea una referencia al nodo del jugador utilizando el identificador personalizado
                                    DatabaseReference jugadorRef = jugadoresRef.child(jugadorId);

                                    // Establece los datos del nuevo jugador en el nodo correspondiente
                                    jugadorRef.child("nombre").setValue(nuevoPlayerNombre);
                                    jugadorRef.child("puntosArte").setValue(0);
                                    jugadorRef.child("puntosDeporte").setValue(0);
                                    jugadorRef.child("puntosEntretenimiento").setValue(0);
                                    jugadorRef.child("puntosGeografia").setValue(0);
                                    jugadorRef.child("puntosHistoria").setValue(0);
                                    jugadorRef.child("turno").setValue(1);


                                    List<Integer> turnos = new ArrayList<>();
                                    for (DataSnapshot jugadorSnapshot : dataSnapshot.getChildren()) {
                                        turnos.add(jugadorSnapshot.child("turno").getValue(Integer.class));
                                    }

                                    // Incrementa en 1 el turno más alto para el nuevo jugador
                                    int nuevoTurno = turnos.stream().mapToInt(v -> v).max()
                                            .orElseThrow(NoSuchElementException::new) + 1;
                                    jugadorRef.child("turno").setValue(nuevoTurno);

                                } else {
                                    // No hay jugadores existentes, establece el turno como 1 para el nuevo jugador
                                    String jugadorId = "jugador1";
                                    DatabaseReference jugadorRef = jugadoresRef.child(jugadorId);

                                    // Establece los datos del nuevo jugador en el nodo correspondiente
                                    jugadorRef.child("nombre").setValue(nuevoPlayerNombre);
                                    jugadorRef.child("puntosArte").setValue(0);
                                    jugadorRef.child("puntosDeporte").setValue(0);
                                    jugadorRef.child("puntosEntretenimiento").setValue(0);
                                    jugadorRef.child("puntosGeografia").setValue(0);
                                    jugadorRef.child("puntosHistoria").setValue(0);
                                    jugadorRef.child("turno").setValue(1);
                                }

                                // Agrega el nombre del jugador a la lista selectedPlayers
                                // Actualiza el intent con la lista selectedPlayers
                                // Limpiar el EditText después de agregar el jugador
                                selectedPlayers.add(nuevoPlayerNombre);
                                intent.putExtra("selectedPlayers", selectedPlayers);
                                nuevoPlayerEditText.setText("");
                                Toast.makeText(NuevoJugadorActivity.this, "Jugador añadido", Toast.LENGTH_SHORT).show();
                            } else {
                                // No hay jugadores existentes, crea el nodo "jugadores" y el jugador1
                                DatabaseReference jugadoresNodeRef = mDatabase.child("jugadores");
                                jugadoresNodeRef.setValue(true, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        if (error == null) {
                                            String jugadorId = "jugador1";
                                            DatabaseReference jugadorRef = jugadoresNodeRef.child(jugadorId);

                                            // Establece los datos del nuevo jugador en el nodo correspondiente
                                            jugadorRef.child("nombre").setValue(nuevoPlayerNombre);
                                            jugadorRef.child("puntosArte").setValue(0);
                                            jugadorRef.child("puntosDeporte").setValue(0);
                                            jugadorRef.child("puntosEntretenimiento").setValue(0);
                                            jugadorRef.child("puntosGeografia").setValue(0);
                                            jugadorRef.child("puntosHistoria").setValue(0);
                                            jugadorRef.child("turno").setValue(1);

                                            // Agrega el nombre del jugador a la lista selectedPlayers
                                            // Actualiza el intent con la lista selectedPlayers
                                            // Limpiar el EditText después de agregar el jugador
                                            selectedPlayers.add(nuevoPlayerNombre);
                                            intent.putExtra("selectedPlayers", selectedPlayers);
                                            nuevoPlayerEditText.setText("");
                                            Toast.makeText(NuevoJugadorActivity.this, "Jugador añadido", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Manejar el error al crear el nodo "jugadores"
                                            Toast.makeText(NuevoJugadorActivity.this, "Error al crear el nodo 'jugadores'", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Manejo del error
                        }
                    });
                }

            }
        });

        Button nextButton = (Button) findViewById(R.id.btn_continue);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();

                Intent intent = new Intent(NuevoJugadorActivity.this, SettingsActivity.class);
                intent.putExtra("selectedPlayers", (ArrayList<String>) selectedPlayers);
                startActivity(intent);
                finish();
            }
        });
    }
}