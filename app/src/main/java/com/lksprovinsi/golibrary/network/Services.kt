package com.lksprovinsi.golibrary.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import com.lksprovinsi.golibrary.R
import com.lksprovinsi.golibrary.libraries.Service
import com.lksprovinsi.golibrary.libraries.StorageBox
import com.lksprovinsi.golibrary.network.dto.BorrowingDTO
import com.lksprovinsi.golibrary.network.dto.LoginDTO
import com.lksprovinsi.golibrary.network.dto.RegisterDTO
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class Services(private val ctx: Context) {
    companion object {
        const val BASE_URL: String = "http://10.0.2.2:5000/api"
    }

    fun register(data: RegisterDTO): Service<JSONObject> {
        val service = Service(
            ctx,
            "$BASE_URL/users",
            "POST",
            JSONObject::class.java
        )
        service.setOnPrepare {
            val outputStream: OutputStream = it.outputStream
            outputStream.write(data.toJson().toByteArray())
            outputStream.flush()
            outputStream.close()

            it
        }
        return service
    }

    fun login(data: LoginDTO): Service<JSONObject>{
        val service = Service(
            ctx,
            "$BASE_URL/auth",
            "POST",
            JSONObject::class.java
        )
        service.setOnPrepare {
            val outputStream: OutputStream = it.outputStream
            outputStream.write(data.toJson().toByteArray())
            outputStream.flush()
            outputStream.close()

            it
        }
        return service
    }

    fun getBooks(search: String = ""): Service<JSONArray>{
        val service = Service(
            ctx,
            "$BASE_URL/book?searchText=$search",
            "GET",
            JSONArray::class.java
        )
        return service
    }

    fun findBook(id: String): Service<JSONObject>{
        val service = Service(
            ctx,
            "$BASE_URL/book/$id",
            "GET",
            JSONObject::class.java
        )
        return service
    }

    fun borrowing(data: BorrowingDTO): Service<JSONObject>{
        val service = Service(
            ctx,
            "$BASE_URL/borrowing",
            "POST",
            JSONObject::class.java
        )
        service.setOnPrepare {
            val outputStream: OutputStream = it.outputStream
            outputStream.write(data.toJson().toByteArray())
            outputStream.flush()
            outputStream.close()

            it
        }
        return service
    }

    fun borrowingHistories(): Service<JSONArray>{
        val service = Service(
            ctx,
            "$BASE_URL/borrowing",
            "GET",
            JSONArray::class.java
        )
        return service
    }

    fun profile(): Service<JSONObject> {
        val service = Service(
            ctx,
            "$BASE_URL/user/me",
            "GET",
            JSONObject::class.java
        )
        return service
    }

    fun historyDetail(id: String): Service<JSONObject> {
        val service = Service(
            ctx,
            "$BASE_URL/borrowing/$id",
            "GET",
            JSONObject::class.java
        )
        return service
    }

    fun profilePhoto(imageView: ImageView): Service<String>{
        val service = Service(
            ctx,
            "$BASE_URL/user/me/photo",
            "GET",
            String::class.java
        )

        service.setOnResponse{res, conn ->
            if(conn.responseCode == 200){
                val bitmap: Bitmap = BitmapFactory.decodeStream(conn.inputStream)
                imageView.setImageBitmap(bitmap)
            }else{
                imageView.setImageResource(R.drawable.default_image_profile)
            }
        }

        return service
    }
}