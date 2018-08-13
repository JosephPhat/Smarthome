package com.example.josephpham.smarhome.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.format.DateFormat
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import com.example.josephpham.smarhome.model.Schedule



@SuppressLint("ValidFragment")
class TimePickerFragment : DialogFragment, TimePickerDialog.OnTimeSetListener {

    val mview: View
    var schedule: Schedule
    var onOrOff: Boolean

    constructor(view: View, schedule: Schedule, onOrOff: Boolean) {
        this.mview = view
        this.schedule = schedule
        this.onOrOff = onOrOff
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timePickerDialog = TimePickerDialog(activity, android.R.style.Theme_Holo_Light_Dialog, this, 0, 0, DateFormat.is24HourFormat(getActivity()))
        return timePickerDialog
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        if(onOrOff == true){
            this.schedule.ontime.set(hourOfDay*3600+minute*60)

        }else{
            this.schedule.offtime.set(hourOfDay*3600+minute*60)
        }
        var hour : String
        var mi : String
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