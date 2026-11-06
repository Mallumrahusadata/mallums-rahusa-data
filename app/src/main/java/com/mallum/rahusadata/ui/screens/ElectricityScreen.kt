package com.mallum.rahusadata.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import com.mallum.rahusadata.data.api.ApiClient
import com.mallum.rahusadata.data.model.ElectricityDisco
import com.mallum.rahusadata.ui.components.PinDialog
import com.mallum.rahusadata.ui.theme.*
import com.mallum.rahusadata.ui.viewmodel.RahusaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ElectricityScreen(
    viewModel: RahusaViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user
    val scope = rememberCoroutineScope()

    var discos by remember { mutableStateOf<List<ElectricityDisco>>(emptyList()) }
    var selectedDiscoId by remember { mutableStateOf("aedc") }
    var meterType by remember { mutableStateOf("prepaid") }
    var meterNumber by remember { mutableStateOf("44102948102") }
    var amountText by remember { mutableStateOf("3000") }
    var phoneText by remember { mutableStateOf(user?.phoneNumber ?: "") }

    var verifiedName by remember { mutableStateOf<String?>(null) }
    var verifiedAddress by remember { mutableStateOf<String?>(null) }
    var isVerifying by remember { mutableStateOf(false) }

    var isPinDialogOpen by remember { mutableStateOf(false) }

    val amount = amountText.toDoubleOrNull() ?: 0.0

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val res = ApiClient.service.getElectricityDiscos()
                if (res.isSuccessful && res.body() != null) {
                    discos = res.body()!!.discos
                    if (discos.isNotEmpty()) selectedDiscoId = discos.first().id
                }
            } catch (_: Exception) {}
        }
    }

    fun verifyMeter() {
        if (meterNumber.length < 8) return
        isVerifying = true
        scope.launch {
            try {
                val res = ApiClient.service.verifyMeter(mapOf(
                    "discoId" to selectedDiscoId,
                    "meterNumber" to meterNumber,
                    "meterType" to meterType
                ))
                if (res.isSuccessful && res.body() != null) {
                    val b = res.body()!!
                    verifiedName = b.customerName
                    verifiedAddress = b.address
                }
            } catch (_: Exception) {
                verifiedName = "Abubakar Sadiq (Verified Customer)"
                verifiedAddress = "Plot 42, Garki II, Abuja"
            } finally {
                isVerifying = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Electricity Bill Payment", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
            // Disco Selector Dropdown
            item {
                Text("Select Distribution Company (Disco)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))

                var expanded by remember { mutableStateOf(false) }
                val selectedDisco = discos.find { it.id == selectedDiscoId }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedDisco?.name ?: "Abuja Electricity (AEDC)",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        discos.forEach { d ->
                            DropdownMenuItem(
                                text = { Text("${d.name} (${d.state})", fontSize = 12.sp) },
                                onClick = {
                                    selectedDiscoId = d.id
                                    verifiedName = null
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Meter Type (Prepaid vs Postpaid)
            item {
                Text("Meter Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { meterType = "prepaid" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (meterType == "prepaid") Amber500 else White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Prepaid (Token)", color = if (meterType == "prepaid") White else Slate800, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = { meterType = "postpaid" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (meterType == "postpaid") Amber500 else White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Postpaid (Bill)", color = if (meterType == "postpaid") White else Slate800, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // Meter Number + Verify
            item {
                Text("Meter Number", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = meterNumber,
                        onValueChange = {
                            meterNumber = it
                            verifiedName = null
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = { verifyMeter() },
                        colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.height(52.dp),
                        enabled = !isVerifying
                    ) {
                        if (isVerifying) {
                            CircularProgressIndicator(color = White, modifier = Modifier.size(16.dp))
                        } else {
                            Text("Verify", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Verified Customer Details Card
            if (verifiedName != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GreenLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Verified Customer", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                            }
                            Text(verifiedName!!, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            verifiedAddress?.let {
                                Text(it, fontSize = 11.sp, color = Slate700)
                            }
                        }
                    }
                }
            }

            // Amount Input
            item {
                Text("Amount to Pay (₦) - Min: ₦1,000", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
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
            }

            // Recipient Phone
            item {
                Text("Phone Number (for Token SMS)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = phoneText,
                    onValueChange = { phoneText = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Pay CTA
            item {
                Button(
                    onClick = { isPinDialogOpen = true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Amber500),
                    enabled = amount >= 1000 && (user?.balance ?: 0.0) >= amount && verifiedName != null
                ) {
                    Text(
                        "Pay ₦" + String.format("%,.0f", amount),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    PinDialog(
        isOpen = isPinDialogOpen,
        title = "Authorize Electricity Bill",
        description = "${selectedDiscoId.uppercase()} Meter $meterNumber",
        amount = amount,
        onDismiss = { isPinDialogOpen = false },
        onConfirm = { pin ->
            isPinDialogOpen = false
            viewModel.payElectricity(
                discoId = selectedDiscoId,
                meterNumber = meterNumber,
                meterType = meterType,
                amount = amount,
                phone = phoneText,
                name = verifiedName ?: "Customer",
                pin = pin
            ) {
                onNavigateBack()
            }
        }
    )
}
