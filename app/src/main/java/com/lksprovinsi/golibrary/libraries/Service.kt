package com.lksprovinsi.golibrary.libraries

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.util.Log
import com.lksprovinsi.golibrary.network.Services
import com.lksprovinsi.golibrary.network.dto.LoginDTO
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Service<B>(
    private val ctx: Context,
    private val url: String,
    private val method: String,
    private val returnType: Class<B>
    ) : AsyncTask<Void, Void, String>() {

    private var onPrepare: OnPrepare? = null
    private var onResponse: OnResponse<B>? = null
    private var onStart: OnStart? = null

    private lateinit var conn: HttpURLConnection

    private val email: String?
        get() {
            return StorageBox(ctx).get("email", String::class.java)
        }

    private val password: String?
        get() {
            return StorageBox(ctx).get("password", String::class.java)
        }

    private val token: String?
        get() {
            return StorageBox(ctx).get("token", String::class.java)
        }

    fun setOnResponse(onResponse: OnResponse<B>){
        this.onResponse = onResponse
    }

    fun setOnPrepare(onPrepare: OnPrepare){
        this.onPrepare = onPrepare
    }

    fun setOnStart(onStart: OnStart){
        this.onStart = onStart
    }

    private fun refreshToken(){
        val service: Service<JSONObject> = Services(ctx).login(LoginDTO(email!!, password!!))

        service.setOnResponse { res, conn ->
            if(conn.responseCode == 200){
                val box = StorageBox(ctx)
                val editor: SharedPreferences.Editor = box.editor
                editor.putString("token", res!!.getString("token"))
                editor.commit()

                reExecute()
            }
        }

        service.execute()
    }

    private fun reExecute(){
        val service = Service(ctx, url, method, returnType)
        if(onStart != null) service.setOnStart(onStart!!)
        if(onPrepare != null) service.setOnPrepare(onPrepare!!)
        if(onResponse != null) service.setOnResponse(onResponse!!)
        service.execute()
    }

    private fun prepareConnection(): HttpURLConnection {
        val conn: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
        conn.requestMethod = method

        if(method == "POST"){
            conn.setRequestProperty("Content-Type", "application/json")
        }

        if(token != null){
            conn.setRequestProperty("Authorization", "Bearer $token")
        }

        return conn
    }

    override fun onPreExecute() {
        onStart?.handle()
    }

    override fun doInBackground(vararg params: Void): String {
        conn = prepareConnection()
        conn = onPrepare?.handle(conn) ?: conn

        val inputStream: InputStream = if(conn.responseCode == 200) conn.inputStream else conn.errorStream
        val bufferReader = BufferedReader(InputStreamReader(inputStream))

        val responseString: StringBuilder = StringBuilder()
        var line: String? = bufferReader.readLine()

        while (line != null){
            responseString.append(line)
            line = bufferReader.readLine()
        }

        return responseString.toString()
    }

    override fun onPostExecute(result: String?) {
        Log.d("NETWORK", "url: $url")
        Log.d("NETWORK", "result: $result")

        if(conn.responseCode == 401 && email != null && password != null){
            refreshToken()
            return
        }

        var response: B? = null
        try{
            response = result?.let {
                when (returnType) {
                    JSONObject::class.java -> JSONObject(it)
                    JSONArray::class.java -> JSONArray(it)
                    else -> returnType.cast(it)
                }
            } as? B?
        }catch (_: Exception){

        }
        onResponse?.handle(response, conn)
    }

    fun interface OnPrepare {
        fun handle(conn: HttpURLConnection): HttpURLConnection
    }

    fun interface OnResponse<A> {
        fun handle(response: A?, conn: HttpURLConnection)
    }

    fun interface OnStart {
        fun handle()
    }

    fun interface OnPrepareReturnValue {
        fun handle(conn: HttpURLConnection)
    }
}