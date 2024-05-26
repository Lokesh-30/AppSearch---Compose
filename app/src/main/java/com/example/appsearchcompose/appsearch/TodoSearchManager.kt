package com.example.appsearchcompose.appsearch

import android.content.Context
import androidx.appsearch.app.AppSearchSession
import androidx.appsearch.app.PutDocumentsRequest
import androidx.appsearch.app.RemoveByDocumentIdRequest
import androidx.appsearch.app.SearchSpec
import androidx.appsearch.app.SearchSpec.RANKING_STRATEGY_USAGE_COUNT
import androidx.appsearch.app.SetSchemaRequest
import androidx.appsearch.localstorage.LocalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoSearchManager(
    private val context: Context
) {
    private var session: AppSearchSession? = null

    suspend fun init() {
        withContext(Dispatchers.IO) {
            val sessionFuture = LocalStorage.createSearchSessionAsync(
                LocalStorage.SearchContext.Builder(
                    context, "todo"
                ).build()
            )
            val setSchemaRequest = SetSchemaRequest.Builder()
                .addDocumentClasses(Todo::class.java)
                .build()
            session = sessionFuture.get()
            session?.setSchemaAsync(setSchemaRequest)
        }
    }

    suspend fun putTodos(todos: List<Todo>): Boolean {
        return withContext(Dispatchers.IO) {
            session?.putAsync(
                PutDocumentsRequest.Builder()
                    .addDocuments(todos)
                    .build()
            )?.get()?.isSuccess == true
        }
    }

    suspend fun searchTodos(query: String): List<Todo> {
        return withContext(Dispatchers.IO) {
            val searchSpec = SearchSpec.Builder()
                .setSnippetCount(10)
                .addFilterNamespaces("my_todos")
                .setRankingStrategy(RANKING_STRATEGY_USAGE_COUNT)
                .build()
            val res = session?.search(query, searchSpec) ?: return@withContext emptyList()

            val page = res.nextPageAsync.get()

            page.mapNotNull {
                if (it.genericDocument.schemaType == Todo::class.java.simpleName) {
                    it.getDocument(Todo::class.java)
                } else null
            }
        }
    }

    suspend fun deleteItem(id: String) {
        withContext(Dispatchers.IO) {
            val req = RemoveByDocumentIdRequest.Builder("my_todos")
                .addIds(id)
                .build()
            session?.removeAsync(req)
        }
    }

    fun closeSession() {
        session?.close()
        session = null
    }
}