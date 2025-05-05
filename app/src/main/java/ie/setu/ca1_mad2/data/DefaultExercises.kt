package ie.setu.ca1_mad2.data

import ie.setu.ca1_mad2.model.Exercise

object DefaultExercises {

    // List of common exercises with their primary muscle groups
    val exercises = listOf(
        // Chest exercises
        Exercise(name = "Flat Barbell Bench Press", muscleGroup = "Chest, Triceps, Shoulders"),
        Exercise(name = "Flat Dumbbell Bench Press", muscleGroup = "Chest, Triceps, Shoulders"),
        Exercise(name = "Incline Dumbbell Bench Press", muscleGroup = "Chest, Triceps, Shoulders"),
        Exercise(name = "Incline Barbell Bench Press", muscleGroup = "Chest, Shoulders, Triceps"),
        Exercise(name = "Push-Ups", muscleGroup = "Chest, Triceps, Shoulders, Core"),
        Exercise(name = "Dumbbell Flyes", muscleGroup = "Chest, Shoulders"),
        Exercise(name = "Machine Flyes", muscleGroup = "Chest, Shoulders"),

        // Back exercises
        Exercise(name = "Pull-Ups", muscleGroup = "Back, Biceps"),
        Exercise(name = "Lat Pulldowns", muscleGroup = "Back, Biceps"),
        Exercise(name = "Bent-Over Rows", muscleGroup = "Back, Biceps, Shoulders"),
        Exercise(name = "Deadlift", muscleGroup = "Back, Glutes, Hamstrings, Core"),
        Exercise(name = "T-Bar Rows", muscleGroup = "Back, Biceps"),
        Exercise(name = "Face Pulls", muscleGroup = "Back, Shoulders"),

        // Shoulder exercises
        Exercise(name = "Overhead Press", muscleGroup = "Shoulders, Triceps"),
        Exercise(name = "Lateral Raises", muscleGroup = "Shoulders"),
        Exercise(name = "Front Raises", muscleGroup = "Shoulders"),
        Exercise(name = "Upright Rows", muscleGroup = "Shoulders, Traps"),
        Exercise(name = "Shrugs", muscleGroup = "Traps, Shoulders"),
        Exercise(name = "Reverse Flyes", muscleGroup = "Shoulders, Back"),

        // Arm exercises
        Exercise(name = "Bicep Curls", muscleGroup = "Biceps"),
        Exercise(name = "Hammer Curls", muscleGroup = "Biceps, Forearms"),
        Exercise(name = "Preacher Curls", muscleGroup = "Biceps"),
        Exercise(name = "Tricep Pushdowns", muscleGroup = "Triceps"),
        Exercise(name = "Skull Crushers", muscleGroup = "Triceps"),
        Exercise(name = "Dips", muscleGroup = "Triceps, Chest, Shoulders"),

        // Leg exercises
        Exercise(name = "Squats", muscleGroup = "Quadriceps, Glutes, Hamstrings, Core"),
        Exercise(name = "Leg Press", muscleGroup = "Quadriceps, Glutes, Hamstrings"),
        Exercise(name = "Lunges", muscleGroup = "Quadriceps, Glutes, Hamstrings"),
        Exercise(name = "Leg Extensions", muscleGroup = "Quadriceps"),
        Exercise(name = "Leg Curls", muscleGroup = "Hamstrings"),
        Exercise(name = "Calf Raises", muscleGroup = "Calves"),

        // Core exercises
        Exercise(name = "Crunches", muscleGroup = "Core"),
        Exercise(name = "Planks", muscleGroup = "Core, Shoulders"),
        Exercise(name = "Russian Twists", muscleGroup = "Core"),
        Exercise(name = "Leg Raises", muscleGroup = "Core"),
        Exercise(name = "Mountain Climbers", muscleGroup = "Core, Shoulders"),
        Exercise(name = "Ab Rollouts", muscleGroup = "Core, Shoulders"),

        // Cardio exercises
        Exercise(name = "Running", muscleGroup = "Cardio, Quadriceps, Hamstrings, Calves"),
        Exercise(name = "Cycling", muscleGroup = "Cardio, Quadriceps, Hamstrings, Calves"),
        Exercise(name = "Rowing", muscleGroup = "Cardio, Back, Biceps, Shoulders, Core"),
        Exercise(name = "Jump Rope", muscleGroup = "Cardio, Calves, Shoulders"),
        Exercise(name = "Burpees", muscleGroup = "Cardio, Chest, Shoulders, Quadriceps, Core")
    )

    // Predefined workout examples
    val sampleWorkouts = listOf(
        "Upper Body" to "Chest, back, and arms to focus upper body",
        "Lower Body" to "Leg exercises to focus lower body",
        "Full Body" to "Full body workout to build overall fitness",
        "Core" to "Strengthen your midsection and improve stability",
        "Push Day" to "All pushing exercises for chest, shoulders, and triceps",
        "Pull Day" to "All pulling exercises for back and biceps"
    )
}