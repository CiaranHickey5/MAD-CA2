package ie.setu.ca1_mad2.data.room

import androidx.room.Entity

@Entity(
    tableName = "workout_exercise_crossref",
    primaryKeys = ["workoutId", "exerciseId"]
)
data class WorkoutExerciseCrossRef(
    val workoutId: String,
    val exerciseId: String
)