package ie.setu.ca1_mad2.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDAO {
    @Query("SELECT * FROM workouts WHERE userId = :userId")
    fun getAllWorkoutsForUser(userId: String): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Transaction
    @Query("SELECT * FROM workouts WHERE userId = :userId")
    fun getWorkoutsWithExercisesForUser(userId: String): Flow<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workouts")
    fun getWorkoutsWithExercises(): Flow<List<WorkoutWithExercises>>

    @Transaction
    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getWorkoutWithExercisesById(id: String): Flow<WorkoutWithExercises>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkouts(workouts: List<WorkoutEntity>)

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExerciseCrossRef(crossRef: WorkoutExerciseCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExerciseCrossRefs(crossRefs: List<WorkoutExerciseCrossRef>)

    @Query("DELETE FROM workout_exercise_crossref WHERE workoutId = :workoutId AND exerciseId = :exerciseId")
    suspend fun deleteWorkoutExerciseCrossRef(workoutId: String, exerciseId: String)

    @Query("DELETE FROM workout_exercise_crossref WHERE workoutId = :workoutId")
    suspend fun deleteAllExercisesFromWorkout(workoutId: String)

    @Query("DELETE FROM workouts WHERE userId = :userId")
    suspend fun deleteWorkoutsForUser(userId: String)

    @Query("DELETE FROM workout_exercise_crossref WHERE workoutId IN (SELECT id FROM workouts WHERE userId = :userId)")
    suspend fun deleteCrossRefsForUser(userId: String)

    @Query("DELETE FROM workouts")
    suspend fun deleteAllWorkouts()

    @Query("DELETE FROM workout_exercise_crossref")
    suspend fun deleteAllCrossRefs()
}