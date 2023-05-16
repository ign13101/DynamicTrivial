package com.example.dynamictrivial;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PreguntaActivity extends AppCompatActivity {

    private TextView tvPregunta;
    private RadioGroup radioGroupOpciones;
    private Button btnResponder;
    MediaPlayer mp;
    List<String> selectedPlayers;
    private List<String> opciones;
    private int respuesta;

    private CountDownTimer countDownTimer;
    private TextView tvTemporizador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);
        mp = MediaPlayer.create(this, R.raw.click_sound);
        tvTemporizador = findViewById(R.id.tv_temporizador);
        // Obtener los datos de la pregunta seleccionada
        Intent intent = getIntent();
        selectedPlayers = intent.getStringArrayListExtra("selectedPlayers");
        String cat = intent.getStringExtra("cat");
        Pregunta pregunta = intent.getParcelableExtra("pregunta");

        // Obtener las vistas del layout
        tvPregunta = findViewById(R.id.tv_pregunta);
        radioGroupOpciones = findViewById(R.id.radio_group_opciones);
        btnResponder = findViewById(R.id.btn_responder);

        // Mostrar la pregunta y opciones en las vistas correspondientes
        tvPregunta.setText(pregunta.getPregunta());
        opciones = pregunta.getOpciones();
        respuesta = pregunta.getRespuesta();
        for (int i = 0; i < opciones.size(); i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(opciones.get(i));
            radioGroupOpciones.addView(radioButton);
        }

        // Establece la duración total del temporizador en milisegundos (20 segundos)
        long totalDuracionTemporizador = 20 * 1000;
        // Establece el intervalo en milisegundos en el que se actualizará el temporizador (cada segundo)
        long intervaloTemporizador = 1000;

        countDownTimer = new CountDownTimer(totalDuracionTemporizador, intervaloTemporizador) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Actualiza el texto del temporizador con el tiempo restante en segundos
                int segundosRestantes = (int) (millisUntilFinished / 1000);
                tvTemporizador.setText(String.valueOf(segundosRestantes));
            }
            @Override
            public void onFinish() {
                // Acciones a realizar cuando el temporizador llega a cero
                // Si no es la respuesta correcta, mostrar un mensaje y lanzar una nueva actividad con la respuesta falsa
                Toast.makeText(PreguntaActivity.this, "Respuesta incorrecta, intenta de nuevo", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PreguntaActivity.this, ResultadoActivity.class);
                intent.putExtra("answer", false);
                intent.putExtra("cat",cat);
                intent.putExtra("selectedPlayers", (ArrayList<String>) selectedPlayers);
                //pasar jugador
                startActivity(intent);
                finish();
            }
        };

        // Inicia el temporizador
        countDownTimer.start();

        // Definir el comportamiento del botón de responder
        btnResponder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                // Obtener la opción seleccionada
                int opcionSeleccionada = radioGroupOpciones.getCheckedRadioButtonId();
                //if (opcionSeleccionada == -1) {
                // Si no se ha seleccionado ninguna opción, mostrar un mensaje
                //  Toast.makeText(PreguntaActivity.this, "Debes seleccionar una opción", Toast.LENGTH_SHORT).show();
                //} else {
                // Si se ha seleccionado una opción, verificar si es la respuesta correcta
                int opcionSeleccionadaIndex = radioGroupOpciones.indexOfChild(findViewById(opcionSeleccionada));
                if (opcionSeleccionadaIndex == respuesta) {
                    // Si es la respuesta correcta, mostrar un mensaje y lanzar una nueva actividad con la respuesta verdadera
                    Toast.makeText(PreguntaActivity.this, "¡Respuesta correcta!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PreguntaActivity.this, ResultadoActivity.class);
                    intent.putExtra("answer", true);
                    intent.putExtra("cat", cat);
                    intent.putExtra("selectedPlayers", (ArrayList<String>) selectedPlayers);
                    //pasar jugador
                    startActivity(intent);
                    finish();
                } else {
                    // Si no es la respuesta correcta, mostrar un mensaje y lanzar una nueva actividad con la respuesta falsa
                    Toast.makeText(PreguntaActivity.this, "Respuesta incorrecta, intenta de nuevo", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PreguntaActivity.this, ResultadoActivity.class);
                    intent.putExtra("answer", false);
                    intent.putExtra("cat", cat);
                    intent.putExtra("selectedPlayers", (ArrayList<String>) selectedPlayers);
                    //pasar jugador
                    startActivity(intent);
                    finish();
                }
            }
            //}
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detiene el temporizador si está en curso
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}

