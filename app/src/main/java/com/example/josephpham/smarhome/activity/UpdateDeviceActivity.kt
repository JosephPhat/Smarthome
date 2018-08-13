package com.example.josephpham.smarhome.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.josephpham.smarhome.viewmodel.DeviceModelView
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.interfaces.Loading
import com.example.josephpham.smarhome.model.Device
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ActivityUpdateDeviceBinding
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject

class UpdateDeviceActivity : AppCompatActivity() {
    var binding: ActivityUpdateDeviceBinding? = null
    val mSocket = Connect.connect()
    var id:  String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSocket.on("server_send_device", onretrieveDataDevice)
        mSocket.on("server_send_update_device", onretrieveResult)


    }
    fun emit(){
        val bundle = intent.extras
        id = bundle.get("id_device") as String
        Loading.loading(this@UpdateDeviceActivity)
        mSocket.emit("client_send_device", id)
    }
    fun update(){
        val name = binding!!.edtNameDevice.text
        var cancel = false
        var focusView: View? = null

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
            jsonsub.put("_id", id)
            jsonsub.put("room_name", name)
            mSocket.emit("client_send_update_device", json)
            Loading.loading(this@UpdateDeviceActivity)
        }

    }
    var onretrieveDataDevice: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                var correct = data.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    val jsonObject = data.getJSONObject("result")
                    Log.d("AAAA", jsonObject.toString())
                    val device = Device.parseJson(jsonObject)
                    binding = DataBindingUtil.setContentView(this, R.layout.activity_update_device)
                    setSupportActionBar(binding!!.toolbarUpdateDevice)
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    val deviceViewModel = DeviceModelView(this@UpdateDeviceActivity, device)
                    binding!!.viewModel = deviceViewModel
                    binding!!.updateViewModel = this
                } else {
                    val err = data.getString("message")
                    val intent: Intent = Intent(this@UpdateDeviceActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }
    var onretrieveResult: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                val correct = data.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    Toast.makeText(this@UpdateDeviceActivity, "update success!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@UpdateDeviceActivity, ListDeviceActivity::class.java)
                    startActivity(intent)
                } else {
                    val err = data.getString("message")
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()

                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }
}
