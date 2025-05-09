package ie.setu.ca1_mad2.ui.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ie.setu.ca1_mad2.navigation.AppRoutes
import androidx.compose.material.icons.filled.Settings

data class DrawerItem(
    val icon: ImageVector,
    val label: String,
    val route: String,
    val contentDescription: String
)

@Composable
fun DrawerContent(
    navController: NavController,
    onDestinationClicked: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val drawerItems = listOf(
        DrawerItem(
            icon = Icons.Default.Home,
            label = "Home",
            route = AppRoutes.HOME,
            contentDescription = "Navigate to Home"
        ),
        DrawerItem(
            icon = Icons.Default.Add,
            label = "Add Workout",
            route = AppRoutes.ADD_WORKOUT,
            contentDescription = "Navigate to Add Workout"
        ),
        DrawerItem(
            icon = Icons.Default.FitnessCenter,
            label = "Workouts",
            route = AppRoutes.LIST_WORKOUTS,
            contentDescription = "Navigate to Workouts List"
        ),
        DrawerItem(
            icon = Icons.Default.Add,
            label = "Add Exercise",
            route = AppRoutes.ADD_EXERCISE,
            contentDescription = "Navigate to Add Exercise"
        ),
        DrawerItem(
            icon = Icons.Default.FilterList,
            label = "Exercises",
            route = AppRoutes.LIST_EXERCISE,
            contentDescription = "Navigate to Exercise List"
        ),
        DrawerItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            route = AppRoutes.SETTINGS,
            contentDescription = "Navigate to Settings"
        )
    )

    Surface(
        modifier = Modifier.width(300.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            DrawerHeader()

            Spacer(modifier = Modifier.height(24.dp))

            // Menu Items
            drawerItems.forEach { item ->
                DrawerMenuItem(
                    icon = item.icon,
                    label = item.label,
                    onClick = { onDestinationClicked(item.route) },
                    contentDescription = item.contentDescription
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Sign Out button
            DrawerMenuItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                label = "Sign Out",
                onClick = onSignOut,
                contentDescription = "Sign out of application"
            )
        }
    }
}

@Composable
fun DrawerHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile picture
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = "Gym Logo",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App name
        Text(
            text = "Gym Tracker",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Slogan
        Text(
            text = "Track your fitness journey",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    contentDescription: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(24.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithDrawer(onMenuClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("Gym Tracker") },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Navigation Drawer"
                )
            }
        }
    )
}