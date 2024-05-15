package com.example.presidentnotes.listeners;

import com.example.presidentnotes.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
