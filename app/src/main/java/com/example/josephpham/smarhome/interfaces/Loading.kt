package com.example.josephpham.smarhome.interfaces

import android.app.ProgressDialog
import android.content.Context

class Loading {
    companion object {
        var dialog: ProgressDialog? = null

        fun loading(context: Context){
            dialog = ProgressDialog(context)
            dialog!!.setMessage("Please wait")
            dialog!!.setTitle("Loading")
            dialog!!.setCancelable(false)
            dialog!!.isIndeterminate=true
            dialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)

            dialog!!.show()

        }
        fun dismiss(){
            dialog!!.dismiss()
        }
    }
}