package com.example.dwmt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DwmtActivity extends AppCompatActivity {

    private Metronome metronome;
    private int answerType;
    private int checkType;
    private int questionNumber;            // 현재 문제 번호 -1
    private TextView leftView;
    private TextView questionView;
    private Button[] answerBtnArray;
    private Quiz[] quizArray;
    private int score;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dwmt);

        Intent intent = getIntent();
        metronome = new Metronome(this);
        answerType = intent.getIntExtra("answerType", 1);
        checkType = intent.getIntExtra("checkType", 1);
        questionNumber = 0;
        score = 0;
        answerBtnArray = new Button[4];
        quizArray = new Quiz[20];
        int answerBtnID[] = {R.id.answer1, R.id.answer2, R.id.answer3, R.id.answer4};
        
        Log.i("intent", "DwmtActivity  answerType: " + answerType + "  checktype: " + checkType);   // DB 설계 이후에 Type변수 이용하는 코드로 변경

        // 퀴즈 초기화
        String tableName = "testQuiz";
        init(tableName);

        // View 초기화
        leftView = findViewById(R.id.leftView);
        questionView = findViewById(R.id.questionView);
        for (int i=0; i<4; i++)
            answerBtnArray[i] = findViewById(answerBtnID[i]);

    }

    public void init(String tableName){             // 매개변수: DB 설계에 따라 달라 질 수 있음

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db;
        try {
            dbHelper.createDataBase();
            dbHelper.openDataBase();

            db = dbHelper.getReadableDatabase();        // 사용할 DB
            String sql ="SELECT * FROM " + tableName;

            Cursor cursor = db.rawQuery(sql, null);
            String[] answerArray = new String[4];

            if(cursor != null){
                int k=0;
                // Table에 있는 튜플 전체 가져오기
                while ( cursor.moveToNext() ){
                    // 보기 1, 2, 3, 4
                    for (int i=0; i<4; i++)
                        answerArray[i] = cursor.getString(i+2);

                    quizArray[k++] = new Quiz(Integer.parseInt(cursor.getString(0)),
                                                cursor.getString(1),
                                                answerArray,
                                                Integer.parseInt(cursor.getString(6)));
                }
            }
            dbHelper.close();
        }catch (Exception e){
            Log.e("DB", e.toString());
        }

    }

    public void gameStart(View v){

        v.setVisibility(View.GONE);
        leftView.setText("" + (questionNumber+1));
        questionView.setText(quizArray[questionNumber].getQuestion());

        String[] answerArray = quizArray[questionNumber].getanswerArray();

        for (int i=0; i<4; i++) {
            answerBtnArray[i].setText(answerArray[i]);
            answerBtnArray[i].setVisibility(View.VISIBLE);
        }

        metronome.setRandomCnt();
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (metronome.getCnt() > 0) {
                    metronome.play();
                    handler.postDelayed(this, 500);
                }
                else{                                       // 게임 종료
                    for (int i=0; i<4; i++)
                        answerBtnArray[i].setVisibility(View.GONE);
                    v.setVisibility(View.VISIBLE);
                }
            }
        }, 500);
    }

    public void clickAnswer(View v){

        if (questionNumber > 18)
            return;

        // score 처리
        String vString = ((Button)v).getText().toString();
        Log.i("score",  quizArray[questionNumber].getRightAnswer() + "   " + vString);   //+ vString.substring(vString.length()-1)

        // 다음 문제 표시 처리
        leftView.setText("" + ((++questionNumber) + 1));
        questionView.setText(quizArray[questionNumber].getQuestion());

        String[] answerArray = quizArray[questionNumber].getanswerArray();
        for (int i=0; i<4; i++) {
            answerBtnArray[i].setText(answerArray[i]);
            answerBtnArray[i].setVisibility(View.VISIBLE);
        }
    }
}