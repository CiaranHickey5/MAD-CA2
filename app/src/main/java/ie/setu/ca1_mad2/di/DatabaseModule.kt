package ie.setu.ca1_mad2.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ie.setu.ca1_mad2.data.room.ExerciseDAO
import ie.setu.ca1_mad2.data.room.GymDatabase
import ie.setu.ca1_mad2.data.room.WorkoutDAO
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GymDatabase {
        return Room.databaseBuilder(
            context,
            GymDatabase::class.java,
            "gym_tracker_database"
        ).build()
    }

    @Provides
    fun provideExerciseDao(database: GymDatabase): ExerciseDAO {
        return database.exerciseDao()
    }

    @Provides
    fun provideWorkoutDao(database: GymDatabase): WorkoutDAO {
        return database.workoutDao()
    }
}