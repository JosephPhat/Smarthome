package com.example.josephpham.smarhome.model

import android.databinding.ObservableField
import org.json.JSONObject

class ModeDetail{
    var id: ObservableField<String> = ObservableField()
    var device: Device? = null
    var schedule: Schedule? = null
    constructor(id: String, device: Device, schedule: Schedule){
        this.id.set(id)
        this.device = device
        this.schedule = schedule
    }
    constructor(device: Device){
        this.device = device
    }

    companion object {
        fun parseJson(data: JSONObject): ModeDetail {
//            val id = data.getString("id_")
            val device = Device.parseJson(data.getJSONObject("device"))
            val schedule = Schedule.parseJson(data.getJSONObject("schedule"))
            val modeDetail = ModeDetail("123", device, schedule)
            return modeDetail
        }
        fun json(modeDetail: ModeDetail): JSONObject{
            val json = JSONObject()
            val device = modeDetail.device
            json.put("device", device)
            val schedule = modeDetail.schedule
            json.put("schedule", schedule)
            return json
        }
    }
}
