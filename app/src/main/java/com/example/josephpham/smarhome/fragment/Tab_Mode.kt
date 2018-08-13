package com.example.josephpham.smarhome.fragment

import android.app.Activity
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.josephpham.smarhome.activity.MainActivity
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.adapter.FragmentModeAdapter
import com.example.josephpham.smarhome.databinding.FragmentModeBinding


class Tab_Mode : Fragment() {
    var activity: Activity? = null
    var listmode = MainActivity.listMode
    var binding: FragmentModeBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mode, container, false)
        return binding!!.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity = getActivity()
        if (activity != null) {
            if (listmode != null) {
                binding!!.recyerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                val adapter = FragmentModeAdapter(activity as FragmentActivity, listmode!!)
                binding!!.recyerview.adapter = adapter
            } else {
                listmode = ArrayList()
                val adapter = FragmentModeAdapter(activity as FragmentActivity, listmode!!)
                binding!!.recyerview.adapter = adapter
            }
        }
    }
}