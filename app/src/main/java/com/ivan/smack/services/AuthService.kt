package com.ivan.smack.services

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ivan.smack.utils.URL_LOGIN
import com.ivan.smack.utils.URL_REGISTER
import org.json.JSONException
import org.json.JSONObject

object AuthService {

    var isLoggedIn = false
    var userMail = ""
    var authToken = ""

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

        Volley.newRequestQueue(context).add(registerRequest)
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
                    userMail = response.getString("user")
                    authToken = response.getString("token")
                    isLoggedIn = true
                    complete(true)
                } catch (e: JSONException) {
                    Log.d("JSON", "login(): ${e.localizedMessage}")
                    isLoggedIn = false
                    complete(false)
                }
            },
            Response.ErrorListener {
                Log.d("ERROR", "Could not login $it")
                isLoggedIn = false
                complete(false)
            }) {

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }
        }

        Volley.newRequestQueue(context).add(loginRequest)
    }
}