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
        return Workout(
            id = workout.id,
            name = workout.name,
            description = workout.description,
            exercises = exercises.map { it.toExercise() }.toMutableList()
        )
    }
}