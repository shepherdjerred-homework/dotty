package com.shepherdjerred.dots;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class GameActivity extends AppCompatActivity {

    private Game game;
    private Map<Coordinate, ImageView> coordinateImageMap;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setup();
    }

    private void setup() {
        createGameObject();
        hideButtons();
        addTagToImages();
        mapImagesToCoords();
        addTouchListenerToGridLayout();
        drawBoard();
        updateGameScoreView();
    }

    private void createGameObject() {
        Intent intent = getIntent();
        String gameTypeString = intent.getStringExtra("gameType");
        Game.GameType gameType = Game.GameType.valueOf(gameTypeString);

        TextView objectiveTextView = (TextView) findViewById(R.id.objectiveText);
        final TextView objectiveValueTextView = (TextView) findViewById(R.id.objectiveValue);

        GameEndEvent gameEndEvent = new GameEndEvent() {
            @Override
            public void run() {
                gameEnd();
            }
        };

        switch (gameType) {
            case MOVES:
                int moves = Integer.valueOf(sharedPreferences.getString("movesAmount", "15"));
                game = new MovesGame(gameEndEvent, moves);

                objectiveTextView.setText("Moves Remaining");

                updateRemainingMovesText();

                break;
            case TIMED:
                int time = Integer.valueOf(sharedPreferences.getString("timeAmount", "30"));
                game = new TimedGame(gameEndEvent, time);
                TimedGame timedGame = (TimedGame) game;

                objectiveTextView.setText("Time Remaining");
                objectiveValueTextView.setText(String.valueOf(time));

                // https://developer.android.com/reference/android/os/CountDownTimer.html
                new CountDownTimer(time * 1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        objectiveValueTextView.setText(millisUntilFinished / 1000 + " seconds");
                    }
                    public void onFinish() {
                        objectiveValueTextView.setText("0 seconds");
                    }
                }.start();

                break;
        }
        game.newGame();
    }

    private void drawBoard() {
        for (ImageView image : coordinateImageMap.values()) {
            Coordinate coordinate = (Coordinate) image.getTag();
            Dot dot = game.getDot(coordinate);
            int colorInt = dot.getColor();

            if (dot.isSelected()) {
                image.setImageAlpha(50);
            } else {
                image.setImageAlpha(255);
            }

            Drawable drawable = getDrawableResourceFromColorInt(colorInt);
            image.setImageDrawable(drawable);
        }
    }

    private Drawable getDrawableResourceFromColorInt(int colorInt) {
        int drawableResourceInt;
        switch (colorInt) {
            case 0:
                drawableResourceInt = R.drawable.dot_red;
                break;
            case 1:
                drawableResourceInt = R.drawable.dot_green;
                break;
            case 2:
                drawableResourceInt = R.drawable.dot_blue;
                break;
            case 3:
                drawableResourceInt = R.drawable.dot_purple;
                break;
            case 4:
                drawableResourceInt = R.drawable.dot_yellow;
                break;
            default:
                drawableResourceInt = R.drawable.dot_black;
                break;
        }
        return getResources().getDrawable(drawableResourceInt, null);
    }

    // TODO cleanup
    private void addTouchListenerToGridLayout() {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        gridLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (game.getGameStatus() == Game.GameStatus.PLAYING) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP ||
                            motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
                        finishMove();
                    }
                    Coordinate touchedDotCoord = getCoordinateFromTouch(motionEvent);
                    Dot touchedDot = game.getDot(touchedDotCoord);
                    if (touchedDot == null) {
                        return true;
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        selectDot(touchedDot);
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                        selectDot(touchedDot);
                    }
                }
                return true;
            }
        });
    }

    private void selectDot(Dot dot) {
        game.addDotToPath(dot);
        drawBoard();
    }

    private void finishMove() {
        game.finishMove();
        updateGameScoreView();
        drawBoard();
        if (game instanceof MovesGame) {
            checkGameStatus();
            updateRemainingMovesText();
        }
    }

    private void updateGameScoreView() {
        TextView textView = (TextView) findViewById(R.id.scoreValue);
        textView.setText(String.valueOf(game.getScore()));
    }

    private void checkGameStatus() {
        if (game.getGameStatus() == Game.GameStatus.DONE) {
            gameEnd();
        }
    }

    private void updateRemainingMovesText() {
        TextView objectiveValueTextView = (TextView) findViewById(R.id.objectiveValue);
        MovesGame movesGame = (MovesGame) game;
        objectiveValueTextView.setText(String.valueOf(movesGame.getRemainingMoves()));
    }

    // TODO update high score
    private void gameEnd() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable runnable  = new Runnable() {
            @Override
            public void run() {
                showButtons();
                int currentHighScore = sharedPreferences.getInt("highScore", 0);
                if (game.getScore() > currentHighScore) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("highScore", game.getScore());
                    editor.apply();
                }
            }
        };
        mainHandler.post(runnable);
    }

    private void showButtons() {
        findViewById(R.id.newGame).setVisibility(View.VISIBLE);
        findViewById(R.id.mainMenu).setVisibility(View.VISIBLE);
    }

    private void hideButtons() {
        findViewById(R.id.newGame).setVisibility(View.INVISIBLE);
        findViewById(R.id.mainMenu).setVisibility(View.INVISIBLE);
    }

    // TODO fix crash on x/y=0, probably has same issue with x/y=5
    // Technically is fixed by checking for null, but we could find a more elegant solution
    private Coordinate getCoordinateFromTouch(MotionEvent motionEvent) {
        GridLayout gridLayout = (GridLayout) findViewById(R.id.gridLayout);

        int gridHeight = gridLayout.getHeight();
        int gridWidth = gridLayout.getWidth();

        int heightOfOneDot = gridHeight / 6;
        int widthOfOneDot = gridWidth / 6;

        float clickedX = motionEvent.getX();
        float clickedY = motionEvent.getY();

        int coordX = (int) Math.ceil(clickedX / heightOfOneDot) - 1;
        int coordY = (int) Math.ceil(clickedY / widthOfOneDot) - 1;

        Coordinate coordinate = new Coordinate(coordX, coordY);
        Log.d("DOT_TOUCH_COORDINATE", coordinate.toString());
        return coordinate;
    }

    /**
     * Sets the tag of all ImageViews to the Coordinate they are associated with
     * This Coordinated can be accessed with View#getTag
     * Very verbose but I'm not sure there's a better way to do this
     */
    private void addTagToImages() {
        // Tag row 0
        findViewById(R.id.x0y0).setTag(new Coordinate(0, 0));
        findViewById(R.id.x1y0).setTag(new Coordinate(1, 0));
        findViewById(R.id.x2y0).setTag(new Coordinate(2, 0));
        findViewById(R.id.x3y0).setTag(new Coordinate(3, 0));
        findViewById(R.id.x4y0).setTag(new Coordinate(4, 0));
        findViewById(R.id.x5y0).setTag(new Coordinate(5, 0));

        // Tag row 1
        findViewById(R.id.x0y1).setTag(new Coordinate(0, 1));
        findViewById(R.id.x1y1).setTag(new Coordinate(1, 1));
        findViewById(R.id.x2y1).setTag(new Coordinate(2, 1));
        findViewById(R.id.x3y1).setTag(new Coordinate(3, 1));
        findViewById(R.id.x4y1).setTag(new Coordinate(4, 1));
        findViewById(R.id.x5y1).setTag(new Coordinate(5, 1));

        // Tag row 2
        findViewById(R.id.x0y2).setTag(new Coordinate(0, 2));
        findViewById(R.id.x1y2).setTag(new Coordinate(1, 2));
        findViewById(R.id.x2y2).setTag(new Coordinate(2, 2));
        findViewById(R.id.x3y2).setTag(new Coordinate(3, 2));
        findViewById(R.id.x4y2).setTag(new Coordinate(4, 2));
        findViewById(R.id.x5y2).setTag(new Coordinate(5, 2));

        // Tag row 3
        findViewById(R.id.x0y3).setTag(new Coordinate(0, 3));
        findViewById(R.id.x1y3).setTag(new Coordinate(1, 3));
        findViewById(R.id.x2y3).setTag(new Coordinate(2, 3));
        findViewById(R.id.x3y3).setTag(new Coordinate(3, 3));
        findViewById(R.id.x4y3).setTag(new Coordinate(4, 3));
        findViewById(R.id.x5y3).setTag(new Coordinate(5, 3));

        // Tag row 4
        findViewById(R.id.x0y4).setTag(new Coordinate(0, 4));
        findViewById(R.id.x1y4).setTag(new Coordinate(1, 4));
        findViewById(R.id.x2y4).setTag(new Coordinate(2, 4));
        findViewById(R.id.x3y4).setTag(new Coordinate(3, 4));
        findViewById(R.id.x4y4).setTag(new Coordinate(4, 4));
        findViewById(R.id.x5y4).setTag(new Coordinate(5, 4));

        // Tag row 5
        findViewById(R.id.x0y5).setTag(new Coordinate(0, 5));
        findViewById(R.id.x1y5).setTag(new Coordinate(1, 5));
        findViewById(R.id.x2y5).setTag(new Coordinate(2, 5));
        findViewById(R.id.x3y5).setTag(new Coordinate(3, 5));
        findViewById(R.id.x4y5).setTag(new Coordinate(4, 5));
        findViewById(R.id.x5y5).setTag(new Coordinate(5, 5));
    }

    /**
     * Maps Coordinates to the ImageView they are associated with
     * Very verbose but I'm not sure there's a better way to do this
     */
    private void mapImagesToCoords() {
        coordinateImageMap = new HashMap<>();

        // Map row 0
        coordinateImageMap.put(new Coordinate(0, 0), (ImageView) findViewById(R.id.x0y0));
        coordinateImageMap.put(new Coordinate(1, 0), (ImageView) findViewById(R.id.x1y0));
        coordinateImageMap.put(new Coordinate(2, 0), (ImageView) findViewById(R.id.x2y0));
        coordinateImageMap.put(new Coordinate(3, 0), (ImageView) findViewById(R.id.x3y0));
        coordinateImageMap.put(new Coordinate(4, 0), (ImageView) findViewById(R.id.x4y0));
        coordinateImageMap.put(new Coordinate(5, 0), (ImageView) findViewById(R.id.x5y0));

        // Map row 1
        coordinateImageMap.put(new Coordinate(0, 1), (ImageView) findViewById(R.id.x0y1));
        coordinateImageMap.put(new Coordinate(1, 1), (ImageView) findViewById(R.id.x1y1));
        coordinateImageMap.put(new Coordinate(2, 1), (ImageView) findViewById(R.id.x2y1));
        coordinateImageMap.put(new Coordinate(3, 1), (ImageView) findViewById(R.id.x3y1));
        coordinateImageMap.put(new Coordinate(4, 1), (ImageView) findViewById(R.id.x4y1));
        coordinateImageMap.put(new Coordinate(5, 1), (ImageView) findViewById(R.id.x5y1));

        // Map row 2
        coordinateImageMap.put(new Coordinate(0, 2), (ImageView) findViewById(R.id.x0y2));
        coordinateImageMap.put(new Coordinate(1, 2), (ImageView) findViewById(R.id.x1y2));
        coordinateImageMap.put(new Coordinate(2, 2), (ImageView) findViewById(R.id.x2y2));
        coordinateImageMap.put(new Coordinate(3, 2), (ImageView) findViewById(R.id.x3y2));
        coordinateImageMap.put(new Coordinate(4, 2), (ImageView) findViewById(R.id.x4y2));
        coordinateImageMap.put(new Coordinate(5, 2), (ImageView) findViewById(R.id.x5y2));

        // Map row 3
        coordinateImageMap.put(new Coordinate(0, 3), (ImageView) findViewById(R.id.x0y3));
        coordinateImageMap.put(new Coordinate(1, 3), (ImageView) findViewById(R.id.x1y3));
        coordinateImageMap.put(new Coordinate(2, 3), (ImageView) findViewById(R.id.x2y3));
        coordinateImageMap.put(new Coordinate(3, 3), (ImageView) findViewById(R.id.x3y3));
        coordinateImageMap.put(new Coordinate(4, 3), (ImageView) findViewById(R.id.x4y3));
        coordinateImageMap.put(new Coordinate(5, 3), (ImageView) findViewById(R.id.x5y3));

        // Map row 4
        coordinateImageMap.put(new Coordinate(0, 4), (ImageView) findViewById(R.id.x0y4));
        coordinateImageMap.put(new Coordinate(1, 4), (ImageView) findViewById(R.id.x1y4));
        coordinateImageMap.put(new Coordinate(2, 4), (ImageView) findViewById(R.id.x2y4));
        coordinateImageMap.put(new Coordinate(3, 4), (ImageView) findViewById(R.id.x3y4));
        coordinateImageMap.put(new Coordinate(4, 4), (ImageView) findViewById(R.id.x4y4));
        coordinateImageMap.put(new Coordinate(5, 4), (ImageView) findViewById(R.id.x5y4));

        // Map row 5
        coordinateImageMap.put(new Coordinate(0, 5), (ImageView) findViewById(R.id.x0y5));
        coordinateImageMap.put(new Coordinate(1, 5), (ImageView) findViewById(R.id.x1y5));
        coordinateImageMap.put(new Coordinate(2, 5), (ImageView) findViewById(R.id.x2y5));
        coordinateImageMap.put(new Coordinate(3, 5), (ImageView) findViewById(R.id.x3y5));
        coordinateImageMap.put(new Coordinate(4, 5), (ImageView) findViewById(R.id.x4y5));
        coordinateImageMap.put(new Coordinate(5, 5), (ImageView) findViewById(R.id.x5y5));
    }

    public void onMainMenuClick(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void onNewGameClick(View view) {
        setup();
    }

}
