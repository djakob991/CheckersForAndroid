package com.example.checkers.match;

import android.content.Context;
import android.util.AttributeSet;

import com.example.checkers.R;
import com.example.checkers.match.CheckersTeam;

public class CheckersMatchPlayerNotifier extends androidx.appcompat.widget.AppCompatTextView {

    private static final String YOUR_MOVE_MESSAGE = "Your turn";
    private static final String OPPONENTS_MOVE_MESSAGE = "Opponent's move";
    private static final String WON_MESSAGE = "You won!";
    private static final String LOST_MESSAGE = "You lost.";

    private CheckersTeam team = null;

    public CheckersMatchPlayerNotifier(Context context) {
        super(context);
    }

    public CheckersMatchPlayerNotifier(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckersMatchPlayerNotifier(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTeam(CheckersTeam team) {
        this.team = team;
    }

    public void notifyCurrentTurn(CheckersTeam currentTurn) {
        if (team == currentTurn) {
            setText(YOUR_MOVE_MESSAGE);
            setTextColor(getContext().getResources().getColor(R.color.blue));
        } else {
            setText(OPPONENTS_MOVE_MESSAGE);
            setTextColor(getContext().getResources().getColor(R.color.gray));
        }
    }

    public void notifyMatchResult(CheckersTeam won) {
        if (team == won) {
            setText(WON_MESSAGE);
            setTextColor(getContext().getResources().getColor(R.color.green));
        } else {
            setText(LOST_MESSAGE);
            setTextColor(getContext().getResources().getColor(R.color.red));
        }
    }
}
