package com.example.josephpham.smarhome.activity

import android.app.TimePickerDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TimePicker
import android.widget.Toast
import com.example.josephpham.smarhome.adapter.ItemDeviceAdapter
import com.example.josephpham.smarhome.adapter.ItemDeviceAdapter.AddRoomViewHolder.Companion.listModeDetail
import com.example.josephpham.smarhome.adapter.ModeAdapter.Companion.listModeDetails
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.interfaces.Loading
import com.example.josephpham.smarhome.model.Device
import com.example.josephpham.smarhome.model.Schedule
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ActivityAddModeBinding
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class AddModeActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener {
    companion object {
        var listDevice = ArrayList<Device>()
    }
    var mSocket = Connect.connect()
    var choose: Boolean = false
    var binding: ActivityAddModeBinding? = null
    var startTime = 0
    var endTime = 0
    var mWeek = ArrayList<Boolean>(7)
    var mCircle = ArrayList<Int>()

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        if (choose == true) {
            binding!!.textView6.setText("" + hourOfDay + ":" + minute)
            startTime = hourOfDay * 3600 + minute * 60
        } else {
            binding!!.textView7.setText("" + hourOfDay + ":" + minute)
            endTime = hourOfDay * 3600 + minute * 60
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_mode)
        binding!!.viewModel = AddModeActivity()
        setSupportActionBar(binding!!.toolbarAddMode)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        emit()
        mSocket.on("server_send_all_device", onretrieveAllDevice)
        mSocket.on("server_send_create_mode", onretrieveResult)

    }

    private fun emit() {
        val bundle = intent.extras
        val idUser = bundle.getString("id_user")
        val json = JSONObject()
        json.put("id_user", idUser)
        Loading.loading(this@AddModeActivity)
        mSocket.emit("client_send_all_device", json)
    }

    fun addEvent() {
        binding!!.listDevice.layoutManager = LinearLayoutManager(this@AddModeActivity, LinearLayoutManager.HORIZONTAL, false)
        val adapter = ItemDeviceAdapter(this@AddModeActivity, listDevice)
        binding!!.listDevice.adapter = adapter
    }

    fun schedule() {
        for (i in 0..6) {
            mWeek.add(false)
        }
        Log.d("EEEE", mWeek.toString())
        binding!!.monday.setOnClickListener {
            monday()
        }
        binding!!.tuesday.setOnClickListener {
            tuesday()
        }
        binding!!.thursday.setOnClickListener {
            thursday()
        }
        binding!!.friday.setOnClickListener {
            friday()
        }
        binding!!.sunday.setOnClickListener {
            sunday()
        }
        binding!!.saturday.setOnClickListener {
            saturday()
        }
        binding!!.wednesday.setOnClickListener {
            wednesday()
        }
        binding!!.textView6.setOnClickListener {
            starTime()
        }
        binding!!.textView7.setOnClickListener {
            endTime()
        }

        binding!!.btnCreateMode.setOnClickListener {
            createMode()
        }
    }

    fun monday() {
        if (mWeek.get(0) == false) {
            binding!!.monday.setImageResource(R.drawable.ic_monday)
            mWeek.set(0, true)
            mCircle.add(1)
        } else {
            binding!!.monday.setImageResource(R.drawable.ic_monday1)
            mWeek.set(0, false)
            mCircle.remove(1)
        }
    }

    fun tuesday() {
        if (mWeek.get(1) == false) {
            binding!!.tuesday.setImageResource(R.drawable.ic_tuesday_)
            mWeek.set(1, true)
            mCircle.add(2)
        } else {
            binding!!.tuesday.setImageResource(R.drawable.ic_tuesday1)
            mWeek.set(1, true)
            mCircle.remove(2)
        }

    }

    fun wednesday() {
        if (mWeek.get(2) == false) {
            binding!!.wednesday.setImageResource(R.drawable.ic_wednesday)
            mWeek.set(2, true)
            mCircle.add(3)
        } else {
            binding!!.wednesday.setImageResource(R.drawable.ic_wednesday1)
            mWeek.set(2, false)
            mCircle.remove(3)
        }

    }

    fun thursday() {
        if (mWeek.get(3) == false) {
            binding!!.thursday.setImageResource(R.drawable.ic_thursday)
            mWeek.set(3, true)
            mCircle.add(4)
        } else {
            binding!!.thursday.setImageResource(R.drawable.ic_thursday1)
            mWeek.set(3, false)
            mCircle.remove(4)
        }

    }

    fun friday() {
        if (mWeek.get(4) == false) {
            binding!!.friday.setImageResource(R.drawable.ic_friday)
            mWeek.set(4, true)
            mCircle.add(5)
        } else {
            binding!!.friday.setImageResource(R.drawable.ic_friday1)
            mWeek.set(4, false)
            mCircle.remove(5)
        }

    }

    fun saturday() {
        if (mWeek.get(5) == false) {
            binding!!.saturday.setImageResource(R.drawable.ic_saturday)
            mWeek.set(5, true)
            mCircle.add(6)
        } else {
            binding!!.saturday.setImageResource(R.drawable.ic_saturday1)
            mWeek.set(5, false)
            mCircle.remove(6)
        }
        Log.d("CCC", mCircle.toString())

    }

    fun sunday() {
        if (mWeek.get(6) == false) {
            binding!!.sunday.setImageResource(R.drawable.ic_sunday)
            mWeek.set(6, true)
            mCircle.add(0)
        } else {
            binding!!.sunday.setImageResource(R.drawable.ic_sunday1)
            mWeek.set(6, false)
            mCircle.remove(0)
        }
        Log.d("CCC", mCircle.toString())

    }

    fun starTime() {
        choose = true
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val timpicker = TimePickerDialog(this@AddModeActivity, this@AddModeActivity as TimePickerDialog.OnTimeSetListener?, hour, minute, android.text.format.DateFormat.is24HourFormat(this@AddModeActivity))
        timpicker.show()
    }


    fun endTime() {
        choose = false
        val calendar = Calendar.getInstance()
        val hourend = calendar.get(Calendar.HOUR_OF_DAY)
        val minuteend = calendar.get(Calendar.MINUTE)
        val timpickerend = TimePickerDialog(this@AddModeActivity, this@AddModeActivity as TimePickerDialog.OnTimeSetListener?, hourend, minuteend, android.text.format.DateFormat.is24HourFormat(this@AddModeActivity))
        timpickerend.show()
    }


    var onretrieveAllDevice: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                val correct = data.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    val jsonArr = data.getJSONArray("result")
                    for (i in 0..jsonArr.length() - 1) {
                        val dataRoom: JSONObject = jsonArr.getJSONObject(i)
                        val device = Device.parseJson(dataRoom)
                        listDevice.add(device)
                    }
                    addEvent()
                    schedule()

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
    fun createMode() {
        val jsonArray = JSONArray()
        var listModeDevice = listModeDetails
        if(listModeDevice != null){
            for(i in 0..listModeDevice.size-1){
                val jsonObject = JSONObject()
                val device = listModeDevice.get(i).device
                val jsonDevice = Device.json(device!!)
                jsonObject.put("device", jsonDevice)
                val schedule = listModeDevice.get(i).schedule
                if (schedule != null){
                    val jsonSchedule = Schedule.json(schedule)
                    jsonObject.put("schedule", jsonSchedule)
                }
                jsonArray.put(jsonObject)
            }
        }else{
            listModeDevice = listModeDetail()
            for(i in 0..listModeDevice.size-1){
                val jsonObject = JSONObject()
                val device = listModeDevice.get(i).device
                val jsonDevice = Device.json(device!!)
                jsonObject.put("device", jsonDevice)
                val schedule = listModeDevice.get(i).schedule
                if (schedule != null){
                    val jsonSchedule = Schedule.json(schedule)
                    jsonObject.put("schedule", jsonSchedule)
                }
                jsonArray.put(jsonObject)
            }
        }

        binding!!.tvNameMode.error = null
        var cancel = false
        var focusView: View? = null
        val name = binding!!.tvNameMode.text.toString()

        if (TextUtils.isEmpty(name)) {
            binding!!.tvNameMode.error = getString(R.string.error_field_required)
            focusView = binding!!.tvNameMode
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {
            val circle = JSONArray(mCircle)
            val json = JSONObject()
            json.put("mode_name", name)
            json.put("circle", circle)
            json.put("starttime", startTime)
            json.put("stoptime", endTime)
            json.put("status", true)
            json.put("modedetail", jsonArray)
            Log.d("BBBB", json.toString())
            Loading.loading(this@AddModeActivity)
            mSocket.emit("client_send_create_mode", json)
        }

    }

    var onretrieveResult: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data1 = args[0] as JSONObject
            try {
                val correct = data1.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    mSocket.emit("client_send_mode")
                    Toast.makeText(this@AddModeActivity, "create success", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AddModeActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    val err = data1.getString("message")
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }
}

