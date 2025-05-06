package ie.setu.ca1_mad2.ui.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ie.setu.ca1_mad2.navigation.AppRoutes

data class NavBarItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

// Function that returns main nav items
fun getMainNavItems() = listOf(
    NavBarItem(AppRoutes.HOME, "Home", Icons.Default.Home),
    NavBarItem(AppRoutes.ADD_WORKOUT, "Add Workout", Icons.Default.Add),
    NavBarItem(AppRoutes.LIST_WORKOUTS, "Workouts", Icons.AutoMirrored.Filled.List)
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        getMainNavItems().forEach { item ->
            NavigationBarItem(
                selected = (currentRoute == item.route),
                onClick = {
                    // Pop up to avoid stacking multiple copies
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                label = { Text(item.label) },
                icon = { Icon(item.icon, contentDescription = item.label) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarComponent(title: String = "Gym Tracker") {
    CenterAlignedTopAppBar(
        title = { Text(title) }
    )
}

@Composable
fun AppNavigation(navController: NavController) {
    BottomNavigationBar(navController = navController)
}