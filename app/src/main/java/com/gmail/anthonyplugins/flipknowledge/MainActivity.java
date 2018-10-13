package com.gmail.anthonyplugins.flipknowledge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onQuestionClick(View v){
        findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);
        findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);
    }

    public void onAnswerClick(View v) {
        findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
        findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
    }
}
