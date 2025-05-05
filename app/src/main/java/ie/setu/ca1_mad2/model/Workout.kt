package ie.setu.ca1_mad2.model

import java.util.UUID

data class Workout(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val exercises: MutableList<Exercise> = mutableListOf()
)
