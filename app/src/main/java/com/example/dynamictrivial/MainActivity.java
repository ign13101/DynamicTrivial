package com.example.dynamictrivial;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

//import com.example.dynamictrivial.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private Button mButtonShowPopup;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mp = MediaPlayer.create(this, R.raw.click_sound);
        Button settingsButton = (Button) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // do something when the TextView is clicked
                mp.start();
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        mButtonShowPopup = findViewById(R.id.button_show_popup);
        mButtonShowPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
                showPopup();
            }
        });
    }

    private void showPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Instrucciones");

        // Set up the scrollable text and image in the popup
        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.tablero);
        // Ajustar el tamaño de la imagen
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        // Establecer el tamaño de la imagen a 200dp
        int imageSizeInDp = 200;
        float density = getResources().getDisplayMetrics().density;
        int imageSizeInPixels = (int) (imageSizeInDp * density + 0.5f);
        imageParams.width = imageSizeInPixels;
        imageParams.height = imageSizeInPixels;
        imageParams.gravity = Gravity.CENTER;
        imageParams.setMargins(0, 20, 0, 0);
        imageView.setLayoutParams(imageParams);
        layout.addView(imageView);

        TextView textView = new TextView(this);
        textView.setText("Para jugar a Dynamic Trivial necesitarás:\n\n- 1 tablero\n- Tantas fichas de " +
                "jugadores como jugadores seáis.\n\nLo primero que deberéis hacer es dibujar en el " +
                "tablero un circuito cíclico, dibujar al menos varias casillas para cada una de las " +
                "categorías y señalar la casilla inicial.\n\nCada jugador tira el dado y elige la categoría " +
                "en la que ha caído, después contesta a la pregunta y si acierta suma un punto en esa " +
                "categoría. El primer jugador en conseguir al menos un punto en todas las categorías gana.");
        // Aumentar el tamaño del texto y establecer el margen lógico
        int textSizeInSp = 16;
        int marginInPixels = (int) (16 * density + 0.5f);
        textView.setTextSize(textSizeInSp);
        textView.setPadding(marginInPixels, marginInPixels, marginInPixels, marginInPixels);
        layout.addView(textView);
        scrollView.addView(layout);
        builder.setView(scrollView);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}