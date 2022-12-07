package com.example.todocomposeapp.ui.todo_list

import com.example.todocomposeapp.data.Todo

sealed class TodolistEvent {
    data class OnDeleteTodoClick(val todo: Todo): TodolistEvent()
    data class OnDoneChange(val todo: Todo, val isDone: Boolean): TodolistEvent()
    object OnUndoDeleteClick: TodolistEvent()
    data class OnTodoClick(val todo: Todo): TodolistEvent()
    object OnAddTodoClick: TodolistEvent()
}
