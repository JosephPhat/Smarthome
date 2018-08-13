package com.example.josephpham.smarhome.activity

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import java.util.*
import android.content.ActivityNotFoundException
import android.databinding.DataBindingUtil
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import com.example.josephpham.smarhome.adapter.KeyAdapter
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.interfaces.Loading
import com.example.josephpham.smarhome.model.KeyOnOffDevice
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ActivitySettingDeviceBinding
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList


class SettingDeviceActivity : AppCompatActivity() {
    private val REQ_CODE_SPEECH_INPUT = 100
    var checkOnOff = false
    var binding: ActivitySettingDeviceBinding? = null
    var mSocket = Connect.connect()
    var idDevice: String =""
    var listKey: ArrayList<KeyOnOffDevice> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@SettingDeviceActivity, R.layout.activity_setting_device)
        binding!!.viewModel = this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        emit()
        mSocket.on("server_send_list_key", onretrieveDataListKey)
        addEvent()

    }
    fun emit(){
        val bundle = intent.extras
        idDevice = bundle.getString("id_device")
        val json = JSONObject()
        val jsonSub = JSONObject()
        jsonSub.put("device", idDevice)
        json.put("data", jsonSub)
        mSocket.emit("client_send_list_key", json)
    }

    private fun listKeyAdapter() {
        binding!!.listKey.layoutManager = LinearLayoutManager(this@SettingDeviceActivity, LinearLayoutManager.VERTICAL, false)
        val adapter = KeyAdapter(this@SettingDeviceActivity, listKey)
        binding!!.listKey.adapter = adapter
    }

    private fun addEvent() {
        binding!!.toggleButton1.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton,
                                          isChecked: Boolean) {
                checkOnOff = isChecked
                if (isChecked) {
                    binding!!.onOff.setText("incantations for on this deviceDetail")
                } else {
                    binding!!.onOff.setText("incantations for off this deviceDetail")
                }

            }
        })
    }

    fun createKey() {
        binding!!.tvSpeech.error = null
        val tv = binding!!.tvSpeech.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(tv)) {
            binding!!.tvSpeech.error = getString(R.string.error_field_required)
            focusView = binding!!.tvSpeech
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {
            val json = JSONObject()
            val jsonSub = JSONObject()
            jsonSub.put("key", tv)
            jsonSub.put("status", checkOnOff)
            jsonSub.put("id_device", idDevice)
            json.put("data", jsonSub)
            mSocket.emit("client_send_create_key", json)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    val result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    binding!!.tvSpeech.setText(result[0])
                }
            }
        }
    }

    var onretrieveDataListKey: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                var correct = data.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    var jsonArray = data.getJSONArray("result")
                    for (i in 0..jsonArray.length()-1){
                        val jsonObject = jsonArray.getJSONObject(i)
                        val key = KeyOnOffDevice.parseJson(jsonObject)
                        listKey.add(key)
                    }
                    listKeyAdapter()
                } else {
                    val err = data.getString("message")
                    val intent: Intent = Intent(this@SettingDeviceActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }

}
