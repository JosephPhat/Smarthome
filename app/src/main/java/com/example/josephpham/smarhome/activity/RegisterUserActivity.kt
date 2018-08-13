package com.example.josephpham.smarhome.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.interfaces.Loading
import com.example.josephpham.smarhome.interfaces.MD5
import com.example.josephpham.smarhome.interfaces.UploadIMG
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ActivityRegisterBinding
import io.socket.emitter.Emitter
import org.json.JSONException


class RegisterUserActivity : AppCompatActivity(), MD5, UploadIMG {

    var mSocket = Connect.connect()
    var dob: Date? = null
    val REQUEST_TAKE_PICTURE = 123
    var byte: ByteArray? = null
    var binding: ActivityRegisterBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binding!!.viewModel = this
        setSupportActionBar(binding!!.toolbarRegister)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mSocket.on("server_send_register", onretrieveDateRegister)
    }

    fun takePicture() {
        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_TAKE_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            var bitmap = data!!.extras.get("data") as Bitmap
            bitmap = resize(bitmap, 200, 200)
            byte = getByteArrayToByBitmap(bitmap)
            binding!!.imgProfile.setImageBitmap(bitmap)
        }
    }

    fun dataPickerDialog(){
        val calendar: Calendar = Calendar.getInstance()
        val ngay = calendar.get(Calendar.DATE)
        val thang = calendar.get(Calendar.MONTH)
        val nam = calendar.get(Calendar.YEAR)
        val dialog = DatePickerDialog(this@RegisterUserActivity, OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            var simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            binding!!.tvDob.setText(simpleDateFormat.format(calendar.time))
            dob = calendar.time
        }, nam, thang, ngay)
        dialog.show()
    }

    fun attemptRegister() {
        binding!!.tvName.error = null
        binding!!.tvEmail.error = null
        binding!!.tvStreet.error = null
        binding!!.tvDistrict.error = null
        binding!!.tvCity.error = null
        binding!!.tvPassword.error = null
        binding!!.tvPhone.error = null
        binding!!.tvDob.error = null
        // Store values at the time of the login attempt.
        val nameStr = binding!!.tvName.text.toString()
        val emailStr = binding!!.tvEmail.text.toString()
        val streetStr = binding!!.tvStreet.text.toString()
        val districtStr = binding!!.tvDistrict.text.toString()
        val cityStr = binding!!.tvCity.text.toString()
        val postcodeStr = binding!!.tvPostcode.text.toString()
        val passwordStr = binding!!.tvPassword.text.toString()
        val cfPassword = binding!!.tvCfpassword.text.toString()
        val phonenumberStr = binding!!.tvPhone.text.toString()

        var cancel = false
        var focusView: View? = null
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(nameStr)) {
            binding!!.tvName.error = getString(R.string.error_field_required)
            focusView = binding!!.tvName
            cancel = true
        }
        if (TextUtils.isEmpty(streetStr)) {
            binding!!.tvStreet.error = getString(R.string.error_field_required)
            focusView = binding!!.tvStreet
            cancel = true
        }
        if (TextUtils.isEmpty(districtStr)) {
            binding!!.tvDistrict.error = getString(R.string.error_field_required)
            focusView = binding!!.tvDistrict
            cancel = true
        }
        if (TextUtils.isEmpty(cityStr)) {
            binding!!.tvCity.error = getString(R.string.error_field_required)
            focusView = binding!!.tvCity
            cancel = true
        }
        if (TextUtils.isEmpty(cityStr)) {
            binding!!.tvPostcode.error = getString(R.string.error_field_required)
            focusView = binding!!.tvPostcode
            cancel = true
        }
        if (TextUtils.isEmpty(phonenumberStr.toString())) {
            binding!!.tvPhone.error = getString(R.string.error_field_required)
            focusView = binding!!.tvPhone
            cancel = true
        }
        if (!isPasswordValid(passwordStr)) {
            binding!!.tvPassword.error = getString(R.string.error_invalid_password)
            focusView = binding!!.tvPassword
            cancel = true
        }
        if (!isCfPasswordValid(passwordStr, cfPassword)) {
            binding!!.tvCfpassword.error = getString(R.string.error_invalid_cfpassword)
            focusView = binding!!.tvCfpassword
            cancel = true
        }
        if (dob == null) {
            binding!!.tvDob.error = getString(R.string.error_field_required)
            focusView = binding!!.tvDob
            cancel = true
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(emailStr)) {
            binding!!.tvEmail.error = getString(R.string.error_field_required)
            focusView = binding!!.tvEmail
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            binding!!.tvEmail.error = getString(R.string.error_invalid_email)
            focusView = binding!!.tvEmail
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            var data: JSONObject = JSONObject()
            data.put("name", nameStr)
            data.put("email", emailStr)
            data.put("street", streetStr)
            data.put("district", districtStr)
            data.put("city", cityStr)
            data.put("phonenumber", phonenumberStr)
            data.put("homephone", binding!!.tvHomephone.text.toString())
            data.put("dob", dob)
            data.put("password", md5(passwordStr))
            data.put("img", byte)
            data.put("postcode", postcodeStr)
            mSocket.emit("client_send_register", data)
            Loading.loading(this@RegisterUserActivity)
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

    private fun isCfPasswordValid(password: String, cfPassword: String): Boolean {
        if (!password.equals(cfPassword)) {
            return false
        } else {
            return true
        }
    }

    var onretrieveDateRegister: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data1 = args[0] as JSONObject
            try {
                var correct = data1.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    Toast.makeText(this@RegisterUserActivity, "register success", Toast.LENGTH_LONG).show()
                    var intent = Intent(this@RegisterUserActivity, LoginActivity::class.java)
                    startActivity(intent)

                } else {
                    val err = data1.getString("message")
                    Toast.makeText(this@RegisterUserActivity, err.toString().trim(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }
}
