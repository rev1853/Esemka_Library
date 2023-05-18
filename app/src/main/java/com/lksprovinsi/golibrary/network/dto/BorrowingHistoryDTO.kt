package com.lksprovinsi.golibrary.network.dto

import org.json.JSONObject

data class BorrowingHistoryDTO(
    var id: String? = null,
    var userEmail: String? = null,
    var start: String? = null,
    var end: String? = null,
    var returnedAt: String? = null,
    var bookCount: Int? = null,
    var status: String? = null,
    var createdAt: String? = null,
    var bookBorrowings: List<BookBorrowingDTO>? = null
) {
    companion object{
        fun fromJson(json: JSONObject): BorrowingHistoryDTO{
            val data = BorrowingHistoryDTO()

            if(json.has("id")) data.id = json.getString("id")
            if(json.has("userEmail")) data.id = json.getString("userEmail")
            if(json.has("start")) data.start = json.getString("start")
            if(json.has("end")) data.end = json.getString("end")
            if(json.has("returnedAt")) data.returnedAt = json.getString("returnedAt")
            if(json.has("bookCount")) data.bookCount = json.getInt("bookCount")
            if(json.has("status")) data.status = json.getString("status")
            if(json.has("createdAt")) data.status = json.getString("createdAt")
            if(json.has("bookBorrowings")){
                val jsonArray = json.getJSONArray("bookBorrowings")
                val bookList = mutableListOf<BookBorrowingDTO>()
                for(i in 0 until jsonArray.length()){
                    bookList.add(BookBorrowingDTO.fromJson(jsonArray.getJSONObject(i)))
                }
                data.bookBorrowings = bookList
            }

            return data
        }
    }
}