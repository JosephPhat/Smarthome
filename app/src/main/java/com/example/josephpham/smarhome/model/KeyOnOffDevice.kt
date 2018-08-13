package com.example.josephpham.smarhome.model

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import org.json.JSONObject

class KeyOnOffDevice {
    var key: ObservableField<String> = ObservableField()
    var status: ObservableBoolean = ObservableBoolean()
    constructor(key: String, status: Boolean){
        this.key.set(key)
        this.status.set(status)
    }
    companion object {
        fun parseJson(data: JSONObject): KeyOnOffDevice {
            val keystr = data.getString("key")
            val status = data.getBoolean("status")
            val key = KeyOnOffDevice(keystr, status)
            return key
        }
    }
}