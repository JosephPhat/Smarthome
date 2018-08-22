package com.example.josephpham.smarhome.fragment

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.activity.LoginActivity
import com.example.josephpham.smarhome.adapter.FragmentModeAdapter
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.databinding.FragmentModeBinding
import com.example.josephpham.smarhome.interfaces.Loading
import com.example.josephpham.smarhome.model.Mode
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject


class Tab_Mode : Fragment() {
    var activity: Activity? = null
    var listMode: ArrayList<Mode>? = null
    var binding: FragmentModeBinding? = null
    var mSocket: Socket = Connect.connect()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mode, container, false)
        return binding!!.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity = getActivity()
        mSocket.emit("client_send_mode")
        mSocket.on("server_send_mode", onretrieveDataMode)

    }

    var onretrieveDataMode: Emitter.Listener = Emitter.Listener { args ->
        activity!!.runOnUiThread {
            val data1 = args[0] as JSONObject
            try {
                listMode = ArrayList()
                val correct = data1.getBoolean("success")
                if (correct == true) {
                    val modeJson = data1.getJSONArray("result")

                    for (i in 0..modeJson.length() - 1) {
                        val json: JSONObject = modeJson.getJSONObject(i)
                        val mode = Mode.parseJson(json)
                        listMode!!.add(mode)
                    }
                    addList()

                } else {
                    val err = data1.getString("message")
                    val intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                }
            } catch (e: JSONException) {

            }
        }
    }

    fun addList() {
        if (activity != null) {
            if (listMode != null) {
                binding!!.recyerview.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                val adapter = FragmentModeAdapter(activity as FragmentActivity, listMode!!)
                binding!!.recyerview.adapter = adapter
            } else {
                listMode = ArrayList()
                val adapter = FragmentModeAdapter(activity as FragmentActivity, listMode!!)
                binding!!.recyerview.adapter = adapter
            }
        }
    }

}