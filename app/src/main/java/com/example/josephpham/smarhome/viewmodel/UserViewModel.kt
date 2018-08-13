package com.example.josephpham.smarhome.viewmodel

import android.content.Context
import android.databinding.BaseObservable
import com.example.josephpham.smarhome.model.User
import java.text.SimpleDateFormat

class UserViewModel: BaseObservable {
    var user: User
    var context: Context

    constructor(context: Context,user: User){
        this.user = user
        this.context = context
    }
    fun name(): String{
        return this.user.name.get().toString()
    }
    fun email(): String{
        return this.user.email.get().toString()
    }
    fun img(): String{
        return this.user.img.get().toString()
    }
    fun address(): String{
        return this.user.street.get().toString() + "/ " + this.user.district.get().toString() + "/ " + this.user.city.get().toString() + "/ " + this.user.postcode.get().toString()
    }
    fun dob(): String{
        var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        return simpleDateFormat.format(this.user.dob.get()).toString()

    }
    fun phoneNumber(): String{
        return this.user.phonenumber.get().toString()
    }
    fun homePhone(): String{
        return this.user.homephone.get().toString()
    }
}