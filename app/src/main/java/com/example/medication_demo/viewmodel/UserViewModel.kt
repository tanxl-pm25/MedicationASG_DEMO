package com.example.medication_demo.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medication_demo.data.SupabaseClientProvider
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class UserViewModel : ViewModel() {

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userGender = MutableStateFlow("")
    val userGender: StateFlow<String> = _userGender.asStateFlow()

    private val _userAge = MutableStateFlow("")
    val userAge: StateFlow<String> = _userAge.asStateFlow()

    // 用来判断这一次 Login 是否是刚刚注册的新账号
    private val _isNewUser = MutableStateFlow(false)
    val isNewUser: StateFlow<Boolean> = _isNewUser.asStateFlow()

    // Loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error message
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _userAvatarUrl = MutableStateFlow<String?>(null)
    val userAvatarUrl: StateFlow<String?> = _userAvatarUrl.asStateFlow()


    // =========================
    // Login
    // =========================

    fun login(
        email: String,
        password: String,
        onSuccess: (isNewUser: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                SupabaseClientProvider.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                loadUserProfile()

                onSuccess(_isNewUser.value)

            } catch (e: Exception) {
                Log.e("Login", "login error: ${e.message}", e)
                _errorMessage.value =
                    "Login failed. Please check your email and password."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUserProfile() {

        val user = SupabaseClientProvider.client.auth.currentUserOrNull()

        val metadata = user?.userMetadata

        _userName.value =
            metadata?.get("full_name")
                ?.toString()
                ?.trim('"')
                ?: ""

        _userEmail.value =
            user?.email
                ?: ""

        _userGender.value =
            metadata?.get("gender")
                ?.toString()
                ?.trim('"')
                ?: ""

        _userAge.value =
            metadata?.get("age")
                ?.toString()
                ?.trim('"')
                ?: ""

        _userAvatarUrl.value =
            metadata?.get("avatar_url")
                ?.toString()
                ?.trim('"')
    }


    // =========================
    // Sign Up
    // =========================

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

                val result =
                    SupabaseClientProvider.client.auth.signUpWith(Email) {

                        this.email = email
                        this.password = password

                        data = buildJsonObject {
                            put("full_name", name)
                        }
                    }

                if (result?.identities.isNullOrEmpty()) {

                    _errorMessage.value =
                        "This email is already registered. Please login instead."

                } else {

                    _userName.value = name
                    _userEmail.value = email
                    _isNewUser.value = true

                    onSuccess()
                }

            } catch (e: Exception) {

                Log.e(
                    "SignUp",
                    "signUp error: ${e.message}",
                    e
                )

                _errorMessage.value =
                    if (
                        e.message?.contains(
                            "already registered",
                            ignoreCase = true
                        ) == true ||
                        e.message?.contains(
                            "already exists",
                            ignoreCase = true
                        ) == true ||
                        e.message?.contains(
                            "user_already_exists",
                            ignoreCase = true
                        ) == true
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


    // =========================
    // Google Login
    // =========================

    fun loginWithGoogle() {

        viewModelScope.launch {

            _errorMessage.value = null

            try {

                SupabaseClientProvider.client.auth.signInWith(Google)

            } catch (e: Exception) {

                Log.e(
                    "GoogleLogin",
                    "google login error: ${e.message}",
                    e
                )

                _errorMessage.value =
                    "Google sign-in failed. Please try again."
            }
        }
    }


    // =========================
    // Forgot Password
    // =========================

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _errorMessage.value = null

            try {

                SupabaseClientProvider.client.auth
                    .resetPasswordForEmail(email)

                onSuccess()

            } catch (e: Exception) {

                Log.e(
                    "ForgotPassword",
                    "reset password error: ${e.message}",
                    e
                )

                _errorMessage.value =
                    "Failed to send reset link. Please try again."

            } finally {
                _isLoading.value = false
            }
        }
    }


    // =========================
    // Reset Password
    // =========================

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

                Log.e(
                    "ResetPassword",
                    "update password error: ${e.message}",
                    e
                )

                _errorMessage.value =
                    "Failed to update password. Please try again."

            } finally {
                _isLoading.value = false
            }
        }
    }


    fun changePassword(
        currentPassword: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onWrongCurrentPassword: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val email =
                    SupabaseClientProvider.client.auth
                        .currentUserOrNull()
                        ?.email

                if (email.isNullOrBlank()) {
                    _errorMessage.value =
                        "Unable to identify the current user."
                    return@launch
                }

                // Re-login to verify the current password
                try {
                    SupabaseClientProvider.client.auth.signInWith(Email) {
                        this.email = email
                        this.password = currentPassword
                    }
                } catch (e: Exception) {
                    Log.e(
                        "ChangePassword",
                        "Wrong current password: ${e.message}",
                        e
                    )

                    onWrongCurrentPassword()
                    return@launch
                }

                // Current password is correct, update to new password
                SupabaseClientProvider.client.auth.updateUser {
                    password = newPassword
                }

                onSuccess()

            } catch (e: Exception) {
                Log.e(
                    "ChangePassword",
                    "Change password error: ${e.message}",
                    e
                )

                _errorMessage.value =
                    "Failed to change password. Please try again."

                onWrongCurrentPassword()
                return@launch

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

                SupabaseClientProvider.client.auth
                    .verifyEmailOtp(
                        type = OtpType.Email.SIGNUP,
                        email = email,
                        token = code
                    )

                loadUserProfile()

                onSuccess()

            } catch (e: Exception) {

                Log.e(
                    "VerifyEmail",
                    "verify error: ${e.message}",
                    e
                )

                _errorMessage.value =
                    "Invalid or expired code. Please try again."

            } finally {
                _isLoading.value = false
            }
        }
    }


    // =========================
    // Resend Verification Code
    // =========================

    fun resendVerificationCode(email: String) {

        viewModelScope.launch {

            try {

                SupabaseClientProvider.client.auth
                    .resendEmail(
                        type = OtpType.Email.SIGNUP,
                        email = email
                    )

            } catch (e: Exception) {

                Log.e(
                    "ResendCode",
                    "resend error: ${e.message}",
                    e
                )
            }
        }
    }


    // =========================
    // Gender & Age
    // 保存到 Supabase Auth Metadata
    // =========================

    fun onGenderSelected(gender: String) {

        _userGender.value = gender

        viewModelScope.launch {

            try {

                SupabaseClientProvider.client.auth.updateUser {

                    data = buildJsonObject {
                        put("gender", gender)
                    }
                }

            } catch (e: Exception) {

                Log.e(
                    "Gender",
                    "save gender error: ${e.message}",
                    e
                )
            }
        }
    }


    fun onAgeSelected(age: Int) {

        _userAge.value = age.toString()

        viewModelScope.launch {

            try {

                SupabaseClientProvider.client.auth.updateUser {

                    data = buildJsonObject {
                        put("age", age)
                    }
                }

                // Gender + Age 完成后，
                // 这个账号以后不再属于 New User
                _isNewUser.value = false

            } catch (e: Exception) {

                Log.e(
                    "Age",
                    "save age error: ${e.message}",
                    e
                )
            }
        }
    }


    // =========================
    // Edit Gender
    // =========================

    fun updateGender(gender: String) {

        viewModelScope.launch {

            try {

                SupabaseClientProvider.client.auth.updateUser {

                    data = buildJsonObject {
                        put("gender", gender)
                    }
                }

                _userGender.value = gender

            } catch (e: Exception) {

                Log.e(
                    "UpdateGender",
                    "update gender error: ${e.message}",
                    e
                )
            }
        }
    }


    // =========================
    // Edit Age
    // =========================

    fun updateAge(age: Int) {

        viewModelScope.launch {

            try {

                SupabaseClientProvider.client.auth.updateUser {

                    data = buildJsonObject {
                        put("age", age)
                    }
                }

                _userAge.value = age.toString()

            } catch (e: Exception) {

                Log.e(
                    "UpdateAge",
                    "update age error: ${e.message}",
                    e
                )
            }
        }
    }


    // =========================
    // Edit Name
    // =========================

    fun updateName(
        newName: String,
        onSuccess: () -> Unit = {}
    ) {

        viewModelScope.launch {

            try {

                SupabaseClientProvider.client.auth.updateUser {

                    data = buildJsonObject {
                        put("full_name", newName)
                    }
                }

                _userName.value = newName

                onSuccess()

            } catch (e: Exception) {

                Log.e(
                    "UpdateName",
                    "update name error: ${e.message}",
                    e
                )

                _errorMessage.value =
                    "Failed to update name. Please try again."
            }
        }
    }


    // =========================
    // Edit Email
    // =========================

    fun updateEmail(
        newEmail: String,
        onSuccess: () -> Unit = {}
    ) {

        viewModelScope.launch {

            _errorMessage.value = null

            try {

                SupabaseClientProvider.client.auth.updateUser {

                    email = newEmail
                }

                onSuccess()

            } catch (e: Exception) {

                Log.e(
                    "UpdateEmail",
                    "update email error: ${e.message}",
                    e
                )

                _errorMessage.value =
                    "Failed to update email. Please try again."
            }
        }
    }


    // =========================
    // Verify Email Change
    // =========================

    fun verifyEmailChange(
        newEmail: String,
        code: String,
        onSuccess: () -> Unit = {}
    ) {

        viewModelScope.launch {

            _isLoading.value = true
            _errorMessage.value = null

            try {

                SupabaseClientProvider.client.auth
                    .verifyEmailOtp(
                        type = OtpType.Email.EMAIL_CHANGE,
                        email = newEmail,
                        token = code
                    )

                _userEmail.value = newEmail

                loadUserProfile()

                onSuccess()

            } catch (e: Exception) {

                Log.e(
                    "VerifyEmailChange",
                    "verify error: ${e.message}",
                    e
                )

                _errorMessage.value =
                    "Invalid or expired code. Please try again."

            } finally {
                _isLoading.value = false
            }
        }
    }


    // =========================
    // Delete Account
    // =========================

    fun deleteAccount(
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {}
    ) {

        viewModelScope.launch {

            try {

                SupabaseClientProvider.client
                    .postgrest
                    .rpc("delete_own_account")

                onSuccess()

            } catch (e: Exception) {

                Log.e(
                    "DeleteAccount",
                    "delete error: ${e.message}",
                    e
                )

                onError()
            }
        }
    }


    // =========================
    // Upload Avatar
    // =========================

    fun uploadAvatar(
        context: Context,
        imageUri: Uri
    ) {

        viewModelScope.launch {

            try {

                val userId =
                    SupabaseClientProvider.client.auth
                        .currentUserOrNull()
                        ?.id
                        ?: return@launch

                val inputStream =
                    context.contentResolver
                        .openInputStream(imageUri)

                val bytes =
                    inputStream?.readBytes()
                        ?: return@launch

                inputStream.close()

                val fileName = "$userId.jpg"

                SupabaseClientProvider.client.storage
                    .from("avatars")
                    .upload(fileName, bytes) {
                        upsert = true
                    }

                val publicUrl =
                    SupabaseClientProvider.client.storage
                        .from("avatars")
                        .publicUrl(fileName)

                // Avatar URL 存进 Auth Metadata
                SupabaseClientProvider.client.auth.updateUser {

                    data = buildJsonObject {
                        put("avatar_url", publicUrl)
                    }
                }

                _userAvatarUrl.value = publicUrl

            } catch (e: Exception) {

                Log.e(
                    "UploadAvatar",
                    "upload error: ${e.message}",
                    e
                )
            }
        }
    }


    // =========================
    // Error
    // =========================

    fun clearErrorMessage() {
        _errorMessage.value = null
    }


    // =========================
    // Logout
    // =========================

    fun logout() {

        viewModelScope.launch {

            try {

                SupabaseClientProvider.client.auth.signOut()

            } catch (e: Exception) {

                Log.e(
                    "Logout",
                    "sign out error: ${e.message}",
                    e
                )
            }
        }

        // 这里只清除本地 State
        // Supabase Auth Metadata 不会被删除
        _userName.value = ""
        _userEmail.value = ""
        _userGender.value = ""
        _userAge.value = ""
        _userAvatarUrl.value = null
        _errorMessage.value = null
        _isNewUser.value = false
    }
}