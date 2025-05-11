package ie.setu.ca1_mad2.model

data class WorkoutImage(
    val id: String = "",
    val workoutId: String = "",
    val downloadUrl: String = "",
    val fileName: String = "",
    val uploadedAt: Long = System.currentTimeMillis()
)