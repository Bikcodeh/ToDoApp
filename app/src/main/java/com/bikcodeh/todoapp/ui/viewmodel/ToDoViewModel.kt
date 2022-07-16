package com.bikcodeh.todoapp.ui.viewmodel

import android.text.TextUtils
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bikcodeh.todoapp.R
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

    private var _helperNotes: MutableList<ToDoData>? = null

    private val _notes: MutableStateFlow<List<ToDoData>> = MutableStateFlow(emptyList())
    val notes: StateFlow<List<ToDoData>>
        get() = _notes.asStateFlow()

    private val _formState: MutableStateFlow<FormUiState> = MutableStateFlow(FormUiState())
    val formState: StateFlow<FormUiState>
        get() = _formState.asStateFlow()

    private val _addNoteEvent: MutableStateFlow<AddNoteUiEvent> =
        MutableStateFlow(AddNoteUiEvent.Idle)
    val addNoteEvent: StateFlow<AddNoteUiEvent> = _addNoteEvent.asStateFlow()

    private val _updateNoteUiEvent: MutableStateFlow<UpdateNoteUiEvent> =
        MutableStateFlow(UpdateNoteUiEvent.Idle)
    val updateNoteUiEvent: StateFlow<UpdateNoteUiEvent>
        get() = _updateNoteUiEvent

    private val _isEmpty: MutableLiveData<Boolean> = MutableLiveData(true)
    val isEmpty: LiveData<Boolean> get() = _isEmpty

    fun onEvent(event: ToDoUiEvent) {
        when (event) {
            ToDoUiEvent.GetAllNotes -> getAllNotes()
            is ToDoUiEvent.InsertNote -> insertNote(event.toDoData)
            is ToDoUiEvent.ValidateForm -> validateForm(event.title, event.description)
            is ToDoUiEvent.UpdateNote -> updateNote(event.toDoData)
            is ToDoUiEvent.DeleteNote -> deleteNote(event.toDoData)
            ToDoUiEvent.DeleteAllNotes -> deleteAllNotes()
            is ToDoUiEvent.FilterNotes -> filterNotes(event.query)
            ToDoUiEvent.OnFilterClear -> {
                _helperNotes?.let {
                    _notes.value = it
                }

            }
            ToDoUiEvent.SortByHighPriority -> sortByHighPriority()
            ToDoUiEvent.SortByLowPriority -> sortByLowPriority()
            ToDoUiEvent.SortByNonePriority -> {
                sortByNonePriority()
            }
        }
    }

    private fun sortByNonePriority() {
        _notes.update { currentNotes -> currentNotes.sortedBy { it.id } }
        _helperNotes = _helperNotes?.sortedBy { it.id }?.toMutableList()
    }

    private fun sortByLowPriority() {
        _notes.update { currentNotes -> currentNotes.sortedByDescending { it.priority.ordinal } }
        _helperNotes = _helperNotes?.sortedByDescending { it.priority.ordinal }?.toMutableList()
    }

    private fun sortByHighPriority() {
        _notes.update { currentNotes -> currentNotes.sortedBy { it.priority.ordinal } }
        _helperNotes = _helperNotes?.sortedBy { it.priority.ordinal }?.toMutableList()
    }

    private fun filterNotes(query: String) {
        _notes.update { currentNotes -> currentNotes.filter { it.title.contains(query) } }
    }

    private fun validateForm(title: String, description: String) {
        if (TextUtils.isEmpty(title)) {
            _formState.update { formUiState ->
                formUiState.copy(
                    isFormValid = false,
                    titleError = R.string.title_required
                )
            }
        } else {
            _formState.update { formUiState -> formUiState.copy(titleError = null) }
        }
        if (TextUtils.isEmpty(description)) {
            _formState.update { formUiState ->
                formUiState.copy(
                    isFormValid = false,
                    descriptionError = R.string.description_required
                )
            }
        } else {
            _formState.update { formUiState -> formUiState.copy(descriptionError = null) }
        }
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)) {
            _formState.update { formUiState ->
                formUiState.copy(
                    isFormValid = true,
                    titleError = null,
                    descriptionError = null
                )
            }
        }
    }

    private fun insertNote(toDoData: ToDoData) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepository.insertNote(toDoData)
            _addNoteEvent.value = AddNoteUiEvent.Success
        }
    }

    private fun getAllNotes() {
        toDoRepository.getAllNotes().map { notes ->
            _isEmpty.postValue(notes.isEmpty())
            _notes.value = notes
            _helperNotes = mutableListOf<ToDoData>().apply {
                addAll(notes)
            }
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun updateNote(toDoData: ToDoData) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepository.updateNote(toDoData)
            _updateNoteUiEvent.value = UpdateNoteUiEvent.Success
        }
    }

    private fun deleteNote(toDoData: ToDoData) {
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepository.deleteNote(toDoData)
            _updateNoteUiEvent.value = UpdateNoteUiEvent.SuccessDelete
        }
    }

    private fun deleteAllNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            toDoRepository.deleteAll()
        }
    }

    init {
        getAllNotes()
    }

    data class FormUiState(
        val isFormValid: Boolean = false,
        @IdRes val titleError: Int? = null,
        @IdRes val descriptionError: Int? = null
    )

    sealed class UpdateNoteUiEvent {
        object Success : UpdateNoteUiEvent()
        object SuccessDelete : UpdateNoteUiEvent()
        object Idle : UpdateNoteUiEvent()
    }

    sealed class AddNoteUiEvent {
        object Idle : AddNoteUiEvent()
        object Success : AddNoteUiEvent()
    }

    sealed class ToDoUiEvent {
        object GetAllNotes : ToDoUiEvent()
        data class InsertNote(val toDoData: ToDoData) : ToDoUiEvent()
        data class UpdateNote(val toDoData: ToDoData) : ToDoUiEvent()
        data class DeleteNote(val toDoData: ToDoData) : ToDoUiEvent()
        data class ValidateForm(val title: String, val description: String) : ToDoUiEvent()
        data class FilterNotes(val query: String) : ToDoUiEvent()
        object OnFilterClear : ToDoUiEvent()
        object SortByHighPriority : ToDoUiEvent()
        object SortByLowPriority : ToDoUiEvent()
        object SortByNonePriority : ToDoUiEvent()
        object DeleteAllNotes : ToDoUiEvent()
    }
}