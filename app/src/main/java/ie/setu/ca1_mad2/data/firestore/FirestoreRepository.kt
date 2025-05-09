package ie.setu.ca1_mad2.data.firestore

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import ie.setu.ca1_mad2.model.Exercise
import ie.setu.ca1_mad2.model.Workout
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreRepository @Inject constructor() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private fun getUserId(): String {
        return auth.currentUser?.uid ?: "guest"
    }

    private fun getExercisesCollection() =
        firestore.collection("users").document(getUserId()).collection("exercises")

    private fun getWorkoutsCollection() =
        firestore.collection("users").document(getUserId()).collection("workouts")

    // Exercise operations
    fun getExercisesFlow(): Flow<List<Exercise>> = callbackFlow {
        val listenerRegistration = getExercisesCollection().addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val exercises = snapshot.documents.mapNotNull { doc ->
                    doc.toObject<FirestoreExercise>()?.toExercise()
                }
                trySend(exercises)
            }
        }

        awaitClose { listenerRegistration.remove() }
    }

    suspend fun insertExercise(exercise: Exercise) {
        val firestoreExercise = FirestoreExercise.fromExercise(exercise)
        getExercisesCollection().document(exercise.id).set(firestoreExercise).await()
    }

    suspend fun updateExercise(exercise: Exercise) {
        val firestoreExercise = FirestoreExercise.fromExercise(exercise)
        getExercisesCollection().document(exercise.id).set(firestoreExercise).await()
    }

    suspend fun deleteExercise(exercise: Exercise) {
        getExercisesCollection().document(exercise.id).delete().await()
    }

    // Workout operations
    fun getWorkoutsFlow(): Flow<List<Workout>> = callbackFlow {
        val listenerRegistration = getWorkoutsCollection().addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val workoutDocs = snapshot.documents

                if (workoutDocs.isEmpty()) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                getExercisesCollection().get().addOnSuccessListener { exercisesSnapshot ->
                    val allExercises = exercisesSnapshot.documents.mapNotNull { doc ->
                        doc.toObject<FirestoreExercise>()?.toExercise()
                    }

                    val workouts = workoutDocs.mapNotNull { doc ->
                        val firestoreWorkout = doc.toObject<FirestoreWorkout>() ?: return@mapNotNull null

                        val workoutExercises = allExercises.filter { exercise ->
                            firestoreWorkout.exerciseIds.contains(exercise.id)
                        }

                        Workout(
                            id = firestoreWorkout.id,
                            name = firestoreWorkout.name,
                            description = firestoreWorkout.description,
                            exercises = workoutExercises.toMutableList()
                        )
                    }

                    trySend(workouts)
                }
            }
        }

        awaitClose { listenerRegistration.remove() }
    }

    suspend fun insertWorkout(workout: Workout) {
        workout.exercises.forEach { exercise ->
            insertExercise(exercise)
        }

        val firestoreWorkout = FirestoreWorkout.fromWorkout(workout)
        getWorkoutsCollection().document(workout.id).set(firestoreWorkout).await()
    }

    suspend fun updateWorkout(workout: Workout) {
        val firestoreWorkout = FirestoreWorkout.fromWorkout(workout)
        getWorkoutsCollection().document(workout.id).set(firestoreWorkout).await()
    }

    suspend fun deleteWorkout(workout: Workout) {
        getWorkoutsCollection().document(workout.id).delete().await()
    }

    suspend fun addExerciseToWorkout(workoutId: String, exercise: Exercise) {
        insertExercise(exercise)

        val workoutDoc = getWorkoutsCollection().document(workoutId).get().await()
        val workout = workoutDoc.toObject<FirestoreWorkout>()

        if (workout != null) {
            if (!workout.exerciseIds.contains(exercise.id)) {
                val updatedExerciseIds = workout.exerciseIds + exercise.id
                getWorkoutsCollection().document(workoutId)
                    .update("exerciseIds", updatedExerciseIds).await()
            }
        }
    }

    suspend fun removeExerciseFromWorkout(workoutId: String, exerciseId: String) {
        val workoutDoc = getWorkoutsCollection().document(workoutId).get().await()
        val workout = workoutDoc.toObject<FirestoreWorkout>()

        if (workout != null) {
            val updatedExerciseIds = workout.exerciseIds.filter { it != exerciseId }
            getWorkoutsCollection().document(workoutId)
                .update("exerciseIds", updatedExerciseIds).await()
        }
    }
}