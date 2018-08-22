package com.example.josephpham.smarhome.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.model.Schedule
import org.json.JSONObject


@SuppressLint("ValidFragment")
class TimePickerFragment : DialogFragment, TimePickerDialog.OnTimeSetListener {

    val mview: View
    var schedule: Schedule
    var onOrOff: Boolean
    var socket = Connect.connect()
    val id: String


    constructor(view: View, schedule: Schedule, onOrOff: Boolean, id: String) {
        this.mview = view
        this.schedule = schedule
        this.onOrOff = onOrOff
        this.id = id
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timePickerDialog = TimePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog, this, 0, 0, DateFormat.is24HourFormat(getActivity()))
        return timePickerDialog
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val modedetail = JSONObject()
        val shedule = JSONObject()
        val time = hourOfDay * 3600 + minute * 60
        if (onOrOff == true) {
            this.schedule.ontime.set(time)
            shedule.put("ontime", time)
            modedetail.put("_id",id)
            modedetail.put("schedule", shedule)
            socket.emit("client_send_update_modedetail", modedetail)

        } else {
            shedule.put("offtime", time)
            modedetail.put("_id",id)
            modedetail.put("schedule", shedule)
            this.schedule.offtime.set(time)
            socket.emit("client_send_update_modedetail", modedetail)

        }
        var hour: String
        var mi: String
        if (hourOfDay < 10) {
            hour = "0" + hourOfDay.toString()
        } else {
            hour = hourOfDay.toString()
        }
        if (minute < 10) {
            mi = "0" + minute.toString()
        } else {
            mi = minute.toString()
        }

        (mview as TextView).text = "" + hour + ":" + mi

    }

}