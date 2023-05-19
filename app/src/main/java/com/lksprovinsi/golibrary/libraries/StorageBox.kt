package com.lksprovinsi.golibrary.libraries

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor

class StorageBox private constructor(boxName: String, ctx: Context) {

    private val sharedPreferences: SharedPreferences = ctx.getSharedPreferences(boxName, Context.MODE_PRIVATE)

    fun edit(block: Editor.() -> Unit){
        val editor: Editor = sharedPreferences.edit()
        block(editor)
        editor.apply()
    }

    fun <T> get(key: String, type: Class<T>): T? {
        val data = sharedPreferences.all ?: return null
        return type.cast(data[key])
    }

    companion object {
        @Volatile
        private var instance: StorageBox? = null

        @Volatile
        private var instanceUser: StorageBox? = null

        fun initGlobal(ctx: Context): StorageBox{
            return instance ?: synchronized(this){
                instance ?: StorageBox("GoLibrary", ctx).also {
                    instance = it
                }
            }
        }

        fun initUser(email: String, ctx: Context): StorageBox{
            return instanceUser ?: synchronized(this){
                instanceUser ?: StorageBox("user-$email", ctx).also {
                    instanceUser = it
                }
            }
        }

        val global: StorageBox?
            get() {
                return instance
            }

        val user: StorageBox?
            get() {
                return instanceUser
            }
    }
}