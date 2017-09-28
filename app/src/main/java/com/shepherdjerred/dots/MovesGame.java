package com.shepherdjerred.dots;

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
}
