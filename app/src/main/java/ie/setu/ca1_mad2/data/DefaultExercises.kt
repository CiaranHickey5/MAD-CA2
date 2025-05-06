package ie.setu.ca1_mad2.data

import ie.setu.ca1_mad2.model.Exercise

object DefaultExercises {

    // List of common exercises with their primary muscle groups
    val exercises = listOf(
        // Chest exercises
        Exercise(id = 1, name = "Flat Barbell Bench Press", muscleGroup = "Chest, Triceps, Shoulders"),
        Exercise(id = 2, name = "Flat Dumbbell Bench Press", muscleGroup = "Chest, Triceps, Shoulders"),
        Exercise(id = 3, name = "Incline Dumbbell Bench Press", muscleGroup = "Chest, Triceps, Shoulders"),
        Exercise(id = 4, name = "Incline Barbell Bench Press", muscleGroup = "Chest, Shoulders, Triceps"),
        Exercise(id = 5, name = "Push-Ups", muscleGroup = "Chest, Triceps, Shoulders, Core"),
        Exercise(id = 6, name = "Dumbbell Flyes", muscleGroup = "Chest, Shoulders"),
        Exercise(id = 7, name = "Machine Flyes", muscleGroup = "Chest, Shoulders"),

        // Back exercises
        Exercise(id = 8, name = "Pull-Ups", muscleGroup = "Back, Biceps"),
        Exercise(id = 9, name = "Lat Pulldowns", muscleGroup = "Back, Biceps"),
        Exercise(id = 10, name = "Bent-Over Rows", muscleGroup = "Back, Biceps, Shoulders"),
        Exercise(id = 11, name = "Deadlift", muscleGroup = "Back, Glutes, Hamstrings, Core"),
        Exercise(id = 12, name = "T-Bar Rows", muscleGroup = "Back, Biceps"),
        Exercise(id = 13, name = "Face Pulls", muscleGroup = "Back, Shoulders"),

        // Shoulder exercises
        Exercise(id = 14, name = "Overhead Press", muscleGroup = "Shoulders, Triceps"),
        Exercise(id = 15, name = "Lateral Raises", muscleGroup = "Shoulders"),
        Exercise(id = 16, name = "Front Raises", muscleGroup = "Shoulders"),
        Exercise(id = 17, name = "Upright Rows", muscleGroup = "Shoulders, Traps"),
        Exercise(id = 18, name = "Shrugs", muscleGroup = "Traps, Shoulders"),
        Exercise(id = 19, name = "Reverse Flyes", muscleGroup = "Shoulders, Back"),

        // Arm exercises
        Exercise(id = 20, name = "Bicep Curls", muscleGroup = "Biceps"),
        Exercise(id = 21, name = "Hammer Curls", muscleGroup = "Biceps, Forearms"),
        Exercise(id = 22, name = "Preacher Curls", muscleGroup = "Biceps"),
        Exercise(id = 23, name = "Tricep Pushdowns", muscleGroup = "Triceps"),
        Exercise(id = 24, name = "Skull Crushers", muscleGroup = "Triceps"),
        Exercise(id = 25, name = "Dips", muscleGroup = "Triceps, Chest, Shoulders"),

        // Leg exercises
        Exercise(id = 25, name = "Squats", muscleGroup = "Quadriceps, Glutes, Hamstrings, Core"),
        Exercise(id = 26, name = "Leg Press", muscleGroup = "Quadriceps, Glutes, Hamstrings"),
        Exercise(id = 27, name = "Lunges", muscleGroup = "Quadriceps, Glutes, Hamstrings"),
        Exercise(id = 28, name = "Leg Extensions", muscleGroup = "Quadriceps"),
        Exercise(id = 29, name = "Leg Curls", muscleGroup = "Hamstrings"),
        Exercise(id = 30, name = "Calf Raises", muscleGroup = "Calves"),

        // Core exercises
        Exercise(id = 31, name = "Crunches", muscleGroup = "Core"),
        Exercise(id = 32, name = "Planks", muscleGroup = "Core, Shoulders"),
        Exercise(id = 33, name = "Russian Twists", muscleGroup = "Core"),
        Exercise(id = 34, name = "Leg Raises", muscleGroup = "Core"),
        Exercise(id = 35, name = "Mountain Climbers", muscleGroup = "Core, Shoulders"),
        Exercise(id = 36, name = "Ab Rollouts", muscleGroup = "Core, Shoulders"),

        // Cardio exercises
        Exercise(id = 37, name = "Running", muscleGroup = "Cardio, Quadriceps, Hamstrings, Calves"),
        Exercise(id = 38, name = "Cycling", muscleGroup = "Cardio, Quadriceps, Hamstrings, Calves"),
        Exercise(id = 39, name = "Rowing", muscleGroup = "Cardio, Back, Biceps, Shoulders, Core"),
        Exercise(id = 40, name = "Jump Rope", muscleGroup = "Cardio, Calves, Shoulders"),
        Exercise(id = 41, name = "Burpees", muscleGroup = "Cardio, Chest, Shoulders, Quadriceps, Core")
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