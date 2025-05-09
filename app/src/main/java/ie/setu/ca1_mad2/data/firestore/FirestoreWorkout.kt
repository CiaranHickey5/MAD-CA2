package ie.setu.ca1_mad2.data.firestore

import com.google.firebase.firestore.DocumentId
import ie.setu.ca1_mad2.model.Workout

data class FirestoreWorkout(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val exerciseIds: List<String> = emptyList()
) {
    companion object {
        fun fromWorkout(workout: Workout): FirestoreWorkout = FirestoreWorkout(
            id = workout.id,
            name = workout.name,
            description = workout.description,
            exerciseIds = workout.exercises.map { it.id }
        )
    }
}