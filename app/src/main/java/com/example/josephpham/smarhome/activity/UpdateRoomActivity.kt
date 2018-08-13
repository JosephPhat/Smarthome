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
import com.example.josephpham.smarhome.viewmodel.RoomViewModel
import com.example.josephpham.smarhome.adapter.ItemDeviceAdapter
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.interfaces.Loading
import com.example.josephpham.smarhome.interfaces.UploadIMG
import com.example.josephpham.smarhome.model.Room
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ActivityUpdateRoomBinding
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject

class UpdateRoomActivity : AppCompatActivity(), UploadIMG {
    var byte: ByteArray? = null
    val REQUEST_TAKE_PICTURE = 123
    var mSocket = Connect.connect()
    var room = Room()

    var binding: ActivityUpdateRoomBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        emit()
        mSocket.on("server_send_room", onretrieveDataRoom)
        mSocket.on("server_send_update_room", onretrieveResult)

    }

    fun emit(){
        val bundle = intent.extras
        val idRoom = bundle.getString("id_room")
        val json = JSONObject()
        json.put("_id", idRoom)
        Loading.loading(this@UpdateRoomActivity)
        mSocket.emit("client_send_room", json)

    }

    fun takePicture() {
        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_TAKE_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            var bitmap = data!!.extras.get("data") as Bitmap
            bitmap = resize(bitmap, 200, 200)
            byte = getByteArrayToByBitmap(bitmap)
            binding!!.imgUpdateRoom.setImageBitmap(bitmap)
        }
    }

    fun update() {
        val name = binding!!.editTextName.text
        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(name)) {
            binding!!.editTextName.error = getString(R.string.error_field_required)
            focusView = binding!!.editTextName
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {
            val json = JSONObject()
            val jsonsub = JSONObject()
            jsonsub.put("_id", room.id.get())
            jsonsub.put("img", byte)
            jsonsub.put("room_name", name)
            json.put("data", jsonsub)
            mSocket.emit("client_send_update_room", json)
            Loading.loading(this@UpdateRoomActivity)
        }
    }

    var onretrieveResult: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                var correct = data.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    Toast.makeText(this@UpdateRoomActivity, "update success!", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@UpdateRoomActivity, MainActivity::class.java)
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
    var onretrieveDataRoom: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                var correct = data.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
//                    var jsonObject = data.getJSONObject("result")
                    val jsonObject = data.getJSONArray("result")
                    room = Room.parseJson(jsonObject.getJSONObject(0))
                    binding = DataBindingUtil.setContentView(this@UpdateRoomActivity,R.layout.activity_update_room)
                    setSupportActionBar(binding!!.toolbarUpdateRoom)
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    val itemRoom = RoomViewModel(this@UpdateRoomActivity, room)
                    binding!!.viewModel = itemRoom
                    binding!!.updateViewModel = this
                    addEvent()
                } else {
                    val err = data.getString("message")
                    val intent: Intent = Intent(this@UpdateRoomActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }
    fun addEvent() {
        binding!!.listDevice.layoutManager = LinearLayoutManager(this@UpdateRoomActivity, LinearLayoutManager.HORIZONTAL, false)
        val adapter = ItemDeviceAdapter(this@UpdateRoomActivity,room.listDevice)
        binding!!.listDevice.adapter = adapter
    }
}
