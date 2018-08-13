package com.example.josephpham.smarhome.viewmodel

import android.content.Context
import android.content.Intent
import android.databinding.BaseObservable
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.activity.SettingDeviceActivity
import com.example.josephpham.smarhome.model.Device

class DeviceModelView(var context: Context, var device: Device) : BaseObservable() {
    fun img(): String{
        return this.device.deviceDetail.get()!!.img.get().toString()
    }
    fun name(): String{
        return this.device.device_name.get().toString()
    }
    fun typeDevice(): String{
        return this.device.deviceDetail.get()!!.name.get().toString()
    }
    fun price(): String{
        return this.device.deviceDetail.get()!!.price.get().toString()
    }
    fun loadimg(): Boolean{
        if (device.deviceDetail.get()!!.type.get() == 1) {//id_device = 0 laf thiet bi bat tac
            if (device.status.get() == false) {
                return false
            } else {
                return true
            }
        }
        return true
    }
    fun onItemClick(){

    }
    fun pupoMenuClick(view: View){
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu_item_device, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.delete -> {
                }
                R.id.update -> {
                }
                R.id.settings -> {
                    val intent: Intent = Intent(context, SettingDeviceActivity::class.java)
                    intent.putExtra("id", device.id.get())
                    context.startActivity(intent)
                }
            }

            true
        })
        popupMenu.show()
    }
}