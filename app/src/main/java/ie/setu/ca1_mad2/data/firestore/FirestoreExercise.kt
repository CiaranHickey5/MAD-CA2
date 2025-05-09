package ie.setu.ca1_mad2.data.firestore

import com.google.firebase.firestore.DocumentId
import ie.setu.ca1_mad2.model.Exercise

data class FirestoreExercise(
    @DocumentId val id: String = "",
    val name: String = "",
    val muscleGroup: String = ""
) {
    fun toExercise(): Exercise = Exercise(
        id = id,
        name = name,
        muscleGroup = muscleGroup
    )

    companion object {
        fun fromExercise(exercise: Exercise): FirestoreExercise = FirestoreExercise(
            id = exercise.id,
            name = exercise.name,
            muscleGroup = exercise.muscleGroup
        )
    }
}