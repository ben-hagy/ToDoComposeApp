package com.example.todocomposeapp.ui.todo_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todocomposeapp.data.Todo
import com.example.todocomposeapp.data.TodoRepository
import com.example.todocomposeapp.util.Routes
import com.example.todocomposeapp.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository
): ViewModel() {

    val todos = repository.getTodos()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var deletedToDo: Todo? = null

    fun onEvent(event: TodolistEvent) {
        when(event) {
            is TodolistEvent.OnTodoClick -> {
                sendUiEvent(UiEvent.Navigate(
                    Routes.ADD_EDIT_TODO + "?todoId=${event.todo.id}"))
            }
            is TodolistEvent.OnAddTodoClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.ADD_EDIT_TODO))
            }
            is TodolistEvent.OnUndoDeleteClick -> {
                deletedToDo?.let { todo ->
                    viewModelScope.launch {
                        repository.insertTodo(todo)
                    }
                }
            }
            is TodolistEvent.OnDeleteTodoClick -> {
                viewModelScope.launch{
                    deletedToDo = event.todo
                    repository.deleteTodo(event.todo)
                    sendUiEvent(UiEvent.ShowSnackbar(
                        message = "To-do Item Deleted!",
                        action = "Undo?"
                    ))
                }

            }
            is TodolistEvent.OnDoneChange -> {
                viewModelScope.launch {
                    repository.insertTodo(
                        event.todo.copy(
                            isDone = event.isDone
                        )
                    )
                }
            }
        }
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}