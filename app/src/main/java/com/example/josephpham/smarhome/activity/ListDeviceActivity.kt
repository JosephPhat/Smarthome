package com.example.josephpham.smarhome.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.interfaces.Loading
import com.example.josephpham.smarhome.model.Device
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.adapter.ListDeviceAdapter
import com.example.josephpham.smarhome.databinding.ActivityListDeviceBinding
import com.example.josephpham.smarhome.database.DatabaseHandler
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject

class ListDeviceActivity : AppCompatActivity() {
    val mSocket = Connect.connect()
    var token: String = ""
    val listDevice: ArrayList<Device> = ArrayList()
    var binding: ActivityListDeviceBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@ListDeviceActivity, R.layout.activity_list_device)
        setSupportActionBar(binding!!.toolbarListDevice)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        emit()
        mSocket.on("server_send_all_device", onretrieveListDevice)
    }

    private fun emit() {
        val bundle = intent.extras
        val idUser = bundle.getString("id_user")
        val json = JSONObject()
        json.put("id_user", idUser)
        Loading.loading(this@ListDeviceActivity)
        mSocket.emit("client_send_all_device", json)
    }

    var onretrieveListDevice: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                var correct = data.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    val jsonArray = data.getJSONArray("result")
                    for (i in 0..jsonArray.length() - 1) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val device = Device.parseJson(jsonObject)
                        listDevice.add(device)
                    }
                    addListAdapter()

                } else {
                    val err = data.getString("message")
                    val intent: Intent = Intent(this@ListDeviceActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }

    fun addListAdapter() {
        binding!!.recyerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val adapter = ListDeviceAdapter(this, listDevice)
        binding!!.recyerview.adapter = adapter
    }

}
