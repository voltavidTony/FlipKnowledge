package com.gmail.anthonyplugins.flipknowledge;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean answerShown = false;
    private float scale;

    private Animator rotate;
    private Animator question;
    private Animator answer;
    private Animator header;
    private AnimatorSet set = new AnimatorSet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scale = getResources().getDisplayMetrics().density;

        findViewById(R.id.flipCard).setCameraDistance(8000 * scale);

        rotate = AnimatorInflater.loadAnimator(this, R.animator.card_flip_rotate);
        question = AnimatorInflater.loadAnimator(this, R.animator.card_flip_text_question);
        answer = AnimatorInflater.loadAnimator(this, R.animator.card_flip_text_answer);
        header = AnimatorInflater.loadAnimator(this, R.animator.card_header);

        rotate.setTarget(findViewById(R.id.flipCard));
        question.setTarget(findViewById(R.id.flashcard_question));
        answer.setTarget(findViewById(R.id.flashcard_answer));
        header.setTarget(findViewById(R.id.headerQuestion));

        header.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                if (answerShown)
                    ((TextView) findViewById(R.id.headerQuestion)).setText(getText(R.string.header_answer));
                else
                    ((TextView) findViewById(R.id.headerQuestion)).setText(getText(R.string.header_question));
            }
        });

        set.playTogether(rotate, question, answer);
    }

    public void onCardClick(View v) {
        if (answerShown) {
            rotate.setInterpolator(new ReverseOffsetInterpolator());
            question.setInterpolator(new ReverseLinearInterpolator());
            answer.setInterpolator(new ReverseLinearInterpolator());
        } else {
            rotate.setInterpolator(new OffsetInterpolator());
            question.setInterpolator(new LinearInterpolator());
            answer.setInterpolator(new LinearInterpolator());
        }
        set.start();
        header.start();
        findViewById(R.id.choiceOne).setBackground(null);
        findViewById(R.id.choiceTwo).setBackground(null);
        findViewById(R.id.choiceThree).setBackground(null);
        answerShown = !answerShown;
    }

    public void onCorrectChoiceClick(View v) {
        if (answerShown) return;
        rotate.setInterpolator(new OffsetInterpolator());
        question.setInterpolator(new LinearInterpolator());
        answer.setInterpolator(new LinearInterpolator());
        set.start();
        header.start();
        v.setBackgroundColor(getColor(R.color.answer_correct));
        answerShown = true;
    }

    public void onIncorrectChoiceClick(View v) {
        if (answerShown) return;
        rotate.setInterpolator(new OffsetInterpolator());
        question.setInterpolator(new LinearInterpolator());
        answer.setInterpolator(new LinearInterpolator());
        set.start();
        header.start();
        v.setBackgroundColor(getColor(R.color.answer_incorrect));
        answerShown = true;
    }
}
