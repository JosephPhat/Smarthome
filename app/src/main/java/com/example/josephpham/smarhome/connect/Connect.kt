package com.example.josephpham.smarhome.connect

import io.socket.client.IO
import io.socket.client.Socket


class Connect {
    companion object {
        var msocket: Socket? = null
        fun connect(): Socket{
            if(msocket == null) {
                msocket = IO.socket("https://smarthome2018.herokuapp.com/")
//                msocket = IO.socket("http://192.168.1.240:3000")
                msocket?.connect()
            }

            return msocket!!
        }
        fun disConnect(): Boolean{
            if(msocket != null){
                msocket!!.disconnect()
                return true
            }
            return false
        }
    }
}