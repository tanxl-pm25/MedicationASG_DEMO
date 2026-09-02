package com.example.medication_demo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medication_demo.data.SupabaseClientProvider
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userGender = MutableStateFlow("")
    val userGender: StateFlow<String> = _userGender.asStateFlow()

    private val _userAge = MutableStateFlow("")
    val userAge: StateFlow<String> = _userAge.asStateFlow()

    // 之后接Supabase login/signup时可以用这个显示loading圈
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 之后接Supabase login/signup时可以用这个显示错误信息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                SupabaseClientProvider.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                _userEmail.value = email
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Login failed. Please check your password."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(
        name: String,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = SupabaseClientProvider.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }

                if (result?.identities.isNullOrEmpty()) {
                    _errorMessage.value = "This email is already registered. Please login instead."
                } else {
                    _userName.value = name
                    _userEmail.value = email
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.e("SignUp", "signUp error: ${e.message}", e)
                _errorMessage.value = if (
                    e.message?.contains("already registered", ignoreCase = true) == true ||
                    e.message?.contains("already exists", ignoreCase = true) == true ||
                    e.message?.contains("user_already_exists", ignoreCase = true) == true
                ) {
                    "This email is already registered. Please login instead."
                } else {
                    "Sign up failed. Please try again."
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loginWithGoogle() {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                SupabaseClientProvider.client.auth.signInWith(Google)
            } catch (e: Exception) {
                Log.e("GoogleLogin", "google login error: ${e.message}", e)
                _errorMessage.value = "Google sign-in failed. Please try again."
            }
        }
    }

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                SupabaseClientProvider.client.auth.resetPasswordForEmail(email)
                onSuccess()
            } catch (e: Exception) {
                Log.e("ForgotPassword", "reset password error: ${e.message}", e)
                _errorMessage.value = "Failed to send reset link. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updatePassword(
        newPassword: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                SupabaseClientProvider.client.auth.updateUser {
                    password = newPassword
                }
                onSuccess()
            } catch (e: Exception) {
                Log.e("ResetPassword", "update password error: ${e.message}", e)
                _errorMessage.value = "Failed to update password. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun verifyEmail(
        email: String,
        code: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                SupabaseClientProvider.client.auth.verifyEmailOtp(
                    type = OtpType.Email.SIGNUP,
                    email = email,
                    token = code
                )
                onSuccess()
            } catch (e: Exception) {
                Log.e("VerifyEmail", "verify error: ${e.message}", e)
                _errorMessage.value = "Invalid or expired code. Please try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resendVerificationCode(email: String) {
        viewModelScope.launch {
            try {
                SupabaseClientProvider.client.auth.resendEmail(
                    type = OtpType.Email.SIGNUP,
                    email = email
                )
            } catch (e: Exception) {
                Log.e("ResendCode", "resend error: ${e.message}", e)
            }
        }
    }

    fun onGenderSelected(gender: String) {
        _userGender.value = gender
    }

    fun onAgeSelected(age: Int) {
        _userAge.value = age.toString()
    }

    fun updateGender(gender: String) {
        _userGender.value = gender
    }

    fun updateAge(age: Int) {
        _userAge.value = age.toString()
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun logout() {
        viewModelScope.launch {
            try {
                SupabaseClientProvider.client.auth.signOut()
            } catch (e: Exception) {
                Log.e("Logout", "sign out error: ${e.message}", e)
            }
        }
        _userName.value = ""
        _userEmail.value = ""
        _userGender.value = ""
        _userAge.value = ""
        _errorMessage.value = null
    }
}