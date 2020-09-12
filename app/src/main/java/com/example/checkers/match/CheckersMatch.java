package com.example.checkers.match;

import android.content.ClipData;
import android.content.Context;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.checkers.chessboard.ChessboardField;
import com.example.checkers.chessboard.ChessboardMove;
import com.example.checkers.geometry.Coordinates;
import com.example.checkers.chessboard.Pawn;
import com.example.checkers.R;
import com.example.checkers.geometry.Vector;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;

public class CheckersMatch {

    public final static int CHESSBOARD_SIZE = 8;

    private Context context;
    private LinearLayout chessboardContainer;
    private CheckersMatchPlayerNotifier blackNotifier, whiteNotifier;

    private CheckersMatchMode matchMode;
    private CheckersMatchState matchState = CheckersMatchState.NOT_STARTED;

    private CheckersTeam currentTurn = CheckersTeam.WHITE;
    private Pawn selectedPawn = null;
    private ArrayList<Coordinates> highlightedFields = new ArrayList<>();
    private ArrayList<Pawn> whitePawns = new ArrayList<>();
    private ArrayList<Pawn> blackPawns = new ArrayList<>();

    private ChessboardField[][] fieldByCoordinates = new ChessboardField[CHESSBOARD_SIZE][CHESSBOARD_SIZE];
    private Map<ChessboardField, Coordinates> coordinatesByField = new IdentityHashMap<>();
    private Map<Pawn, Coordinates> coordinatesByPawn = new IdentityHashMap<>();


    private final static Vector[] blackNormalMoveVectors = new Vector[] {
            new Vector(1, 1),
            new Vector(1, -1)
    };

    private final static Vector[] whiteNormalMoveVectors = new Vector[] {
            new Vector(-1, 1),
            new Vector(-1, -1)
    };

    private final static Vector[] breakingMoveVectors = new Vector[] {
            new Vector(2, 2),
            new Vector(2, -2),
            new Vector(-2, 2),
            new Vector(-2, -2)
    };

