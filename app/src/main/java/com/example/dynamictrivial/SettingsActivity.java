package com.example.dynamictrivial;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

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

public class SettingsActivity extends AppCompatActivity {

    private LinearLayout checkBoxPlayers;
    private Switch switch1;
    DatabaseReference dadoRef;
    Button nextButton;
    Button nuevoPlayerButton;
    MediaPlayer mp;
    List<String> selectedPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        mp = MediaPlayer.create(this, R.raw.click_sound);

        // Obtener las vistas del layout
        checkBoxPlayers = findViewById(R.id.check_layout);

        // Obtenemos las referencias a los Switches del layout y su estado
        switch1 = findViewById(R.id.btnDado);
        Switch btnDado = findViewById(R.id.btnDado);
        boolean superDadoSensorActivado = btnDado.isChecked();

        // Obtener la referencia al campo "dado" en la base de datos de Firebase
        dadoRef = FirebaseDatabase.getInstance().getReference().child("dado");
        dadoRef.setValue(false);
        // Agregar un listener para actualizar el valor del campo "dado" cuando cambie el estado del Switch
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // Actualizar el valor del campo "dado" en la base de datos de Firebase según el estado del Switch
                dadoRef.setValue(isChecked);
            }
        });

        // Inicializar la lista de jugadores seleccionados
        selectedPlayers = new ArrayList<>();

        // Obtener la referencia a la base de datos de Firebase
        DatabaseReference jugadoresRef = FirebaseDatabase.getInstance().getReference().child("jugadores");

        // Agregar un listener para obtener los datos de los jugadores
        jugadoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Limpiar la lista de jugadores seleccionados
                selectedPlayers.clear();

                // Recorrer los datos de los jugadores
                for (DataSnapshot jugadorSnapshot : dataSnapshot.getChildren()) {
                    // Obtener el nombre de cada jugador
                    String nombre = jugadorSnapshot.child("nombre").getValue(String.class);
                    /*
                    // Mostrar el nombre del jugador y agregar por cada uno un CheckBox al LinearLayout
                    CheckBox checkBox = new CheckBox(SettingsActivity.this);
                    checkBox.setText(nombre);
                    checkBox.setOnCheckedChangeListener(checkBoxListener);
                    checkBoxPlayers.addView(checkBox);
                    */

                    ScrollView scrollView = findViewById(R.id.scrollCheck);
                    LinearLayout.LayoutParams checkBoxParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    checkBoxParams.setMarginEnd(15); // Margen en píxeles

                    // Crear un LinearLayout secundario para contener cada CheckBox y su botón de borrado
                    LinearLayout playerLayout = new LinearLayout(SettingsActivity.this);
                    playerLayout.setOrientation(LinearLayout.HORIZONTAL);
                    playerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));

                    CheckBox checkBox = new CheckBox(SettingsActivity.this);
                    checkBox.setText(nombre);
                    checkBox.setOnCheckedChangeListener(checkBoxListener);
                    checkBox.setLayoutParams(checkBoxParams);
                    playerLayout.addView(checkBox);

                    // Crear el botón de borrado para el jugador
                    Button deleteButton = new Button(SettingsActivity.this);
                    deleteButton.setText("Borrar");
                    LinearLayout.LayoutParams deleteButtonParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    deleteButtonParams.setMarginStart(15); // Margen en píxeles
                    deleteButton.setLayoutParams(deleteButtonParams);
                    deleteButton.setOnClickListener(deleteButtonListener);
                    playerLayout.addView(deleteButton);

                    // Agregar el LinearLayout secundario al LinearLayout principal
                    ((LinearLayout) scrollView.getChildAt(0)).addView(playerLayout);
                }
            }

            private View.OnClickListener deleteButtonListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Obtener el LinearLayout padre del botón de borrado
                    LinearLayout playerLayout = (LinearLayout) v.getParent();

                    // Obtener el nombre del jugador del CheckBox dentro del LinearLayout
                    CheckBox checkBox = (CheckBox) playerLayout.getChildAt(0);
                    String playerName = checkBox.getText().toString();

                    // Quitar el jugador de la lista de jugadores seleccionados
                    selectedPlayers.remove(playerName);

                    // Remover el LinearLayout del jugador del LinearLayout principal
                    checkBoxPlayers.removeView(playerLayout);

                    // Crear una referencia al nodo "jugadores" en la base de datos
                    DatabaseReference jugadoresRef = FirebaseDatabase.getInstance().getReference().child("jugadores");

                    // Obtener la referencia del jugador a eliminar
                    Query jugadorAEliminar = jugadoresRef.orderByChild("nombre").equalTo(playerName);

                    // Agregar un listener para obtener el snapshot del jugador a eliminar
                    jugadorAEliminar.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    // Eliminar el jugador de la base de datos
                                    snapshot.getRef().removeValue();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Manejo del error
                        }
                    });
                }
            };

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error en caso de que ocurra
            }
        });

        nextButton = findViewById(R.id.btn_continue);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                // Verificar si se ha seleccionado al menos un jugador
                if (!selectedPlayers.isEmpty()) {
                    jugadoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (String playerName : selectedPlayers) {
                                Query jugadorQuery = jugadoresRef.orderByChild("nombre").equalTo(playerName);
                                jugadorQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot jugadorSnapshot : dataSnapshot.getChildren()) {
                                            DatabaseReference jugadorRef = jugadorSnapshot.getRef();
                                            jugadorRef.child("puntosArte").setValue(0);
                                            jugadorRef.child("puntosDeporte").setValue(0);
                                            jugadorRef.child("puntosEntretenimiento").setValue(0);
                                            jugadorRef.child("puntosGeografia").setValue(0);
                                            jugadorRef.child("puntosHistoria").setValue(0);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Manejar el error en caso de que ocurra
                                    }
                                });
                            }
                            // Mostrar mensaje de jugadores seleccionados y seguir
                            Toast.makeText(SettingsActivity.this, "¡Jugadores añadidos!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SettingsActivity.this, OrderActivity.class);
                            intent.putExtra("selectedPlayers", (ArrayList<String>) selectedPlayers);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Manejar el error en caso de que ocurra
                        }
                    });
                } else {
                    // Mostrar mensaje de ningún jugador seleccionado y volver a la actividad actual
                    Toast.makeText(SettingsActivity.this, "¡No se ha seleccionado ningún jugador! Selecciona al menos uno.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nuevoPlayerButton = findViewById(R.id.btn_add_player);
        nuevoPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                // Mostrar mensaje de jugadores seleccionados y seguir
                Toast.makeText(SettingsActivity.this, "Añade nuevos jugadores", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingsActivity.this, NuevoJugadorActivity.class);
                intent.putExtra("selectedPlayers", (ArrayList<String>) selectedPlayers);
                startActivity(intent);
                finish();
            }
        });
    }

    private CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String playerName = buttonView.getText().toString();

            if (isChecked) {
                // Agregar el jugador a la lista de jugadores seleccionados
                selectedPlayers.add(playerName);
            } else {
                // Quitar el jugador de la lista de jugadores seleccionados
                selectedPlayers.remove(playerName);
            }
        }
    };
}
