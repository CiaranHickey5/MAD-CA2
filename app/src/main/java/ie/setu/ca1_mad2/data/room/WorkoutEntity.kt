package ie.setu.ca1_mad2.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import ie.setu.ca1_mad2.model.Workout

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val userId: String
) {
    companion object {
        fun fromWorkout(workout: Workout, userId: String? = null): WorkoutEntity = WorkoutEntity(
            id = workout.id,
            name = workout.name,
            description = workout.description,
            userId = userId ?: "guest"
        )
    }
}