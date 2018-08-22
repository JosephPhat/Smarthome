package com.example.josephpham.smarhome.viewmodel

import android.content.Context
import android.databinding.BaseObservable
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.model.Device
import org.json.JSONObject

class DeviceModelView(var context: Context, var device: Device, var id: String) : BaseObservable() {
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
    fun keyOn(): String{
        return this.device.keyOnOff.get()!!.on.toString()
    }
    fun keyOff(): String{
        return this.device.keyOnOff.get()!!.on.toString()
    }

    fun loadimg(): Boolean {
        if (device.deviceDetail.get()!!.type.get() == 1) {//id_device = 0 laf thiet bi bat tac
            if (device.status.get() == false) {
                return false
            } else {
                return true
            }
        }
        return true
    }

    fun onItemClick() {
        val json = JSONObject()
        json.put("_id", this.device.id.get())
        mSocket.emit("control-device", json)
        val json1 = JSONObject()
        json1.put("id_room", this.id)


    }

    fun pupoMenuClick(view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu_item_device_in_room, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.delete -> {
                    mSocket.emit("client_send_remove_device_in_room", this.device.id.get().toString())
                    val json = JSONObject()
                    json.put("id_room", this.id)
                    mSocket.emit("client_send_device_in_room", json)

                }
            }

            true
        })
        popupMenu.show()
    }
}