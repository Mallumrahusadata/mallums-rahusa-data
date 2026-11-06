package com.mallum.rahusadata.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mallum.rahusadata.ui.theme.*
import com.mallum.rahusadata.ui.viewmodel.RahusaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FundWalletScreen(
    viewModel: RahusaViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf("virtual") } // "virtual", "card", "manual"
    var simAmountText by remember { mutableStateOf("2000") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fund Wallet", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Channel Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Slate100)
                        .padding(4.dp)
                ) {
                    val tabs = listOf(
                        "virtual" to "Bank Transfer",
                        "card" to "Card / Gateway",
                        "manual" to "Manual Proof"
                    )
                    tabs.forEach { (id, label) ->
                        val isSelected = selectedTab == id
                        Button(
                            onClick = { selectedTab = id },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) White else androidx.compose.ui.graphics.Color.Transparent
                            ),
                            shape = RoundedCornerShape(10.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = if (isSelected) 2.dp else 0.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) BluePrimary else Slate700,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            if (selectedTab == "virtual") {
                // Info banner
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = BlueLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Instant Automated Bank Funding", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BluePrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Transfer from any bank app (Kuda, OPay, Palmpay, GTBank, Zenith, Access, Moniepoint) to your dedicated virtual account below. Your wallet credits automatically in under 60 seconds.",
                                fontSize = 11.sp,
                                color = Slate700,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Dedicated Virtual Account Cards
                item {
                    Text("Your Dedicated Account Numbers", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        user?.virtualAccounts?.forEach { acc ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(acc.bankName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                                        Text(
                                            acc.accountNumber,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 17.sp,
                                            color = BluePrimary,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        )
                                        Text(acc.accountName, fontSize = 11.sp, color = Slate500)
                                    }

                                    Button(
                                        onClick = {
                                            val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            cb.setPrimaryClip(ClipData.newPlainText("Account Number", acc.accountNumber))
                                            Toast.makeText(context, "${acc.bankName} account number copied", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Slate100),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Slate800, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy", color = Slate800, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // Test Inflow Simulator
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Test Inflow (Webhook Simulator)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                                Surface(color = GreenLight, shape = RoundedCornerShape(6.dp)) {
                                    Text("Simulated", fontSize = 10.sp, color = GreenPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = simAmountText,
                                onValueChange = { simAmountText = it.filter { c -> c.isDigit() } },
                                label = { Text("Amount to Deposit (₦)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    val amt = simAmountText.toDoubleOrNull() ?: 1000.0
                                    viewModel.simulateBankInflow(amt, "Wema Bank / Monnify") {
                                        onNavigateBack()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Simulate Bank Inflow Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            if (selectedTab == "card") {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Secured 256-bit Online Gateway", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                            }

                            OutlinedTextField(
                                value = "5000",
                                onValueChange = {},
                                label = { Text("Amount (₦)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = "5399 8201 9283 4410",
                                onValueChange = {},
                                label = { Text("Card Number") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(value = "09/28", onValueChange = {}, label = { Text("Expiry") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                                OutlinedTextField(value = "381", onValueChange = {}, label = { Text("CVV") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Button(
                                onClick = {
                                    viewModel.simulateBankInflow(5000.0, "Card Gateway") {
                                        onNavigateBack()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Pay ₦5,000 Securely", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            if (selectedTab == "manual") {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Official Corporate Account Details", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
                            Text("Bank: Moniepoint Microfinance Bank", fontSize = 12.sp, color = Slate700)
                            Text("Account Name: MALLUM'S RAHUSA VENTURES", fontSize = 12.sp, color = Slate700)
                            Text("Account Number: 8134902811", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = BluePrimary)

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    val url = "https://wa.me/2348031234567?text=Hello%20Admin,%20I%20transferred%20payment%20for%20wallet%20funding.%20Email:%20${user?.email}"
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth().height(44.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Send Proof on WhatsApp", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
