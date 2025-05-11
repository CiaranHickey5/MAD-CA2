# Assignment Two - Mobile App Development Two

## Student Information

| Field          | Value         |
| -------------- | ------------- |
| Name           | Ciaran Hickey |
| Student Number | 20088959      |

## Features Demonstration Video
...

## Technical Report

### Functionality Overview

The Gym Tracker app is a comprehensive fitness tracking application app that allows users to:

- Create, edit, and delete workout routines
- Create, edit and delete exercises from workouts.
- Filter workouts by muscle groups
- Filter exercises by muscle groups
- Search exercises
- Register/Login with email and password or Google Sign-In
- Upload and manage photos of workouts
- Switch between light and dark themes

#### Third-Party APIs and Libraries Used

| Library/API | Purpose |
| ----------- | ------- |
| Firebase Authentication | User authentication with email/password and Google Sign-In |
| Firebase Firestore | Storing workout and exercise data in the cloud|
| Firebase Storage | Cloud storage for workout images |
| Room Database | Local persistence for workouts and exercises |
| Dagger Hilt | Dependency injection |
| Coil | Image loading and caching |
| DataStore Preferences | Storing user preferences (theme settings) |
| Navigation Compose | App navigation |
| Material 3 | UI components and theming |

### Architecture and Design

#### UML Diagram

#### Class Diagram

#### App Architecture

The app follows the MVVM (Model-View-ViewModel) architecture pattern:

- **Model**: Represents the data layer including Room entities, Firestore models, and repositories
- **View**: Compose UI components that display the data
- **ViewModel**: Acts as a bridge between the Model and View, handling UI logic and data operations

Key components:
- `GymTrackerViewModel`: Manages workout and exercise data operations
- `AuthViewModel`: Handles user authentication
- `ThemeViewModel`: Manages theme preferences
- `GymRepository`: Provides data access layer for Room database
- `FirestoreRepository`: Provides data access layer for Firestore
- `StorageRepository`: Manages image upload and retrieval

### UX / DX Approach

The app is built entirely using Jetpack Compose, following the declarative UI paradigm rather than the traditional View-based approach.

The app implements several UX features:
- Bottom navigation for primary screens
- Navigation drawer for additional options
- Dark mode support with user preferences
- Image gallery for workout progress photos
- Filter for quick access to filtered content
- Splash screen for the app identity

### Git Approach

The project utilizes GitFlow as the branching strategy:

- `main`: Completed/production code, only committed a few times
- `release`: Stable features ready for testing
- `develop`: Active development branch, most commonly used branch
- Feature branches: Created for specific features

Version control approach:
- Semantic versioning (MAJOR.MINOR.PATCH)
- Tagged releases for significant milestones
- Included commit messages
- Regular commits to ensure traceable progress

### Personal Statement

Working on this Gym Tracker app has been a valuable learning experience. This project has made me much more confident with android development and I have enjoyed the development progress. Despite a number of challenges, it has been very rewarding and I will continue creating android apps in the future.

Key challenges included:
- Implementing proper Firebase synchronization
- Creating an intuitive UI
- Handling image uploads
- Implementing filtering system for workouts and exercises

The project closely follows the requirements outlined in the grading rubric, my attached demo video shows and discusses the features I have completed.

### References

1. [Official Android Documentation](https://developer.android.com/jetpack/compose)
2. [Firebase Documentation](https://firebase.google.com/docs)
3. [Material 3 Design Guidelines](https://m3.material.io/)
4. [Room Persistence Library](https://developer.android.com/training/data-storage/room)
5. [Coil Image Loading Library](https://coil-kt.github.io/coil/)

