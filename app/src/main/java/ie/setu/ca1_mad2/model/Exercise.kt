package ie.setu.ca1_mad2.model

import java.util.UUID

data class Exercise(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val muscleGroup: String
)
