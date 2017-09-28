package com.shepherdjerred.dots;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GameModel {
    public static int NUM_COLORS = 5;
    private static int NUM_CELLS = 6;
    private static int INIT_MOVES = 15;
    private static int INIT_TIME = 30;

    public enum AddDotStatus {
        ADDED, REJECTED, REMOVED, COMPLETE_CYCLE
    }

    public enum GameType {
        TIMED, MOVES
    }

    private GameType mGameType;

    private int mScore;

    private int mNumCells = NUM_CELLS;
    private Dot[][] mDots;

    private int mDotColors[];

    private List<Dot> mDotPath;

    public GameModel(GameType gameType) {

        mGameType = gameType;
        mScore = 0;

        mDotColors = new int[NUM_COLORS];
        for (int i = 0; i < NUM_COLORS; i++) {
            mDotColors[i] = i;
        }

        mDots = new Dot[mNumCells][mNumCells];

        for (int y = 0; y < mNumCells; y++) {
            for (int x = 0; x < mNumCells; x++) {
                mDots[y][x] = new Dot(new Coordinate(x, y));
            }
        }

        mDotPath = new ArrayList<>();
    }

    public void newGame() {
        mScore = 0;

        for (int y = 0; y < mNumCells; y++) {
            for (int x = 0; x < mNumCells; x++) {
                mDots[y][x].changeColor();
            }
        }
    }

    public GameType getGameType() {
        return mGameType;
    }

    public int getScore() {
        return mScore;
    }

    public Dot getDot(int x, int y) {
        if(x < mNumCells && y < mNumCells)
            return mDots[y][x];

        return null;
    }

    public List<Dot> getDotPath() {
        return mDotPath;
    }

    // Sort by y
    private List<Dot> getSortedDotPath() {
        Collections.sort(mDotPath, new Comparator<Dot>() {
            public int compare(Dot dot1, Dot dot2) {
                return Integer.compare(dot1.getCoordinate().getY(), dot2.getCoordinate().getY());
            }
        });

        return mDotPath;
    }

    public void clearDotPath() {
        for (Dot dot : mDotPath) {
            dot.setSelected(false);
        }
        mDotPath.clear();
    }

    public AddDotStatus addDotToPath(Dot dot) {
        if (mDotPath.size() == 0) {
            mDotPath.add(dot);
            dot.setSelected(true);
            Log.d("MODEL_FIRST", dot.toString());
            return AddDotStatus.ADDED;
        } else {
            Dot lastDot = mDotPath.get(mDotPath.size() - 1);

            if (!mDotPath.contains(dot)) {
                Log.d("MODEL_NEW_DOT", dot.toString());

                // New dot encountered
                Log.d("IS_ADJACENT", String.valueOf(lastDot.isAdjacent(dot)));
                if (lastDot.getColor() == dot.getColor() && lastDot.isAdjacent(dot)) {
                    mDotPath.add(dot);
                    dot.setSelected(true);
                    return AddDotStatus.ADDED;
                }
            } else if (mDotPath.size() > 1) {


                // Backtracking or cycle
                Dot secondLast = mDotPath.get(mDotPath.size() - 2);

                // Backtracking
                if (secondLast.equals(dot)) {
                    Log.d("MODEL_BACKTRACK", dot.toString());
                    Dot removedDot = mDotPath.remove(mDotPath.size() - 1);
                    removedDot.setSelected(false);
                    return AddDotStatus.REMOVED;
                } else if (!lastDot.equals(dot) && lastDot.isAdjacent(dot)) {
                    Log.d("MODEL_CYCLE", dot.toString());
                    // Made cycle, so add all dots of same color to path
                    mDotPath.clear();
                    for (int y = 0; y < mNumCells; y++) {
                        for (int x = 0; x < mNumCells; x++) {
                            Dot currentDot = getDot(y, x);
                            if (currentDot.getColor() == dot.getColor()) {
                                dot.setSelected(true);
                                mDotPath.add(currentDot);
                            }
                        }
                    }

                    return AddDotStatus.COMPLETE_CYCLE;
                }
            }
        }
        Log.d("MODEL_REJECTED", dot.toString());
        return AddDotStatus.REJECTED;
    }

    public void finishMove() {
        if (mDotPath.size() > 1) {
            // Move all dots above each dot in the path down
            for (Dot dot : getSortedDotPath()) {
                dot.setSelected(false);
                // Put new dots in place
                for (int y = dot.getCoordinate().getY(); y > 0; y--) {
                    Dot dotToMove = getDot(dot.getCoordinate().getX(), y);
                    Dot dotAbove = getDot(dot.getCoordinate().getX(), y - 1);
                    dotToMove.setColor(dotAbove.getColor());
                }
                Dot topDot = getDot(dot.getCoordinate().getX(), 0);
                topDot.changeColor();
            }

            mScore += mDotPath.size();
        }
    }
}
