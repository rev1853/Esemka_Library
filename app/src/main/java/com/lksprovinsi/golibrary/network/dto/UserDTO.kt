package com.lksprovinsi.golibrary.network.dto

import org.json.JSONObject

data class UserDTO(
    var email: String? = null,
    var name: String? = null
){
    companion object {
        fun fromJson(json: JSONObject): UserDTO{
            val user = UserDTO()
            if(json.has("Email")) user.email = json.getString("email")
            if(json.has("name")) user.name = json.getString("name")
            return user
        }
    }
}
