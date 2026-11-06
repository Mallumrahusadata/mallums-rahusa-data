package com.mallum.rahusadata.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mallum.rahusadata.data.model.User
import com.mallum.rahusadata.ui.theme.*
import com.mallum.rahusadata.ui.viewmodel.RahusaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: RahusaViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val stats = uiState.adminStats
    val users = uiState.adminUsers

    var selectedUserForAdjustment by remember { mutableStateOf<User?>(null) }
    var adjustType by remember { mutableStateOf("CREDIT") }
    var adjustAmountText by remember { mutableStateOf("1000") }
    var adjustReason by remember { mutableStateOf("Reconciliation") }

    LaunchedEffect(Unit) {
        viewModel.loadAdminData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Control Portal", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.loadAdminData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = White)
            )
        },
        containerColor = Slate50
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("OWNER ADMINISTRATOR", color = GreenAccent, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("MALLUM'S RAHUSA DATA", color = White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                        Text("Owner: abubakarisahmallum@gmail.com", color = Slate500, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            // Stats KPI Grid
            item {
                Text("Platform Metrics", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate900)
                Spacer(modifier = Modifier.height(8.dp))

                stats?.let { s ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard("Total Users", s.totalUsers.toString(), BluePrimary, Modifier.weight(1f))
                            StatCard("Customer Balances", "₦" + String.format("%,.0f", s.totalBalance), GreenPrimary, Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard("Today's Sales", "₦" + String.format("%,.0f", s.todaySales), Color(0xFF6366F1), Modifier.weight(1f))
                            StatCard("Gross Profit", "₦" + String.format("%,.0f", s.totalProfit), GreenAccent, Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatCard("All-Time Sales", "₦" + String.format("%,.0f", s.totalSales), Slate900, Modifier.weight(1f))
                            StatCard("VTU API Balance", "₦" + String.format("%,.0f", s.apiBalance), Amber500, Modifier.weight(1f))
                        }
                    }
                }
            }

            // User Management Section
            item {
                Text("Manage Users (${users.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate900)
            }

            items(users) { u ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(u.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                            Text("${u.phoneNumber} · ${u.email}", fontSize = 10.sp, color = Slate500, fontFamily = FontFamily.Monospace)
                            Text(
                                "Balance: ₦" + String.format("%,.2f", u.balance),
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = GreenPrimary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Button(
                            onClick = { selectedUserForAdjustment = u },
                            colors = ButtonDefaults.buttonColors(containerColor = BlueLight),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Adjust Wallet", color = BluePrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Adjust Wallet Dialog
    selectedUserForAdjustment?.let { targetUser ->
        Dialog(onDismissRequest = { selectedUserForAdjustment = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Adjust User Balance", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Slate900)
                    Text("User: ${targetUser.fullName}", fontSize = 12.sp, color = Slate700)
                    Text("Current: ₦${String.format("%,.2f", targetUser.balance)}", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = GreenPrimary)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { adjustType = "CREDIT" },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = if (adjustType == "CREDIT") GreenPrimary else Slate100),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Credit (+)", color = if (adjustType == "CREDIT") White else Slate800, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Button(
                            onClick = { adjustType = "DEBIT" },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = if (adjustType == "DEBIT") Red600 else Slate100),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Debit (-)", color = if (adjustType == "DEBIT") White else Slate800, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    OutlinedTextField(
                        value = adjustAmountText,
                        onValueChange = { adjustAmountText = it.filter { c -> c.isDigit() } },
                        label = { Text("Amount (₦)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = adjustReason,
                        onValueChange = { adjustReason = it },
                        label = { Text("Reason") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val amt = adjustAmountText.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                viewModel.adjustUserWallet(targetUser.id, adjustType, amt, adjustReason)
                                selectedUserForAdjustment = null
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Apply Adjustment", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, valueColor: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 10.sp, color = Slate500, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 16.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, color = valueColor)
        }
    }
}
