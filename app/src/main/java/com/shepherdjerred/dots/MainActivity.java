package com.shepherdjerred.dots;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateHighScore();
    }

    private void updateHighScore() {
        TextView highScoreTextView = (TextView) findViewById(R.id.highScoreValue);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int currentHighScore = prefs.getInt("highScore", 0);

        highScoreTextView.setText(String.valueOf(currentHighScore));
    }

    public void onTimedClick(View view) {
        Intent intent = buildIntent(Game.GameType.TIMED);
        startActivity(intent);
    }

    public void onMovesClick(View view) {
        Intent intent = buildIntent(Game.GameType.MOVES);
        startActivity(intent);
    }

    public void onSettingsClick(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private Intent buildIntent(Game.GameType gameType) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("gameType", gameType.toString());
        return intent;
    }
}
