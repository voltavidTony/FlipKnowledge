package com.gmail.anthonyplugins.flipknowledge;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.renderscript.Sampler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean answerShown = false;
    private boolean animRunning = false;
    private boolean choices_hidden = false;
    private float scale;
    private int choices_height;

    private Animator answer;
    private Animator flip;
    private Animator question;
    private AnimatorSet set_card = new AnimatorSet();
    private TransitionDrawable sel_ans;
    private ValueAnimator hide_choices;

    public static final int REQUEST_CODE = 1;

    public static final String EDIT_DATA = "EDIT_DATA";
    public static final String EDIT_PREVIOUS = "EDIT_PREVIOUS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar title = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(title);

        answer = AnimatorInflater.loadAnimator(this, R.animator.card_flip_text_answer);
        flip = AnimatorInflater.loadAnimator(this, R.animator.card_flip_rotate);
        question = AnimatorInflater.loadAnimator(this, R.animator.card_flip_text_question);
        answer.setTarget(findViewById(R.id.flashcard_answer));
        flip.setTarget(findViewById(R.id.flipCard));
        question.setTarget(findViewById(R.id.flashcard_question));

        Animator header = AnimatorInflater.loadAnimator(this, R.animator.card_header);
        header.setTarget(findViewById(R.id.headerQuestion));
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
                animRunning = false;
                findViewById(R.id.flipCard).setElevation(2 * scale);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                answerShown = !answerShown;
                ((TextView) findViewById(R.id.headerQuestion)).setText(answerShown ? R.string.header_answer : R.string.header_question);
            }
        });
        set_card.playTogether(question, answer, header, flip);

        scale = getResources().getDisplayMetrics().density;
        findViewById(R.id.flipCard).setCameraDistance(8000 * scale);

        findViewById(R.id.choices_section).post(new Runnable() {
            View c = findViewById(R.id.choices_section);
            View b = findViewById(R.id.show_button);

            @Override
            public void run() {
                choices_height = c.getMeasuredHeight();
                hide_choices = ValueAnimator.ofInt(choices_height, 0);
                hide_choices.setInterpolator(new DecelerateInterpolator(1.5f));
                hide_choices.setDuration(getResources().getInteger(R.integer.anim_duration_three_quarter));
                hide_choices.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ViewGroup.LayoutParams lp = c.getLayoutParams();
                        lp.height = (int) animation.getAnimatedValue();
                        c.setLayoutParams(lp);
                        if (choices_hidden) b.setAlpha(animation.getAnimatedFraction());
                        else b.setAlpha(1 - animation.getAnimatedFraction());
                    }
                });
                hide_choices.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        animRunning = true;
                        choices_hidden = !choices_hidden;
                        if (choices_hidden) b.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animRunning = false;
                        if (!choices_hidden) {
                            ViewGroup.LayoutParams lp = c.getLayoutParams();
                            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            c.setLayoutParams(lp);
                            b.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_card) {
            if (answerShown) flipCard(true);
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra(EDIT_PREVIOUS, true);
            intent.putExtra(EDIT_DATA, new String[]{
                    ((TextView) findViewById(R.id.flashcard_question)).getText().toString(),
                    ((TextView) findViewById(R.id.choiceOne)).getText().toString(),
                    ((TextView) findViewById(R.id.choiceTwo)).getText().toString(),
                    ((TextView) findViewById(R.id.choiceThree)).getText().toString()});
            startActivityForResult(intent, REQUEST_CODE);
        } else return super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK &&
                data != null && data.getBooleanExtra(EDIT_PREVIOUS, false)) {
            String[] new_data = data.getStringArrayExtra(EDIT_DATA);
            ((TextView) findViewById(R.id.flashcard_question)).setText(new_data[0]);
            ((TextView) findViewById(R.id.flashcard_answer)).setText(new_data[1]);
            ((TextView) findViewById(R.id.choiceOne)).setText(new_data[1]);
            ((TextView) findViewById(R.id.choiceTwo)).setText(new_data[2]);
            ((TextView) findViewById(R.id.choiceThree)).setText(new_data[3]);
        }
    }

    public void onAddClick(View v) {
        if (answerShown) flipCard(true);
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EDIT_PREVIOUS, false);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void onCardClick(View v) {
        flipCard(answerShown);
    }

    public void onCorrectChoiceClick(View v) {
        if (answerShown || animRunning) return;
        sel_ans = (TransitionDrawable) ((RippleDrawable) v.getBackground()).getDrawable(1);
        sel_ans.startTransition(getResources().getInteger(R.integer.anim_duration));
        flipCard(false);
    }

    public void onHideChoices(View v) {
        if (animRunning) return;
        hide_choices.setIntValues(choices_height, 0);
        hide_choices.start();
    }

    public void onIncorrectChoiceClick(View v) {
        if (answerShown || animRunning) return;
        sel_ans = (TransitionDrawable) ((RippleDrawable) v.getBackground()).getDrawable(1);
        sel_ans.startTransition(getResources().getInteger(R.integer.anim_duration));
        flipCard(false);
    }

    public void onShowChoices(View v) {
        if (animRunning) return;
        hide_choices.setIntValues(0, choices_height);
        hide_choices.start();
    }

    private void flipCard(boolean reverse) {
        if (animRunning) return;
        if (reverse) {
            if (sel_ans != null) {
                sel_ans.reverseTransition(getResources().getInteger(R.integer.anim_duration));
                sel_ans = null;
            }
            question.setInterpolator(new ReverseLinearInterpolator());
            answer.setInterpolator(new ReverseLinearInterpolator());
            flip.setInterpolator(new ReverseOffsetInterpolator());
        } else {
            question.setInterpolator(new LinearInterpolator());
            answer.setInterpolator(new LinearInterpolator());
            flip.setInterpolator(new OffsetInterpolator());
        }
        set_card.start();
    }
}
