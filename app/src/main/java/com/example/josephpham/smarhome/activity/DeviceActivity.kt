package com.example.josephpham.smarhome.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.databinding.ActivityDeviceBinding
import com.example.josephpham.smarhome.interfaces.Loading
import com.example.josephpham.smarhome.model.Device
import com.example.josephpham.smarhome.viewmodel.DeviceModelView_
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.dialog_change_name_device.view.*
import kotlinx.android.synthetic.main.dialog_setting_device.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class DeviceActivity : AppCompatActivity() {
    val mSocket = Connect.connect()
    var id: String = ""
    var binding: ActivityDeviceBinding? = null
    var device: Device? = null
    private val REQ_CODE_SPEECH_INPUT = 100
    var mView: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSocket.on("server_send_device", onretrieveDataDevice)
        mSocket.on("server_send_update_device", onretrieveResult)
        mSocket.on("server_send_update_key", onretrieveData)
        emit()

    }

    fun emit() {
        val bundle = intent.extras
        id = bundle.getString("id_device")
        Loading.loading(this@DeviceActivity)
        mSocket.emit("client_send_device", id)
    }

    var onretrieveDataDevice: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                var correct = data.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    val jsonObject = data.getJSONObject("result")
                    Log.d("AAAAA", jsonObject.toString())
                    device = Device.parseJson(jsonObject)
                    Log.d("AAAAA", device.toString())
                    binding = DataBindingUtil.setContentView(this, R.layout.activity_device)
                    setSupportActionBar(binding!!.toolbarDevice)
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    val deviceViewModel = DeviceModelView_(this@DeviceActivity, device!!)
                    binding!!.viewModel = deviceViewModel
                    binding!!.device = this
                } else {
                    val err = data.getString("message")
                    val intent: Intent = Intent(this@DeviceActivity, LoginActivity::class.java)
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
                    Toast.makeText(this@DeviceActivity, "update success!", Toast.LENGTH_LONG).show()
                    mSocket.emit("client_send_device", id)
                } else {
                    val err = data.getString("message")
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()

                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }

    fun updateName() {
        val mBundle = AlertDialog.Builder(this@DeviceActivity)
        val mView = layoutInflater.inflate(R.layout.dialog_change_name_device, null)
        mBundle.setView(mView)
        val dialog = mBundle.create()
        mView.updatenamedevice.setText(device!!.device_name.get().toString())
        mView.submit.setOnClickListener {
            var cancel = false
            var focusView: View? = null

            if (TextUtils.isEmpty(mView.updatenamedevice.text)) {
                mView.updatenamedevice.error = getString(R.string.error_field_required)
                focusView = mView.updatenamedevice
                cancel = true
            }
            if (cancel) {
                focusView?.requestFocus()
            } else {
                val json = JSONObject()
                json.put("device_name", mView.updatenamedevice.text)
                json.put("_id", device!!.id.get().toString())
                mSocket.emit("client_send_update_device", json)
                dialog.dismiss()
                mSocket.emit("client_send_device", id)

            }
        }
        dialog.show()
    }

    fun changeKeyOff() {
        val mBundle = AlertDialog.Builder(this@DeviceActivity)
        mView = layoutInflater.inflate(R.layout.dialog_setting_device, null)
        mBundle.setView(mView)
        val dialog = mBundle.create()
        mView!!.btn_speech.setOnClickListener {
            getSpeechInput()
        }
        mView!!.key.setText("setting key off for device")
        mView!!.btn_add_key.setOnClickListener {
            createKey("off")
            dialog.dismiss()

        }
        dialog.show()

    }

    fun changeKeyOn() {
        val mBundle = AlertDialog.Builder(this@DeviceActivity)
        mView = layoutInflater.inflate(R.layout.dialog_setting_device, null)
        mBundle.setView(mView)
        val dialog = mBundle.create()
        mView!!.key.setText("setting key on for device")
        mView!!.btn_speech.setOnClickListener {
            getSpeechInput()
        }
        mView!!.btn_add_key.setOnClickListener {
            createKey("on")
            dialog.dismiss()
        }
        dialog.show()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    val result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    mView!!.tv_speech.setText(result[0])
                }
            }
        }
    }

    fun getSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt))
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(applicationContext,
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show()
        }
    }

    fun createKey(key: String) {

        mView!!.tv_speech.error = null
        val tv = mView!!.tv_speech.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(tv)) {
            mView!!.tv_speech.error = getString(R.string.error_field_required)
            focusView = mView!!.tv_speech
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {
            val json = JSONObject()
            if (key.equals("on")) {
                json.put("turnon", mView!!.tv_speech.text)
                json.put("device", id)
                mSocket.emit("client_send_update_key", json)
                mSocket.emit("client_send_device", id)

            } else {
                json.put("turnoff", mView!!.tv_speech.text)
                json.put("device", id)
                mSocket.emit("client_send_update_key", json)
                mSocket.emit("client_send_device", id)
            }
        }
    }

    var onretrieveData: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                var correct = data.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    Toast.makeText(this, "success", Toast.LENGTH_LONG).show()

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
