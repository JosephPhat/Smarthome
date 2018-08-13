package com.example.josephpham.smarhome.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.josephpham.smarhome.adapter.RoomAdapter
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.interfaces.Loading
import com.example.josephpham.smarhome.model.Device
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.database.DatabaseHandler
import com.example.josephpham.smarhome.databinding.ActivityRoomBinding
import com.example.josephpham.smarhome.fragment.AddDeviceInRoomDialogFragment
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject


class RoomActivity : AppCompatActivity() {
    var listDeviceInRoom: ArrayList<Device>? = null
    var listDeviceNotRoom: ArrayList<Device>? = null
    var mSocket = Connect.connect()
    var binding: ActivityRoomBinding? = null
    var img_room: String = ""
    var id_room: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@RoomActivity, R.layout.activity_room)
        setSupportActionBar(binding!!.toolbarRoom)
        binding!!.viewModel = this
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        emit()
        mSocket.on("server_send_device_in_room", onretrieveDataDeviceInRoom)
        mSocket.on("server_send_device_no_room", onretrieveDeviceNotRoom)

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main2, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun emit() {
        val bundle = intent.extras
        id_room = bundle.getString("id_room")
        img_room = bundle.getString("img")
        val json = JSONObject()
        json.put("id_room", id_room)
        Loading.loading(this@RoomActivity)
        mSocket.emit("client_send_device_in_room", json)

    }


    var onretrieveDataDeviceInRoom: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data1 = args[0] as JSONObject
            try {
                listDeviceInRoom = ArrayList()
                val correct = data1.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    val roomJson = data1.getJSONArray("result")
                    for (i in 0..roomJson.length() - 1) {
                        val dataRoom: JSONObject = roomJson.getJSONObject(i)
                        val deviceinroom = Device.parseJson(dataRoom)
                        listDeviceInRoom!!.add(deviceinroom)
                    }
                    addList()

                } else {
                    val err = data1.getString("message")
                    val intent = Intent(this@RoomActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {

            }
        }
    }

    fun addList() {
        val adapter = RoomAdapter(listDeviceInRoom!!, this@RoomActivity)
        binding!!.device.adapter = adapter
    }

    var onretrieveDeviceNotRoom: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                listDeviceNotRoom = ArrayList()
                val correct = data.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    val jsonArr = data.getJSONArray("result")
                    for (i in 0..jsonArr.length() - 1) {
                        val dataRoom: JSONObject = jsonArr.getJSONObject(i)
                        val device = Device.parseJson(dataRoom)
                        listDeviceNotRoom!!.add(device)
                    }
                    openDialog()
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

    fun fabOnClick() {
        Loading.loading(this@RoomActivity)
        val db = DatabaseHandler(this@RoomActivity)
        val token = db.readData()
        mSocket.emit("client_send_device_no_room", token)

    }

    fun openDialog() {
        val fragment = AddDeviceInRoomDialogFragment(listDeviceNotRoom!!, id_room)
        try {
            fragment.show(getFragmentManager(), "myDialog")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
