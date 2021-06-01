package com.ivan.smack.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ivan.smack.controller.App
import com.ivan.smack.utils.*
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    fun registerUser(
        context: Context,
        mail: String,
        password: String,
        complete: (Boolean) -> Unit
    ) {
        val jsonBody = JSONObject()
        jsonBody.put("email", mail)
        jsonBody.put("password", password)
        val requestBody = jsonBody.toString()

        val registerRequest =
            object : StringRequest(Request.Method.POST, URL_REGISTER, Response.Listener {
                complete(true)
            }, Response.ErrorListener {
                Log.d("ERROR", "Could not register user: $it")
                complete(false)
            }) {
                override fun getBodyContentType(): String {
                    return "application/json; charset=utf-8"
                }

                override fun getBody(): ByteArray {
                    return requestBody.toByteArray()
                }
            }

        App.sharedPreferences.requestQueue.add(registerRequest)
    }

    fun login(context: Context, email: String, password: String, complete: (Boolean) -> Unit) {
        val body = JSONObject()
        body.put("email", email)
        body.put("password", password)
        val requestBody = body.toString()

        val loginRequest = object : JsonObjectRequest(
            Method.POST,
            URL_LOGIN,
            null,
            Response.Listener { response ->
                try {
                    App.sharedPreferences.userEmail = response.getString("user")
                    App.sharedPreferences.authToken = response.getString("token")
                    App.sharedPreferences.isLoggedIn = true
                    complete(true)
                } catch (e: JSONException) {
                    Log.d("JSON", "login(): ${e.localizedMessage}")
                    App.sharedPreferences.isLoggedIn = false
                    complete(false)
                }
            },
            Response.ErrorListener {
                Log.d("ERROR", "Could not login $it")
                App.sharedPreferences.isLoggedIn = false
                complete(false)
            }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        App.sharedPreferences.requestQueue.add(loginRequest)
    }

    fun createUser(
        context: Context,
        name: String,
        email: String,
        avatarName: String,
        avatarColor: String,
        complete: (Boolean) -> Unit
    ) {
        val body = JSONObject()
        body.put("name", name)
        body.put("email", email)
        body.put("avatarName", avatarName)
        body.put("avatarColor", avatarColor)
        val requestBody = body.toString()

        val createRequest = object : JsonObjectRequest(
            Method.POST,
            URL_CREATE_USER,
            null,
            Response.Listener { response ->
                try {
                    UserDataService.name = response.getString("name")
                    UserDataService.email = response.getString("email")
                    UserDataService.avatarName = response.getString("avatarName")
                    UserDataService.avatarColor = response.getString("avatarColor")
                    UserDataService.id = response.getString("_id")
                    complete(true)
                } catch (e: JSONException) {
                    Log.d("JSON", "EXC ${e.localizedMessage}")
                    complete(false)
                }
            },
            Response.ErrorListener { error ->
                Log.d("ERROR", "Could not add user: $error")
                complete(false)
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${App.sharedPreferences.authToken}"
                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(createRequest)
    }

    fun findUserByEmail(context: Context, complete: (Boolean) -> Unit) {

        val findUserRequest = object : JsonObjectRequest(
            Method.GET,
            "$URL_GET_USER${App.sharedPreferences.userEmail}",
            null,
            Response.Listener { response ->
                try {
                    UserDataService.name = response.getString("name")
                    UserDataService.email = response.getString("email")
                    UserDataService.avatarName = response.getString("avatarName")
                    UserDataService.avatarColor = response.getString("avatarColor")
                    UserDataService.id = response.getString("_id")

                    val userDataChange = Intent(BROADCAST_USER_DATA_CHANGE)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(userDataChange)

                    complete(true)
                } catch (e: JSONException) {
                    Log.d("JSON", "EXC: ${e.localizedMessage}")
                    complete(false)
                }
            },
            Response.ErrorListener { error ->
                Log.d("ERROR", "Could not find user.")
                complete(false)
            }){

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer ${App.sharedPreferences.authToken}"
                return headers
            }
        }

        App.sharedPreferences.requestQueue.add(findUserRequest)
    }
}