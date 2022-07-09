package com.bikcodeh.todoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.todoapp.data.model.ToDoData
import com.bikcodeh.todoapp.domain.repository.ToDoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToDoViewModel @Inject constructor(
    private val toDoRepository: ToDoRepository
) : ViewModel() {

    private val _notes: MutableStateFlow<List<ToDoData>> = MutableStateFlow(emptyList())
    val notes: StateFlow<List<ToDoData>>
        get() = _notes.asStateFlow()

    val events = Channel<ToDoUiEvents>(Channel.CONFLATED)
    private val _events: Flow<ToDoUiEvents> = events.receiveAsFlow()

    private fun observeEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            _events.collect { event ->
                when (event) {
                    ToDoUiEvents.GetAllNotes -> getAllNotes()
                }
            }
        }
    }

    private fun getAllNotes() {
        toDoRepository.getAllNotes().map { notes ->
            _notes.value = notes
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    init {
        observeEvents()
        getAllNotes()
    }

    sealed class ToDoUiEvents {
        object GetAllNotes : ToDoUiEvents()
    }
}