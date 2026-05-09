package com.example.gokula.health.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*

import com.example.gokula.health.ui.screens.*

private data class Tab(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val tabs = listOf(
    Tab("cattle", "Cattle", Icons.Filled.Pets),
    Tab("milk", "Milk", Icons.Filled.WaterDrop),
    Tab("vacc", "Vaccine", Icons.Filled.Vaccines),
    Tab("heat", "Heat", Icons.Filled.Favorite),
    Tab("yield", "Yield", Icons.Filled.ShowChart),
)

@Composable
fun GokulaNavHost() {

    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp,
                containerColor = Color(0xFFE8F5E9) // light green background
            ) {

                tabs.forEach { tab ->

                    val selected =
                        current?.hierarchy?.any { it.route == tab.route } == true

                    NavigationBarItem(
                        selected = selected,

                        onClick = {
                            nav.navigate(tab.route) {
                                popUpTo(nav.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },

                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label
                            )
                        },

                        label = {
                            Text(tab.label)
                        },

                        alwaysShowLabel = true,

                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            indicatorColor = Color(0xFF4CAF50),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = nav,
            startDestination = "cattle",
            modifier = Modifier.padding(padding)
        ) {
            composable("cattle") { CattleScreen() }
            composable("milk") { MilkScreen() }
            composable("vacc") { VaccinationScreen() }
            composable("heat") { HeatScreen() }
            composable("yield") { YieldScreen() }
        }
    }
}