package com.shepherdjerred.dots;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class TimedGame extends Game {

    private int gameDurationInSeconds;

    public TimedGame(GameEndEvent gameEndEvent, int gameDurationInSeconds) {
        super(gameEndEvent);
        this.gameDurationInSeconds = gameDurationInSeconds;
    }

    @Override
    public void newGame() {
        super.newGame();
        startTimer();
    }

    private void startTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                endGame();
            }
        }, gameDurationInSeconds * 1000);
    }

    public int getGameDurationInSeconds() {
        return gameDurationInSeconds;
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
        for(int i = 0; i < SIZE_OF_GRID - 1; i++){
            for(int j = 0; j < SIZE_OF_GRID - 1; j++){
                Bundle b = gameBundle.getBundle("dot" + i + j);
                dotGrid[i][j].loadFromDotBundle(b);
            }
        }
        dotColors = gameBundle.getIntArray("colors");
        gameStatus = (GameStatus) gameBundle.getSerializable("status");
    }
}
