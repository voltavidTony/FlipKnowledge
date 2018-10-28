package com.gmail.anthonyplugins.flipknowledge;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean answerShown = false;
    private boolean cardAnimRunning = false;
    private boolean choicesHidden = false;
    private boolean hideAnimRunning = false;
    private boolean isEditing = false;
    private float scale;
    private int choicesHeight;

    private Animator answer;
    private Animator flip;
    private Animator question;
    private AnimatorSet animSet;
    private TransitionDrawable selAns;
    private ValueAnimator toggleChoices;

    private static final int REQUEST_CODE = 1;

    public static final String EDIT_DATA = "EDIT_DATA";
    public static final String EDIT_PREVIOUS = "EDIT_PREVIOUS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar title = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(title);

        scale = getResources().getDisplayMetrics().density;
        findViewById(R.id.card_question).setCameraDistance(8000 * scale);

        answer = AnimatorInflater.loadAnimator(this, R.animator.card_answer);
        flip = AnimatorInflater.loadAnimator(this, R.animator.card_flip);
        question = AnimatorInflater.loadAnimator(this, R.animator.card_question);
        answer.setTarget(findViewById(R.id.text_answer));
        flip.setTarget(findViewById(R.id.card_question));
        question.setTarget(findViewById(R.id.text_question));

        Animator header = AnimatorInflater.loadAnimator(this, R.animator.card_header);
        header.setTarget(findViewById(R.id.header_question));
        header.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                cardAnimRunning = true;
                findViewById(R.id.card_question).setElevation(0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                cardAnimRunning = false;
                findViewById(R.id.card_question).setElevation(2 * scale);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationEnd(animation);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                answerShown = !answerShown;
                ((TextView) findViewById(R.id.header_question)).setText(answerShown ? R.string.header_answer : R.string.header_question);
            }
        });
        animSet = new AnimatorSet();
        animSet.playTogether(question, answer, header, flip);

        findViewById(R.id.section_choices).post(new Runnable() {
            final View sc = findViewById(R.id.section_choices);

            @Override
            public void run() {
                choicesHeight = sc.getMeasuredHeight();
                toggleChoices = ValueAnimator.ofInt(choicesHeight, 0);
                toggleChoices.setInterpolator(new DecelerateInterpolator(1.5f));
                toggleChoices.setDuration(getResources().getInteger(R.integer.anim_duration_400));
                toggleChoices.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ViewGroup.LayoutParams lp = sc.getLayoutParams();
                        lp.height = (int) animation.getAnimatedValue();
                        sc.setLayoutParams(lp);
                    }
                });
                toggleChoices.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        hideAnimRunning = true;
                        choicesHidden = !choicesHidden;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        hideAnimRunning = false;
                        if (!choicesHidden) {
                            ViewGroup.LayoutParams lp = sc.getLayoutParams();
                            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            sc.setLayoutParams(lp);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        onAnimationEnd(animation);
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
        if (item.getItemId() == R.id.button_edit) {
            if (isEditing) return true;
            isEditing = true;
            if (answerShown) flipCard(true);
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra(EDIT_PREVIOUS, true);
            intent.putExtra(EDIT_DATA, new String[]{
                    ((TextView) findViewById(R.id.text_question)).getText().toString(),
                    ((TextView) findViewById(R.id.text_choice_one)).getText().toString(),
                    ((TextView) findViewById(R.id.text_choice_two)).getText().toString(),
                    ((TextView) findViewById(R.id.text_choice_three)).getText().toString()});
            startActivityForResult(intent, REQUEST_CODE);
        } else return super.onOptionsItemSelected(item);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null)
            if (data.getBooleanExtra(EDIT_PREVIOUS, false)) {
                String[] ed = data.getStringArrayExtra(EDIT_DATA);
                ((TextView) findViewById(R.id.text_question)).setText(ed[0]);
                ((TextView) findViewById(R.id.text_answer)).setText(ed[1]);
                ((TextView) findViewById(R.id.text_choice_one)).setText(ed[1]);
                ((TextView) findViewById(R.id.text_choice_two)).setText(ed[2]);
                ((TextView) findViewById(R.id.text_choice_three)).setText(ed[3]);
                Snackbar.make(findViewById(R.id.activity_main_root), R.string.msg_edit_success, Snackbar.LENGTH_SHORT).show();
            } else
                Snackbar.make(findViewById(R.id.activity_main_root), R.string.msg_add_success, Snackbar.LENGTH_SHORT).show();
        answerShown = false;
        isEditing = false;
    }

    public void onAddClick(View v) {
        if (isEditing) return;
        isEditing = true;
        if (answerShown) flipCard(true);
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EDIT_PREVIOUS, false);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void onCardClick(View v) {
        flipCard(answerShown);
    }

    public void onCorrectChoiceClick(View v) {
        if (answerShown || cardAnimRunning) return;
        selAns = (TransitionDrawable) ((RippleDrawable) v.getBackground()).getDrawable(1);
        selAns.startTransition(getResources().getInteger(R.integer.anim_duration_400));
        flipCard(false);
    }

    public void onToggleChoices(View v) {
        if (hideAnimRunning) return;
        if (choicesHidden) {
            toggleChoices.setIntValues(0, choicesHeight);
            ((ImageButton) findViewById(R.id.button_toggle)).setImageResource(R.drawable.ic_visibility_off);
        } else {
            toggleChoices.setIntValues(choicesHeight, 0);
            ((ImageButton) findViewById(R.id.button_toggle)).setImageResource(R.drawable.ic_visibility);
        }
        toggleChoices.start();
    }

    public void onIncorrectChoiceClick(View v) {
        if (answerShown || cardAnimRunning) return;
        selAns = (TransitionDrawable) ((RippleDrawable) v.getBackground()).getDrawable(1);
        selAns.startTransition(getResources().getInteger(R.integer.anim_duration_400));
        flipCard(false);
    }

    private void flipCard(boolean reverse) {
        if (cardAnimRunning) return;
        if (reverse) {
            if (selAns != null) {
                selAns.reverseTransition(getResources().getInteger(R.integer.anim_duration_400));
                selAns = null;
            }
            question.setInterpolator(new ReverseLinearInterpolator());
            answer.setInterpolator(new ReverseLinearInterpolator());
            flip.setInterpolator(new ReverseOffsetInterpolator());
        } else {
            question.setInterpolator(new LinearInterpolator());
            answer.setInterpolator(new LinearInterpolator());
            flip.setInterpolator(new OffsetInterpolator());
        }
        animSet.start();
    }
}
