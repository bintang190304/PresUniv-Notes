package com.example.presidentnotes.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.presidentnotes.R;
import com.example.presidentnotes.activities.CreateNoteActivity;
import com.example.presidentnotes.adapter.NotesAdapter;
import com.example.presidentnotes.database.NotesDatabase;
import com.example.presidentnotes.entities.Note;
import com.example.presidentnotes.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ADD_NOTE = 1;
//    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    private RecyclerView notesRecyclerView;
    private NotesAdapter notesAdapter;
    private List<Note> noteList;
//    private int noteClickedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), CreateNoteActivity.class), REQUEST_CODE_ADD_NOTE
                );
            }
        });

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        );
        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList);
        notesRecyclerView.setAdapter(notesAdapter);

        getNotes();

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (noteList.size() != 0){
                    notesAdapter.searchNotes(s.toString());
                }
            }
        });
    }

//    @Override
//    public void onNoteClicked(Note note, int position) {
//        noteClickedPosition = position;
//        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
//        intent.putExtra("isViewOrUpdate", true);
//        intent.putExtra("note", note);
//        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
//    }

    private void getNotes(){
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>>{

            @Override
            protected List<Note> doInBackground(Void... voids){
                return NotesDatabase.getDatabase(getApplicationContext()).noteDao().getAllNotes();
            }
            @Override
            protected void onPostExecute(List<Note> notes){
                super.onPostExecute(notes);
                if (noteList.size() == 0){
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                }else {
                    noteList.add(0, notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                }
                notesRecyclerView.smoothScrollToPosition(0);
            }
        }

        new GetNotesTask().execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
            getNotes();
        }
    }
}