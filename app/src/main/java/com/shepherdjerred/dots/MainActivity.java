package com.shepherdjerred.dots;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onTimedClick(View view) {
        Intent intent = buildIntent(GameModel.GameType.TIMED);
        startActivity(intent);
    }

    public void onMovesClick(View view) {
        Intent intent = buildIntent(GameModel.GameType.MOVES);
        startActivity(intent);
    }

    private Intent buildIntent(GameModel.GameType gameType) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameType", gameType.toString());
        return intent;
    }
}
