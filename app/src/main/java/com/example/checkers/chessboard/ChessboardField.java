package com.example.checkers.chessboard;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.checkers.R;

public class ChessboardField extends RelativeLayout {

    private Pawn pawn = null;
    private boolean isDark;

    public ChessboardField(Context context, boolean isDark) {
        super(context);

        this.isDark = isDark;

        this.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1
        ));

        this.setGravity(Gravity.CENTER);

        setDefaultBackgroundColor();
    }

    public void highlight(int colorId) {
        this.setBackgroundColor(colorId);
    }

    public void cancelHighlight() {
        setDefaultBackgroundColor();
    }

    public boolean hasPawn() {
        return this.pawn != null;
    }

    public Pawn getPawn() {
        return this.pawn;
    }

    public void setPawn(Pawn pawn) {
        if (hasPawn()) {
            removePawn();
        }

        this.addView(pawn);
        this.pawn = pawn;
    }

    public void removePawn() {
        this.removeView(this.pawn);
        this.pawn = null;
    }

    private void setDefaultBackgroundColor() {
        if (isDark) {
            this.setBackgroundColor(getResources().getColor(R.color.lightGray));
        } else {
            this.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }


}
