package com.example.nammanala.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    var isLoading = mutableStateOf(false)
        private set

    var authSuccess = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun signup(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {

        isLoading.value = true
        errorMessage.value = null

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                isLoading.value = false
                onSuccess()
            }

            .addOnFailureListener {

                isLoading.value = false
                errorMessage.value = it.message
            }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {

        isLoading.value = true
        errorMessage.value = null

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                isLoading.value = false
                onSuccess()
            }

            .addOnFailureListener {

                isLoading.value = false
                errorMessage.value = it.message
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}