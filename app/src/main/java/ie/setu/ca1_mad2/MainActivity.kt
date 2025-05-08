package ie.setu.ca1_mad2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ie.setu.ca1_mad2.navigation.AppNavGraph
import ie.setu.ca1_mad2.navigation.AppRoutes
import ie.setu.ca1_mad2.ui.components.navigation.BottomNavigationBar
import ie.setu.ca1_mad2.ui.components.navigation.DrawerContent
import ie.setu.ca1_mad2.ui.components.navigation.TopAppBarWithDrawer
import ie.setu.ca1_mad2.ui.screens.LoginScreen
import ie.setu.ca1_mad2.ui.theme.CA1MAD2Theme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CA1MAD2Theme {
                val gymViewModel: GymTrackerViewModel = hiltViewModel()
                val authViewModel: AuthViewModel = hiltViewModel()
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val currentUser by authViewModel.currentUser.collectAsState()

                if (currentUser == null) {
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoginSuccess = {
                            navController.navigate(AppRoutes.HOME) {
                                popUpTo(AppRoutes.LOGIN) { inclusive = true }
                            }
                        }
                    )
                } else {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            DrawerContent(
                                navController = navController,
                                onDestinationClicked = { route ->
                                    scope.launch {
                                        drawerState.close()
                                    }
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                },
                                onSignOut = {
                                    authViewModel.signOut()
                                }
                            )
                        }
                    ) {
                        Scaffold(
                            topBar = {
                                TopAppBarWithDrawer(
                                    onMenuClick = {
                                        scope.launch {
                                            drawerState.open()
                                        }
                                    }
                                )
                            },
                            bottomBar = {
                                BottomNavigationBar(navController)
                            }
                        ) { paddingValues ->
                            AppNavGraph(
                                navController = navController,
                                viewModel = gymViewModel,
                                innerPadding = paddingValues
                            )
                        }
                    }
                }
            }
        }
    }
}