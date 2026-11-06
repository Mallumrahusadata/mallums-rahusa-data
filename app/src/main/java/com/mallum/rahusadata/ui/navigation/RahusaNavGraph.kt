package com.mallum.rahusadata.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.mallum.rahusadata.ui.components.ReceiptSheet
import com.mallum.rahusadata.ui.screens.*
import com.mallum.rahusadata.ui.theme.*
import com.mallum.rahusadata.ui.viewmodel.RahusaViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    object Auth : Screen("auth", "Sign In")
    object Dashboard : Screen("dashboard", "Home", Icons.Default.Home)
    object BuyData : Screen("buy_data", "Data", Icons.Default.Wifi)
    object BuyAirtime : Screen("buy_airtime", "Airtime", Icons.Default.Phone)
    object Electricity : Screen("electricity", "Electricity", Icons.Default.Bolt)
    object Cable : Screen("cable", "Cable TV", Icons.Default.Tv)
    object FundWallet : Screen("fund_wallet", "Fund", Icons.Default.AccountBalanceWallet)
    object Transactions : Screen("transactions", "History", Icons.Default.ReceiptLong)
    object Admin : Screen("admin", "Admin", Icons.Default.AdminPanelSettings)
}

@Composable
fun RahusaNavGraph(
    viewModel: RahusaViewModel
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        Screen.Dashboard,
        Screen.BuyData,
        Screen.FundWallet,
        Screen.Transactions
    )

    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Transactions.route,
        Screen.BuyData.route,
        Screen.FundWallet.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar && uiState.user != null) {
                NavigationBar(
                    containerColor = White,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                            label = { Text(screen.title, fontSize = 10.sp) },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BluePrimary,
                                selectedTextColor = BluePrimary,
                                indicatorColor = BlueLight,
                                unselectedIconColor = Slate500,
                                unselectedTextColor = Slate500
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (uiState.user != null) Screen.Dashboard.route else Screen.Auth.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Auth.route) {
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToData = { navController.navigate(Screen.BuyData.route) },
                    onNavigateToAirtime = { navController.navigate(Screen.BuyAirtime.route) },
                    onNavigateToElectricity = { navController.navigate(Screen.Electricity.route) },
                    onNavigateToCable = { navController.navigate(Screen.Cable.route) },
                    onNavigateToFund = { navController.navigate(Screen.FundWallet.route) },
                    onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) },
                    onNavigateToAdmin = { navController.navigate(Screen.Admin.route) }
                )
            }

            composable(Screen.BuyData.route) {
                BuyDataScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.BuyAirtime.route) {
                BuyAirtimeScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Electricity.route) {
                ElectricityScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Cable.route) {
                CableScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.FundWallet.route) {
                FundWalletScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Transactions.route) {
                TransactionsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Admin.route) {
                AdminScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        // Global Transaction Receipt Modal Sheet
        ReceiptSheet(
            transaction = uiState.activeReceipt,
            onDismiss = { viewModel.dismissReceipt() }
        )
    }
}
