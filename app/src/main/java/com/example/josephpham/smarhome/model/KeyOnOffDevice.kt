package com.example.josephpham.smarhome.model

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Log
import org.json.JSONObject

class KeyOnOffDevice {
    var on: ObservableField<String> = ObservableField()
    var off: ObservableField<String> = ObservableField()

    constructor(on: String, off: String) {
        this.on.set(on)
        this.off.set(off)
    }

    companion object {
        fun parseJson(data: JSONObject): KeyOnOffDevice {
            val on = data.getString("turnon")
            val off = data.getString("turnoff")
            return KeyOnOffDevice(on,off)
        }
    }
}