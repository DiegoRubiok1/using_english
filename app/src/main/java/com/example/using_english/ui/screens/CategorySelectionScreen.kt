package com.example.using_english.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.using_english.ui.theme.Using_englishTheme

data class CategoryItem(val name: String, val enabled: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionScreen(level: String, onCategorySelected: (String) -> Unit, onBack: () -> Unit) {
    val categories = listOf(
        CategoryItem("Reading"),
        CategoryItem("Use of English", enabled = true),
        CategoryItem("Listening")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Level $level - Categories") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                CategoryCard(category, onCategorySelected)
            }
        }
    }
}

@Composable
fun CategoryCard(category: CategoryItem, onCategorySelected: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = category.enabled) { onCategorySelected(category.name) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (category.enabled) MaterialTheme.colorScheme.surfaceVariant 
                             else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (category.enabled) MaterialTheme.colorScheme.onSurface 
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategorySelectionScreenPreview() {
    Using_englishTheme {
        CategorySelectionScreen(level = "C1", onCategorySelected = {}, onBack = {})
    }
}
