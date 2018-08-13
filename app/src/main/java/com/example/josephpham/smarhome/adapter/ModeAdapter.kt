package com.example.josephpham.smarhome.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.josephpham.smarhome.fragment.TimePickerFragment
import com.example.josephpham.smarhome.model.ModeDetail
import com.example.josephpham.smarhome.model.Schedule
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.activity.ModeActivity
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.databinding.ItemDeviceInModeBinding
import org.json.JSONObject
import kotlin.collections.ArrayList


class ModeAdapter : RecyclerView.Adapter<ModeAdapter.ViewHolder> {
    companion object {
        var listModeDetails: ArrayList<ModeDetail>? = null
        var context: Context? = null
    }

    val id_mode: String


    constructor(contexts: Context, listItem: ArrayList<ModeDetail>, id: String) {
        context = contexts
        listModeDetails = listItem
        this.id_mode = id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding: ItemDeviceInModeBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_device_in_mode, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listModeDetails!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = listModeDetails!!.get(position).device
        val id = listModeDetails!!.get(position).id.get().toString()
        val schedule = listModeDetails!!.get(position).schedule
        holder.set(device!!.device_name.get().toString(), device.deviceDetail.get()!!.img.get().toString(), schedule!!, id)
    }

    inner class ViewHolder : RecyclerView.ViewHolder {


        var binding: ItemDeviceInModeBinding
        val img: ObservableField<String> = ObservableField()
        val name: ObservableField<String> = ObservableField()
        val onTime: ObservableField<String> = ObservableField()
        val offTime: ObservableField<String> = ObservableField()
        val id: ObservableField<String> = ObservableField()
        var schedule: Schedule? = null


        constructor(binding: ItemDeviceInModeBinding) : super(binding.root) {
            this.binding = binding
        }


        fun set(name: String, img: String, schedule: Schedule, id: String) {
            if (binding.viewModel == null) {
                binding.viewModel = this
                this.name.set(name)
                this.img.set(img)
                this.onTime.set(schedule.ontime.get().toString())
                this.offTime.set(schedule.offtime.get().toString())
                this.schedule = schedule
                this.id.set(id)

            } else {
                this.name.set(name)
                this.img.set(img)
                this.onTime.set(schedule.ontime.get().toString())
                this.offTime.set(schedule.offtime.get().toString())
                this.schedule = schedule
                this.id.set(id)

            }

        }

        fun setOnTime(view: View) {
            val time = TimePickerFragment(view, this.schedule!!, true)
            time.show((context as ModeActivity).supportFragmentManager, "time")
        }

        fun setOffTime(view: View) {
            if (schedule!!.ontime.get() == 0) {
                Toast.makeText(context, "you haven't choose on time", Toast.LENGTH_SHORT).show()
            } else {
                val time = TimePickerFragment(view, this.schedule!!, false)
                time.show((context as ModeActivity).supportFragmentManager, "time")
            }
        }

        fun onClick(view: View) {
            val popupMenu = PopupMenu(context!!, view)
            popupMenu.menuInflater.inflate(R.menu.popup_menu_item_mode, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
                when (item!!.itemId) {
                    R.id.delete -> {
                        val mSocket = Connect.connect()
                        val json = JSONObject()
                        json.put("idDeviceDetail", id)
                        json.put("id_mode", id_mode)
                        val json2 = JSONObject()
                        json2.put("id_mode", id_mode)
                        mSocket.emit("client_send_delete_device_in_mode", json)
                        mSocket.emit("client_send_device_in_mode", json2)
                    }
                }

                true

            })
            popupMenu.show()
        }
    }
}
