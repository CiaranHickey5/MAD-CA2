package ie.setu.ca1_mad2

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import ie.setu.ca1_mad2.data.firestore.StorageRepository
import ie.setu.ca1_mad2.data.room.GymRepository
import ie.setu.ca1_mad2.model.Exercise
import ie.setu.ca1_mad2.model.Workout
import ie.setu.ca1_mad2.model.WorkoutImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class GymTrackerViewModel @Inject constructor(
    private val repository: GymRepository,
    private val storageRepository: StorageRepository
) : ViewModel() {
    private val TAG = "GymTrackerViewModel"
    private val auth = FirebaseAuth.getInstance()

    // Exercise state - Now always user-specific through repository
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()

    // Workout state - Now always user-specific through repository
    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts.asStateFlow()

    // Muscle group filter state
    private val _filterMuscleGroups = MutableStateFlow<List<String>>(emptyList())
    val filterMuscleGroups = _filterMuscleGroups.asStateFlow()

    private val _workoutImages = MutableStateFlow<Map<String, List<WorkoutImage>>>(emptyMap())
    val workoutImages: StateFlow<Map<String, List<WorkoutImage>>> = _workoutImages.asStateFlow()

    private val _uploadingImage = MutableStateFlow(false)
    val uploadingImage: StateFlow<Boolean> = _uploadingImage.asStateFlow()

    // Collection to track active image flow jobs to prevent duplicates
    private val activeImageFlowJobs = mutableMapOf<String, Job>()

    // Filtered workouts based on muscle group filters
    val filteredWorkouts = combine(workouts, filterMuscleGroups) { workoutList, muscleGroups ->
        if (muscleGroups.isEmpty()) {
            workoutList
        } else {
            workoutList.filter { workout ->
                // Workout included if it has at least one exercise for each selected muscle group
                muscleGroups.all { muscleGroup ->
                    workout.exercises.any { exercise ->
                        exercise.muscleGroup.contains(muscleGroup, ignoreCase = true)
                    }
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Clear data on initialization if there's no authenticated user
        val currentUser = auth.currentUser
        if (currentUser == null) {
            viewModelScope.launch {
                repository.clearLocalData()
            }
        }

        // Collect exercises
        viewModelScope.launch {
            repository.exercises.collect { exerciseList ->
                Log.d(TAG, "Exercises updated: ${exerciseList.size} items")
                _exercises.value = exerciseList
            }
        }

        // Collect workouts
        viewModelScope.launch {
            repository.workouts.collect { workoutList ->
                Log.d(TAG, "Workouts updated: ${workoutList.size} items")
                _workouts.value = workoutList
            }
        }

        // Listen for auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            Log.d(TAG, "Auth state changed. User: ${user?.uid ?: "null"}")

            if (user == null) {
                // User logged out, clear local data
                viewModelScope.launch {
                    repository.clearLocalData()
                    clearImageFlows()
                }
            }
        }
    }

    // Filter functions
    fun updateFilterMuscleGroups(muscleGroups: List<String>) {
        _filterMuscleGroups.value = muscleGroups
    }

    fun clearFilters() {
        _filterMuscleGroups.value = emptyList()
    }

    // Create global exercise
    fun addExercise(name: String, muscleGroup: String) {
        if (name.isNotBlank()) {
            viewModelScope.launch {
                repository.insertExercise(
                    Exercise(name = name, muscleGroup = muscleGroup)
                )
            }
        }
    }

    // Create a new workout
    fun addWorkout(name: String, description: String) {
        if (name.isNotBlank()) {
            viewModelScope.launch {
                repository.insertWorkout(
                    Workout(name = name, description = description)
                )
            }
        }
    }

    // Update an existing workout
    fun updateWorkout(workoutId: String, newName: String, newDescription: String) {
        if (newName.isNotBlank()) {
            viewModelScope.launch {
                // Find the workout
                val workout = _workouts.value.find { it.id == workoutId } ?: return@launch

                // Update the workout
                val updatedWorkout = workout.copy(
                    name = newName,
                    description = newDescription
                )

                repository.updateWorkout(updatedWorkout)
            }
        }
    }

    // Delete a workout by ID
    fun deleteWorkout(workoutId: String) {
        viewModelScope.launch {
            try {
                val workout = _workouts.value.find { it.id == workoutId } ?: return@launch
                repository.deleteWorkout(workout)

                // Delete all images for this workout
                storageRepository.deleteAllWorkoutImages(workoutId)

                // Cancel the image flow job for this workout
                activeImageFlowJobs[workoutId]?.cancel()
                activeImageFlowJobs.remove(workoutId)

                // Remove the images from the state
                val updatedImagesMap = _workoutImages.value.toMutableMap()
                updatedImagesMap.remove(workoutId)
                _workoutImages.value = updatedImagesMap

                Log.d(TAG, "Successfully deleted workout and its images: $workoutId")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting workout or its images: ${e.message}", e)
            }
        }
    }

    // Add an exercise to a specific workout
    fun addExerciseToWorkout(workoutId: String, exerciseName: String, exerciseMuscleGroup: String) {
        if (exerciseName.isNotBlank()) {
            viewModelScope.launch {
                val exercise = Exercise(
                    name = exerciseName,
                    muscleGroup = exerciseMuscleGroup
                )

                repository.addExerciseToWorkout(workoutId, exercise)
            }
        }
    }

    // Update an exercise within a workout
    fun updateWorkoutExercise(workoutId: String, exerciseId: String, newName: String, newMuscleGroup: String) {
        if (newName.isNotBlank()) {
            viewModelScope.launch {
                val exercise = Exercise(
                    id = exerciseId,
                    name = newName,
                    muscleGroup = newMuscleGroup
                )

                repository.updateExercise(exercise)
            }
        }
    }

    // Remove an exercise from a specific workout
    fun removeExerciseFromWorkout(workoutId: String, exerciseId: String) {
        viewModelScope.launch {
            repository.removeExerciseFromWorkout(workoutId, exerciseId)
        }
    }

    // Upload an image
    fun uploadWorkoutImage(imageUri: Uri, workoutId: String) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Starting image upload for workout: $workoutId")
                _uploadingImage.value = true

                val workoutImage = storageRepository.uploadWorkoutImage(imageUri, workoutId)
                Log.d(TAG, "Image uploaded successfully: ${workoutImage.id}")

                Log.d(TAG, "Successfully uploaded image for workout: $workoutId")
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading image to workout $workoutId: ${e.message}", e)
            } finally {
                _uploadingImage.value = false
            }
        }
    }

    // Load images for a workout
    fun loadWorkoutImages(workoutId: String) {
        // Check if we already have an active flow for this workout
        if (activeImageFlowJobs.containsKey(workoutId)) {
            Log.d(TAG, "Image flow already active for workout: $workoutId")
            return
        }

        Log.d(TAG, "Loading images for workout: $workoutId")

        // Create a new job for this image flow
        val job = viewModelScope.launch {
            try {
                storageRepository.getWorkoutImagesFlow(workoutId).collect { images ->
                    Log.d(TAG, "Received ${images.size} images for workout $workoutId")

                    // Use a set to remove duplicates based on image ID
                    val uniqueImages = images.distinctBy { it.id }

                    // Always update the state with the latest images
                    val currentMap = _workoutImages.value.toMutableMap()
                    currentMap[workoutId] = uniqueImages
                    _workoutImages.value = currentMap

                    Log.d(TAG, "Updated workout images state. Current map size: ${_workoutImages.value.size}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading workout images for $workoutId: ${e.message}", e)

                // Set empty list on error to prevent crash
                val currentMap = _workoutImages.value.toMutableMap()
                currentMap[workoutId] = emptyList()
                _workoutImages.value = currentMap
            }
        }

        // Store the job so we can cancel it later if needed
        activeImageFlowJobs[workoutId] = job
    }

    // Delete an image
    fun deleteWorkoutImage(workoutImage: WorkoutImage) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Deleting image: ${workoutImage.id}")

                storageRepository.deleteWorkoutImage(workoutImage)

                Log.d(TAG, "Successfully deleted image: ${workoutImage.id}")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting image ${workoutImage.id}: ${e.message}", e)
            }
        }
    }

    // Clear all image flows when logging out
    private fun clearImageFlows() {
        activeImageFlowJobs.values.forEach { it.cancel() }
        activeImageFlowJobs.clear()
        _workoutImages.value = emptyMap()
    }

    // Clean up when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        // Cancel all active image flow jobs
        clearImageFlows()
    }
}