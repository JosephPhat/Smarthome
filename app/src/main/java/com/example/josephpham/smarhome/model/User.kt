package com.example.josephpham.smarhome.model

import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ObservableInt
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class User {
    var id: ObservableField<String> = ObservableField<String>()
    var email: ObservableField<String> = ObservableField<String>()
    var password: ObservableField<String> = ObservableField<String>()
    var street: ObservableField<String> = ObservableField<String>()
    var district: ObservableField<String> = ObservableField<String>()
    var city: ObservableField<String> = ObservableField<String>()
    var postcode: ObservableInt = ObservableInt()
    var phonenumber: ObservableField<String> = ObservableField<String>()
    var homephone: ObservableField<String> = ObservableField<String>()
    var dob: ObservableField<Date> = ObservableField<Date>()
    var type: ObservableField<String> = ObservableField<String>()
    var status: ObservableField<String> = ObservableField<String>()
    var startdateregister: ObservableField<Date> = ObservableField<Date>()
    var name: ObservableField<String> = ObservableField<String>()
    var img: ObservableField<String> = ObservableField<String>()
    var listRoom: ObservableArrayList<Room> = ObservableArrayList()
    var listMode: ObservableArrayList<Mode> = ObservableArrayList()

    constructor(id: String, email: String, street: String, district: String, city: String, postcode: Int, phonenumber: String,
                homephone: String, dob: Date, type: String, status: String, startdateregister: Date, name: String, img: String) {
        this.id.set(id)
        this.email.set(email)
        this.street.set(street)
        this.district.set(district)
        this.city.set(city)
        this.postcode.set(postcode)
        this.phonenumber.set(phonenumber)
        this.homephone.set(homephone)
        this.dob.set(dob)
        this.type.set(type)
        this.status.set(status)
        this.startdateregister.set(startdateregister)
        this.name.set(name)
        this.img.set(img)
    }
    constructor()

    companion object {
        fun parseJson(data: JSONObject): User {
            var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm")
            val id: String = data.getString("_id")
            val email: String = data.getString("email")
            val name: String = data.getString("name")
            val street: String = data.getString("street")
            val district: String = data.getString("district")
            val city: String = data.getString("city")
            val postcode: Int = data.getInt("postcode")
            val phonenumber: String = data.getString("phonenumber")
            val homephone: String = data.getString("homephone")
            val dob: Date = simpleDateFormat.parse(data.getString("dob"))
            val type: String = data.getString("type")
            val status: String = data.getString("status")
            val startdateregister: Date = simpleDateFormat.parse(data.getString("startdateregister"))
            val img: String = data.getString("img")

            var user = User(id, email, street, district, city, postcode, phonenumber, homephone, dob, type, status, startdateregister, name, img)

            return user
        }
    }

}