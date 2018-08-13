package com.example.josephpham.smarhome.model

import android.databinding.ObservableDouble
import android.databinding.ObservableField
import android.databinding.ObservableInt
import org.json.JSONObject

class DeviceDetail {
    var id: ObservableField<String> = ObservableField()
    var name: ObservableField<String> = ObservableField()
    var img: ObservableField<String> = ObservableField()
    var description: ObservableField<String> = ObservableField()
    var price: ObservableDouble = ObservableDouble()
    var type : ObservableInt = ObservableInt()
     constructor(id: String, name: String, img: String, description: String, price: Double, type: Int){
         this.id.set(id)
         this.name.set(name)
         this.img.set(img)
         this.description.set(description)
         this.price.set(price)
         this.type.set(type)
     }
    companion object {
        fun parseJson(data : JSONObject): DeviceDetail {
            val device_id =  data.getString("_id")
            val device_name = data.getString("name")
            val img = data.getString("img")
            val description = data.getString("description")
            val price = data.getDouble("price")
            val type = data.getInt("type")
            val device = DeviceDetail(device_id, device_name, img, description, price, type)
            return device
        }

        fun json(deviceDetail : DeviceDetail) : JSONObject{
            val json =JSONObject()
            val id = deviceDetail.id.get()
            val name = deviceDetail.name.get()
            val img = deviceDetail.img.get()
            val description = deviceDetail.description.get()
            val price = deviceDetail.price.get()
            val type = deviceDetail.type.get()
            json.put("_id", id)
            json.put("name", name)
            json.put("img", img)
            json.put("description", description)
            json.put("price", price)
            json.put("type", type)
            return json
        }
    }

}