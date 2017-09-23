package com.shepherdjerred.dots;

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

        for (int row = 0; row < mNumCells; row++) {
            for (int col = 0; col < mNumCells; col++) {
                mDots[row][col] = new Dot(row, col);
            }
        }

        mDotPath = new ArrayList<>();
    }

    public void newGame() {
        mScore = 0;

        for (int row = 0; row < mNumCells; row++) {
            for (int col = 0; col < mNumCells; col++) {
                mDots[row][col].changeColor();
            }
        }
    }

    public GameType getGameType() {
        return mGameType;
    }

    public int getScore() {
        return mScore;
    }

    public Dot getDot(int row, int col) {
        return mDots[row][col];
    }

    public List<Dot> getDotPath() {
        return mDotPath;
    }

    // Sort by rows
    private List<Dot> getSortedDotPath() {
        Collections.sort(mDotPath, new Comparator<Dot>() {
            public int compare(Dot dot1, Dot dot2) {
                return Integer.compare(dot1.getRow(), dot2.getRow());
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
            return AddDotStatus.ADDED;
        } else {
            Dot lastDot = mDotPath.get(mDotPath.size() - 1);

            if (!mDotPath.contains(dot)) {
                // New dot encountered
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
                    Dot removedDot = mDotPath.remove(mDotPath.size() - 1);
                    removedDot.setSelected(false);
                    return AddDotStatus.REMOVED;
                } else if (!lastDot.equals(dot) && lastDot.isAdjacent(dot)) {
                    // Made cycle, so add all dots of same color to path
                    mDotPath.clear();
                    for (int row = 0; row < mNumCells; row++) {
                        for (int col = 0; col < mNumCells; col++) {
                            Dot currentDot = getDot(row, col);
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

        return AddDotStatus.REJECTED;
    }

    public void finishMove() {
        if (mDotPath.size() > 1) {
            // Move all dots above each dot in the path down
            for (Dot dot : getSortedDotPath()) {
                dot.setSelected(false);
                // Put new dots in place
                for (int row = dot.getRow(); row > 0; row--) {
                    Dot dotToMove = getDot(row, dot.getCol());
                    Dot dotAbove = getDot(row - 1, dot.getCol());
                    dotToMove.setColor(dotAbove.getColor());;
                }
                Dot topDot = getDot(0, dot.getCol());
                topDot.changeColor();
            }

            mScore += mDotPath.size();
        }
    }
}
