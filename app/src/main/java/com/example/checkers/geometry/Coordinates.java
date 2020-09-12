package com.example.checkers.geometry;

public class Coordinates {
    private int rowIndex, columnIndex;

    public Coordinates(int rowIndex, int columnIndex) {
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public Coordinates shift(Vector vector) {
        return new Coordinates(getRowIndex() + vector.getRowShift(), getColumnIndex() + vector.getColumnShift());
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass()) {
            return false;
        }

        Coordinates second = (Coordinates) obj;

        if (getRowIndex() != second.getRowIndex() || getColumnIndex() != second.getColumnIndex()) {
            return false;
        }

        return true;
    }

    public static boolean areOnDiagonal(Coordinates a, Coordinates b, int distance) {
        int rowDistance = Math.abs(a.getRowIndex() - b.getRowIndex());
        int columnDistance = Math.abs(a.getColumnIndex() - b.getColumnIndex());

        return rowDistance == columnDistance && rowDistance == distance;
    }

}
