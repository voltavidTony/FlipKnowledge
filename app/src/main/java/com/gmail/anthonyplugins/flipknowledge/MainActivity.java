package com.gmail.anthonyplugins.flipknowledge;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean answerShown = false;
    private boolean animRunning = false;
    private float scale;
    private Animator question;
    private Animator answer;
    private Animator header;
    private Animator flip;
    private AnimatorSet set = new AnimatorSet();
    private TransitionDrawable sel_ans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar title = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(title);

        question = AnimatorInflater.loadAnimator(this, R.animator.card_flip_text_question);
        answer = AnimatorInflater.loadAnimator(this, R.animator.card_flip_text_answer);
        header = AnimatorInflater.loadAnimator(this, R.animator.card_header);
        flip = AnimatorInflater.loadAnimator(this, R.animator.card_flip_rotate);

        question.setTarget(findViewById(R.id.flashcard_question));
        answer.setTarget(findViewById(R.id.flashcard_answer));
        header.setTarget(findViewById(R.id.headerQuestion));
        flip.setTarget(findViewById(R.id.flipCard));

        header.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                animRunning = true;
                findViewById(R.id.flipCard).setElevation(0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animRunning = false;
                findViewById(R.id.flipCard).setElevation(2 * scale);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                answerShown = !answerShown;
                ((TextView) findViewById(R.id.headerQuestion)).setText(answerShown ? R.string.header_answer : R.string.header_question);
            }
        });
        set.playTogether(question, answer, header, flip);

        scale = getResources().getDisplayMetrics().density;
        findViewById(R.id.flipCard).setCameraDistance(8000 * scale);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_card) {
            startActivity(new Intent(this, EditActivity.class));
        } else return super.onOptionsItemSelected(item);
        return true;
    }

    public void onAddClick(View v) {
        startActivity(new Intent(this, EditActivity.class));
    }

    public void onCardClick(View v) {
        flipCard(answerShown);
        if (answerShown && sel_ans != null) {
            sel_ans.reverseTransition(getResources().getInteger(R.integer.anim_duration_ripple));
            sel_ans = null;
        }
    }

    public void onCorrectChoiceClick(View v) {
        if (answerShown || animRunning) return;
        sel_ans = (TransitionDrawable) ((RippleDrawable) v.getBackground()).getDrawable(1);
        sel_ans.startTransition(getResources().getInteger(R.integer.anim_duration_ripple));
        flipCard(false);
    }

    public void onIncorrectChoiceClick(View v) {
        if (answerShown || animRunning) return;
        sel_ans = (TransitionDrawable) ((RippleDrawable) v.getBackground()).getDrawable(1);
        sel_ans.startTransition(getResources().getInteger(R.integer.anim_duration_ripple));
        flipCard(false);
    }

    private void flipCard(boolean reverse) {
        if (animRunning) return;
        if (reverse) {
            question.setInterpolator(new ReverseLinearInterpolator());
            answer.setInterpolator(new ReverseLinearInterpolator());
            flip.setInterpolator(new ReverseOffsetInterpolator());
        } else {
            question.setInterpolator(new LinearInterpolator());
            answer.setInterpolator(new LinearInterpolator());
            flip.setInterpolator(new OffsetInterpolator());
        }
        set.start();
    }
}
