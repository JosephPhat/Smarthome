package com.example.josephpham.smarhome.model

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import org.json.JSONArray
import org.json.JSONObject

class Mode {
    var id: ObservableField<String> = ObservableField()
    var mode_name: ObservableField<String> = ObservableField()
    var status: ObservableBoolean = ObservableBoolean()
    var starttime: ObservableInt = ObservableInt()
    var stoptime: ObservableInt = ObservableInt()
    var circle: ObservableArrayList<Int> = ObservableArrayList()
    var listModeDetail: ObservableArrayList<ModeDetail> = ObservableArrayList()
    constructor(id: String, mode_name: String, status: Boolean, starttime: Int, stoptime: Int) {
        this.id.set(id)
        this.mode_name.set(mode_name)
        this.status.set(status)
        this.starttime.set(starttime)
        this.stoptime.set(stoptime)
    }

    companion object {
        fun parseJson(data: JSONObject): Mode {
            val id_Mode = data.getString("_id")
            val modename = data.getString("mode_name")
            val status = data.getBoolean("status")
            val starttime = data.getInt("starttime")
            val stoptime = data.getInt("stoptime")
            val circle: JSONArray = data.getJSONArray("circle")
            val modeDetail: JSONArray = data.getJSONArray("modedetail")
            val mode = Mode(id_Mode, modename, status, starttime, stoptime)
            for (j in 0..circle.length() - 1) {
                mode.circle.add(circle.getInt(j))
            }
            for (j in 0..modeDetail.length() - 1) {
                val jsonObject = modeDetail.getJSONObject(j)
                val modeDetailitem = ModeDetail.parseJson(jsonObject)
                mode.listModeDetail.add(modeDetailitem)
            }
            return mode

        }
    }
    fun time(): String{
        val startime = "" + this.starttime.get()/3600 + ":" + this.starttime.get()%3600/60
        val offtime = "" + this.stoptime.get()/3600 + ":" + this.stoptime.get()%3600/60
        return startime + "-" + offtime
    }
    fun circleString(): String{
        val s = StringBuffer()
        for (i in 0..this.circle.size-1){
            s.append(this.circle.get(i))
            s.append("-")
        }
        return s.toString()

    }
}