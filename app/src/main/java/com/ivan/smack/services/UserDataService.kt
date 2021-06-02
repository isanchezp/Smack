package com.ivan.smack.services

import android.graphics.Color
import com.ivan.smack.controller.App

object UserDataService {

    var id = ""
    var avatarColor = ""
    var avatarName = ""
    var email = ""
    var name = ""

    /**
     * [0.16176261673, 0.6565125632, 0.6327676372, 1]
     * [r, g, b, transparency] we dont care the transparency
     */
    fun returnAvatarColor(color: String): Int {
        val rgbColors = mutableListOf<Int>()
        color.split(",")
            .forEach {
                rgbColors.add((it.toDouble() * 255).toInt())
            }

        if (rgbColors.count() >= 3) {
            return Color.rgb(rgbColors[0], rgbColors[1], rgbColors[2])
        } else {
            return Color.rgb(0,0,0)
        }
    }

    fun logout() {
        id = ""
        avatarColor = ""
        avatarName = ""
        email = ""
        name = ""
        App.sharedPreferences.authToken = ""
        App.sharedPreferences.userEmail = ""
        App.sharedPreferences.isLoggedIn = false
        MessageService.clearMessages()
        MessageService.clearChannels()
    }
}