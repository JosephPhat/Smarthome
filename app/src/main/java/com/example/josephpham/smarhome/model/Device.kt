package com.example.josephpham.smarhome.model

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.util.Log
import org.json.JSONObject


class Device {
    var id: ObservableField<String> = ObservableField()
    var deviceDetail: ObservableField<DeviceDetail> = ObservableField()
    var device_name: ObservableField<String> = ObservableField()
    var status: ObservableBoolean = ObservableBoolean()
    var keyOnOff: ObservableField<KeyOnOffDevice> = ObservableField()
    var selected: ObservableBoolean = ObservableBoolean()

    constructor(id: String, deviceDetail: DeviceDetail, device_name: String, status: Boolean, key: KeyOnOffDevice) {
        this.id.set(id)
        this.deviceDetail.set(deviceDetail)
        this.device_name.set(device_name)
        this.status.set(status)
        this.keyOnOff.set(key)
    }

    constructor(id: String, deviceDetail: DeviceDetail, device_name: String, status: Boolean) {
        this.id.set(id)
        this.deviceDetail.set(deviceDetail)
        this.device_name.set(device_name)
        this.status.set(status)
    }

    companion object {
        fun parseJson(dataRoom: JSONObject): Device {
            val id = dataRoom.getString("_id")
            val name = dataRoom.getString("device_name")
            val status = dataRoom.getBoolean("status")
            // deviceDetail
            val deviceJson = dataRoom.getJSONObject("device")
            val device = DeviceDetail.parseJson(deviceJson)
            if (!dataRoom.isNull("keyOnOff")) {
                val keyJson = dataRoom.getJSONObject("keyOnOff")
                val key = KeyOnOffDevice.parseJson(keyJson)
                val deviceInRoom = Device(id, device, name, status, key)
                return deviceInRoom
            } else {
                val deviceInRoom = Device(id, device, name, status)
                return deviceInRoom
            }
        }


        fun json(device: Device): JSONObject {
            val json = JSONObject()
            val id = device.id.get()
            val device_name = device.device_name.get()
            val status = device.status.get()
            val deviceDetail = device.deviceDetail.get()
            val jsonDeviceDetail = DeviceDetail.json(deviceDetail!!)
            json.put("_id", id)
            json.put("device_name", device_name)
            json.put("status", status)
//            json.put("device",jsonDeviceDetail)
            return json

        }
    }
}
