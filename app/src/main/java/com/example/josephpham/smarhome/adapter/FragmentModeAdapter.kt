package com.example.josephpham.smarhome.adapter

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.activity.ModeActivity
import com.example.josephpham.smarhome.databinding.ItemModeBinding
import com.example.josephpham.smarhome.model.Mode


class FragmentModeAdapter : RecyclerView.Adapter<FragmentModeAdapter.ViewHolder> {


    var modeList = ArrayList<Mode>()
    var context: Context

    constructor(context: Context, modeList: ArrayList<Mode>) {
        this.context = context
        this.modeList = modeList
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.set(modeList.get(position).status.get(), modeList.get(position).mode_name.get().toString(),
                modeList.get(position).time(), modeList.get(position).circleString(), modeList.get(position).id.get().toString())

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemModeBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_mode, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        Log.d("size", modeList.size.toString())
        return modeList.size
    }


    inner class ViewHolder : RecyclerView.ViewHolder {
        var status: ObservableBoolean = ObservableBoolean()
        var name: ObservableField<String> = ObservableField()
        var time: ObservableField<String> = ObservableField()
        var circle: ObservableField<String> = ObservableField()
        var id: ObservableField<String> = ObservableField()

        var binding: ItemModeBinding

        constructor(binding: ItemModeBinding) : super(binding.root) {
            this.binding = binding
        }

        fun set(img: Boolean, name: String, circle: String, time: String, id: String) {
            if (binding.viewModel == null) {
                binding.viewModel = this
                this.status.set(img)
                this.name.set(name)
                this.time.set(time)
                this.circle.set(circle)
                this.id.set(id)
            } else {
                this.status.set(img)
                this.name.set(name)
                this.time.set(time)
                this.circle.set(circle)
                this.id.set(id)

            }
        }

        fun onClick() {
            val intent = Intent(context, ModeActivity::class.java)

            intent.putExtra("id_mode", this.id.get().toString())
            context.startActivity(intent)
        }
    }
}