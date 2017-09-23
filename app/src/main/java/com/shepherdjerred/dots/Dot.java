package com.shepherdjerred.dots;

import java.util.Random;

public class Dot {
    private int color;
    private int row;
    private int col;
    private boolean selected;

    private Random randomGen;

    public Dot(int row, int col) {
        randomGen = new Random();
        color = randomGen.nextInt(GameModel.NUM_COLORS);
        selected = false;
        this.row = row;
        this.col = col;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setSelected(boolean selected) {

        this.selected = selected;
    }

    public boolean isSelected() {

        return selected;
    }

    public int getColor() {

        return color;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
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

        return row == dot.row && col == dot.col;
    }

    @Override
    public String toString() {
        return "Dot{" +
                "color=" + color +
                ", row=" + row +
                ", col=" + col +
                ", selected=" + selected +
                ", randomGen=" + randomGen +
                '}';
    }
}
