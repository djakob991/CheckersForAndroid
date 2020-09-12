package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button onePlayerPlayButton, twoPlayersPlayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onePlayerPlayButton = findViewById(R.id.play_button_one_player);
        twoPlayersPlayButton = findViewById(R.id.play_button_two_players);

        initializeListenersForButtons();
    }

    private void initializeListenersForButtons() {

        onePlayerPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = PlayActivity.getIntent(MainActivity.this);
                intent.putExtra("matchMode", "ONE_PLAYER");

                startActivity(intent);
            }
        });

        twoPlayersPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = PlayActivity.getIntent(MainActivity.this);
                intent.putExtra("matchMode", "TWO_PLAYERS");

                startActivity(intent);
            }
        });
    }
}