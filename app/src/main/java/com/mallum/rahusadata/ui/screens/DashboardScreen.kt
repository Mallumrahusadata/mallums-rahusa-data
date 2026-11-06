package com.mallum.rahusadata.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mallum.rahusadata.data.model.Transaction
import com.mallum.rahusadata.ui.theme.*
import com.mallum.rahusadata.ui.viewmodel.RahusaViewModel

@Composable
fun DashboardScreen(
    viewModel: RahusaViewModel,
    onNavigateToData: () -> Unit,
    onNavigateToAirtime: () -> Unit,
    onNavigateToElectricity: () -> Unit,
    onNavigateToCable: () -> Unit,
    onNavigateToFund: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user
    val context = LocalContext.current

    var isBalanceVisible by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate50)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Top Greeting Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BluePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user?.fullName?.firstOrNull()?.toString() ?: "M",
                            color = White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Hello, ${user?.fullName?.split(" ")?.firstOrNull() ?: "Customer"}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = Slate900
                        )
                        Text(
                            text = "Affordable Data, Fast Delivery.",
                            fontSize = 11.sp,
                            color = Slate500
                        )
                    }
                }

                if (user?.role == "admin") {
                    Surface(
                        color = GreenLight,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.clickable { onNavigateToAdmin() }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Admin", color = GreenPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Announcement Banner
        item {
            Surface(
                color = Slate900,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = GreenPrimary,
                        shape = CircleShape
                    ) {
                        Text(
                            "PROMO",
                            color = White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MTN SME 1GB @ ₦280 & Airtel CG 1GB @ ₦310 are delivering in < 5 seconds!",
                        color = Slate100,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }

        // Wallet Balance Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BluePrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Available Balance", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Balance",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { isBalanceVisible = !isBalanceVisible }
                            )
                        }

                        Button(
                            onClick = onNavigateToFund,
                            colors = ButtonDefaults.buttonColors(containerColor = GreenAccent),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Slate900, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Fund Wallet", color = Slate900, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isBalanceVisible) "₦" + String.format("%,.2f", user?.balance ?: 0.0) else "₦••••••••",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Monospace,
                        color = White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Virtual Account Quick Card
                    user?.virtualAccounts?.firstOrNull()?.let { acc ->
                        Surface(
                            color = Color.Black.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Automated Bank Transfer (Instant Credit)", fontSize = 10.sp, color = GreenLight, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(acc.bankName, fontSize = 11.sp, color = White, fontWeight = FontWeight.Bold)
                                        Text(" · ", color = White)
                                        Text(acc.accountNumber, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, color = GreenAccent)
                                    }
                                }

                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable {
                                        val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        cb.setPrimaryClip(ClipData.newPlainText("Account Number", acc.accountNumber))
                                        Toast.makeText(context, "Account number copied", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = White, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy", color = White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick VTU Services Grid
        item {
            Text("Quick VTU Services", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Slate900)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ServiceCard(
                    title = "Buy Data",
                    subtitle = "SME & Direct",
                    icon = Icons.Default.Wifi,
                    iconColor = BluePrimary,
                    bgColor = BlueLight,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToData
                )
                ServiceCard(
                    title = "Buy Airtime",
                    subtitle = "2% Discount",
                    icon = Icons.Default.Phone,
                    iconColor = GreenPrimary,
                    bgColor = GreenLight,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAirtime
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ServiceCard(
                    title = "Electricity",
                    subtitle = "Prepaid Token",
                    icon = Icons.Default.Bolt,
                    iconColor = Amber500,
                    bgColor = Amber100,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToElectricity
                )
                ServiceCard(
                    title = "Cable TV",
                    subtitle = "DStv, GOtv",
                    icon = Icons.Default.Tv,
                    iconColor = Color(0xFF6366F1),
                    bgColor = Color(0xFFEEF2FF),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToCable
                )
            }
        }

        // Live Rates Glance
        item {
            Text("Live Best Rates", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Slate900)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    RateRow("MTN SME 1.0GB", "30 Days", "₦280", "Instant", Amber500, onNavigateToData)
                    Divider(color = Slate100, modifier = Modifier.padding(vertical = 8.dp))
                    RateRow("Airtel CG 1.0GB", "30 Days", "₦310", "Instant", Red600, onNavigateToData)
                    Divider(color = Slate100, modifier = Modifier.padding(vertical = 8.dp))
                    RateRow("Glo SME 1.0GB", "30 Days", "₦275", "Instant", GreenPrimary, onNavigateToData)
                }
            }
        }

        // Recent Transactions Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent Transactions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Slate900)
                Text(
                    "View All",
                    fontSize = 12.sp,
                    color = BluePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToTransactions() }
                )
            }
        }

        if (uiState.transactions.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transactions yet. Recharge data or airtime above!", fontSize = 12.sp, color = Slate500)
                    }
                }
            }
        } else {
            items(uiState.transactions.take(5)) { tx ->
                TransactionCard(tx = tx) {
                    viewModel.showReceipt(tx)
                }
            }
        }

        item {
            // WhatsApp Support Banner
            Surface(
                color = GreenLight,
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth().clickable {
                    val url = "https://wa.me/2348031234567?text=Hello%20MALLUM'S%20RAHUSA%20DATA%20Support"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Need Assistance?", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GreenPrimary)
                        Text("We respond in < 2 mins on WhatsApp", fontSize = 11.sp, color = Slate700)
                    }
                    Button(
                        onClick = {
                            val url = "https://wa.me/2348031234567?text=Hello%20MALLUM'S%20RAHUSA%20DATA%20Support"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Chat WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ServiceCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        modifier = modifier.clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Slate900)
            Text(subtitle, fontSize = 11.sp, color = Slate500)
        }
    }
}

@Composable
private fun RateRow(
    title: String,
    validity: String,
    price: String,
    badge: String,
    networkColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(networkColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900)
                Text(validity, fontSize = 10.sp, color = Slate500)
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(price, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace, fontSize = 13.sp, color = BluePrimary)
            Text(badge, fontSize = 10.sp, color = GreenPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun TransactionCard(
    tx: Transaction,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when (tx.service) {
                                "DATA" -> BlueLight
                                "AIRTIME" -> GreenLight
                                "ELECTRICITY" -> Amber100
                                "CABLE" -> Color(0xFFEEF2FF)
                                else -> Slate100
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        when (tx.service) {
                            "DATA" -> Icons.Default.Wifi
                            "AIRTIME" -> Icons.Default.Phone
                            "ELECTRICITY" -> Icons.Default.Bolt
                            "CABLE" -> Icons.Default.Tv
                            else -> Icons.Default.AccountBalanceWallet
                        },
                        contentDescription = null,
                        tint = when (tx.service) {
                            "DATA" -> BluePrimary
                            "AIRTIME" -> GreenPrimary
                            "ELECTRICITY" -> Amber500
                            "CABLE" -> Color(0xFF6366F1)
                            else -> Slate700
                        },
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(tx.description, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900, maxLines = 1)
                    Text("${tx.beneficiary} · ${tx.reference}", fontSize = 10.sp, color = Slate500, maxLines = 1)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (tx.service == "FUNDING") "+" else "-"}₦" + String.format("%,.0f", tx.amount),
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    color = if (tx.service == "FUNDING") GreenPrimary else Slate900
                )
                Surface(
                    color = if (tx.status == "SUCCESSFUL") GreenLight else Red100,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        tx.status,
                        color = if (tx.status == "SUCCESSFUL") GreenPrimary else Red600,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
