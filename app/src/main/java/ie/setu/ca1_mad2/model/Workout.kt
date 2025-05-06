package ie.setu.ca1_mad2.model

import androidx.room.PrimaryKey

data class Workout(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String = "",
    val exercises: MutableList<Exercise> = mutableListOf()
)
