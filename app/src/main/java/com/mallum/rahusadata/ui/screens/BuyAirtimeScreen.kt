package com.mallum.rahusadata.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
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
import com.mallum.rahusadata.ui.components.PinDialog
import com.mallum.rahusadata.ui.theme.*
import com.mallum.rahusadata.ui.viewmodel.RahusaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyAirtimeScreen(
    viewModel: RahusaViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    var selectedNetwork by remember { mutableStateOf("MTN") }
    var phoneNumber by remember { mutableStateOf(user?.phoneNumber ?: "") }
    var amountText by remember { mutableStateOf("1000") }

    var isPinDialogOpen by remember { mutableStateOf(false) }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val discountPercent = uiState.settings?.airtimeDiscountPercent ?: 2.0
    val discount = (amount * discountPercent) / 100.0
    val payableAmount = (amount - discount).coerceAtLeast(0.0)

    val networks = listOf("MTN", "Airtel", "Glo", "9mobile")
    val quickAmounts = listOf("100", "200", "500", "1000", "2000", "5000")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buy Airtime", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Network Selector
            item {
                Text("Select Network", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    networks.forEach { net ->
                        val isSelected = selectedNetwork == net
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) {
                                        when (net) {
                                            "MTN" -> Amber500
                                            "Airtel" -> Red600
                                            "Glo" -> GreenPrimary
                                            else -> Color(0xFF65A30D)
                                        }
                                    } else White
                                )
                                .border(1.dp, if (isSelected) Color.Transparent else Slate200, RoundedCornerShape(12.dp))
                                .clickable { selectedNetwork = net }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(net, color = if (isSelected) White else Slate800, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Phone Number
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Phone Number", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                    user?.phoneNumber?.let { myPhone ->
                        Text("Use My Number", fontSize = 11.sp, color = BluePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { phoneNumber = myPhone })
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Slate500) },
                    placeholder = { Text("08012345678") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Amount Input & Quick Chips
            item {
                Text("Amount (₦)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickAmounts.forEach { amt ->
                        val isSelected = amountText == amt
                        Surface(
                            color = if (isSelected) GreenPrimary else White,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GreenPrimary else Slate200),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { amountText = amt }
                        ) {
                            Text(
                                text = "₦$amt",
                                color = if (isSelected) White else Slate800,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Discount Summary Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GreenLight),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Airtime Discount (2%)", fontSize = 12.sp, color = GreenPrimary, fontWeight = FontWeight.SemiBold)
                            Text("- ₦" + String.format("%,.2f", discount), fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = GreenPrimary)
                        }
                        Divider(color = GreenPrimary.copy(alpha = 0.2f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("You Pay", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            Text("₦" + String.format("%,.2f", payableAmount), fontSize = 16.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, color = Slate900)
                        }
                    }
                }
            }

            // Recharging CTA
            item {
                Button(
                    onClick = { isPinDialogOpen = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    enabled = amount >= 50 && (user?.balance ?: 0.0) >= payableAmount && phoneNumber.length >= 10
                ) {
                    Text("Recharge ₦${String.format("%,.0f", amount)} Airtime", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }

    PinDialog(
        isOpen = isPinDialogOpen,
        title = "Authorize Airtime Recharge",
        description = "$selectedNetwork ₦$amount Airtime to $phoneNumber",
        amount = payableAmount,
        onDismiss = { isPinDialogOpen = false },
        onConfirm = { pin ->
            isPinDialogOpen = false
            viewModel.buyAirtime(selectedNetwork, amount, phoneNumber, pin) {
                onNavigateBack()
            }
        }
    )
}
