package com.example.checkers.geometry;

public class Vector {
    private int rowShift;
    private int columnShift;

    public Vector(int rowShift, int columnShift) {
        this.rowShift = rowShift;
        this.columnShift = columnShift;
    }

    public int getRowShift() {
        return rowShift;
    }

    public int getColumnShift() {
        return columnShift;
    }
}
