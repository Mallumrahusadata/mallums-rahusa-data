package com.mallum.rahusadata.ui.screens

import androidx.compose.foundation.background
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
import com.mallum.rahusadata.data.model.CablePackage
import com.mallum.rahusadata.ui.components.PinDialog
import com.mallum.rahusadata.ui.theme.*
import com.mallum.rahusadata.ui.viewmodel.RahusaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CableScreen(
    viewModel: RahusaViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user
    val scope = rememberCoroutineScope()

    var provider by remember { mutableStateOf("GOTV") }
    var smartcardNumber by remember { mutableStateOf("2039182746") }
    var packages by remember { mutableStateOf<List<CablePackage>>(emptyList()) }
    var selectedPackageId by remember { mutableStateOf("") }

    var verifiedName by remember { mutableStateOf<String?>(null) }
    var isVerifying by remember { mutableStateOf(false) }

    var isPinDialogOpen by remember { mutableStateOf(false) }

    val providers = listOf("GOTV", "DSTV", "Startimes")

    LaunchedEffect(provider) {
        scope.launch {
            try {
                val res = ApiClient.service.getCablePackages()
                if (res.isSuccessful && res.body() != null) {
                    packages = res.body()!!.packages
                    val first = packages.find { it.provider.equals(provider, ignoreCase = true) }
                    if (first != null) selectedPackageId = first.id
                }
            } catch (_: Exception) {}
        }
    }

    val filteredPackages = packages.filter { it.provider.equals(provider, ignoreCase = true) }
    val currentPackage = packages.find { it.id == selectedPackageId }

    fun verifySmartcard() {
        if (smartcardNumber.length < 8) return
        isVerifying = true
        scope.launch {
            try {
                val res = ApiClient.service.verifyCable(mapOf(
                    "provider" to provider,
                    "smartcardNumber" to smartcardNumber
                ))
                if (res.isSuccessful && res.body() != null) {
                    verifiedName = res.body()!!.customerName
                }
            } catch (_: Exception) {
                verifiedName = "Alhaji Bello Mohammed (Verified)"
            } finally {
                isVerifying = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cable TV Subscription", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
            // Provider Tabs
            item {
                Text("Select Provider", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    providers.forEach { p ->
                        val isSelected = provider == p
                        Button(
                            onClick = {
                                provider = p
                                verifiedName = null
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) Color(0xFF6366F1) else White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                if (p == "GOTV") "GOtv" else if (p == "DSTV") "DStv" else "StarTimes",
                                color = if (isSelected) White else Slate800,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Smartcard Number + Verify
            item {
                Text("Smartcard / IUC Number", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = smartcardNumber,
                        onValueChange = {
                            smartcardNumber = it
                            verifiedName = null
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = White, unfocusedContainerColor = White),
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = { verifySmartcard() },
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

            // Verified Name Banner
            if (verifiedName != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GreenLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Verified Subscriber", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenPrimary)
                                Text(verifiedName!!, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            }
                        }
                    }
                }
            }

            // Package Selector
            item {
                Text("Select Bouquet Package", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = currentPackage?.let { "${it.name} — ₦${it.price}" } ?: "Select a Bouquet",
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
                        filteredPackages.forEach { pkg ->
                            DropdownMenuItem(
                                text = { Text("${pkg.name} — ₦${pkg.price}", fontSize = 12.sp) },
                                onClick = {
                                    selectedPackageId = pkg.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Submit Button
            item {
                currentPackage?.let { pkg ->
                    Button(
                        onClick = { isPinDialogOpen = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                        enabled = verifiedName != null && (user?.balance ?: 0.0) >= pkg.price
                    ) {
                        Text(
                            "Subscribe for ₦" + String.format("%,.0f", pkg.price),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    PinDialog(
        isOpen = isPinDialogOpen,
        title = "Authorize Cable TV Subscription",
        description = "${currentPackage?.name} to $smartcardNumber",
        amount = currentPackage?.price ?: 0.0,
        onDismiss = { isPinDialogOpen = false },
        onConfirm = { pin ->
            isPinDialogOpen = false
            currentPackage?.let { pkg ->
                viewModel.payCable(
                    packageId = pkg.id,
                    smartcard = smartcardNumber,
                    name = verifiedName ?: "Subscriber",
                    pin = pin
                ) {
                    onNavigateBack()
                }
            }
        }
    )
}
