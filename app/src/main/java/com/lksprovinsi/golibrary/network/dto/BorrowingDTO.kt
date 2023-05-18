package com.lksprovinsi.golibrary.network.dto

import org.json.JSONArray
import org.json.JSONObject

data class BorrowingDTO(val start: String? = null, val end: String? = null, val bookIds: List<String>? = null) {
    fun toJson(): String{
        val json = JSONObject()
        json.put("start", start)
        json.put("end", end)

        val bookIdsJson = JSONArray(bookIds)
        json.put("bookIds", bookIdsJson)

        return json.toString()
    }
}