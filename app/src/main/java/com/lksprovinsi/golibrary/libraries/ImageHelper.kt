package com.lksprovinsi.golibrary.libraries

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import com.lksprovinsi.golibrary.R
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageHelper {
    companion object{
        fun loadImage(url: String, imageView: ImageView){
            val fetcher = ImageFetcher(url, imageView)
            fetcher.execute()
        }
    }

    class ImageFetcher(private val url: String, private val image: ImageView): AsyncTask<String, Void, Unit>() {

        override fun doInBackground(vararg params: String?) {
            val conn: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"

            val token: String = StorageBox.global!!.get("token", String::class.java)!!
            conn.setRequestProperty("Authorization", "Bearer $token")

            val resCode = conn.responseCode

            if(resCode == 200){
                val bitmap = BitmapFactory.decodeStream(conn.inputStream)
                image.setImageBitmap(bitmap)
            }else{
                image.setImageResource(R.drawable.default_image_profile)
            }
        }
    }
}