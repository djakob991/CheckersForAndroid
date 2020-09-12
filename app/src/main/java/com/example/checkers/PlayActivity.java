package com.example.checkers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.checkers.match.CheckersMatch;
import com.example.checkers.match.CheckersMatchMode;
import com.example.checkers.match.CheckersMatchPlayerNotifier;

public class PlayActivity extends AppCompatActivity {

    private LinearLayout chessboardContainer;
    private CheckersMatchPlayerNotifier topNotifier, bottomNotifier;

    private CheckersMatch checkersMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        chessboardContainer = findViewById(R.id.chessboard_container);
        topNotifier = findViewById(R.id.topNotifier);
        bottomNotifier = findViewById(R.id.bottomNotifier);

        checkersMatch = new CheckersMatch(this, chessboardContainer, topNotifier, bottomNotifier);

        Intent intent = getIntent();

        switch (intent.getStringExtra("matchMode")) {
            case "ONE_PLAYER":
                checkersMatch.startMatch(CheckersMatchMode.ONE_PLAYER);
                break;
            case "TWO_PLAYERS":
                checkersMatch.startMatch(CheckersMatchMode.TWO_PLAYERS);
                break;
            default:
                break;
        }
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, PlayActivity.class);
    }

}