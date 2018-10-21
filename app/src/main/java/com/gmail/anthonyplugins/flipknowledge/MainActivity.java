package com.gmail.anthonyplugins.flipknowledge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean answerShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onCardClick(View v) {
        if (answerShown) {
            findViewById(R.id.flashcard_question).setVisibility(View.VISIBLE);
            findViewById(R.id.flashcard_answer).setVisibility(View.INVISIBLE);
            ((TextView)findViewById(R.id.headerQuestion)).setText(getText(R.string.header_question));
        } else {
            findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);
            findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.headerQuestion)).setText(getText(R.string.header_answer));
        }
        findViewById(R.id.choiceOne).setBackground(null);
        findViewById(R.id.choiceTwo).setBackground(null);
        findViewById(R.id.choiceThree).setBackground(null);
        answerShown = !answerShown;
    }

    public void onCorrectChoiceClick(View v) {
        if (answerShown) return;
        findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);
        findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.headerQuestion)).setText(getText(R.string.header_answer));
        v.setBackgroundColor(getColor(R.color.answer_correct));
        answerShown = true;
    }

    public void onIncorrectChoiceClick(View v) {
        if (answerShown) return;
        findViewById(R.id.flashcard_question).setVisibility(View.INVISIBLE);
        findViewById(R.id.flashcard_answer).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.headerQuestion)).setText(getText(R.string.header_answer));
        v.setBackgroundColor(getColor(R.color.answer_incorrect));
        answerShown = true;
    }
}
