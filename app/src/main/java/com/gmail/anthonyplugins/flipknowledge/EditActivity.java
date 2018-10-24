package com.gmail.anthonyplugins.flipknowledge;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar title = findViewById(R.id.activity_edit_toolbar);
        title.setTitle(R.string.edit_card);
        setSupportActionBar(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.done_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done_card) {
            Toast toast = new Toast(this);
            toast.setView(LayoutInflater.from(this).inflate(R.layout.dark_toast, null));
            toast.setDuration(Toast.LENGTH_SHORT);

            View v = findViewById(R.id.total_window);
            int offset = v.getRootView().getHeight() - v.getHeight() - getSupportActionBar().getHeight();
            toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, offset);

            if (isEmpty((EditText) findViewById(R.id.edit_question))) {
                toast.setText(R.string.edit_no_question);
                toast.show();
                return true;
            }
            if (isEmpty((EditText) findViewById(R.id.edit_answer))) {
                toast.setText(R.string.edit_no_answer);
                toast.show();
                return true;
            }
            if (isEmpty((EditText) findViewById(R.id.edit_answer_1)) && isEmpty((EditText) findViewById(R.id.edit_answer_2))) {
                toast.setText(R.string.edit_no_alternative);
                toast.show();
                return true;
            }

            Intent result = new Intent();
            result.putExtra("", "Entry");
            setResult(Activity.RESULT_OK, result);
            finish();
        } else return super.onOptionsItemSelected(item);
        return true;
    }

    private boolean isEmpty(TextView tv) {
        return tv.getText().toString().trim().length() == 0;
    }
}
