package com.lksprovinsi.golibrary.network.dto

import org.json.JSONArray
import org.json.JSONObject

data class BookBorrowingDTO(
    var borrowingHistoryId: String?  = null,
    var bookId: String? = null,
    var book: BookDTO? = null
){
    companion object{
        fun fromJson(json: JSONObject): BookBorrowingDTO{
            return BookBorrowingDTO().apply {
                if(json.has("borrowingHistoryId")) borrowingHistoryId = json.getString("borrowingHistoryId")
                if(json.has("bookId")) bookId = json.getString("bookId")
                if(json.has("book")){
                    book = BookDTO.fromJson(json.getJSONObject("book"))
                }
            }
        }
    }
}
