package com.example.josephpham.smarhome.activity

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.josephpham.smarhome.adapter.ItemDeviceAdapter
import com.example.josephpham.smarhome.adapter.ItemDeviceAdapter.AddRoomViewHolder.Companion.listSelected
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.interfaces.Loading
import com.example.josephpham.smarhome.interfaces.UploadIMG
import com.example.josephpham.smarhome.model.Device
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ActivityAddRoomBinding
import com.example.josephpham.smarhome.database.DatabaseHandler
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AddRoomActivity : AppCompatActivity(), UploadIMG {
    var token: String = ""
    var listDeviceNotRoom = ArrayList<Device>()
    val REQUEST_TAKE_PICTURE = 123
    var byte: ByteArray? = null
    var mSocket = Connect.connect()

    var dataBind: ActivityAddRoomBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBind = DataBindingUtil.setContentView(this, R.layout.activity_add_room)
        dataBind!!.viewModel = this
        setSupportActionBar(dataBind!!.toolbarAddRoom)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mSocket.on("server_send_device_no_room", onretrieveDeviceNotRoom)
        emit()

    }

    fun takePicture() {
        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_TAKE_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            var bitmap = data!!.extras.get("data") as Bitmap
            bitmap = resize(bitmap, 700, 350)
            byte = getByteArrayToByBitmap(bitmap)
            dataBind!!.imgroom.setImageBitmap(bitmap)
        }
    }

    fun emit() {
        val db = DatabaseHandler(this@AddRoomActivity)
        token = db.readData()
        val json = JSONObject()
        json.put("token", token)
        mSocket.emit("client_send_device_no_room", json)
        Loading.loading(this@AddRoomActivity)
    }

    var onretrieveDeviceNotRoom: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                var correct = data.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    var jsonArr = data.getJSONArray("result")
                    for (i in 0..jsonArr.length() - 1) {
                        var dataRoom: JSONObject = jsonArr.getJSONObject(i)
                        val device = Device.parseJson(dataRoom)
                        listDeviceNotRoom.add(device)
                    }
                    addEvent()

                } else {
                    val err = data.getString("message")
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }

    fun addEvent() {
        dataBind!!.listDevice.layoutManager = LinearLayoutManager(this@AddRoomActivity, LinearLayoutManager.HORIZONTAL, false)
        val adapter = ItemDeviceAdapter(this@AddRoomActivity, listDeviceNotRoom)
        dataBind!!.listDevice.adapter = adapter
    }

    fun attemptCreate() {
        dataBind!!.nameRoom.error = null
        val nameStr = dataBind!!.nameRoom.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(nameStr)) {
            dataBind!!.nameRoom.error = getString(R.string.error_field_required)
            focusView = dataBind!!.nameRoom
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {

            val jsonsub = JSONObject()
            val jsonArray = JSONArray(listSelected())
            jsonsub.put("room_name", nameStr)
            jsonsub.put("img", byte)
            jsonsub.put("device", jsonArray)
            val json = JSONObject()
            json.put("data", jsonsub)
            json.put("token", token)
            Loading.loading(this@AddRoomActivity)
            mSocket.emit("client_send_create_room", json)
            mSocket.on("server_send_create_room", onretrieveResult)

        }
    }

    var onretrieveResult: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                var correct = data.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    mSocket.emit("client_send_data_user", token)
                    Toast.makeText(this, "create success", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivity::class.java)
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
