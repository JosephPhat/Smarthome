package com.example.josephpham.smarhome.viewmodel

import android.databinding.BaseObservable
import com.example.josephpham.smarhome.model.Room
import android.content.Context
import android.content.Intent
import android.support.v7.widget.PopupMenu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.example.josephpham.smarhome.activity.RoomActivity
import com.example.josephpham.smarhome.activity.UpdateRoomActivity
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.database.DatabaseHandler
import org.json.JSONObject


class RoomViewModel : BaseObservable{

    var room: Room
    var mContext : Context

    constructor(context: Context, room: Room){
        this.room = room
        this.mContext = context
    }
    fun id(): String{
        return this.room.id.get().toString()
    }
    fun name(): String{
        return this.room.room.get().toString()
    }
    fun img(): String{
        return this.room.img.get().toString()
    }
    fun error(): Boolean{
        if (name().isEmpty()){
            return true
        }else {
            return false
        }
    }
    fun onClick(){
        Toast.makeText(mContext, name(), Toast.LENGTH_LONG).show()
        val intent = Intent(mContext, RoomActivity::class.java)
        intent.putExtra("id_room", id())
        intent.putExtra("img", img())
        mContext.startActivity(intent)
    }
    fun pupoMenuClick(view: View){
        val popupMenu = PopupMenu(mContext, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu_item_room, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.delete -> {
                    var token = DatabaseHandler(mContext).readData()
                    var mSocket = Connect.connect()
                    var json = JSONObject()
                    json.put("token", token)
                    var jsonsup = JSONObject()
                    jsonsup.put("_id", id())
                    jsonsup.put("isDeleteDevice", 0)
                    json.put("data", jsonsup)
                    mSocket.emit("client_send_delete_room", json)
                    mSocket.emit("client_send_room")
                }
                R.id.update -> {
                    val intent: Intent = Intent(mContext, UpdateRoomActivity::class.java)
                    intent.putExtra("id_room",  id())
                    mContext.startActivity(intent)
                }
            }

            true

        })
        popupMenu.show()
    }
}