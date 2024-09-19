package com.paytondeveloper.sharedqandroid.sync

import android.content.Context
import android.icu.util.Output
import android.provider.Settings.Global
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import com.paytondeveloper.sharedqandroid.AppInfo
import com.paytondeveloper.sharedqandroid.protocol.AddGroupRequest
import com.paytondeveloper.sharedqandroid.protocol.NewSession
import com.paytondeveloper.sharedqandroid.protocol.SQGroup
import com.paytondeveloper.sharedqandroid.protocol.SQUser
import com.paytondeveloper.sharedqandroid.protocol.UserSignup
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.OutputStream

//FIRManager on Apple platforms
class SQManager(env: ServerID = ServerID.superDev) : ViewModel() {
    private val _uiState = MutableStateFlow(SQManagerUIState())
    val uiState: StateFlow<SQManagerUIState> = _uiState.asStateFlow()
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }
    init {
        _uiState.update {
            _uiState.value.copy(
                env = env,
                baseURL = "http://${env.url}",
                baseWSURL = "ws://${env.url}"
            )
        }
        var prefs = AppInfo.application.getSharedPreferences("auth", Context.MODE_PRIVATE)
        _uiState.update {
            _uiState.value.copy(
                authToken = prefs.getString("token", null)
            )
        }
        GlobalScope.launch {
            refreshData()
        }
    }
    suspend fun refreshData() {
        _uiState.update {
            _uiState.value.copy(
                loaded = false
            )
        }
        _uiState.value.authToken?.let {
            var request = Request.Builder().url("${_uiState.value.baseURL}/users/fetch-user")
                .method("GET", body = null)
                .addHeader("Authorization", "Bearer ${_uiState.value.authToken}")
                .build()
            var response = AppInfo.httpClient.newCall(request).execute()
            try {
                response.body?.let {
                    val user = json.decodeFromStream<SQUser>(response.body!!.byteStream())
                    Log.d("refreshdata", "user ${user.username}")
                    _uiState.update {
                        _uiState.value.copy(
                            currentUser = user,
                            loaded = true
                        )
                    }
                }
            } catch (e:Exception) {
                Log.e("refreshdata", "error refresing: ${e}")
            }
        }
    }
    suspend fun signUp(username: String, email: String, password: String): SQSignUpResponse {
        var data = json.encodeToString(value = UserSignup(email, username, password))
        var requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), content = data)
        var request = Request.Builder()
            .url("${_uiState.value.baseURL}/users/signup")
            .post(requestBody)
            .build()
        var response = AppInfo.httpClient.newCall(request).execute()
        try {
            response.body?.let {
                var session = json.decodeFromStream<NewSession>(response.body!!.byteStream())
                storeToken(session.token)
                _uiState.update {
                    _uiState.value.copy(
                        currentUser = session.user,
                        authToken = session.token
                    )
                }
                Log.d("signup", "username: ${session.user.username}")
                return SQSignUpResponse.SUCCESS
            }
            if (response.code == 409) {
                return SQSignUpResponse.ALREADY_EXISTS
            }
        } catch (e: Exception) {
            Log.e("signup", "error signing up: ${e}")
        }
        return SQSignUpResponse.NO_CONNECTION
    }
    suspend fun signIn(email: String, password: String): SQSignUpResponse {
        Log.d("signin", Base64.encodeToString("${email.lowercase()}:${password}".encodeToByteArray(), Base64.DEFAULT))
        var request = Request.Builder()
            .url("${_uiState.value.baseURL}/users/login")
            .method("PUT", RequestBody.create("application/json".toMediaTypeOrNull(), ""))

            .addHeader("Authorization", "Basic ${Base64.encodeToString("${email.lowercase()}:${password}".encodeToByteArray(), Base64.NO_WRAP)}")
            .build()
        val response = AppInfo.httpClient.newCall(request).execute()
        try {
            response.body?.let {
                val tokenResponse = json.decodeFromStream<NewSession>(response.body!!.byteStream())
                storeToken(tokenResponse.token)
                _uiState.update {
                    _uiState.value.copy(
                        currentUser = tokenResponse.user,
                        authToken = tokenResponse.token
                    )
                }
                Log.d("signin", "username ${tokenResponse.user.username}")
                return SQSignUpResponse.SUCCESS
            }
            if (response.code == 401) {
                return SQSignUpResponse.INCORRECT_PASSWORD
            }
        } catch (e: Exception) {
            Log.e("signin", "error loggin in: ${e}")
        }
        return SQSignUpResponse.NO_CONNECTION
    }
    suspend fun sendPasswordResetEmail(email: String) {
        var request = Request.Builder()
            .url("${_uiState.value.baseURL}/users/pwresetemail/${email}")
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), ""))
            .build()
        var res = AppInfo.httpClient.newCall(request).execute()
        Log.d("pwreset", "${res.code}")
    }
    suspend fun createGroup(group: SQGroup): Boolean {
        var data = json.encodeToString(value = group)
        Log.d("creategroup", data)
        var request = Request.Builder()
            .url("${_uiState.value.baseURL}/groups/create")
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), data))
            .bearer(_uiState.value.authToken)
            .build()
        var response = AppInfo.httpClient.newCall(request).execute()
        Log.d("creategroup", response.code.toString())
        refreshData()
        return response.code in 200..299

    }
    suspend fun updateGroup(group: SQGroup): Boolean {
        var request = Request.Builder()
            .url("${_uiState.value.baseURL}/groups/update")
            .put(RequestBody.create("application/json".toMediaTypeOrNull(), json.encodeToString(value = group)))
            .bearer(_uiState.value.authToken)
            .build()
        var response = AppInfo.httpClient.newCall(request).execute()
        Log.d("creategroup", response.code.toString())
        refreshData()
        return response.code in 200..299

    }
    suspend fun addGroup(groupID: String, groupURLID: String): Boolean {
        if (_uiState.value.currentUser == null) {
            refreshData()
        }
        var request = Request.Builder()
            .url("${_uiState.value.baseURL}/groups/add-group/${groupID}/${groupURLID}")
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), json.encodeToString(value = AddGroupRequest(myUID = _uiState.value.currentUser!!.id))))
            .bearer(_uiState.value.authToken)
            .build()
        var response = AppInfo.httpClient.newCall(request).execute()
        Log.d("creategroup", response.code.toString())
        refreshData()
        return response.code in 200..299

    }
    private fun storeToken(token: String) {
        val prefs = AppInfo.application.getSharedPreferences("auth", Context.MODE_PRIVATE)
        prefs.edit().putString("token", token).apply()
    }
    companion object {
        var shared = SQManager()
    }
}

fun Request.Builder.bearer(token: String?): Request.Builder {
    return this.addHeader("Authorization", "Bearer ${token}")
}

data class SQManagerUIState(
    var currentUser: SQUser? = null,
    var connectedGroup: SQGroup? = null,
    var connectedToGroup: Boolean = false,
    var loaded: Boolean = false,
    var authToken: String? = null,
    var syncManager: String = ""/*replace with SQSM*/,
    var setupQueue: Boolean = false,
    var env: ServerID = ServerID.beta,
    var baseURL: String = "",
    var baseWSURL: String = ""
)

enum class ServerID(val url: String) {
    superDev("192.168.68.112:8080"),
    beta("sq.paytondev.cloud:8080")
}
enum class SQSignUpResponse(val message: String) {
    SUCCESS("Success!"),
    ALREADY_EXISTS("That account already exists! Try logging in instead."),
    NO_CONNECTION("Unable to reach SharedQ. Maybe check your network connection?"),
    INCORRECT_PASSWORD("That password wasn't correct. Try again!")
}