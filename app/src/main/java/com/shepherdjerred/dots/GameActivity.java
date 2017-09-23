package com.shepherdjerred.dots;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private GameModel gameModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        String gameTypeString = intent.getStringExtra("gameType");
        if (gameTypeString != null) {
            gameModel = new GameModel(GameModel.GameType.valueOf(gameTypeString));
        } else {
            // Let's default to timed if it wasn't passed
            gameModel = new GameModel(GameModel.GameType.TIMED);
        }

        startGame();
    }

    private void startGame() {
        gameModel.newGame();
    }

    private void drawBoard() {

    }

}
