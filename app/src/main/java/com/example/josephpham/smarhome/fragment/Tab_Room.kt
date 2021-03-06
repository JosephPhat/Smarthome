package com.example.josephpham.smarhome.fragment

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.josephpham.smarhome.adapter.FragmentRoomAdapter
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.activity.LoginActivity
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.databinding.FragmentRoomBinding
import com.example.josephpham.smarhome.model.Room
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject

class Tab_Room : Fragment() {
    var activity: Activity? = null
    var binding: FragmentRoomBinding? = null
    var mSocket: Socket = Connect.connect()
    var listRoom: ArrayList<Room>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_room, container, false)

        return binding!!.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity = getActivity()
        mSocket.emit("client_send_room")
        mSocket.on("server_send_room", onretrieveDataRoom)

    }

    var onretrieveDataRoom: Emitter.Listener = Emitter.Listener { args ->
        activity!!.runOnUiThread {
            val data = args[0] as JSONObject
            try {
                var correct = data.getBoolean("success")
                listRoom = ArrayList()
                if (correct == true) {
                    val result = data.getJSONArray("result")
                    for (i in 0..result.length() - 1) {
                        val jsonObject = result.getJSONObject(i)
                        val room = Room.parseJson(jsonObject)
                        listRoom!!.add(room)

                    }
                    addList()
                } else {
                    val err = data.getString("message")
                    val intent: Intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }
    fun addList(){
        if (activity != null) {
            if (listRoom != null) {
                val adapter = FragmentRoomAdapter(activity as FragmentActivity, listRoom!!)
                binding!!.listRoom.adapter = adapter
            } else {
                listRoom = ArrayList()
                val adapter = FragmentRoomAdapter(activity as FragmentActivity, listRoom!!)
                binding!!.listRoom.adapter = adapter
            }
        }
    }
}