package ie.setu.ca1_mad2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.AndroidEntryPoint
import ie.setu.ca1_mad2.navigation.AppNavGraph
import ie.setu.ca1_mad2.navigation.AppRoutes
import ie.setu.ca1_mad2.ui.components.navigation.BottomNavigationBar
import ie.setu.ca1_mad2.ui.components.navigation.DrawerContent
import ie.setu.ca1_mad2.ui.components.navigation.TopAppBarWithDrawer
import ie.setu.ca1_mad2.ui.screens.LoginScreen
import ie.setu.ca1_mad2.ui.screens.SplashScreen
import ie.setu.ca1_mad2.ui.theme.CA1MAD2Theme
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var authViewModel: AuthViewModel

    // Create launcher for Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            authViewModel.handleGoogleSignInResult(task) { success, message ->
                if (!success) {
                    // Show error message
                    Toast.makeText(this, message ?: "Google Sign-In failed", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: ApiException) {
            // Handle error
            Toast.makeText(this, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CA1MAD2Theme {
                val gymViewModel: GymTrackerViewModel = hiltViewModel()
                authViewModel = hiltViewModel()
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val currentUser by authViewModel.currentUser.collectAsState()

                // Add state for showing splash screen
                var showSplash by remember { mutableStateOf(true) }

                // Show splash screen
                if (showSplash) {
                    SplashScreen(onSplashFinished = { showSplash = false })
                } else if (currentUser == null) {
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoginSuccess = {
                            navController.navigate(AppRoutes.HOME) {
                                popUpTo(AppRoutes.LOGIN) { inclusive = true }
                            }
                        },
                        onGoogleSignInClick = {
                            // Launch Google Sign-In
                            googleSignInLauncher.launch(authViewModel.getGoogleSignInIntent())
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