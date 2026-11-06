package com.mallum.rahusadata.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptSheet(
    transaction: Transaction?,
    onDismiss: () -> Unit
) {
    if (transaction == null) return
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BluePrimary)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = GreenAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "MALLUM'S RAHUSA DATA",
                        color = White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Text(
                        "Affordable Data, Fast Delivery.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "TRANSACTION " + transaction.status,
                            color = GreenLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Amount Display
            Text("Amount Paid", fontSize = 11.sp, color = Slate500, fontWeight = FontWeight.Medium)
            Text(
                text = "₦" + String.format("%,.2f", transaction.amount),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = Slate900
            )

            // Prepaid Token Highlight (if applicable)
            if (!transaction.token.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(GreenLight)
                        .border(1.dp, GreenPrimary.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("PREPAID METER TOKEN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                    Text(
                        transaction.token,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Slate900,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    transaction.units?.let {
                        Text("Units: $it", fontSize = 11.sp, color = GreenPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Details Breakdown
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReceiptRow("Service", transaction.service)
                ReceiptRow("Description", transaction.description)
                ReceiptRow("Beneficiary", transaction.beneficiary)
                ReceiptRow("Provider", transaction.network)
                ReceiptRowWithCopy("Reference", transaction.reference) {
                    copyToClipboard(context, transaction.reference, "Reference copied")
                }
                ReceiptRow("Date & Time", transaction.createdAt)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // WhatsApp Share and Done buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        shareOnWhatsApp(context, transaction)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = Slate500)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate900)
    }
}

@Composable
private fun ReceiptRowWithCopy(label: String, value: String, onCopy: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = Slate500)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onCopy() }
        ) {
            Text(value, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = Slate900)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = BluePrimary, modifier = Modifier.size(14.dp))
        }
    }
}

private fun copyToClipboard(context: Context, text: String, toastMessage: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Transaction Reference", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
}

private fun shareOnWhatsApp(context: Context, tx: Transaction) {
    val message = """
        *MALLUM'S RAHUSA DATA - TRANSACTION RECEIPT*
        --------------------------------
        *Status:* ${tx.status}
        *Service:* ${tx.service}
        *Description:* ${tx.description}
        *Beneficiary:* ${tx.beneficiary}
        *Amount:* ₦${tx.amount}
        ${if (!tx.token.isNullOrBlank()) "*Prepaid Token:* ${tx.token}\n" else ""}
        *Reference:* ${tx.reference}
        *Date:* ${tx.createdAt}
        --------------------------------
        Affordable Data, Fast Delivery.
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(Intent.createChooser(intent, "Share Transaction Receipt"))
}
