package com.shepherdjerred.dots;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class Game {
    public static int NUMBER_OF_COLORS = 5;
    protected static int SIZE_OF_GRID = 6;

    protected int score;
    protected Dot[][] dotGrid;
    protected int dotColors[];
    protected List<Dot> selectedDotPath;
    protected GameStatus gameStatus;
    protected GameEndEvent gameEndEvent;

    public Game(GameEndEvent gameEndEvent) {
        score = 0;
        this.gameEndEvent = gameEndEvent;

        dotColors = new int[NUMBER_OF_COLORS];
        for (int i = 0; i < NUMBER_OF_COLORS; i++) {
            dotColors[i] = i;
        }

        dotGrid = new Dot[SIZE_OF_GRID][SIZE_OF_GRID];

        for (int y = 0; y < SIZE_OF_GRID; y++) {
            for (int x = 0; x < SIZE_OF_GRID; x++) {
                dotGrid[y][x] = new Dot(new Coordinate(x, y));
            }
        }

        selectedDotPath = new ArrayList<>();
    }

    public void newGame() {
        gameStatus = GameStatus.PLAYING;
        score = 0;

        for (int y = 0; y < SIZE_OF_GRID; y++) {
            for (int x = 0; x < SIZE_OF_GRID; x++) {
                dotGrid[y][x].changeColor();
            }
        }
    }

    protected void endGame() {
        gameStatus = GameStatus.DONE;
        gameEndEvent.run();
    }

    public Dot getDot(Coordinate coordinate) {
        if (coordinate.getX() > SIZE_OF_GRID - 1 ||
                coordinate.getY() > SIZE_OF_GRID - 1 ||
                coordinate.getX() < 0 ||
                coordinate.getY() < 0) {
            return null;
        }
        return dotGrid[coordinate.getY()][coordinate.getX()];
    }

    // Sort by y
    private List<Dot> getSortedDotPath() {
        Collections.sort(selectedDotPath, new Comparator<Dot>() {
            public int compare(Dot dot1, Dot dot2) {
                return Integer.compare(dot1.getCoordinate().getY(), dot2.getCoordinate().getY());
            }
        });

        return selectedDotPath;
    }

    public void clearDotPath() {
        for (Dot dot : selectedDotPath) {
            dot.setSelected(false);
        }
        selectedDotPath.clear();
    }

    public AddDotStatus addDotToPath(Dot dot) {
        if (selectedDotPath.size() == 0) {
            selectedDotPath.add(dot);
            dot.setSelected(true);
            return AddDotStatus.ADDED;
        } else {
            Dot lastDot = selectedDotPath.get(selectedDotPath.size() - 1);

            if (!selectedDotPath.contains(dot)) {
                // New dot encountered
                if (lastDot.getColor() == dot.getColor() && lastDot.isAdjacent(dot)) {
                    selectedDotPath.add(dot);
                    dot.setSelected(true);
                    return AddDotStatus.ADDED;
                }
            } else if (selectedDotPath.size() > 1) {


                // Backtracking or cycle
                Dot secondLast = selectedDotPath.get(selectedDotPath.size() - 2);

                // Backtracking
                if (secondLast.equals(dot)) {
                    Dot removedDot = selectedDotPath.remove(selectedDotPath.size() - 1);
                    removedDot.setSelected(false);
                    return AddDotStatus.REMOVED;
                } else if (!lastDot.equals(dot) && lastDot.isAdjacent(dot)) {
                    // Made cycle, so add all dots of same color to path
                    selectedDotPath.clear();
                    for (int y = 0; y < SIZE_OF_GRID; y++) {
                        for (int x = 0; x < SIZE_OF_GRID; x++) {
                            Dot currentDot = getDot(new Coordinate(x, y));
                            if (currentDot.getColor() == dot.getColor()) {
                                dot.setSelected(true);
                                selectedDotPath.add(currentDot);
                            }
                        }
                    }

                    return AddDotStatus.COMPLETE_CYCLE;
                }
            }
        }
        return AddDotStatus.REJECTED;
    }

    public int finishMove() {
        int numberOfDotsSelected = selectedDotPath.size();
        if (numberOfDotsSelected > 1) {
            // Move all dots above each dot in the path down
            for (Dot dot : getSortedDotPath()) {
                dot.setSelected(false);
                // Put new dots in place
                for (int y = dot.getCoordinate().getY(); y > 0; y--) {
                    Dot dotToMove = getDot(new Coordinate(dot.getCoordinate().getX(), y));
                    Dot dotAbove = getDot(new Coordinate(dot.getCoordinate().getX(), y - 1));
                    dotToMove.setColor(dotAbove.getColor());
                }
                Dot topDot = getDot(new Coordinate(dot.getCoordinate().getX(), 0));
                topDot.changeColor();
            }
            score += numberOfDotsSelected;
        }
        clearDotPath();
        return numberOfDotsSelected;
    }

    public int getScore() {
        return score;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public enum AddDotStatus {
        ADDED, REJECTED, REMOVED, COMPLETE_CYCLE
    }

    public enum GameType {
        TIMED, MOVES
    }

    public enum GameStatus {
        PLAYING, DONE
    }
}
