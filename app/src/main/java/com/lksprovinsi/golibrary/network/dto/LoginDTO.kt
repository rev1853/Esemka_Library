package com.lksprovinsi.golibrary.network.dto

import org.json.JSONObject

data class LoginDTO(val email: String, val password: String){
    fun toJson(): String {
        val json = JSONObject()
        json.put("email", email)
        json.put("password", password)
        return json.toString()
    }
}
