package com.example.tugasmokom

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotesViewModel : ViewModel() {
    private val _notes = MutableStateFlow<List<String>>(emptyList())
    val notes: StateFlow<List<String>> = _notes

    fun addNote(note: String) {
        _notes.value = _notes.value + note
    }

    fun deleteNote(index: Int) {
        _notes.value = _notes.value.toMutableList().also { it.removeAt(index) }
    }
}
