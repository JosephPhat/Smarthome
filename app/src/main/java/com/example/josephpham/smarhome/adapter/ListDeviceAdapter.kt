package com.example.josephpham.smarhome.adapter

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.model.Device
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.activity.SettingDeviceActivity
import com.example.josephpham.smarhome.activity.UpdateDeviceActivity
import com.example.josephpham.smarhome.databinding.ItemDeviceAddRoomModeBinding
import com.example.josephpham.smarhome.databinding.ItemDeviceBinding
import java.util.*


class ListDeviceAdapter : RecyclerView.Adapter<ListDeviceAdapter.ViewHolder> {

    var listDevice: ArrayList<Device>? = null
    var contexts: Context? = null


    constructor(context: Context, listItem: ArrayList<Device>) {
        contexts = context
        listDevice = listItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding: ItemDeviceBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_device, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listDevice!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var device = listDevice!!.get(position)
        holder.set(device.device_name.get().toString(), device.deviceDetail.get()!!.img.get().toString(), device.id.get().toString())
    }

    inner class ViewHolder : RecyclerView.ViewHolder {
        var binding: ItemDeviceBinding
        val img: ObservableField<String> = ObservableField()
        val name: ObservableField<String> = ObservableField()
        val id: ObservableField<String> = ObservableField()


        constructor(binding: ItemDeviceBinding) : super(binding.root) {
            this.binding = binding
        }

        fun set(name: String, img: String, id: String) {
            if (binding.viewModel == null) {
                binding.viewModel = this
                this.name.set(name)
                this.img.set(img)
                this.id.set(id)

            } else {
                this.name.set(name)
                this.img.set(img)
                this.id.set(id)
            }

        }

        fun onClick(view: View) {
            val popupMenu = PopupMenu(contexts!!, view)
            popupMenu.menuInflater.inflate(R.menu.popup_menu_item_device, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
                when (item!!.itemId) {
                    R.id.delete -> {
                        val mSocket = Connect.connect()
                        mSocket.emit("client_send_delete_device", id)
                        mSocket.emit("client_send_all_device")
                    }
                    R.id.update -> {
                        val intent: Intent = Intent(contexts!!, UpdateDeviceActivity::class.java)
                        intent.putExtra("id_device", id)
                        contexts!!.startActivity(intent)
                    }
                    R.id.settings ->{
                        val intent: Intent = Intent(contexts!!, SettingDeviceActivity::class.java)
                        intent.putExtra("id_device", id.get().toString())
                        contexts!!.startActivity(intent)
                    }
                }

                true

            })
            popupMenu.show()
        }
    }
}