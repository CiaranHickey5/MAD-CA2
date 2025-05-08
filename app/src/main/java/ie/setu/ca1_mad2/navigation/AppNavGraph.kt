package ie.setu.ca1_mad2.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ie.setu.ca1_mad2.GymTrackerViewModel
import ie.setu.ca1_mad2.ui.screens.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: GymTrackerViewModel,
    innerPadding: PaddingValues
) {
    // Collect workouts as state
    val workouts by viewModel.workouts.collectAsState()
    val exercises by viewModel.exercises.collectAsState()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.HOME,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(AppRoutes.HOME) {
            ListWorkoutsScreen(
                workouts = workouts,
                viewModel = viewModel,
                onWorkoutSelected = { workout ->
                    navController.navigate(AppRoutes.WORKOUT_DETAIL + "/${workout.id}")
                }
            )
        }

        composable(AppRoutes.ADD_WORKOUT) {
            AddWorkoutScreen(viewModel = viewModel)
        }

        composable(AppRoutes.LIST_WORKOUTS) {
            ListWorkoutsScreen(
                workouts = workouts,
                viewModel = viewModel,
                onWorkoutSelected = { workout ->
                    navController.navigate(AppRoutes.WORKOUT_DETAIL + "/${workout.id}")
                }
            )
        }

        composable(
            route = AppRoutes.WORKOUT_DETAIL + "/{workoutId}",
            arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: return@composable
            val workoutIdInt = workoutId.toIntOrNull() ?: return@composable
            val workout = workouts.find { it.id == workoutIdInt } ?: return@composable

            WorkoutDetailScreen(
                viewModel = viewModel,
                workout = workout
            )
        }

        composable(AppRoutes.ADD_EXERCISE) {
            AddExerciseScreen(viewModel = viewModel)
        }

        composable(AppRoutes.LIST_EXERCISE) {
            ListExerciseScreen(exercises = exercises)
        }
    }
}