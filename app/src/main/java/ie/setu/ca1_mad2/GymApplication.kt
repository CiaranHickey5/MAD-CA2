package ie.setu.ca1_mad2

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ie.setu.ca1_mad2.data.DataInitializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class GymApplication : Application() {

    @Inject
    lateinit var dataInitializer: DataInitializer

    override fun onCreate() {
        super.onCreate()

        // Initialize sample workouts in background if needed
        CoroutineScope(Dispatchers.IO).launch {
            dataInitializer.initializeSampleWorkoutsIfNeeded()
        }
    }
}