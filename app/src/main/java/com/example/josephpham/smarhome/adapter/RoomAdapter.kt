package com.example.josephpham.smarhome.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.josephpham.smarhome.viewmodel.DeviceModelView
import com.example.josephpham.smarhome.model.Device
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ItemDeviceInRoomBinding

class RoomAdapter : BaseAdapter {
    var deviceList = ArrayList<Device>()
    var context: Context
    val id : String
    constructor(deviceList: ArrayList<Device>, context: Context, id: String){
        this.context = context
        this.deviceList = deviceList
        this.id = id
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var deviceInRoom = this.deviceList.get(position)
        var binding = DataBindingUtil.inflate<ItemDeviceInRoomBinding>(LayoutInflater.from(parent!!.context), R.layout.item_device_in_room, parent, false)
        var viewmodel = DeviceModelView(this.context, deviceInRoom, id)
        binding.viewModel = viewmodel

        return binding.root

    }

    override fun getItem(position: Int): Any {
        return deviceList.get(position)

    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return deviceList.size
    }
}

