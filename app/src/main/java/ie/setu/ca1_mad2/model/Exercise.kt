package ie.setu.ca1_mad2.model

import androidx.room.PrimaryKey

data class Exercise(
    @PrimaryKey val id: Int,
    val name: String,
    val muscleGroup: String
)
