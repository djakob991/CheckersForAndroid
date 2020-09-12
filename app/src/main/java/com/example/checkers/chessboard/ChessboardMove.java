package com.example.checkers.chessboard;

import com.example.checkers.geometry.Coordinates;

public class ChessboardMove {

    private Coordinates start, destination;

    public ChessboardMove(Coordinates start, Coordinates end) {
        this.start = start;
        this.destination = end;
    }

    public Coordinates getStart() {
        return start;
    }

    public Coordinates getDestination() {
        return destination;
    }
}

