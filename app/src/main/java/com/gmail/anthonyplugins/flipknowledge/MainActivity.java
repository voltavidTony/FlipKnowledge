package com.gmail.anthonyplugins.flipknowledge;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.FormatFlagsConversionMismatchException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private boolean answerShown = false;
    private boolean cardAnimRunning = false;
    private boolean choicesHidden = false;
    private boolean hideAnimRunning = false;
    private boolean isEditing = false;
    private boolean switchingCard = false;
    private float scale;
    private int choicesHeight;
    private int currentIndex;

    private Animator card_flip;
    private ValueAnimator card_switch;
    private AnimatorSet animSet_flip;
    private AnimatorSet animSet_switch;
    private TransitionDrawable selAns;
    private ValueAnimator toggleChoices;

    private List<CardObject> cards;
    private CardStorage cardStorage;
    private CardObject current;
    private CardObject nextCard;

    private static final int REQUEST_CODE = 1;
    public static final int RESULT_DELETE = 2;

    public static final String EDIT_DATA = "EDIT_DATA";
    public static final String EDIT_DELETABLE = "EDIT_DELETABLE";
    public static final String EDIT_PREVIOUS = "EDIT_PREVIOUS";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                setCard((CardObject) data.getExtras().getSerializable(EDIT_DATA));
                if (data.getExtras().getBoolean(EDIT_PREVIOUS, false)) {
                    cardStorage.updateCard(current);
                    Snackbar.make(findViewById(R.id.activity_main_root), R.string.msg_edit_success, Snackbar.LENGTH_SHORT).show();
                } else {
                    cardStorage.insertCards(current);
                    cards.add(current);
                    currentIndex = cards.size() - 1;
                    findViewById(R.id.button_prev).setVisibility(View.VISIBLE);
                    findViewById(R.id.button_next).setVisibility(View.GONE);
                    Snackbar.make(findViewById(R.id.activity_main_root), R.string.msg_add_success, Snackbar.LENGTH_SHORT).show();
                }
            }
            if (resultCode == RESULT_DELETE) {
                cardStorage.deleteCard(current);
                cards.remove(current);
                if (currentIndex == cards.size()) currentIndex--;
                setCard(cards.get(currentIndex));
                if (cards.size() == 1) findViewById(R.id.button_prev).setVisibility(View.GONE);
            }
        }
        answerShown = false;
        isEditing = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar title = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(title);

        scale = getResources().getDisplayMetrics().density;
        findViewById(R.id.card_question).setCameraDistance(8000 * scale);
        findViewById(R.id.layout_question).setCameraDistance(8000 * scale);

        Animator header = AnimatorInflater.loadAnimator(this, R.animator.card_header);
        header.setTarget(findViewById(R.id.header_question));
        header.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                cardAnimRunning = true;
                if (!switchingCard) findViewById(R.id.card_question).setElevation(0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                cardAnimRunning = switchingCard = false;
                findViewById(R.id.card_question).setElevation(2 * scale);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationEnd(animation);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                if (switchingCard)
                    ((TextView) findViewById(R.id.header_question)).setText(R.string.header_question);
                else if (answerShown) {
                    ((TextView) findViewById(R.id.header_question)).setText(R.string.header_answer);
                    findViewById(R.id.text_question).setAlpha(0);
                    findViewById(R.id.text_answer).setAlpha(1);
                } else {
                    ((TextView) findViewById(R.id.header_question)).setText(R.string.header_question);
                    findViewById(R.id.text_question).setAlpha(1);
                    findViewById(R.id.text_answer).setAlpha(0);
                }
            }
        });

        card_flip = AnimatorInflater.loadAnimator(this, R.animator.card_flip);
        card_flip.setTarget(findViewById(R.id.card_question));
        animSet_flip = new AnimatorSet();
        animSet_flip.playTogether(header, card_flip);

        card_switch = ValueAnimator.ofInt();
        card_switch.setInterpolator(new AccelerateInterpolator(1.5f));
        card_switch.setDuration(getResources().getInteger(R.integer.anim_duration_300));
        card_switch.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            View cq = findViewById(R.id.card_question);
            View ca = findViewById(R.id.card_answers);

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                cq.setTranslationX((int) animation.getAnimatedValue());
                ca.setTranslationX((int) animation.getAnimatedValue());
            }
        });
        card_switch.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                findViewById(R.id.card_question_dummy).setVisibility(View.VISIBLE);
                View v = findViewById(R.id.text_question_dummy);
                v.setAlpha(1);
                ((TextView) v).setText(nextCard.getQuestion());
                v = findViewById(R.id.text_answer_dummy);
                v.setAlpha(0);
                ((TextView) v).setText(nextCard.getAnswer());
                findViewById(R.id.button_next_dummy).setVisibility(currentIndex == cards.size() - 1 ? View.GONE : View.VISIBLE);
                findViewById(R.id.button_prev_dummy).setVisibility(currentIndex == 0 ? View.GONE : View.VISIBLE);
                findViewById(R.id.card_answers_dummy).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.text_choice_one_dummy)).setText(nextCard.getAnswer());
                ((TextView) findViewById(R.id.text_choice_two_dummy)).setText(nextCard.getWrongAnswer1());
                ((TextView) findViewById(R.id.text_choice_three_dummy)).setText(nextCard.getWrongAnswer2());
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setCard(nextCard);
                findViewById(R.id.button_next).setVisibility(currentIndex == cards.size() - 1 ? View.GONE : View.VISIBLE);
                findViewById(R.id.button_prev).setVisibility(currentIndex == 0 ? View.GONE : View.VISIBLE);
                findViewById(R.id.text_question).setAlpha(1);
                findViewById(R.id.text_answer).setAlpha(0);
                findViewById(R.id.card_question).setTranslationX(0);
                findViewById(R.id.card_question_dummy).setVisibility(View.GONE);
                findViewById(R.id.card_answers).setTranslationX(0);
                findViewById(R.id.card_answers_dummy).setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animSet_switch = new AnimatorSet();
        animSet_switch.playTogether(card_switch, header);

        toggleChoices = ValueAnimator.ofInt();
        toggleChoices.setInterpolator(new DecelerateInterpolator(1.5f));
        toggleChoices.setDuration(getResources().getInteger(R.integer.anim_duration_400));
        toggleChoices.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            View sc = findViewById(R.id.section_choices);

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ViewGroup.LayoutParams lp = sc.getLayoutParams();
                lp.height = (int) animation.getAnimatedValue();
                sc.setLayoutParams(lp);
            }
        });
        toggleChoices.addListener(new Animator.AnimatorListener() {
            View sc = findViewById(R.id.section_choices);

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

        cardStorage = new CardStorage(getApplicationContext());
        if ((cards = cardStorage.getAllCards()).isEmpty()) {
            cards.add(new CardObject("Who was the 44th President of the United States?", "Barack Obama", "George Bush", "Donald Trump"));
            cardStorage.insertCards(cards.get(0));
        }
        setCard(cards.get(currentIndex = 0));

        if (cards.size() == 1)
            findViewById(R.id.button_next).setVisibility(View.GONE);
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
            intent.putExtra(EDIT_DATA, current);
            intent.putExtra(EDIT_DELETABLE, cards.size() != 1);
            intent.putExtra(EDIT_PREVIOUS, true);
            startActivityForResult(intent, REQUEST_CODE);
        } else return super.onOptionsItemSelected(item);
        return true;
    }

    public void onAddClick(View v) {
        if (isEditing) return;
        isEditing = true;
        if (answerShown) flipCard(true);
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(EDIT_DELETABLE, false);
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

    public void onIncorrectChoiceClick(View v) {
        if (answerShown || cardAnimRunning) return;
        selAns = (TransitionDrawable) ((RippleDrawable) v.getBackground()).getDrawable(1);
        selAns.startTransition(getResources().getInteger(R.integer.anim_duration_400));
        flipCard(false);
    }

    public void onSwitchCard(View v) {
        if (cardAnimRunning) return;
        if (selAns != null) {
            selAns.reverseTransition(getResources().getInteger(R.integer.anim_duration_300));
            selAns = null;
        }
        answerShown = false;
        switchingCard = true;
        boolean browseNext = v.getId() == R.id.button_prev;
        card_switch.setIntValues(0, findViewById(R.id.layout_question).getWidth() * (browseNext ? 1 : -1));
        nextCard = cards.get(browseNext ? --currentIndex : ++currentIndex);
        animSet_switch.start();
    }

    public void onToggleChoices(View v) {
        if (hideAnimRunning) return;
        if (choicesHidden) {
            toggleChoices.setIntValues(0, choicesHeight);
            ((ImageButton) findViewById(R.id.button_toggle)).setImageResource(R.drawable.ic_visibility_off);
        } else {
            toggleChoices.setIntValues(choicesHeight = findViewById(R.id.card_answers).getHeight(), 0);
            ((ImageButton) findViewById(R.id.button_toggle)).setImageResource(R.drawable.ic_visibility);
        }
        toggleChoices.start();
    }

    private void flipCard(boolean reverse) {
        if (cardAnimRunning) return;
        if (answerShown && selAns != null) {
            selAns.reverseTransition(getResources().getInteger(R.integer.anim_duration_400));
            selAns = null;
        }
        answerShown = !answerShown;
        card_flip.setInterpolator(new OffsetInterpolator(reverse));
        animSet_flip.start();
    }

    private void setCard(@NonNull CardObject card) {
        current = card;
        ((TextView) findViewById(R.id.text_question)).setText(card.getQuestion());
        ((TextView) findViewById(R.id.text_answer)).setText(card.getAnswer());
        ((TextView) findViewById(R.id.text_choice_one)).setText(card.getAnswer());
        ((TextView) findViewById(R.id.text_choice_two)).setText(card.getWrongAnswer1());
        ((TextView) findViewById(R.id.text_choice_three)).setText(card.getWrongAnswer2());
    }
}
