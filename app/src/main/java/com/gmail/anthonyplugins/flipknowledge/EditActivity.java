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

@SuppressWarnings("ConstantConditions")
public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar title = findViewById(R.id.activity_edit_toolbar);
        if (getIntent().getExtras().getBoolean(MainActivity.EDIT_PREVIOUS)) {
            title.setTitle(R.string.title_edit);
            String[] data = getIntent().getExtras().getStringArray(MainActivity.EDIT_DATA);
            if (data != null) {
                ((TextView) findViewById(R.id.edit_question)).setText(data[0]);
                ((TextView) findViewById(R.id.edit_answer)).setText(data[1]);
                ((TextView) findViewById(R.id.edit_answer1)).setText(data[2]);
                ((TextView) findViewById(R.id.edit_answer2)).setText(data[3]);
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
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent result = new Intent();
        result.putExtra(MainActivity.EDIT_PREVIOUS, false);
        setResult(Activity.RESULT_CANCELED, result);
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
            result.putExtra(MainActivity.EDIT_PREVIOUS, true);
            result.putExtra(MainActivity.EDIT_DATA, new String[]{
                    ((TextView) findViewById(R.id.edit_question)).getText().toString(),
                    ((TextView) findViewById(R.id.edit_answer)).getText().toString(),
                    ((TextView) findViewById(R.id.edit_answer1)).getText().toString(),
                    ((TextView) findViewById(R.id.edit_answer2)).getText().toString()});
            setResult(Activity.RESULT_OK, result);
            finish();
        } else return super.onOptionsItemSelected(item);
        return true;
    }

    private boolean isEmpty(TextView tv) {
        return tv.getText().toString().trim().length() == 0;
    }
}
