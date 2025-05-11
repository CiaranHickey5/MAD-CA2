package ie.setu.ca1_mad2.data.room

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import ie.setu.ca1_mad2.data.firestore.FirestoreRepository
import ie.setu.ca1_mad2.model.Exercise
import ie.setu.ca1_mad2.model.Workout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GymRepository @Inject constructor(
    private val exerciseDao: ExerciseDAO,
    private val workoutDao: WorkoutDAO,
    private val firestoreRepository: FirestoreRepository
) {
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "GymRepository"

    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: "guest"
    }

    // Exercise operations
    val exercises: Flow<List<Exercise>> = auth.currentUser?.uid?.let { userId ->
        exerciseDao.getAllExercisesForUser(userId).map { entities ->
            entities.map { it.toExercise() }
        }
    } ?: flowOf(emptyList())

    suspend fun getExerciseById(id: String): Flow<Exercise> =
        exerciseDao.getExerciseById(id).map { it.toExercise() }

    suspend fun insertExercise(exercise: Exercise) {
        val userId = getCurrentUserId()

        // Insert locally with user context
        exerciseDao.insertExercise(ExerciseEntity.fromExercise(exercise, userId))

        // Sync to Firestore if authenticated
        if (userId != "guest") {
            try {
                firestoreRepository.insertExercise(exercise)
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing exercise to Firestore: ${e.message}")
            }
        }
    }

    suspend fun updateExercise(exercise: Exercise) {
        val userId = getCurrentUserId()

        // Update locally
        exerciseDao.updateExercise(ExerciseEntity.fromExercise(exercise, userId))

        // Sync to Firestore if authenticated
        if (userId != "guest") {
            try {
                firestoreRepository.updateExercise(exercise)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating exercise in Firestore: ${e.message}")
            }
        }
    }

    suspend fun deleteExercise(exercise: Exercise) {
        val userId = getCurrentUserId()

        // Delete locally
        exerciseDao.deleteExercise(ExerciseEntity.fromExercise(exercise, userId))

        // Sync to Firestore if authenticated
        if (userId != "guest") {
            try {
                firestoreRepository.deleteExercise(exercise)
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting exercise from Firestore: ${e.message}")
            }
        }
    }

    // Workout operations
    val workouts: Flow<List<Workout>> = auth.currentUser?.uid?.let { userId ->
        workoutDao.getWorkoutsWithExercisesForUser(userId).map { workoutEntities ->
            workoutEntities.map { it.toWorkout() }
        }
    } ?: flowOf(emptyList())

    suspend fun getWorkoutById(id: String): Flow<Workout> =
        workoutDao.getWorkoutWithExercisesById(id).map { it.toWorkout() }

    suspend fun insertWorkout(workout: Workout) {
        val userId = getCurrentUserId()

        // Insert the workout locally with user context
        workoutDao.insertWorkout(WorkoutEntity.fromWorkout(workout, userId))

        // Insert all exercises if they don't exist
        workout.exercises.forEach { exercise ->
            insertExercise(exercise)

            // Create cross-reference
            workoutDao.insertWorkoutExerciseCrossRef(
                WorkoutExerciseCrossRef(
                    workoutId = workout.id,
                    exerciseId = exercise.id
                )
            )
        }

        // Sync to Firestore if authenticated
        if (userId != "guest") {
            try {
                firestoreRepository.insertWorkout(workout)
            } catch (e: Exception) {
                Log.e(TAG, "Error syncing workout to Firestore: ${e.message}")
            }
        }
    }

    suspend fun updateWorkout(workout: Workout) {
        val userId = getCurrentUserId()

        // Update workout details locally
        workoutDao.updateWorkout(WorkoutEntity.fromWorkout(workout, userId))

        // Sync to Firestore if authenticated
        if (userId != "guest") {
            try {
                firestoreRepository.updateWorkout(workout)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating workout in Firestore: ${e.message}")
            }
        }
    }

    suspend fun deleteWorkout(workout: Workout) {
        val userId = getCurrentUserId()

        // Delete the workout locally
        workoutDao.deleteWorkout(WorkoutEntity.fromWorkout(workout, userId))

        // Delete all cross references for the workout
        workoutDao.deleteAllExercisesFromWorkout(workout.id)

        // Sync to Firestore if authenticated
        if (userId != "guest") {
            try {
                firestoreRepository.deleteWorkout(workout)
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting workout from Firestore: ${e.message}")
            }
        }
    }

    // Add exercise to workout
    suspend fun addExerciseToWorkout(workoutId: String, exercise: Exercise) {
        val userId = getCurrentUserId()

        // Make sure exercise exists locally
        insertExercise(exercise)

        // Create cross-reference
        workoutDao.insertWorkoutExerciseCrossRef(
            WorkoutExerciseCrossRef(
                workoutId = workoutId,
                exerciseId = exercise.id
            )
        )

        // Sync to Firestore if authenticated
        if (userId != "guest") {
            try {
                firestoreRepository.addExerciseToWorkout(workoutId, exercise)
            } catch (e: Exception) {
                Log.e(TAG, "Error adding exercise to workout in Firestore: ${e.message}")
            }
        }
    }

    // Remove exercise from workout
    suspend fun removeExerciseFromWorkout(workoutId: String, exerciseId: String) {
        val userId = getCurrentUserId()

        // Remove locally
        workoutDao.deleteWorkoutExerciseCrossRef(workoutId, exerciseId)

        // Sync to Firestore if authenticated
        if (userId != "guest") {
            try {
                firestoreRepository.removeExerciseFromWorkout(workoutId, exerciseId)
            } catch (e: Exception) {
                Log.e(TAG, "Error removing exercise from workout in Firestore: ${e.message}")
            }
        }
    }

    // Sync data from Firestore to local database
    suspend fun syncDataFromFirestore() {
        val userId = auth.currentUser?.uid
        if (userId == null || userId == "guest") {
            Log.d(TAG, "No user logged in, skipping sync")
            return
        }

        try {
            Log.d(TAG, "Syncing data from Firestore for user: $userId")

            // Clear existing data for this user
            exerciseDao.deleteExercisesForUser(userId)
            workoutDao.deleteWorkoutsForUser(userId)
            workoutDao.deleteCrossRefsForUser(userId)

            // Fetch and insert exercises from Firestore
            firestoreRepository.getExercisesFlow().first().forEach { exercise ->
                exerciseDao.insertExercise(
                    ExerciseEntity.fromExercise(exercise, userId)
                )
            }

            // Fetch and insert workouts from Firestore
            firestoreRepository.getWorkoutsFlow().first().forEach { workout ->
                // Insert workout
                workoutDao.insertWorkout(
                    WorkoutEntity.fromWorkout(workout, userId)
                )

                // Insert exercises and cross-references
                workout.exercises.forEach { exercise ->
                    // Make sure exercise exists
                    exerciseDao.insertExercise(
                        ExerciseEntity.fromExercise(exercise, userId)
                    )

                    // Create cross-reference
                    workoutDao.insertWorkoutExerciseCrossRef(
                        WorkoutExerciseCrossRef(
                            workoutId = workout.id,
                            exerciseId = exercise.id
                        )
                    )
                }
            }

            Log.d(TAG, "Data sync completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing data from Firestore: ${e.message}", e)
            throw e
        }
    }

    // Clear local data for a specific user
    suspend fun clearUserData(userId: String) {
        Log.d(TAG, "Clearing local data for user: $userId")

        try {
            // Delete user-specific exercises
            exerciseDao.deleteExercisesForUser(userId)

            // Delete user-specific workouts
            workoutDao.deleteWorkoutsForUser(userId)

            // Delete user-specific cross-references
            workoutDao.deleteCrossRefsForUser(userId)

            Log.d(TAG, "User data cleared successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing user data: ${e.message}", e)
            throw e
        }
    }

    // Clear all local data (when logging out completely)
    suspend fun clearLocalData() {
        val currentUserId = getCurrentUserId()

        if (currentUserId != "guest") {
            clearUserData(currentUserId)
        } else {
            // Clear everything if guest
            Log.d(TAG, "Clearing all local data")

            try {
                exerciseDao.deleteAllExercises()
                workoutDao.deleteAllWorkouts()
                workoutDao.deleteAllCrossRefs()

                Log.d(TAG, "All local data cleared successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing all local data: ${e.message}", e)
                throw e
            }
        }
    }
}