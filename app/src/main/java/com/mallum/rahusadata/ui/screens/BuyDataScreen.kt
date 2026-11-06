package com.mallum.rahusadata.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.mallum.rahusadata.data.model.DataPlan
import com.mallum.rahusadata.ui.components.PinDialog
import com.mallum.rahusadata.ui.theme.*
import com.mallum.rahusadata.ui.viewmodel.RahusaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyDataScreen(
    viewModel: RahusaViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    var selectedNetwork by remember { mutableStateOf("MTN") }
    var selectedPlan by remember { mutableStateOf<DataPlan?>(null) }
    var phoneNumber by remember { mutableStateOf(user?.phoneNumber ?: "") }
    var selectedType by remember { mutableStateOf("ALL") }

    var isPinDialogOpen by remember { mutableStateOf(false) }

    val networks = listOf("MTN", "Airtel", "Glo", "9mobile")

    LaunchedEffect(selectedNetwork) {
        viewModel.loadDataPlans(selectedNetwork)
    }

    fun detectNetwork(phone: String): String? {
        val clean = phone.filter { it.isDigit() }
        if (clean.length < 4) return null
        val prefix = clean.take(4)
        val mtn = listOf("0803", "0806", "0703", "0706", "0813", "0816", "0810", "0814", "0903", "0906", "0913", "0916")
        val airtel = listOf("0802", "0808", "0708", "0812", "0701", "0902", "0901", "0904", "0907", "0912")
        val glo = listOf("0805", "0807", "0705", "0815", "0811", "0905", "0915")
        val etisalat = listOf("0809", "0817", "0818", "0909", "0908")
        return when {
            mtn.contains(prefix) -> "MTN"
            airtel.contains(prefix) -> "Airtel"
            glo.contains(prefix) -> "Glo"
            etisalat.contains(prefix) -> "9mobile"
            else -> null
        }
    }

    val plansForNetwork = uiState.dataPlans.filter { it.network.equals(selectedNetwork, ignoreCase = true) }
    val planTypes = listOf("ALL") + plansForNetwork.map { it.type }.distinct()
    val filteredPlans = if (selectedType == "ALL") plansForNetwork else plansForNetwork.filter { it.type == selectedType }

    LaunchedEffect(filteredPlans) {
        if (selectedPlan == null || !filteredPlans.contains(selectedPlan)) {
            selectedPlan = filteredPlans.firstOrNull()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buy Mobile Data", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
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
            // Network Selector Tabs
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
                                .border(
                                    1.dp,
                                    if (isSelected) Color.Transparent else Slate200,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedNetwork = net
                                    selectedType = "ALL"
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = net,
                                color = if (isSelected) White else Slate800,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Plan Type Chips
            if (planTypes.size > 1) {
                item {
                    Text("Plan Type", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(planTypes) { type ->
                            val isSelected = selectedType == type
                            Surface(
                                color = if (isSelected) BluePrimary else White,
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) BluePrimary else Slate200),
                                modifier = Modifier.clickable { selectedType = type }
                            ) {
                                Text(
                                    text = if (type == "ALL") "All Types" else type,
                                    color = if (isSelected) White else Slate700,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Phone Number Input
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Phone Number", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                    user?.phoneNumber?.let { myPhone ->
                        Text(
                            "Use My Number",
                            fontSize = 11.sp,
                            color = BluePrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                phoneNumber = myPhone
                                detectNetwork(myPhone)?.let { selectedNetwork = it }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        detectNetwork(it)?.let { net ->
                            selectedNetwork = net
                        }
                    },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Slate500) },
                    placeholder = { Text("08012345678") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Bundle Selector Cards
            item {
                Text("Select Data Bundle", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate700)
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    filteredPlans.forEach { plan ->
                        val isSelected = selectedPlan?.id == plan.id
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) BlueLight else White),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (isSelected) BluePrimary else Slate200
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPlan = plan }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(plan.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Slate900)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Slate100,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(plan.validity, fontSize = 9.sp, color = Slate700, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                    Text(plan.type, fontSize = 11.sp, color = Slate500)
                                }

                                Text(
                                    text = "₦" + String.format("%,.0f", plan.price),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 16.sp,
                                    color = BluePrimary
                                )
                            }
                        }
                    }
                }
            }

            // Summary & Submit CTA
            item {
                selectedPlan?.let { plan ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Order Summary", fontSize = 12.sp, color = Slate500)
                                Text("${plan.network} ${plan.size}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate900)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Payable Amount", fontSize = 12.sp, color = Slate500)
                                Text("₦${plan.price}", fontSize = 14.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.ExtraBold, color = BluePrimary)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Your Wallet Balance", fontSize = 11.sp, color = Slate500)
                                Text(
                                    "₦" + String.format("%,.2f", user?.balance ?: 0.0),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if ((user?.balance ?: 0.0) >= plan.price) GreenPrimary else Red600
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { isPinDialogOpen = true },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        enabled = (user?.balance ?: 0.0) >= plan.price && phoneNumber.length >= 10
                    ) {
                        Text("Continue to Authorize", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }

    PinDialog(
        isOpen = isPinDialogOpen,
        title = "Authorize Data Subscription",
        description = "${selectedPlan?.network} ${selectedPlan?.name} to $phoneNumber",
        amount = selectedPlan?.price ?: 0.0,
        onDismiss = { isPinDialogOpen = false },
        onConfirm = { pin ->
            isPinDialogOpen = false
            selectedPlan?.let { plan ->
                viewModel.buyData(plan.id, phoneNumber, pin) {
                    onNavigateBack()
                }
            }
        }
    )
}
