package ie.setu.ca1_mad2.data

import ie.setu.ca1_mad2.data.room.GymRepository
import ie.setu.ca1_mad2.model.Workout
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataInitializer @Inject constructor(
    private val repository: GymRepository
) {
    suspend fun initializeSampleWorkoutsIfNeeded() {
        val existingWorkouts = repository.workouts.first()

        // Create samples if no workouts
        if (existingWorkouts.isEmpty()) {
            val samples = DefaultExercises.sampleWorkouts

            samples.forEach { (name, description) ->
                val workout = Workout(
                    name = name,
                    description = description
                )
                repository.insertWorkout(workout)
            }
        }
    }
}