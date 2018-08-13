package com.example.josephpham.smarhome.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.josephpham.smarhome.viewmodel.RoomViewModel
import com.example.josephpham.smarhome.model.Room
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ItemRoomBinding


class FragmentRoomAdapter : BaseAdapter {

    var roomsList = ArrayList<Room>()
    var context: Context

    constructor(context: Context, roomList: ArrayList<Room>){
        this.context = context
        this.roomsList = roomList
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val room = this.roomsList[position]

        var binding: ItemRoomBinding = DataBindingUtil.inflate(LayoutInflater.from(parent!!.context), R.layout.item_room, parent, false)
        var viewmodel = RoomViewModel(this.context, room)
        binding.itemRoom = viewmodel

        return binding.root
    }

    override fun getItem(position: Int): Any {
        return roomsList[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return roomsList.size
    }
}