package com.example.josephpham.smarhome.fragment

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.josephpham.smarhome.activity.MainActivity
import com.example.josephpham.smarhome.adapter.FragmentRoomAdapter
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.FragmentRoomBinding

class Tab_Room: Fragment() {
    var activity: Activity? = null
    var listRoom = MainActivity.listRoom
    var binding : FragmentRoomBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_room, container, false)
        return binding!!.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity = getActivity()
        if (activity != null) {
            if (listRoom != null) {
                val adapter = FragmentRoomAdapter(activity as FragmentActivity, listRoom!!)
                binding!!.listRoom.adapter = adapter
            }else{
                listRoom = ArrayList()
                val adapter = FragmentRoomAdapter(activity as FragmentActivity, listRoom!!)
                binding!!.listRoom.adapter = adapter
            }
        }
    }
}