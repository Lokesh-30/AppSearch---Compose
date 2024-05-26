package com.example.appsearchcompose.presentation

import com.example.appsearchcompose.appsearch.Todo

data class TodoListState(
    val todos: List<Todo> = emptyList(),
    val query: String = ""
)