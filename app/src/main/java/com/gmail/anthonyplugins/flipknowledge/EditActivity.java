package com.gmail.anthonyplugins.flipknowledge;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;

public class EditActivity extends AppCompatActivity {

    private CardObject current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar title = findViewById(R.id.activity_edit_toolbar);
        if (getIntent().getExtras().getBoolean(MainActivity.EDIT_PREVIOUS)) {
            title.setTitle(R.string.title_edit);
            current = (CardObject) getIntent().getExtras().getSerializable(MainActivity.EDIT_DATA);
            if (current != null) {
                ((TextView) findViewById(R.id.edit_question)).setText(current.getQuestion());
                ((TextView) findViewById(R.id.edit_answer)).setText(current.getAnswer());
                ((TextView) findViewById(R.id.edit_answer1)).setText(current.getWrongAnswer1());
                ((TextView) findViewById(R.id.edit_answer2)).setText(current.getWrongAnswer2());
            }
        } else title.setTitle(R.string.title_new);
        setSupportActionBar(title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        ((EditText) findViewById(R.id.edit_answer2)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN))
                    return findViewById(R.id.button_done).callOnClick();
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        if (!getIntent().getExtras().getBoolean(MainActivity.EDIT_DELETABLE))
            menu.removeItem(R.id.button_delete);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        setResult(Activity.RESULT_CANCELED);
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.button_done) {
            Toast toast = new Toast(this);
            toast.setView(LayoutInflater.from(this).inflate(R.layout.layout_toast, null));
            toast.setDuration(Toast.LENGTH_SHORT);

            View v = findViewById(R.id.activity_edit_root);
            int offset = v.getRootView().getHeight() - v.getHeight() - getSupportActionBar().getHeight();
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, offset);

            if (isEmpty((EditText) findViewById(R.id.edit_question))) {
                toast.setText(R.string.err_no_question);
                toast.show();
                return true;
            }
            if (isEmpty((EditText) findViewById(R.id.edit_answer))) {
                toast.setText(R.string.err_no_answer);
                toast.show();
                return true;
            }
            if (isEmpty((EditText) findViewById(R.id.edit_answer1)) || isEmpty((EditText) findViewById(R.id.edit_answer2))) {
                toast.setText(R.string.err_no_alternative);
                toast.show();
                return true;
            }

            Intent result = new Intent();
            result.putExtra(MainActivity.EDIT_PREVIOUS, getIntent().getExtras().getBoolean(MainActivity.EDIT_PREVIOUS));
            result.putExtra(MainActivity.EDIT_DATA, new CardObject(
                    current != null ? current.getUuid() : UUID.randomUUID().toString(),
                    ((TextView) findViewById(R.id.edit_question)).getText().toString(),
                    ((TextView) findViewById(R.id.edit_answer)).getText().toString(),
                    ((TextView) findViewById(R.id.edit_answer1)).getText().toString(),
                    ((TextView) findViewById(R.id.edit_answer2)).getText().toString()));
            setResult(Activity.RESULT_OK, result);
            finish();
            current = null;
            return true;
        }
        if (item.getItemId() == R.id.button_delete) {
            setResult(MainActivity.RESULT_DELETE);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isEmpty(TextView tv) {
        return tv.getText().toString().trim().length() == 0;
    }
}
