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
        val userId = auth.currentUser?.uid ?: "guest"
        Log.d(TAG, "Getting user ID: $userId")
        return userId
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
            Log.d(TAG, "Starting upload for workout: $workoutId")

            // Generate a unique file name
            val fileName = "workout_image_${UUID.randomUUID()}.jpg"
            val imageRef = getStorageRef().child(fileName)

            Log.d(TAG, "Uploading to path: ${imageRef.path}")

            // Upload the file to Firebase Storage
            val uploadTask = imageRef.putFile(imageUri).await()
            Log.d(TAG, "Upload task completed")

            // Get the download URL
            val downloadUrl = imageRef.downloadUrl.await().toString()
            Log.d(TAG, "Download URL obtained: $downloadUrl")

            // Create a WorkoutImage object
            val workoutImage = WorkoutImage(
                id = UUID.randomUUID().toString(),
                workoutId = workoutId,
                downloadUrl = downloadUrl,
                fileName = fileName
            )

            Log.d(TAG, "Saving metadata to Firestore: $workoutImage")

            // Save metadata to Firestore
            getImagesCollection().document(workoutImage.id).set(workoutImage).await()

            Log.d(TAG, "Successfully uploaded image: ${workoutImage.id}")
            return workoutImage

        } catch (e: Exception) {
            Log.e(TAG, "Error uploading image for workout $workoutId: ${e.message}", e)
            Log.e(TAG, "Stack trace: ${e.stackTrace.joinToString("\n")}")
            throw e
        }
    }

    // Get all images for a specific workout
    fun getWorkoutImagesFlow(workoutId: String): Flow<List<WorkoutImage>> = callbackFlow {
        Log.d(TAG, "Creating image flow for workout: $workoutId")

        val listenerRegistration = getImagesCollection()
            .whereEqualTo("workoutId", workoutId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening for images: ${error.message}", error)
                    close(error)
                    return@addSnapshotListener
                }

                Log.d(TAG, "Received snapshot for workout $workoutId")

                if (snapshot != null) {
                    try {
                        val images = snapshot.documents.mapNotNull { doc ->
                            try {
                                Log.d(TAG, "Processing document: ${doc.id}")
                                val image = doc.toObject(WorkoutImage::class.java)
                                if (image != null) {
                                    Log.d(TAG, "Successfully converted document to WorkoutImage: ${image.id}")
                                    image
                                } else {
                                    Log.w(TAG, "Document ${doc.id} could not be converted to WorkoutImage")
                                    null
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error converting document ${doc.id}: ${e.message}", e)
                                null
                            }
                        }
                        Log.d(TAG, "Found ${images.size} images for workout $workoutId")
                        trySend(images)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing snapshot: ${e.message}", e)
                        close(e)
                    }
                } else {
                    Log.d(TAG, "Snapshot is null for workout $workoutId")
                    trySend(emptyList())
                }
            }

        awaitClose {
            Log.d(TAG, "Closing image flow listener for workout: $workoutId")
            listenerRegistration.remove()
        }
    }

    // Delete an image
    suspend fun deleteWorkoutImage(workoutImage: WorkoutImage) {
        try {
            Log.d(TAG, "Deleting image: ${workoutImage.id}")

            // Delete from Firestore
            getImagesCollection().document(workoutImage.id).delete().await()
            Log.d(TAG, "Deleted from Firestore: ${workoutImage.id}")

            // Delete from Storage
            getStorageRef().child(workoutImage.fileName).delete().await()
            Log.d(TAG, "Deleted from Storage: ${workoutImage.fileName}")

            Log.d(TAG, "Successfully deleted image: ${workoutImage.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting image ${workoutImage.id}: ${e.message}", e)
            throw e
        }
    }

    // Delete all images for a workout
    suspend fun deleteAllWorkoutImages(workoutId: String) {
        try {
            Log.d(TAG, "Deleting all images for workout: $workoutId")

            // Get images
            val images = getImagesCollection()
                .whereEqualTo("workoutId", workoutId)
                .get()
                .await()
                .toObjects(WorkoutImage::class.java)

            Log.d(TAG, "Found ${images.size} images to delete for workout $workoutId")

            // Delete images
            images.forEach { workoutImage ->
                Log.d(TAG, "Deleting image ${workoutImage.id} for workout $workoutId")
                deleteWorkoutImage(workoutImage)
            }

            Log.d(TAG, "Successfully deleted all images for workout: $workoutId")
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting workout images for $workoutId: ${e.message}", e)
            throw e
        }
    }
}