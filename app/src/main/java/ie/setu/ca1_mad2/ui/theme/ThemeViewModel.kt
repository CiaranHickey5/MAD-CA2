package ie.setu.ca1_mad2.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Define theme modes
enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

class ThemeViewModel(private val context: Context) : ViewModel() {

    // Define DataStore
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("theme_preferences")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }

    // Simple StateFlow
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode

    init {
        // Load saved theme mode if available
        viewModelScope.launch {
            try {
                context.dataStore.data.collect { preferences ->
                    val savedMode = preferences[THEME_MODE_KEY]
                    if (savedMode != null) {
                        _themeMode.value = ThemeMode.valueOf(savedMode)
                    }
                }
            } catch (e: Exception) {
                // Default to SYSTEM if there's an error
                _themeMode.value = ThemeMode.SYSTEM
            }
        }
    }

    // Function to update the theme mode
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            _themeMode.value = mode
            context.dataStore.edit { preferences ->
                preferences[THEME_MODE_KEY] = mode.name
            }
        }
    }

    // Factory to create the ViewModel with context
    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ThemeViewModel::class.java)) {
                return ThemeViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}