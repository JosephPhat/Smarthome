package com.example.josephpham.smarhome.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.adapter.ModeAdapter
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.databinding.ActivityModeBinding
import com.example.josephpham.smarhome.fragment.AddDeviceInModeDialogFragment
import com.example.josephpham.smarhome.model.Device
import com.example.josephpham.smarhome.model.DeviceDetail
import com.example.josephpham.smarhome.model.ModeDetail
import com.example.josephpham.smarhome.model.Schedule

class ModeActivity : AppCompatActivity() {
    var binding: ActivityModeBinding? = null
    var socket = Connect.connect()
    var listModeDetail: ArrayList<ModeDetail>? = null
    var id_mode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mode)
        setSupportActionBar(binding!!.toolbarMode)
        binding!!.viewModel = this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        emit()
        addEvent()

    }

    fun emit() {
        socket.emit("")
    }

    fun addEvent() {
        listModeDetail = ArrayList()
        listModeDetail!!.add(ModeDetail("12", Device("123", DeviceDetail("132", "dfa", "ádfa", "ádf", 2.1, 1), "den1", true), Schedule(231, 2343)))
        listModeDetail!!.add(ModeDetail("12", Device("123", DeviceDetail("132", "dfa", "ádfa", "ádf", 2.1, 1), "den1", true), Schedule(231, 2343)))
        listModeDetail!!.add(ModeDetail("12", Device("123", DeviceDetail("132", "dfa", "ádfa", "ádf", 2.1, 1), "den1", true), Schedule(231, 2343)))
        if (listModeDetail != null) {
            val extras = intent.extras
            id_mode = extras.getString("id_mode")
            binding!!.recyerview.layoutManager = LinearLayoutManager(this@ModeActivity, LinearLayoutManager.VERTICAL, false)
            val adapter = ModeAdapter(this@ModeActivity, listModeDetail!!, id_mode)
            binding!!.recyerview.adapter = adapter
        } else {
            Toast.makeText(this, "no device", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    fun getlistDevice(): ArrayList<Device>{
        val listDevice = ArrayList<Device>()
        for (i in 0.. listModeDetail!!.size-1){
            listDevice.add(i,listModeDetail!!.get(i).device!!)
        }
        return listDevice
    }

    fun fabOnClick() {
        val fragment = AddDeviceInModeDialogFragment(getlistDevice(), id_mode)
        try {
            fragment.show(getFragmentManager(), "myDialog")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
