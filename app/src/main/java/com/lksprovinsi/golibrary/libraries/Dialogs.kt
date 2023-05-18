package com.lksprovinsi.golibrary.libraries

import android.app.ProgressDialog
import android.content.Context

class Dialogs {
    companion object{
        fun loading(ctx: Context): ProgressDialog{
            val dialog = ProgressDialog(ctx)
            dialog.setCancelable(false)
            dialog.setMessage("Loading...")
            return dialog
        }
    }
}