package com.example.josephpham.smarhome.adapter

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ItemTypeDeviceBinding
import com.example.josephpham.smarhome.model.DeviceDetail
import kotlin.collections.ArrayList

class AddDeviceAdapter : RecyclerView.Adapter<AddDeviceAdapter.ViewHolder> {
    var listAllDeviceDetail: ArrayList<DeviceDetail>
    var context: Context? = null

    constructor(context: Context, listDeviceDetail: ArrayList<DeviceDetail>) {
        this.context = context
        this.listAllDeviceDetail = listDeviceDetail
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setImg(listAllDeviceDetail.get(position).img.get().toString(), listAllDeviceDetail.get(position).name.get().toString())
        holder.itemView.setOnClickListener {
            val intent = Intent("custom-message")
            intent.putExtra("_id", listAllDeviceDetail.get(position).id.get().toString())
            intent.putExtra("img", listAllDeviceDetail.get(position).img.get().toString())
            intent.putExtra("name", listAllDeviceDetail.get(position).name.get().toString())
            intent.putExtra("price", listAllDeviceDetail.get(position).price.get().toString())
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemTypeDeviceBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_type_device, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        Log.d("size", listAllDeviceDetail.size.toString())
        return listAllDeviceDetail.size
    }


    inner class ViewHolder : RecyclerView.ViewHolder {
        var img: ObservableField<String> = ObservableField()
        var name: ObservableField<String> = ObservableField()
        var binding: ItemTypeDeviceBinding

        constructor(binding: ItemTypeDeviceBinding) : super(binding.root) {
            this.binding = binding
        }

        fun setImg(img: String, name: String) {
            if (binding.viewModel == null) {
                binding.viewModel = this
                this.img.set(img)
                this.name.set(name)
            } else {
                this.img.set(img)
                this.name.set(name)
            }
        }
    }
}