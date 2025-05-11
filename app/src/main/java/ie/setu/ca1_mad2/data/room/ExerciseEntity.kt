package ie.setu.ca1_mad2.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import ie.setu.ca1_mad2.model.Exercise

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val muscleGroup: String,
    val userId: String
) {
    fun toExercise(): Exercise = Exercise(
        id = id,
        name = name,
        muscleGroup = muscleGroup
    )

    companion object {
        fun fromExercise(exercise: Exercise, userId: String? = null): ExerciseEntity = ExerciseEntity(
            id = exercise.id,
            name = exercise.name,
            muscleGroup = exercise.muscleGroup,
            userId = userId ?: "guest"
        )
    }
}



