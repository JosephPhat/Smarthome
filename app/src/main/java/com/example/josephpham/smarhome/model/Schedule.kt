package com.example.josephpham.smarhome.model

import android.databinding.ObservableInt
import org.json.JSONObject

class  Schedule{
    var ontime: ObservableInt = ObservableInt()
    var offtime:  ObservableInt = ObservableInt()
    constructor(ontime: Int, offtime: Int){
        this.ontime.set(ontime)
        this.offtime.set(offtime)
    }
    constructor()
    companion object {
        fun parseJson(data: JSONObject): Schedule {
            val onTime = data.getInt("ontime")
            val ofTime = data.getInt("offtime")
            val schedule = Schedule(onTime, ofTime)
            return schedule
        }
        fun json(schedule: Schedule): JSONObject{
            val jsonObject = JSONObject()
            val ontime = schedule.ontime.get()
            val offtime = schedule.offtime.get()
            jsonObject.put("ontime", ontime)
            jsonObject.put("offtime", offtime)
            return jsonObject
        }
    }
}