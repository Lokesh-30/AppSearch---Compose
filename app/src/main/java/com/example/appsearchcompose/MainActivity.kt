package com.example.appsearchcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appsearchcompose.appsearch.Todo
import com.example.appsearchcompose.appsearch.TodoSearchManager
import com.example.appsearchcompose.presentation.SearchViewModel
import com.example.appsearchcompose.ui.theme.AppSearchComposeTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SearchViewModel by viewModels(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(
                        TodoSearchManager(this@MainActivity)
                    ) as T
                }
            }
        }
    )

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppSearchComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                modifier = Modifier.padding(horizontal = 15.dp)
                            ) {
                                Text(text = "Todo", modifier = Modifier.weight(1f))
                                Image(
                                    painter = painterResource(id = R.drawable.ic_trash),
                                    contentDescription = "Clean storage",
                                    modifier = Modifier.clickable {

                                    }
                                )
                            }
                        },
                        colors = topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }) { innerPadding ->
                    Content(innerPadding)
                }
            }
        }
    }


    @Composable
    private fun Content(innerPadding: PaddingValues = PaddingValues(0.dp)) {
        Surface(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                OutlinedTextField(
                    value = viewModel.state.query,
                    onValueChange = viewModel::searchQuery,
                    singleLine = true,
                    label = {
                        Text(text = "Search here")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                )
                LazyColumn(
                    contentPadding = PaddingValues(15.dp),
                ) {
                    items(
                        items = viewModel.state.todos,
                    ) { todo ->
                        ItemView(todo)
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun ItemView(todo: Todo = Todo("hello", "hello", 1, "hello", "hello", true)) {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp,
                pressedElevation = 4.dp
            ),
            shape = RoundedCornerShape(6.dp),
            onClick = {
                viewModel.deleteItem(todo.id)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 10.dp)
        ) {
            Row(
                modifier = Modifier.padding(10.dp)
            ) {
                Column(
                    Modifier.weight(1f)
                ) {
                    Text(text = todo.title, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = todo.text)
                }
                Checkbox(
                    checked = todo.isDone,
                    onCheckedChange = { viewModel.onDoneChange(todo, it) }
                )
            }
        }
    }
}