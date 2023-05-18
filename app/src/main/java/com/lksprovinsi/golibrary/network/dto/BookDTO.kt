package com.lksprovinsi.golibrary.network.dto

import org.json.JSONObject

data class BookDTO(
    val id: String,
    var name: String? = null,
    var authors: String? = null,
    var isbn: String? = null,
    var publisher: String? = null,
    var available: Int = 0,
    var description: String? = null
    ) {
    fun toJson(): String{
        val json = JSONObject()
        json.put("id", id)
        json.put("name", name)
        json.put("authors", authors)
        json.put("isbn", isbn)
        json.put("publisher", publisher)
        json.put("available", available)
        json.put("description", description)

        return json.toString()
    }

    companion object {
        fun fromJson(json: JSONObject): BookDTO{
            val book = BookDTO(json["id"] as String)
            if(json.has("name")) book.name = json["name"] as String
            if(json.has("authors")) book.authors = json["authors"] as String
            if(json.has("isbn")) book.isbn = json["isbn"] as String
            if(json.has("publisher")) book.publisher = json["publisher"] as String
            if(json.has("available")) book.available = json["available"] as Int
            if(json.has("description")) book.description = json["description"] as String
            return book
        }
    }
}