package com.example.josephpham.smarhome.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.josephpham.smarhome.model.KeyOnOffDevice
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ItemKeyBinding

class KeyAdapter: RecyclerView.Adapter<KeyAdapter.ViewHolder>{


    var listkey: ArrayList<KeyOnOffDevice>
    var context: Context? = null

    constructor(context: Context, listkey: ArrayList<KeyOnOffDevice>) {
        this.context = context
        this.listkey = listkey
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemKeyBinding>(LayoutInflater.from(context), R.layout.item_key, parent, false )
        return  ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listkey.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setKey(listkey.get(position).key.get().toString())
    }


    inner class ViewHolder : RecyclerView.ViewHolder {
        var key:  ObservableField<String> = ObservableField()
        var binding: ItemKeyBinding

        constructor(binding: ItemKeyBinding) : super(binding.root) {
            this.binding = binding
        }
        fun setKey(key: String){
            this.key.set(key)
        }

    }
}