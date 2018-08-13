package com.example.josephpham.smarhome.activity


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView

import android.content.Intent
import android.databinding.DataBindingUtil
import android.util.Log
import android.widget.Toast
import com.example.josephpham.smarhome.interfaces.MD5
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.interfaces.Loading
import com.example.josephpham.smarhome.viewmodel.LoginViewModel
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ActivityLoginBinding
import com.example.josephpham.smarhome.database.DatabaseHandler
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject


class LoginActivity() : AppCompatActivity(), MD5 {
    var msocket: Socket = Connect.connect()

    var binding: ActivityLoginBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@LoginActivity, R.layout.activity_login)
        var viewModel = LoginViewModel(this@LoginActivity)
        binding!!.viewModel = viewModel
        binding!!.loginViewModel = this
        addEvent()
        msocket.on("server_send_login", onretrievetoken)

    }

    fun addEvent() {
        binding!!.password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                return@OnEditorActionListener true
            }
            false
        })

    }

    var onretrievetoken: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {

            val data = args[0] as JSONObject
            try {
                var token = data.getString("token")
                Loading.dismiss()
                if (token.equals("error")) {
                    Toast.makeText(this@LoginActivity, "Email or password error! " + "\n" +
                            "login try again", Toast.LENGTH_SHORT).show()
                } else {
                    val db = DatabaseHandler(this@LoginActivity)
                    db.insertTable(token)
                    var intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }


    fun attemptLogin() {
        binding!!.email.error = null
        binding!!.password.error = null
        // Store values at the time of the login attempt.
        val emailStr = binding!!.email.text.toString()
        val passwordStr = binding!!.password.text.toString()

        var cancel = false
        var focusView: View? = null
        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(passwordStr)) {
            binding!!.password.error = getString(R.string.error_invalid_password)
            focusView = binding!!.password
            cancel = true
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            binding!!.email.error = getString(R.string.error_field_required)
            focusView = binding!!.email
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            binding!!.email.error = getString(R.string.error_invalid_email)
            focusView = binding!!.email
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            var data: JSONObject = JSONObject()
            data.put("email", emailStr)
            data.put("password", md5(passwordStr))
            msocket.emit("client_send_login", data)
            Loading.loading(this@LoginActivity)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        if (!email.contains("@")) {
            return false
        } else {
            return true
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        if (password.length < 6) {
            return false
        } else {
            return true
        }
    }

}
