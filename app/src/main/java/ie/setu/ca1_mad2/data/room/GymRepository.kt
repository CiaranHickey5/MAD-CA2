package ie.setu.ca1_mad2.data.room

import ie.setu.ca1_mad2.model.Exercise
import ie.setu.ca1_mad2.model.Workout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GymRepository @Inject constructor(
    private val exerciseDao: ExerciseDAO,
    private val workoutDao: WorkoutDAO
) {
    // Exercise operations
    val exercises: Flow<List<Exercise>> = exerciseDao.getAllExercises()
        .map { entities -> entities.map { it.toExercise() } }

    suspend fun getExerciseById(id: String): Flow<Exercise> =
        exerciseDao.getExerciseById(id).map { it.toExercise() }

    suspend fun insertExercise(exercise: Exercise) {
        exerciseDao.insertExercise(ExerciseEntity.fromExercise(exercise))
    }

    suspend fun updateExercise(exercise: Exercise) {
        exerciseDao.updateExercise(ExerciseEntity.fromExercise(exercise))
    }

    suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.deleteExercise(ExerciseEntity.fromExercise(exercise))
    }

    // Workout operations
    val workouts: Flow<List<Workout>> = workoutDao.getWorkoutsWithExercises()
        .map { workoutsWithExercises -> workoutsWithExercises.map { it.toWorkout() } }

    suspend fun getWorkoutById(id: String): Flow<Workout> =
        workoutDao.getWorkoutWithExercisesById(id).map { it.toWorkout() }

    suspend fun insertWorkout(workout: Workout) {
        // Insert the workout
        workoutDao.insertWorkout(WorkoutEntity.fromWorkout(workout))

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
    }

    suspend fun updateWorkout(workout: Workout) {
        // Update workout details
        workoutDao.updateWorkout(WorkoutEntity.fromWorkout(workout))
    }

    suspend fun deleteWorkout(workout: Workout) {
        // Delete the workout
        workoutDao.deleteWorkout(WorkoutEntity.fromWorkout(workout))

        // Delete all cross references for the workout
        workoutDao.deleteAllExercisesFromWorkout(workout.id)
    }

    // Add exercise to workout
    suspend fun addExerciseToWorkout(workoutId: String, exercise: Exercise) {
        // Make sure exercise exists
        insertExercise(exercise)

        // Create cross-reference
        workoutDao.insertWorkoutExerciseCrossRef(
            WorkoutExerciseCrossRef(
                workoutId = workoutId,
                exerciseId = exercise.id
            )
        )
    }

    // Remove exercise from workout
    suspend fun removeExerciseFromWorkout(workoutId: String, exerciseId: String) {
        workoutDao.deleteWorkoutExerciseCrossRef(workoutId, exerciseId)
    }
}