package ie.setu.ca1_mad2.data.room

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import ie.setu.ca1_mad2.model.Workout

data class WorkoutWithExercises(
    @Embedded val workout: WorkoutEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            WorkoutExerciseCrossRef::class,
            parentColumn = "workoutId",
            entityColumn = "exerciseId"
        )
    )
    val exercises: List<ExerciseEntity>
) {
    fun toWorkout(): Workout {
        // Filter exercises to only include those belonging to the same user as the workout
        val userExercises = exercises.filter { it.userId == workout.userId }

        return Workout(
            id = workout.id,
            name = workout.name,
            description = workout.description,
            exercises = userExercises.map { it.toExercise() }.toMutableList()
        )
    }
}

// Helper function to create WorkoutWithExercises for a specific user
fun WorkoutWithExercises.forUser(userId: String): WorkoutWithExercises {
    return this.copy(
        exercises = this.exercises.filter { it.userId == userId }
    )
}