package com.example.josephpham.smarhome.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.josephpham.smarhome.model.Device
import com.example.josephpham.smarhome.model.ModeDetail
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ItemDeviceHorizontalBinding

class ItemDeviceAdapter : RecyclerView.Adapter<ItemDeviceAdapter.AddRoomViewHolder> {

    companion object {
        var listDeviceNotRoom: ArrayList<Device>? = null
    }
    var context: Context? = null


    constructor(context: Context, listItem: ArrayList<Device>) {
        this.context = context
        listDeviceNotRoom = listItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddRoomViewHolder {
        var binding: ItemDeviceHorizontalBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_device_horizontal, parent, false)
        return AddRoomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listDeviceNotRoom!!.size
    }

    override fun onBindViewHolder(holder: AddRoomViewHolder, position: Int) {
        var device = listDeviceNotRoom!!.get(position)
        holder.setName(device.device_name.get().toString(), device.deviceDetail.get()!!.img.get().toString(), device.id.get().toString(), position)
    }

    class AddRoomViewHolder : RecyclerView.ViewHolder {
        var binding: ItemDeviceHorizontalBinding
        val img: ObservableField<String> = ObservableField()
        val name: ObservableField<String> = ObservableField()
        val id: ObservableField<String> = ObservableField()
        val position: ObservableInt = ObservableInt()
        companion object {
            fun listSelected(): ArrayList<String>{
                var list: ArrayList<String> = ArrayList()
                for (i in 0.. listDeviceNotRoom!!.size-1){
                    if(listDeviceNotRoom!!.get(i).selected.get() == true){
                        list.add(listDeviceNotRoom!!.get(i).id.get().toString())
                    }
                }
                return list
            }
            fun listModeDetail(): ArrayList<ModeDetail>{
                val list: ArrayList<ModeDetail> = ArrayList()
                for (i in 0.. listDeviceNotRoom!!.size-1){
                    if(listDeviceNotRoom!!.get(i).selected.get() == true){
                        val modeDetail = ModeDetail(listDeviceNotRoom!!.get(i))
                        list.add(modeDetail)
                    }
                }
                return list
            }
        }


        constructor(binding: ItemDeviceHorizontalBinding) : super(binding.root) {
            this.binding = binding
        }

        fun setName(name: String, img: String, id: String, position: Int) {
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

        fun onClick() {
            val checked = binding.checkbox
            if(checked.isChecked){
                listDeviceNotRoom!!.get(this.position.get()).selected.set(true)
            }else{
                listDeviceNotRoom!!.get(this.position.get()).selected.set(false)
            }
        }
    }
}