    private class PawnOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() != MotionEvent.ACTION_DOWN) {
                return false;
            }

            Pawn pawn = (Pawn) view;

            if (pawn.getTeam() != currentTurn) {
                return true;
            }

            selectPawn(pawn);

            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(pawn);

            pawn.startDrag(data, shadowBuilder, pawn, 0);
            pawn.setVisibility(View.INVISIBLE);
            return true;
        }
    }

    private class FieldDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            Pawn pawn = (Pawn) dragEvent.getLocalState();
            Coordinates sourceCoordinates = coordinatesByPawn.get(pawn);

            ChessboardField targetField = (ChessboardField) view;
            Coordinates targetCoordinates = coordinatesByField.get(targetField);

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    if (getPossibleDestinations(pawn).contains(targetCoordinates)) {
                        resetHighlight();
                        unselectPawn();
                        move(sourceCoordinates, targetCoordinates);
                    }

                    pawn.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }

            return true;
        }
    }

    private class DropOutsideChessboardListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            Pawn pawn = (Pawn) dragEvent.getLocalState();

            switch (dragEvent.getAction()) {
                case DragEvent.ACTION_DROP:
                    return false;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (!dragEvent.getResult()) {
                        pawn.setVisibility(View.VISIBLE);
                    }

                    break;
                default:
                    break;
            }

            return true;
        }
    }

    public CheckersMatch(Context context,
                         LinearLayout chessboardContainer,
                         CheckersMatchPlayerNotifier topNotifier,
                         CheckersMatchPlayerNotifier bottomNotifier) {
        this.context = context;
        this.chessboardContainer = chessboardContainer;
        this.blackNotifier = topNotifier;
        this.whiteNotifier = bottomNotifier;

        initializeChessboard();
        initializePawns();
        setTeamsForNotifiers();
    }

    public void startMatch(CheckersMatchMode mode) {
        if (matchState != CheckersMatchState.NOT_STARTED) {
            return;
        }

        matchState = CheckersMatchState.ACTIVE;
        matchMode = mode;

        chessboardContainer.setOnDragListener(new DropOutsideChessboardListener());
        initializeFieldListeners();
        initializeWhitePawnListeners();

        if (mode == CheckersMatchMode.TWO_PLAYERS) {
            initializeBlackPawnListeners();
        }

        notifyCurrentTurn();
    }

    private void initializeChessboard() {
        for (int rowIndex = 0; rowIndex < CHESSBOARD_SIZE; rowIndex++) {
            LinearLayout row = createChessboardRow(rowIndex);
            chessboardContainer.addView(row);
        }
    }

    private void initializePawns() {
        for (int rowIndex = 0; rowIndex < 3; rowIndex++) {
            int columnIndex = rowIndex % 2 == 0 ? 1 : 0;

            while (columnIndex < CHESSBOARD_SIZE) {
                Pawn pawn = new Pawn(context, CheckersTeam.BLACK);
                addPawn(pawn, rowIndex, columnIndex);

                columnIndex += 2;
            }
        }

        for (int rowIndex = CHESSBOARD_SIZE - 3; rowIndex < CHESSBOARD_SIZE; rowIndex++) {
            int columnIndex = rowIndex % 2 == 0 ? 1 : 0;

            while (columnIndex < CHESSBOARD_SIZE) {
                Pawn pawn = new Pawn(context, CheckersTeam.WHITE);
                addPawn(pawn, rowIndex, columnIndex);

                columnIndex += 2;
            }
        }
    }

    private void setTeamsForNotifiers() {
        blackNotifier.setTeam(CheckersTeam.BLACK);
        whiteNotifier.setTeam(CheckersTeam.WHITE);
    }

    private void initializeFieldListeners() {
        for (int rowIndex = 0; rowIndex < CHESSBOARD_SIZE; rowIndex++) {
            for (int columnIndex = 0; columnIndex < CHESSBOARD_SIZE; columnIndex++) {
                fieldByCoordinates[rowIndex][columnIndex].setOnDragListener(new FieldDragListener());
            }
        }
    }

    private void initializeWhitePawnListeners() {
        for (Pawn pawn : whitePawns) {
            pawn.setOnTouchListener(new PawnOnTouchListener());
        }
    }

    private void initializeBlackPawnListeners() {
        for (Pawn pawn : blackPawns) {
            pawn.setOnTouchListener(new PawnOnTouchListener());
        }
    }

    private void deletePawnListeners() {
        for (Pawn pawn : whitePawns) {
            pawn.setOnTouchListener(null);
        }

        for (Pawn pawn : blackPawns) {
            pawn.setOnTouchListener(null);
        }
    }

    private LinearLayout createChessboardRow(int rowIndex) {
        LinearLayout row = new LinearLayout(context);

        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1
        ));

        for (int columnIndex = 0; columnIndex < CHESSBOARD_SIZE; columnIndex++) {
            ChessboardField field = createChessboardField(rowIndex, columnIndex);
            row.addView(field);
        }

        return row;
    }

    private ChessboardField createChessboardField(int rowIndex, int columnIndex) {
        ChessboardField field = new ChessboardField(context, isDarkField(rowIndex, columnIndex));

        fieldByCoordinates[rowIndex][columnIndex] = field;
        coordinatesByField.put(field, new Coordinates(rowIndex, columnIndex));
        return field;
    }

    private void addPawn(Pawn pawn, int rowIndex, int columnIndex) {
        fieldByCoordinates[rowIndex][columnIndex].setPawn(pawn);
        coordinatesByPawn.put(pawn, new Coordinates(rowIndex, columnIndex));

        if (pawn.getTeam() == CheckersTeam.WHITE) {
            whitePawns.add(pawn);
        } else {
            blackPawns.add(pawn);
        }
    }

    private void move(Coordinates start, Coordinates destination) {
        ChessboardField startField = getFieldByCoordinates(start);
        ChessboardField destinationField = getFieldByCoordinates(destination);
        Pawn pawn = startField.getPawn();

        startField.removePawn();
        destinationField.setPawn(pawn);
        coordinatesByPawn.put(pawn, destination);

        if (Coordinates.areOnDiagonal(start, destination, 2)) {
            int brokenPawnRowIndex = Math.min(start.getRowIndex(), destination.getRowIndex()) + 1;
            int brokenPawnColumnIndex = Math.min(start.getColumnIndex(), destination.getColumnIndex()) + 1;

            breakPawn(brokenPawnRowIndex, brokenPawnColumnIndex);
        } else {
            flipCurrentTurn();
        }

        if (blackPawns.size() == 0 || whitePawns.size() == 0) {
            finishMatch();
            return;
        }

        notifyCurrentTurn();

        if (currentTurn == CheckersTeam.BLACK && matchMode == CheckersMatchMode.ONE_PLAYER) {
            artificialBlackPlayerMove();
        }
    }

    private void artificialBlackPlayerMove() {
        ArrayList<ChessboardMove> normalMoves = new ArrayList<>();
        ArrayList<ChessboardMove> breakingMoves = new ArrayList<>();

        for (Pawn pawn: blackPawns) {
            Coordinates start = coordinatesByPawn.get(pawn);
            ArrayList<Coordinates> possibleDestinations = getPossibleDestinations(pawn);

            for (Coordinates destination : possibleDestinations) {
                ChessboardMove move = new ChessboardMove(start, destination);

                if (Coordinates.areOnDiagonal(start, destination, 2)) {
                    breakingMoves.add(move);
                } else {
                    normalMoves.add(move);
                }
            }
        }

        ChessboardMove resolvedMove;

        if (breakingMoves.size() > 0) {
            resolvedMove = getRandomMoveFromList(breakingMoves);
        } else {
            resolvedMove = getRandomMoveFromList(normalMoves);
        }

        move(resolvedMove.getStart(), resolvedMove.getDestination());
    }

    private void flipCurrentTurn() {
        currentTurn = currentTurn == CheckersTeam.BLACK ? CheckersTeam.WHITE : CheckersTeam.BLACK;
    }

    private void notifyCurrentTurn() {
        if (matchMode == CheckersMatchMode.TWO_PLAYERS) {
            blackNotifier.notifyCurrentTurn(currentTurn);
            whiteNotifier.notifyCurrentTurn(currentTurn);
        }
    }

    private void finishMatch() {
        CheckersTeam won = blackPawns.size() == 0 ? CheckersTeam.WHITE : CheckersTeam.BLACK;
        notifyMatchResult(won);

        unselectPawn();
        resetHighlight();
        deletePawnListeners();
    }

    private void notifyMatchResult(CheckersTeam won) {
        if (matchMode == CheckersMatchMode.TWO_PLAYERS) {
            blackNotifier.notifyMatchResult(won);
        }

        whiteNotifier.notifyMatchResult(won);
    }

    private void breakPawn(int rowIndex, int columnIndex) {
        Pawn pawn = fieldByCoordinates[rowIndex][columnIndex].getPawn();
        coordinatesByPawn.remove(pawn);
        fieldByCoordinates[rowIndex][columnIndex].removePawn();
        getPawnListByTeam(pawn.getTeam()).remove(pawn);
    }

    private void selectPawn(Pawn pawn) {
        if (selectedPawn == pawn) {
            return;
        }

        if (selectedPawn != null) {
            getFieldByCoordinates(coordinatesByPawn.get(selectedPawn)).cancelHighlight();
        }

        selectedPawn = pawn;
        ArrayList<Coordinates> possibleDestinations = getPossibleDestinations(pawn);
        highlightFields(possibleDestinations);
        getFieldByCoordinates(coordinatesByPawn.get(pawn)).highlight(context.getResources().getColor(R.color.lightBlue));
    }

    private void unselectPawn() {
        if (selectedPawn == null) {
            return;
        }

        resetHighlight();
        getFieldByCoordinates(coordinatesByPawn.get(selectedPawn)).cancelHighlight();
        selectedPawn = null;
    }

    private void highlightFields(ArrayList<Coordinates> coordinateList) {
        resetHighlight();

        for (Coordinates coordinates : coordinateList) {
            getFieldByCoordinates(coordinates).highlight(context.getResources().getColor(R.color.lightGreen));
            highlightedFields.add(coordinates);
        }
    }

    private void resetHighlight() {
        for (Coordinates coordinates : highlightedFields) {
            getFieldByCoordinates(coordinates).cancelHighlight();
        }

        highlightedFields = new ArrayList<>();
    }

    private ArrayList<Coordinates> getPossibleDestinations(Pawn pawn) {
        Coordinates start = coordinatesByPawn.get(pawn);
        ArrayList<Coordinates> result = new ArrayList<>();

        result.addAll(getNormalMoveCoordinates(start, pawn.getTeam()));
        result.addAll(getBreakingMoveCoordinates(start, pawn.getTeam()));

        return result;
    }

    private ArrayList<Coordinates> getNormalMoveCoordinates(Coordinates start, CheckersTeam team) {
        Vector[] vectors = team == CheckersTeam.BLACK ? blackNormalMoveVectors : whiteNormalMoveVectors;
        ArrayList<Coordinates> result = new ArrayList<>();

        for (Vector vector : vectors) {
            Coordinates moved = start.shift(vector);

            if (!isInChessboard(moved) || getFieldByCoordinates(moved).hasPawn()) {
                continue;
            }

            result.add(moved);
        }

        return result;
    }

    private ArrayList<Coordinates> getBreakingMoveCoordinates(Coordinates start, CheckersTeam team) {
        ArrayList<Coordinates> result = new ArrayList<>();

        for (Vector vector : breakingMoveVectors) {
            Coordinates moved = start.shift(vector);

            if (!isInChessboard(moved) || getFieldByCoordinates(moved).hasPawn()) {
                continue;
            }

            int jumpedFieldRowIndex = Math.min(start.getRowIndex(), moved.getRowIndex()) + 1;
            int jumpedFieldColumnIndex = Math.min(start.getColumnIndex(), moved.getColumnIndex()) + 1;
            ChessboardField jumpedField = fieldByCoordinates[jumpedFieldRowIndex][jumpedFieldColumnIndex];

            if (!jumpedField.hasPawn() || jumpedField.getPawn().getTeam() == team) {
                continue;
            }

            result.add(moved);
        }

        return result;
    }

    private boolean isInChessboard(Coordinates coordinates) {
        return coordinates.getRowIndex() >= 0 &&
                coordinates.getRowIndex() < CHESSBOARD_SIZE &&
                coordinates.getColumnIndex() >= 0 &&
                coordinates.getColumnIndex() < CHESSBOARD_SIZE;
    }

    private boolean isDarkField(int rowIndex, int columnIndex) {
        return (rowIndex % 2 == 0 && columnIndex % 2 != 0) || (rowIndex % 2 != 0 && columnIndex % 2 == 0);
    }

    private ChessboardField getFieldByCoordinates(Coordinates coordinates) {
        return fieldByCoordinates[coordinates.getRowIndex()][coordinates.getColumnIndex()];
    }

    private ArrayList<Pawn> getPawnListByTeam(CheckersTeam team) {
        return team == CheckersTeam.BLACK ? blackPawns : whitePawns;
    }

    private ChessboardMove getRandomMoveFromList(ArrayList<ChessboardMove> moves) {
        Random rand = new Random();
        return moves.get(rand.nextInt(moves.size()));
    }

}
