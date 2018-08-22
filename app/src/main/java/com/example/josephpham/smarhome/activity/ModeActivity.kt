package com.example.josephpham.smarhome.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.adapter.ModeAdapter
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.databinding.ActivityModeBinding
import com.example.josephpham.smarhome.fragment.AddDeviceInModeDialogFragment
import com.example.josephpham.smarhome.interfaces.Loading
import com.example.josephpham.smarhome.model.Device
import com.example.josephpham.smarhome.model.ModeDetail
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject

class ModeActivity : AppCompatActivity() {
    var binding: ActivityModeBinding? = null
    var socket = Connect.connect()
    var listModeDetail: ArrayList<ModeDetail>? = null
    var id_mode: String = ""
    var mSocket = Connect.connect()
    var listDevice: ArrayList<Device>? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mode)
        setSupportActionBar(binding!!.toolbarMode)
        binding!!.viewModel = this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        emit()
        mSocket.on("server_send_device_in_mode", onretrieveDataDeviceInMode)
        mSocket.on("server_send_all_device", onretrieveListDevice)

    }

    fun emit() {
        val extras = intent.extras
        id_mode = extras.getString("id_mode")
        Loading.loading(this@ModeActivity)
        mSocket.emit("client_send_device_in_mode", id_mode)
    }

    var onretrieveDataDeviceInMode: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data1 = args[0] as JSONObject
            try {
                listModeDetail = ArrayList()
                val correct = data1.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    val modedetailJson = data1.getJSONArray("result")

                    for (i in 0..modedetailJson.length() - 1) {
                        val dataRoom: JSONObject = modedetailJson.getJSONObject(i)
                        val deviceinmode = ModeDetail.parseJson(dataRoom)
                        listModeDetail!!.add(deviceinmode)
                    }
                    addList()
                } else {
                    val err = data1.getString("message")
                    val intent = Intent(this@ModeActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {

            }
        }
    }

    fun addList() {
        if (listModeDetail != null) {
            binding!!.recyerview.layoutManager = LinearLayoutManager(this@ModeActivity, LinearLayoutManager.VERTICAL, false)
            val adapter = ModeAdapter(this@ModeActivity, listModeDetail!!, id_mode)
            binding!!.recyerview.adapter = adapter
        } else {
            Toast.makeText(this, "no device", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    var onretrieveListDevice: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                var correct = data.getBoolean("success")
                Loading.dismiss()
                listDevice = ArrayList()
                if (correct == true) {
                    val jsonArray = data.getJSONArray("result")
                    for (i in 0..jsonArray.length() - 1) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val device = Device.parseJson(jsonObject)
                        listDevice!!.add(device)
                    }
                    openDialog()
                } else {
                    val err = data.getString("message")
                    val intent: Intent = Intent(this@ModeActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }

    fun fabOnClick() {
        mSocket.emit("client_send_all_device")

    }
    fun openDialog(){
        val fragment = AddDeviceInModeDialogFragment(listDevice!!, id_mode)
        try {
            fragment.show(getFragmentManager(), "myDialog")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
