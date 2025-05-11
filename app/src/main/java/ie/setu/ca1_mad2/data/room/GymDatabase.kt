package ie.setu.ca1_mad2.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutEntity::class,
        WorkoutExerciseCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
abstract class GymDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDAO
    abstract fun workoutDao(): WorkoutDAO

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add userId column to exercises table
                database.execSQL("ALTER TABLE exercises ADD COLUMN userId TEXT NOT NULL DEFAULT 'guest'")

                // Add userId column to workouts table
                database.execSQL("ALTER TABLE workouts ADD COLUMN userId TEXT NOT NULL DEFAULT 'guest'")
            }
        }
    }
}