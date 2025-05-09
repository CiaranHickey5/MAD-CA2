package ie.setu.ca1_mad2.data.firestore

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import ie.setu.ca1_mad2.model.WorkoutImage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepository @Inject constructor() {
    private val TAG = "StorageRepository"
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Get the current user ID or return guest if not logged in
    private fun getUserId(): String {
        return auth.currentUser?.uid ?: "guest"
    }

    // Reference to the user's images collection in Firestore
    private fun getImagesCollection() =
        firestore.collection("users").document(getUserId()).collection("images")

    // Reference to the user's storage bucket
    private fun getStorageRef() =
        storage.reference.child("users/${getUserId()}/images")

    // Upload an image to Firebase Storage and save its metadata in Firestore
    suspend fun uploadWorkoutImage(imageUri: Uri, workoutId: String): WorkoutImage {
        try {
            // Generate a unique file name
            val fileName = "workout_image_${UUID.randomUUID()}.jpg"
            val imageRef = getStorageRef().child(fileName)

            // Upload the file to Firebase Storage
            val uploadTask = imageRef.putFile(imageUri).await()

            // Get the download URL
            val downloadUrl = imageRef.downloadUrl.await().toString()

            // Create a WorkoutImage object
            val workoutImage = WorkoutImage(
                id = UUID.randomUUID().toString(),
                workoutId = workoutId,
                downloadUrl = downloadUrl,
                fileName = fileName
            )

            // Save metadata to Firestore
            getImagesCollection().document(workoutImage.id).set(workoutImage).await()

            Log.d(TAG, "Successfully uploaded image: ${workoutImage.id}")
            return workoutImage

        } catch (e: Exception) {
            Log.e(TAG, "Error uploading image: ${e.message}", e)
            throw e
        }
    }

    // Get all images for a specific workout
    fun getWorkoutImagesFlow(workoutId: String): Flow<List<WorkoutImage>> = callbackFlow {
        val listenerRegistration = getImagesCollection()
            .whereEqualTo("workoutId", workoutId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val images = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(WorkoutImage::class.java)
                    }
                    trySend(images)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    // Delete an image
    suspend fun deleteWorkoutImage(workoutImage: WorkoutImage) {
        try {
            // Delete from Firestore
            getImagesCollection().document(workoutImage.id).delete().await()

            // Delete from Storage
            getStorageRef().child(workoutImage.fileName).delete().await()

            Log.d(TAG, "Successfully deleted image: ${workoutImage.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting image: ${e.message}", e)
            throw e
        }
    }

    // Delete all images for a workout
    suspend fun deleteAllWorkoutImages(workoutId: String) {
        try {
            // Get images
            val images = getImagesCollection()
                .whereEqualTo("workoutId", workoutId)
                .get()
                .await()
                .toObjects(WorkoutImage::class.java)

            // Delete images
            images.forEach { workoutImage ->
                deleteWorkoutImage(workoutImage)
            }

            Log.d(TAG, "Successfully deleted all images for workout: $workoutId")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting workout images: ${e.message}", e)
            throw e
        }
    }
}