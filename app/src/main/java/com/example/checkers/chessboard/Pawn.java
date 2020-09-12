package com.example.checkers.chessboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.checkers.R;
import com.example.checkers.match.CheckersTeam;

public class Pawn extends View {

    private static int instances = 0;

    private int instanceId;
    private CheckersTeam team;

    public Pawn(Context context, CheckersTeam team) {
        super(context);

        this.team = team;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        layoutParams.setMargins(5, 5, 5, 5);

        this.setLayoutParams(layoutParams);
        this.setBackground(resolvePawnBackground(team));

        instances++;
        instanceId = instances;
    }

    public CheckersTeam getTeam() {
        return team;
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }

        return instanceId == ((Pawn) obj).instanceId;
    }

    private Drawable resolvePawnBackground(CheckersTeam team) {
        int value = 0;

        switch (team) {
            case BLACK:
                value = R.drawable.black_circle;
                break;
            case WHITE:
                value = R.drawable.white_circle;
                break;
        }

        return getResources().getDrawable(value);
    }
}
