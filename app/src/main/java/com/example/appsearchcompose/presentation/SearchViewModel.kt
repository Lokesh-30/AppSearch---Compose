package com.example.appsearchcompose.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appsearchcompose.appsearch.Todo
import com.example.appsearchcompose.appsearch.TodoSearchManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.random.Random

class SearchViewModel(
    private val searchManager: TodoSearchManager
): ViewModel() {

    var state by mutableStateOf(TodoListState())
        private set

    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            searchManager.init()
            val list = (1..100).map {
                Todo(
                    nameSpace = "my_todos",
                    id = UUID.randomUUID().toString(),
                    score = 1,
                    title = "Todo $it",
                    text = "Description $it",
                    isDone = Random.nextBoolean()
                )
            }
            searchManager.putTodos(list)
        }
    }

    fun searchQuery(query: String) {
        state = state.copy(query = query)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            state = state.copy(todos = searchManager.searchTodos(query))
        }
    }

    fun onDoneChange(todo: Todo, isDone: Boolean) {
        viewModelScope.launch {
            searchManager.putTodos(
                listOf(
                    todo.copy(
                        isDone = isDone
                    )
                )
            )
            state = state.copy(
                todos = state.todos.map {
                    if (it.id == todo.id) {
                        it.copy(isDone = isDone)
                    } else it
                }
            )
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            state = state.copy(
                todos = state.todos.filter { it.id != id }
            )
            withContext(Dispatchers.IO) {
                searchManager.deleteItem(id)
            }
        }
    }

    override fun onCleared() {
        searchManager.closeSession()
        super.onCleared()
    }
}