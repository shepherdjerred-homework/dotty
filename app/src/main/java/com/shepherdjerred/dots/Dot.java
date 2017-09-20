package com.shepherdjerred.dots;

import java.util.Random;

public class Dot {
    public int color;
    public int row;
    public int col;
    public boolean selected;

    private Random randomGen;

    public Dot(int row, int col) {
        randomGen = new Random();
        color = randomGen.nextInt(GameModel.NUM_COLORS);
        selected = false;
        this.row = row;
        this.col = col;
    }

    private void setColor() {
        color = randomGen.nextInt(GameModel.NUM_COLORS);
    }

    public boolean isAdjacent(Dot lastDot) {
        int colDiff = Math.abs(this.col - lastDot.col);
        int rowDiff = Math.abs(this.row - lastDot.row);
        return (colDiff + rowDiff == 1);
    }

    public void changeColor() {
        setColor();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dot dot = (Dot) o;

        if (row != dot.row) return false;
        return col == dot.col;
    }

    @Override
    public String toString() {
        return "Dot{" +
                "color=" + color +
                ", row=" + row +
                ", col=" + col +
                '}';
    }
}
