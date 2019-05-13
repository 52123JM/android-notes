package com.mx.notes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mx.notes.R;
import com.mx.notes.model.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class NoteDetailActivity extends AppCompatActivity {

    Note note = null;
    EditText editTitle;
    EditText editContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        Intent intent = getIntent();
        if (intent != null)
            note = (Note) intent.getSerializableExtra("note");

        if (note == null) {
            note = new Note();
            String id = UUID.randomUUID().toString();
            note.setId(id);
        }
        editTitle = findViewById(R.id.title);
        editContent = findViewById(R.id.content);

        editTitle.setText(note.getTitle());
        editContent.setText(note.getContent());

        Button saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(v -> save());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "标题：" + editTitle.getText().toString() + "    " +
                        "内容：" + editContent.getText().toString());
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }

    String getTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = sdf.format(date);
        return now;
    }

    void save() {
        String title = editTitle.getText().toString();
        String content = editContent.getText().toString();

        if(title.trim().equals("") || content.trim().equals("")){
            Toast.makeText(this,"请输入标题和内容",Toast.LENGTH_LONG).show();
        }else {
            note.setTitle(title);
            note.setContent(content);
            note.setTime(getTime());

            Intent intent1 = new Intent();
            intent1.putExtra("note", note);
            setResult(RESULT_OK, intent1);
            finish();
        }
    }
}
