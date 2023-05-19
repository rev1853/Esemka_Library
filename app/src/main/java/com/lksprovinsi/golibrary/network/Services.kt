package com.lksprovinsi.golibrary.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import java.io.DataOutput
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection

class Services() {
    companion object {
        const val BASE_URL: String = "http://10.0.2.2:5000/api"

        fun register(data: RegisterDTO): Service<JSONObject> {
            val service = Service(
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
                "$BASE_URL/book?searchText=$search",
                "GET",
                JSONArray::class.java
            )
            return service
        }

        fun findBook(id: String): Service<JSONObject>{
            val service = Service(
                "$BASE_URL/book/$id",
                "GET",
                JSONObject::class.java
            )
            return service
        }

        fun borrowing(data: BorrowingDTO): Service<JSONObject>{
            val service = Service(
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
                "$BASE_URL/borrowing",
                "GET",
                JSONArray::class.java
            )
            return service
        }

        fun profile(): Service<JSONObject> {
            val service = Service(
                "$BASE_URL/user/me",
                "GET",
                JSONObject::class.java
            )
            return service
        }

        fun historyDetail(id: String): Service<JSONObject> {
            val service = Service(
                "$BASE_URL/borrowing/$id",
                "GET",
                JSONObject::class.java
            )
            return service
        }

        fun uploadPhoto(fileName: String, fileType: String, file: File): Service<JSONObject> {
            val service = Service(
                "$BASE_URL/user/me/photo",
                "POST",
                JSONObject::class.java
            )

            service.setOnPrepare{
                val line = "\r\n"
                val boundary = "*****"

                it.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
                it.doOutput = true
                it.doInput = true
                it.useCaches = false

                DataOutputStream(it.outputStream).use { output ->
                    output.writeBytes("--$boundary$line")
                    output.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"$fileName\"$line")
                    output.writeBytes("Content-Type: application/octet-stream$line$line")

                    FileInputStream(file).use { input ->
                        val buffer = ByteArray(1024)
                        var byte: Int
                        while (input.read(buffer).also { b -> byte = b } != -1) {
                            output.write(buffer, 0, byte)
                        }
                    }
                    output.writeBytes("$line--$boundary--$line")
                    output.flush()
                }

                it
            }

            return service
        }
    }
}