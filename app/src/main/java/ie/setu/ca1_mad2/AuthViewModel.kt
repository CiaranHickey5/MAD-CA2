package ie.setu.ca1_mad2

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ie.setu.ca1_mad2.data.room.GymRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gymRepository: GymRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val TAG = "AuthViewModel"

    init {
        // Listen for auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _currentUser.value = user

            // Handle user state changes
            if (user != null) {
                // User is signed in, sync data
                syncDataAfterLogin()
            } else {
                // User is signed out, clear local data
                clearUserData()
            }
        }

        // Initialize Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("625162121467-a1ao41mjkb4fkvujo1c38mue9gh67s48.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    private fun syncDataAfterLogin() {
        viewModelScope.launch {
            try {
                // Log the user ID
                val userId = auth.currentUser?.uid ?: "guest"
                Log.d(TAG, "Syncing data for user: $userId")

                // Sync data from Firestore to local database
                gymRepository.syncDataFromFirestore()

                Log.d(TAG, "Data sync complete for user: $userId")
            } catch (e: Exception) {
                // Log errors during sync
                Log.e(TAG, "Error syncing data: ${e.message}", e)
            }
        }
    }

    private fun clearUserData() {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Clearing user data after sign out")

                // Clear local data when signing out
                gymRepository.clearLocalData()

                Log.d(TAG, "User data cleared successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing user data: ${e.message}", e)
            }
        }
    }

    fun signInWithEmailPassword(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Data sync is handled in auth state listener
                            onComplete(true, null)
                        } else {
                            onComplete(false, task.exception?.message)
                        }
                    }
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

    fun registerWithEmailPassword(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Data sync is handled in auth state listener
                            onComplete(true, null)
                        } else {
                            onComplete(false, task.exception?.message)
                        }
                    }
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    // Handle Google Sign-In result
    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>, onComplete: (Boolean, String?) -> Unit) {
        try {
            val account = task.getResult(ApiException::class.java)
            // Successfully signed in, authenticate with Firebase
            firebaseAuthWithGoogle(account, onComplete)
        } catch (e: ApiException) {
            // Sign in failed
            onComplete(false, e.message)
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount, onComplete: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Data sync is handled in auth state listener
                            onComplete(true, null)
                        } else {
                            onComplete(false, task.exception?.message)
                        }
                    }
            } catch (e: Exception) {
                onComplete(false, e.message)
            }
        }
    }

    fun signOut() {
        // Sign out from Firebase
        auth.signOut()

        // Sign out from Google
        googleSignInClient.signOut()
    }
}