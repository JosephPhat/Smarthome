package com.example.josephpham.smarhome.viewmodel

import android.content.Context
import android.databinding.BaseObservable
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.model.Device

class DeviceModelView_(var context: Context, var device: Device) : BaseObservable() {
    var mSocket = Connect.connect()

    fun img(): String {
        return this.device.deviceDetail.get()!!.img.get().toString()
    }

    fun name(): String {
        return this.device.device_name.get().toString()
    }

    fun typeDevice(): String {
        return this.device.deviceDetail.get()!!.name.get().toString()
    }

    fun price(): String {
        return this.device.deviceDetail.get()!!.price.get().toString()
    }

    fun keyOn(): String {
        if (this.device.keyOnOff.get() == null) {
            return ""

        } else {
            return this.device.keyOnOff.get()!!.on.get().toString()
        }

    }

    fun keyOff(): String {
        if (this.device.keyOnOff.get() == null) {
            return ""
        } else {
            return this.device.keyOnOff.get()!!.off.get().toString()
        }

    }
}