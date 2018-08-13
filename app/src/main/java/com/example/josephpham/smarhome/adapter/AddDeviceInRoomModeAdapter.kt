package com.example.josephpham.smarhome.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.josephpham.smarhome.model.Device
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ItemDeviceAddRoomModeBinding
import java.util.*


class AddDeviceInRoomModeAdapter : RecyclerView.Adapter<AddDeviceInRoomModeAdapter.ViewHolder> {

    companion object {
        var listDeviceNotRoom: ArrayList<Device>? = null
        var contexts: Context? = null
    }

    constructor(context: Context, listItem: ArrayList<Device>) {
        contexts = context
        listDeviceNotRoom = listItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var binding: ItemDeviceAddRoomModeBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_device_add_room_mode, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listDeviceNotRoom!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var device = listDeviceNotRoom!!.get(position)
        holder.set(device.device_name.get().toString(), device.deviceDetail.get()!!.img.get().toString(), device.id.get().toString(), position)
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var binding: ItemDeviceAddRoomModeBinding
        val img: ObservableField<String> = ObservableField()
        val name: ObservableField<String> = ObservableField()
        val id: ObservableField<String> = ObservableField()
        var position: ObservableInt = ObservableInt()

        companion object {
            fun listDeviceSelectedAddRoomMode(): ArrayList<String> {
                var list: ArrayList<String> = ArrayList()
                for (i in 0..listDeviceNotRoom!!.size - 1) {
                    if (listDeviceNotRoom!!.get(i).selected.get() == true) {
                        list.add(listDeviceNotRoom!!.get(i).id.get().toString())
                    }
                }
                return list
            }
        }


        constructor(binding: ItemDeviceAddRoomModeBinding) : super(binding.root) {
            this.binding = binding
        }

        fun set(name: String, img: String, id: String, position: Int) {
            if (binding.viewModel == null) {
                binding.viewModel = this
                this.name.set(name)
                this.img.set(img)
                this.id.set(id)
                this.position.set(position)

            } else {
                this.name.set(name)
                this.img.set(img)
                this.id.set(id)
                this.position.set(position)
            }

        }

        fun checkBox() {
            val checBox = binding.cbox
            if (checBox.isClickable) {
                listDeviceNotRoom!!.get(this.position.get()).selected.set(true)
            } else {
                listDeviceNotRoom!!.get(this.position.get()).selected.set(false)
            }
        }

    }
}