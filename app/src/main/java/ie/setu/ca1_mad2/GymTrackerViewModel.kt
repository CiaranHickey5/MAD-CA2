package ie.setu.ca1_mad2

import ie.setu.ca1_mad2.data.room.GymRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ie.setu.ca1_mad2.model.Exercise
import ie.setu.ca1_mad2.model.Workout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GymTrackerViewModel @Inject constructor(
    private val repository: GymRepository
) : ViewModel() {

    // Exercise state
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()

    // Workout state
    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts.asStateFlow()

    // Muscle group filter state
    private val _filterMuscleGroups = MutableStateFlow<List<String>>(emptyList())
    val filterMuscleGroups = _filterMuscleGroups.asStateFlow()

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
        // Collect exercises
        viewModelScope.launch {
            repository.exercises.collect { exerciseList ->
                _exercises.value = exerciseList
            }
        }

        // Collect workouts
        viewModelScope.launch {
            repository.workouts.collect { workoutList ->
                _workouts.value = workoutList
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
            val workout = _workouts.value.find { it.id == workoutId } ?: return@launch
            repository.deleteWorkout(workout)
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
}