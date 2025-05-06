package ie.setu.ca1_mad2.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import ie.setu.ca1_mad2.model.Workout

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String
) {
    companion object {
        fun fromWorkout(workout: Workout): WorkoutEntity = WorkoutEntity(
            id = workout.id,
            name = workout.name,
            description = workout.description
        )
    }
}