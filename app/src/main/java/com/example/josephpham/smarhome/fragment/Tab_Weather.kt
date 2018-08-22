package com.example.josephpham.smarhome.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.activity.LoginActivity
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.databinding.FragmentRoomBinding
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class Tab_Weather : Fragment() {
    var activity: Activity? = null
    var binding: FragmentRoomBinding? = null
    var mSocket: Socket = Connect.connect()
    private var temp : String = ""
    private var humi : String = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_weather, container, false)
        return rootView
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity = getActivity()
        mSocket.on("server_send_weather", onretrieveData)

    }
    var onretrieveData: Emitter.Listener = Emitter.Listener { args ->
        activity!!.runOnUiThread {
            val data = args[0] as JSONObject
            try {
                var correct = data.getBoolean("success")
                if (correct == true) {
                    val json = JSONObject("result")
                    temp = json.getString("temp")
                    humi = json.getString("humi")

                } else {
                    val err = data.getString("message")
                    val intent: Intent = Intent(activity, LoginActivity::class.java)
                    startActivity(intent)
                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }
    fun getTemp(): String{
        return this.getTemp()
    }
    fun getHumi(): String{
        return this.humi
    }
    fun getCalendar(): String{
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val time = "" + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + "" + calendar.get(Calendar.DAY_OF_WEEK) + ", " + calendar.get(Calendar.DAY_OF_MONTH) +  calendar.get(Calendar.MONTH) +  calendar.get(Calendar.YEAR)
        return time

    }

}