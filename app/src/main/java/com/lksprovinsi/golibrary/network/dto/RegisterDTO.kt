package com.lksprovinsi.golibrary.network.dto

import org.json.JSONObject

data class RegisterDTO(var name: String, var password: String, var email: String){
    fun toJson(): String{
        val json = JSONObject()
        json.put("name", name)
        json.put("password", password)
        json.put("email", email)
        return json.toString()
    }
}
