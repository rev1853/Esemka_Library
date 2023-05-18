package com.lksprovinsi.golibrary.libraries

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import com.lksprovinsi.golibrary.R
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageHelper {
    companion object{
        fun loadImage(url: String, imageView: ImageView){
            val fetcher = ImageFetcher(imageView.context, url)

            fetcher.setOnResponse {
                if(it != null){
                    val bitmap = BitmapFactory.decodeStream(it)
                    imageView.setImageBitmap(bitmap)
                }else{
                    imageView.setImageResource(R.drawable.default_image_profile)
                }
            }

            fetcher.execute()
        }
    }

    class ImageFetcher(val ctx: Context, private val url: String): AsyncTask<String, Void, InputStream?>() {

        private var onResponse: OnResponse? = null

        fun setOnResponse(onResponse: OnResponse){
            this.onResponse = onResponse
        }

        override fun doInBackground(vararg params: String?): InputStream? {
            val conn: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
            conn.requestMethod = "GET"

            val token: String = StorageBox(ctx).get("token", String::class.java)!!
            conn.setRequestProperty("Authorization", token)

            val resCode = conn.responseCode

             return if(resCode == 200) conn.inputStream else null
        }

        override fun onPostExecute(result: InputStream?) {
            onResponse?.handle(result)
        }

        fun interface OnResponse {
            fun handle(inputStream: InputStream?)
        }
    }
}