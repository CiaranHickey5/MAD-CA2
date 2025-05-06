package ie.setu.ca1_mad2.data.room

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "workout_exercise_crossref",
    primaryKeys = ["workoutId", "exerciseId"],
    indices = [
        Index(value = ["workoutId"]),
        Index(value = ["exerciseId"])
    ]
)
data class WorkoutExerciseCrossRef(
    val workoutId: Int,
    val exerciseId: Int
)