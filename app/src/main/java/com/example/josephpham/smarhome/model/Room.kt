package com.example.josephpham.smarhome.model

import android.databinding.BaseObservable
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import org.json.JSONObject


class Room: BaseObservable{
    var id: ObservableField<String> = ObservableField()
    var room: ObservableField<String> = ObservableField()
    var img: ObservableField<String> = ObservableField()

    var listDevice: ObservableArrayList<Device> = ObservableArrayList()

    constructor(id: String, room: String, img: String){
        this.id.set(id)
        this.room.set(room)
        this.img.set(img)
    }
    constructor()
    companion object {
        fun parseJson(data: JSONObject): Room {
            val id: String = data.getString("_id")
            val name: String = data.getString("room_name")
            val img = data.getString("img")
            val room = Room(id, name, img)
            val jsonArray = data.getJSONArray("listDevice")
            val listDevice = ArrayList<Device>()
            for(i in 0.. jsonArray.length()-1){
                val jsonObject = jsonArray.getJSONObject(i)
                val device = Device.parseJson(jsonObject)
                listDevice.add(device)
                room.listDevice.add(device)
            }
            return room
        }
    }

}