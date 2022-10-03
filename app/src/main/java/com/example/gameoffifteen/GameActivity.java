package com.example.gameoffifteen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    //Private Variables
    private int emptyX = 3;
    private int emptyY = 3;
    private RelativeLayout group;
    private Button[][] buttons;
    private int[] tiles;
    private TextView textViewSteps;
    private int stepCount = 0;
    private TextView textViewTime;
    private Timer timer;
    private int timeCount = 0 ;
    private Button buttonShuffle;
    private Button buttonStop;
    private boolean isTimeRunning;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        loadViews();
        loadNumbers();
        generateNumbers();
        loadDataToViews();
    }

    private void loadDataToViews() {
        emptyX = 3;
        emptyY = 3;
        for (int i = 0; i < group.getChildCount() - 1; i++) {
            buttons[i / 4][i % 4].setText(String.valueOf(tiles[i]));
            buttons[i / 4][i % 4].setBackgroundResource(android.R.drawable.btn_default);
        }

        buttons[emptyX][emptyY].setText("");
        buttons[emptyX][emptyY].setBackgroundColor(ContextCompat.getColor(this, R.color.colorFreeButton));
    }

    //This method generates the numbers 1-15
    private void generateNumbers() {
        int n = 15;
        Random random = new Random();
        while (n > 1) {
            int randomNum = random.nextInt(n--);
            int temp = tiles[randomNum];
            tiles[randomNum] = tiles[n];
            tiles[n] = temp;
        }

        if (!isSolvable())
            generateNumbers();
    }

    //This method makes sure the board is solvable
    private boolean isSolvable() {
        int countInversions = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < i; j++) {
                if (tiles[j] > tiles[i])
                    countInversions++;
            }
        }
        return countInversions % 2 == 0;
    }

    //This loads the numbers from 1-15 on the screen
    private void loadNumbers() {
        tiles = new int[16];
        for (int i = 0; i < group.getChildCount() - 1; i++) {
            tiles[i] = i + 1;
        }
    }


    private void loadTimer(){
        isTimeRunning = true;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeCount++;
                setTime(timeCount);
            }
        },1000, 1000);
    }

    //This method keeps track of the time elapsed since the game has begun
    private void setTime(int timeCount){
        int second = timeCount % 60;
        int hour = timeCount / 3600;
        int minute = (timeCount - hour*3600) / 60;
        textViewTime.setText(String.format("Time: %02d:%02d:%02d",hour,minute,second));
    }

    private void loadViews() {
        group = findViewById(R.id.group);
        textViewSteps = findViewById(R.id.text_view_steps);
        textViewTime = findViewById(R.id.text_view_time);
        buttonShuffle = findViewById(R.id.button_shuffle);
        buttonStop = findViewById(R.id.button_stop);

        loadTimer();
        buttons = new Button[4][4];

        for (int i = 0; i < group.getChildCount(); i++) {
            buttons[i / 4][i % 4] = (Button) group.getChildAt(i);
        }

        buttonShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateNumbers();
                loadDataToViews();
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTimeRunning){
                    timer.cancel();
                    buttonStop.setText("Resume");
                    isTimeRunning = false;
                    for (int i = 0; i < group.getChildCount(); i++){
                        buttons[i/4][i%4].setClickable(false);
                    }
                }else{
                    loadTimer();
                    buttonStop.setText("Stop");
                    for (int i = 0; i < group.getChildCount(); i++){
                        buttons[i/4][i%4].setClickable(true);
                    }
                }
            }
        });
    }

    public void buttonClick(View view) {
        Button button = (Button) view;
        int x = button.getTag().toString().charAt(0) - '0';
        int y = button.getTag().toString().charAt(1) - '0';

        if ((Math.abs(emptyX - x) == 1 && emptyY == y) || (Math.abs(emptyY - y) == 1 && emptyX == x)) {
            buttons[emptyX][emptyY].setText(button.getText().toString());
            buttons[emptyX][emptyY].setBackgroundResource(android.R.drawable.btn_default);
            button.setText("");
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.colorFreeButton));
            emptyX = x;
            emptyY = y;
            stepCount++;
            //Dsiplays "Steps" to keep track of the moves the player makes
            textViewSteps.setText("Steps:" +stepCount);
            checkWin();
        }
    }

    //Checks if the user has won when all pieces are placed correctly
    private void checkWin() {
        boolean isWin = false;
        if (emptyX == 3 && emptyY == 3) {
            for (int i = 0; i < group.getChildCount() - 1; i++) {
                if (buttons[i / 4][i % 4].getText().toString().equals(String.valueOf(i + 1)))
                    isWin = true;
                else {
                    isWin = false;
                    break;
                }

            }
        }
         //Will display Win when the game is won
        if (isWin) {
            Toast.makeText(this, "Win!!!\nSteps: "+stepCount, Toast.LENGTH_SHORT).show();
            for (int i = 0; i < group.getChildCount(); i++) {
                buttons[i / 4][i % 4].setClickable(false);
            }
            timer.cancel();
            buttonShuffle.setClickable(false);
            buttonStop.setClickable(false);
        }


    }
}




