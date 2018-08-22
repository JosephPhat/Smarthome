package com.example.josephpham.smarhome.activity

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ActivityTestAppBinding
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_test_app.*
import org.json.JSONObject


class TestApp : AppCompatActivity() {
    var mSocket: Socket = Connect.connect()
    var binding: ActivityTestAppBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test_app)
        addEvent()
    }

    fun addEvent() {
        on1.setOnClickListener {
            val json = JSONObject()
            json.put("id", "device1")
            json.put("key", "on")

            mSocket.emit("control", json)
        }
        off1.setOnClickListener {
            val json = JSONObject()
            json.put("id", "device1")
            json.put("key", "off")
            mSocket.emit("control", json)
        }
        on2.setOnClickListener {
            val json = JSONObject()
            json.put("id", "device2")
            json.put("key", "on")

            mSocket.emit("control", json)
        }
        off2.setOnClickListener {
            val json = JSONObject()
            json.put("id", "device2")
            json.put("key", "off")
            mSocket.emit("control", json)
        }
        on3.setOnClickListener {
            val json = JSONObject()
            json.put("id", "device3")
            json.put("key", "on")

            mSocket.emit("control", json)
        }
        off3.setOnClickListener {
            val json = JSONObject()
            json.put("id", "device3")
            json.put("key", "off")
            mSocket.emit("control", json)
        }
        connect.setOnClickListener {
            val listDevice = ArrayList<String>()
            listDevice.add("device1")
            listDevice.add("device2")
            listDevice.add("device3")
            listDevice.add("device4")
            val json = JSONObject()
            for(i in 0.. listDevice.size-1){
                val port: Int = i + 5
                val d: String = "D" + port
                json.put(d, listDevice.get(i))
            }
            mSocket.emit("connectss", json)
        }
    }
}
