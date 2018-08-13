package com.example.josephpham.smarhome.activity

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ActivityTestAppBinding
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_test_app.*


class TestApp : AppCompatActivity() {
    var mSocket: Socket = Connect.connect()
    var binding: ActivityTestAppBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test_app)
        addEvent()
    }
    fun addEvent(){
        on1.setOnClickListener {
            mSocket.emit("led-change", "on1")
        }
        off1.setOnClickListener {
            mSocket.emit("led-change", "off1")
        }
        on2.setOnClickListener {
            mSocket.emit("led-change", "on2")
        }
        off2.setOnClickListener {
            mSocket.emit("led-change", "off2")
        }
    }
}
