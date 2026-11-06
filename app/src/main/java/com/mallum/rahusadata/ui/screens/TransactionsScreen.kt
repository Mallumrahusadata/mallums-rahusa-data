package com.mallum.rahusadata.ui.screens
import androidx.compose.foundation.clickable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mallum.rahusadata.data.model.Transaction
import com.mallum.rahusadata.ui.theme.*
import com.mallum.rahusadata.ui.viewmodel.RahusaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: RahusaViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var serviceFilter by remember { mutableStateOf("ALL") }

    val serviceTabs = listOf("ALL", "DATA", "AIRTIME", "ELECTRICITY", "CABLE", "FUNDING")

    val filtered = uiState.transactions.filter { tx ->
        val matchesService = serviceFilter == "ALL" || tx.service.equals(serviceFilter, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                tx.reference.contains(searchQuery, ignoreCase = true) ||
                tx.beneficiary.contains(searchQuery, ignoreCase = true) ||
                tx.description.contains(searchQuery, ignoreCase = true)
        matchesService && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction History", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadTransactions() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = Slate50
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate500) },
                placeholder = { Text("Search reference, phone, service...") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                modifier = Modifier.fillMaxWidth()
            )

            // Category filter chips
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(serviceTabs) { tab ->
                    val isSelected = serviceFilter == tab
                    Surface(
                        color = if (isSelected) BluePrimary else White,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) BluePrimary else Slate200),
                        modifier = Modifier.clickable { serviceFilter = tab }
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) White else Slate700,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Results count
            Text(
                "${filtered.size} records found",
                fontSize = 11.sp,
                color = Slate500,
                fontWeight = FontWeight.Medium
            )

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions found", fontSize = 13.sp, color = Slate500)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtered) { tx ->
                        TransactionCard(tx = tx) {
                            viewModel.showReceipt(tx)
                        }
                    }
                }
            }
        }
    }
}
