package com.example.dynamictrivial;

import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DondeCaiActivity extends AppCompatActivity {

    private DatabaseReference categoriasRef;
    private List<String> categoriasList = new ArrayList<>();
    private String categoriaNext;
    MediaPlayer mp;
    List<String> selectedPlayers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donde_cai);

        mp = MediaPlayer.create(this, R.raw.click_sound);
        /*VideoView videoView = findViewById(R.id.videoView);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.inicio_trivial;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.start();
            }
        });
        videoView.start();*/
        Intent intent = getIntent();
        selectedPlayers = intent.getStringArrayListExtra("selectedPlayers");
        categoriasRef = FirebaseDatabase.getInstance().getReference().child("categorias");
        categoriasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoriasList.clear();
                for (DataSnapshot categoriaSnapshot : dataSnapshot.getChildren()) {
                    String categoriaNombre = categoriaSnapshot.getKey();
                    categoriasList.add(categoriaNombre);
                }
                createButtons();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void createButtons() {
        LinearLayout buttonsLayout = findViewById(R.id.buttons_layout);
        int marginBetweenButtons = getResources().getDimensionPixelSize(R.dimen.margin_between_buttons);
        for (final String categoria : categoriasList) {
            Button button = new Button(this);
            button.setText(categoria);
            button.setBackgroundColor(ContextCompat.getColor(DondeCaiActivity.this, R.color.purple_700));
            button.setTextColor(ContextCompat.getColor(DondeCaiActivity.this, R.color.white));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, marginBetweenButtons);
            button.setLayoutParams(layoutParams);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mp.start();
                    obtenerPreguntaAleatoria(categoria);
                }
            });
            buttonsLayout.addView(button);
        }
    }

    private void obtenerPreguntaAleatoria(final String categoriaSeleccionada) {
        categoriaNext = categoriaSeleccionada;
        DatabaseReference preguntasRef = FirebaseDatabase.getInstance().getReference()
                .child("categorias").child(categoriaSeleccionada);
        preguntasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Pregunta> preguntas = new ArrayList<>();
                for (DataSnapshot preguntaSnapshot : dataSnapshot.getChildren()) {
                    Pregunta pregunta = preguntaSnapshot.getValue(Pregunta.class);
                    preguntas.add(pregunta);
                }
                if (preguntas.isEmpty()) {
                    Toast.makeText(DondeCaiActivity.this, "No hay preguntas en la categoría seleccionada", Toast.LENGTH_SHORT).show();
                    return;
                }
                int randomIndex = new Random().nextInt(preguntas.size());
                Pregunta preguntaAleatoria = preguntas.get(randomIndex);
                mostrarPregunta(preguntaAleatoria);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void mostrarPregunta(Pregunta pregunta) {
        Intent intent = new Intent(this, PreguntaActivity.class);
        intent.putExtra("cat", categoriaNext);
        intent.putExtra("selectedPlayers", (ArrayList<String>) selectedPlayers);
        intent.putExtra("pregunta", pregunta);
        startActivity(intent);
        finish();
    }

}



