package com.shepherdjerred.dots;

import java.util.Random;

public class Dot {
    private int color;
    private Coordinate coordinate;
    private boolean selected;

    private Random randomGen;

    public Dot(Coordinate coordinate) {
        randomGen = new Random();
        color = randomGen.nextInt(GameModel.NUM_COLORS);
        selected = false;
        this.coordinate = coordinate;
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

    private void setColor() {
        color = randomGen.nextInt(GameModel.NUM_COLORS);
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public boolean isAdjacent(Dot lastDot) {
        int colDiff = Math.abs(coordinate.getX() - lastDot.getCoordinate().getX());
        int rowDiff = Math.abs(coordinate.getY() - lastDot.getCoordinate().getY());
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

        if (color != dot.color) return false;
        if (selected != dot.selected) return false;
        if (!coordinate.equals(dot.coordinate)) return false;
        return randomGen != null ? randomGen.equals(dot.randomGen) : dot.randomGen == null;

    }

    @Override
    public int hashCode() {
        int result = color;
        result = 31 * result + coordinate.hashCode();
        result = 31 * result + (selected ? 1 : 0);
        result = 31 * result + (randomGen != null ? randomGen.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Dot{" +
                "color=" + color +
                ", coordinate=" + coordinate +
                ", selected=" + selected +
                '}';
    }
}
