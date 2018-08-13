package com.example.josephpham.smarhome.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import com.example.josephpham.smarhome.model.DeviceDetail
import kotlin.collections.ArrayList
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.interfaces.Loading
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.adapter.AddDeviceAdapter
import com.example.josephpham.smarhome.databinding.ActivityAddDeviceBinding
import com.example.josephpham.smarhome.database.DatabaseHandler
import com.squareup.picasso.Picasso
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject


class AddDeviceActivity : AppCompatActivity() {
    var mSocket: Socket = Connect.connect()
    var token: String = ""
    var list = ArrayList<DeviceDetail>()
    var binding: ActivityAddDeviceBinding? = null
    var id: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_device)
        binding!!.viewModel = this
        setSupportActionBar(binding!!.toolbarAddDevice)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val db = DatabaseHandler(this@AddDeviceActivity)
        token = db.readData()
        emit(token)
        mSocket.on("server_send_create_device_in_room", onretrieveResult)
        mSocket.on("server_send_list_device", onretrieveDataListDevice)


    }

    private fun emit(token: String) {
        Loading.loading(this@AddDeviceActivity)
        mSocket.emit("client_send_list_device", token)

    }

    private fun controll() {
        binding!!.listTypeDevice.layoutManager = LinearLayoutManager(this@AddDeviceActivity, LinearLayoutManager.HORIZONTAL, false)
        val adapter = AddDeviceAdapter(this@AddDeviceActivity, list)
        binding!!.listTypeDevice.adapter = adapter
        LocalBroadcastManager.getInstance(this@AddDeviceActivity).registerReceiver(mMessageReceiver,
                IntentFilter("custom-message"))
    }

    //get data from Adapter
    var mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // Get extra data included in the Intent
            val name = intent.getStringExtra("name")
            id = intent.getStringExtra("_id")
            val itemimg = intent.getStringExtra("img")
            val itemprice = intent.getStringExtra("price")
            Picasso.get().load(itemimg).into(binding!!.imgTypeDevice)
            binding!!.tvPrice.setText(itemprice)
            binding!!.tvTypeDevice.setText(name)
            Toast.makeText(this@AddDeviceActivity, name, Toast.LENGTH_SHORT).show()

        }
    }

    fun createDevice() {
        binding!!.edtNameDevice.error = null
        val name = binding!!.edtNameDevice.text.toString()

        var cancel = false
        var focusView: View? = null
        // Check for a valid password, if the user entered one.
        if (id.equals("")) {
            Toast.makeText(this@AddDeviceActivity, "please choose type deviceDetail", Toast.LENGTH_LONG).show()
        } else {
            if (TextUtils.isEmpty(name)) {
                binding!!.edtNameDevice.error = getString(R.string.error_field_required)
                focusView = binding!!.edtNameDevice
                cancel = true
            }

            if (cancel) {
                focusView?.requestFocus()
            } else {
                val json = JSONObject()
                val jsonsub = JSONObject()
                jsonsub.put("device", id)
                jsonsub.put("device_name", name)
                json.put("token", token)
                json.put("data", jsonsub)
                Loading.loading(this@AddDeviceActivity)
                mSocket.emit("client_send_create_device_in_room", json)
            }
        }

    }

    var onretrieveDataListDevice: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                var correct = data.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    var jsonArr = data.getJSONArray("result")
                    for (i in 0..jsonArr.length() - 1) {
                        var dataRoom: JSONObject = jsonArr.getJSONObject(i)
                        val device = DeviceDetail.parseJson(dataRoom)
                        list.add(device)
                        controll()
                    }
                } else {
                    val err = data.getString("message")
                    val intent = Intent(this@AddDeviceActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                Log.d("listDevice", e.toString())
            }
        }
    }
    var onretrieveResult: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                var correct = data.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    Toast.makeText(this@AddDeviceActivity, "device was created", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@AddDeviceActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    val err = data.getString("message")
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                Log.d("listDevice", e.toString())
            }
        }
    }
}
