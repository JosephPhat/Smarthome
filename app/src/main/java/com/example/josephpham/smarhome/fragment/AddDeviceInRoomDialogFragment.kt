package com.example.josephpham.smarhome.fragment

import android.annotation.SuppressLint
import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.adapter.AddDeviceInRoomModeAdapter
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.model.Device
import org.json.JSONArray
import org.json.JSONObject

@SuppressLint("ValidFragment")
class AddDeviceInRoomDialogFragment : DialogFragment {
    var recyerview: RecyclerView? = null
    val listDeVice: ArrayList<Device>
    var mSocket = Connect.connect()
    val id_room: String


    constructor(listDevice: ArrayList<Device>, id_room: String) {
        this.listDeVice = listDevice
        this.id_room = id_room
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.list_device_add_room_mode, null, false)
        recyerview = view.findViewById(R.id.recyerview) as RecyclerView
        dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        val button = view.findViewById(R.id.button) as Button
        button.setOnClickListener {
            submit()
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = AddDeviceInRoomModeAdapter(this.activity, listDeVice)
        recyerview!!.layoutManager = LinearLayoutManager(this.activity, LinearLayoutManager.VERTICAL, false)
        recyerview!!.adapter = adapter
    }

    fun submit() {
        val json = JSONObject()
        val jsonArray = JSONArray(AddDeviceInRoomModeAdapter.ViewHolder.listDeviceSelectedAddRoomMode())
        json.put("device", jsonArray)
        val json2 = JSONObject()
        json.put("id_room", id_room)
        json2.put("id_room", id_room)
        mSocket.emit("client_send_add_device_in_room", json)
        mSocket.emit("client_send_device_in_room", json2)
        Log.d("AAAA", jsonArray.toString())
        this.dismiss()

    }
}