package com.mx.notes.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mx.notes.R;
import com.mx.notes.adapter.NoteAdapter;
import com.mx.notes.database.MyDatabaseHelper;
import com.mx.notes.model.Note;

import java.util.ArrayList;
import java.util.List;

public class NoteListActivity extends AppCompatActivity {

    private List<Note> noteList;
    private NoteAdapter adapter;
    private int position;
    private MyDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        databaseHelper = new MyDatabaseHelper(this, "notes.db", null, 1);

        initList();
        adapter = new NoteAdapter(this, noteList);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            update(position);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            delete(position);
            return true;
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_newnote:
                Intent intent = new Intent(this, NoteDetailActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.menu_exit:
                finish();
                break;
            default:
                break;
        }
        return  true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Note note = (Note) data.getSerializableExtra("note");
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    db.execSQL("insert into notes(id,title,content,time) values(?,?,?,?)", new String[]{note.getId(), note.getTitle(), note.getContent(), note.getTime()});
                    noteList.add(note);
                    adapter.notifyDataSetChanged();
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    Note note = (Note) data.getSerializableExtra("note");
                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                    db.execSQL("update notes set title = ?,content = ?, time = ? where id = ?", new String[]{note.getTitle(), note.getContent(), note.getTime(), note.getId()});
                    noteList.set(position, note);
                    adapter.notifyDataSetChanged();
                }
            default:
                break;
        }
    }

    private void initList() {
        noteList = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from notes", null);
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String time = cursor.getString(cursor.getColumnIndex("time"));

                Note note = new Note(id, title, content, time);
                noteList.add(note);
            }
        }
    }

    public void update(int position) {
        Intent intent = new Intent(this, NoteDetailActivity.class);
        intent.putExtra("note", noteList.get(position));
        this.position = position;
        startActivityForResult(intent, 2);
    }


    public void delete(int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("删除");
        alertDialog.setMessage("确定要删除吗");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("确定", (dialog, which) -> {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            Note note = noteList.get(position);
            db.execSQL("delete from notes where id = ?", new String[]{note.getId()});
            noteList.remove(position);
            adapter.notifyDataSetChanged();
        });
        alertDialog.setNegativeButton("取消", (dialog, which) -> {
        });

        alertDialog.show();
    }
}
