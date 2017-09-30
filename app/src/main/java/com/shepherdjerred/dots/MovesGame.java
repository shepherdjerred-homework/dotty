package com.shepherdjerred.dots;

import android.os.Bundle;

import java.util.List;

public class MovesGame extends Game {

    private int remainingMoves;

    public MovesGame(GameEndEvent gameEndEvent, int remainingMoves) {
        super(gameEndEvent);
        this.remainingMoves = remainingMoves;
    }

    @Override
    public int finishMove() {
        int numberOfDotsSelected =  super.finishMove();
        if (numberOfDotsSelected > 1) {
            remainingMoves -= 1;
            if (remainingMoves == 0) {
                endGame();
            }
        }
        return numberOfDotsSelected;
    }

    public int getRemainingMoves() {
        return remainingMoves;
    }

    public Bundle getGameBundle(){
        Bundle bundle = new Bundle();

        bundle.putInt("score",this.score);
        for(int i = 0; i < SIZE_OF_GRID; i++){
            for(int j = 0; j < SIZE_OF_GRID; j++){
                Bundle b = dotGrid[i][j].getDotBundle();
                bundle.putBundle("dot" + i + j,b);
            }
        }
        bundle.putIntArray("colors",this.dotColors);

        bundle.putSerializable("status",gameStatus);

        return bundle;
    }

    public void loadFromGameBundle(Bundle gameBundle){
        score = gameBundle.getInt("score");
        for(int i = 0; i < SIZE_OF_GRID; i++){
            for(int j = 0; j < SIZE_OF_GRID; i++){
                Bundle b = gameBundle.getBundle("dot" + i + j);
                dotGrid[i][j].loadFromDotBundle(b);
            }
        }
        dotColors = gameBundle.getIntArray("colors");
        gameStatus = (GameStatus) gameBundle.getSerializable("status");
    }
}
