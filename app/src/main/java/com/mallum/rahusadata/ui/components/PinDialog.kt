package com.mallum.rahusadata.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.FragmentActivity
import com.mallum.rahusadata.security.BiometricAuthManager
import com.mallum.rahusadata.security.BiometricStatus
import com.mallum.rahusadata.ui.theme.*

@Composable
fun PinDialog(
    isOpen: Boolean,
    title: String,
    description: String,
    amount: Double,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    if (!isOpen) return

    val context = LocalContext.current
    val activity = context as? FragmentActivity
    var pin by remember { mutableStateOf("") }

    LaunchedEffect(pin) {
        if (pin.length == 4) {
            onConfirm(pin)
            pin = ""
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(BlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate500)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
                Text(description, fontSize = 12.sp, color = Slate500, maxLines = 1)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "₦" + String.format("%,.2f", amount),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Slate900
                )
                Text("Default PIN: 1234", fontSize = 11.sp, color = BluePrimary, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.height(16.dp))

                // PIN dots
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 4) {
                        val isFilled = pin.length > i
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) BluePrimary else Slate200)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Numpad Grid
                val digits = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("BIO", "0", "DEL")
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    digits.forEach { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            row.forEach { d ->
                                if (d == "BIO") {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp, 48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(GreenLight)
                                            .clickable {
                                                activity?.let { act ->
                                                    BiometricAuthManager.authenticate(
                                                        activity = act,
                                                        title = "Authorize Payment",
                                                        subtitle = "Verify fingerprint or face to pay ₦${String.format("%,.0f", amount)}",
                                                        onSuccess = {
                                                            onConfirm("1234")
                                                        },
                                                        onError = {}
                                                    )
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Fingerprint,
                                            contentDescription = "Biometric Authorize",
                                            tint = GreenPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                } else if (d == "DEL") {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp, 48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Slate100)
                                            .clickable {
                                                if (pin.isNotEmpty()) pin = pin.dropLast(1)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.Backspace,
                                            contentDescription = "Backspace",
                                            tint = Slate700,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp, 48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Slate100)
                                            .clickable {
                                                if (pin.length < 4) pin += d
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = d,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate900
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
