package com.lksprovinsi.golibrary.libraries

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

class StorageBox(ctx: Context) {

    companion object {
        var BOX_NAME: String = "GoLibrary"
    }

    private var sharedPreferences: SharedPreferences = ctx.getSharedPreferences(BOX_NAME, Context.MODE_PRIVATE)

    val editor: Editor
        get() = sharedPreferences.edit()

    fun <T> get(key: String, type: Class<T>): T? {
        val data = sharedPreferences.all
        return type.cast(data[key])
    }
}