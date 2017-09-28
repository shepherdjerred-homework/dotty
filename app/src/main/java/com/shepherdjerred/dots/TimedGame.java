package com.shepherdjerred.dots;

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
}
