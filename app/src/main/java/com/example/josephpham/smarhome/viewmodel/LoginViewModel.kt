package com.example.josephpham.smarhome.viewmodel

import android.content.Context
import android.content.Intent
import com.example.josephpham.smarhome.activity.RegisterUserActivity


class LoginViewModel {
    var context: Context

    constructor(context: Context) {
        this.context = context
    }

    fun forgotPass() {
//        val intent = Intent(context, RegisterUserActivity::class.java)
//        context.startActivity(intent)
    }

    fun register() {
        val intent = Intent(context, RegisterUserActivity::class.java)
        context.startActivity(intent)
    }

}