package com.example.josephpham.smarhome.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.josephpham.smarhome.model.User
import com.example.josephpham.smarhome.viewmodel.UserViewModel
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.interfaces.Loading
import com.example.josephpham.smarhome.interfaces.UploadIMG
import com.example.josephpham.smarhome.database.DatabaseHandler
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ActivityUserBinding
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.dialog_active_mail.view.*
import kotlinx.android.synthetic.main.dialog_change_address.view.*
import kotlinx.android.synthetic.main.dialog_change_homephone.view.*
import kotlinx.android.synthetic.main.dialog_change_name.view.*
import kotlinx.android.synthetic.main.dialog_change_phonenumber.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class UserActivity : AppCompatActivity(), UploadIMG {
    var user = User()
    var mSocket = Connect.connect()
    val REQUEST_TAKE_PICTURE = 123
    var bitmap: Bitmap? = null
    var token: String = ""
    var binding: ActivityUserBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = DatabaseHandler(this@UserActivity)
        token = db.readData()
        Loading.loading(this@UserActivity)
        mSocket.emit("client_send_data_user", token)
        mSocket.on("server_send_data_user", onretrieveDataUser)
        mSocket.on("server_send_update_user", onretrieveupdataUser)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (user.status.get().toString().equals("Pending")) {
            menuInflater.inflate(R.menu.menu_user, menu)
        } else {
            menuInflater.inflate(R.menu.menu_user_active, menu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_update_name -> {
                openDialogUpdateName()

            }
            R.id.action_active_mail -> {
                openDialogActiveEmail()

            }
        }
        return true
    }
    var onretrieveDataUser: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                val correct = data.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    val result = data.getJSONObject("result")
                    user = User.parseJson(result)
                    binding = DataBindingUtil.setContentView(this@UserActivity, R.layout.activity_user)
                    setSupportActionBar(binding!!.toolbarUser)
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    var viewModel = UserViewModel(this@UserActivity, user)
                    binding!!.viewModel = viewModel
                    binding!!.event = this
                } else {
                    val err = data.getString("message")
                    val intent: Intent = Intent(this@UserActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }



    fun takePicture() {
        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_TAKE_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            bitmap = data!!.extras.get("data") as Bitmap
            bitmap = resize(bitmap!!, 200, 200)
            binding!!.civimguser.setImageBitmap(bitmap)
            val bytes = getByteArrayToByBitmap(bitmap!!)
            var json = JSONObject()
            json.put("token", token)
            var jsonsub = JSONObject()
            jsonsub.put("img", bytes)
            jsonsub.put("_id", user.id.get())
            json.put("data", jsonsub)
            emit(json)
        }
    }

    fun openDialogUpdateName() {
        val mBundle = AlertDialog.Builder(this@UserActivity)
        val mView = layoutInflater.inflate(R.layout.dialog_change_name, null)
        mBundle.setView(mView)
        val dialog = mBundle.create()
        mView.updatename.setText(user.name.get())
        mView.submitname.setOnClickListener {
            updateName(mView.updatename.text.toString())
            dialog.dismiss()
        }
        dialog.show()
    }

    fun openDialogActiveEmail() {
        val mBundle = AlertDialog.Builder(this@UserActivity)
        val mView = layoutInflater.inflate(R.layout.dialog_active_mail, null)
        mBundle.setView(mView)
        val dialog = mBundle.create()
        mView.submitActiveUser.setOnClickListener {
            val json = JSONObject()
            json.put("email", user.email)
            json.put("token", token)
            mSocket.emit("client_send_active_user", json)
            dialog.dismiss()
        }
        mView.cancelActive.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun openDialogChangeAddress(){
        val mBundle = AlertDialog.Builder(this@UserActivity)
        val mView = layoutInflater.inflate(R.layout.dialog_change_address, null)
        mView.streetupdate.setText(user.street.get().toString())
        mView.districtupdate.setText(user.district.get().toString())
        mView.cityupdate.setText(user.city.get().toString())
        mView.postcode.setText(user.postcode.get().toString())
        mBundle.setView(mView)
        val dialog = mBundle.create()

        mView.submitAddress.setOnClickListener {
            updateAddress(mView.streetupdate.text.toString(), mView.districtupdate.text.toString(),
                    mView.cityupdate.text.toString(), mView.postcode.text.toString())
            dialog.dismiss()
        }
        dialog.show()
    }

    fun openDialogChangePhoneNumber(): Boolean {
        val mBundle = AlertDialog.Builder(this@UserActivity)
        val mView = layoutInflater.inflate(R.layout.dialog_change_phonenumber, null)
        mView.updatephonenumber.setText(user.phonenumber.get())
        mBundle.setView(mView)
        val dialog = mBundle.create()
        mView.submitphonenumber.setOnClickListener {
            updatePhoneNumber(mView.updatephonenumber.text.toString())
            dialog.dismiss()
        }
        dialog.show()
        return true

    }

    fun openDialogChangeHomePhoneNumber(): Boolean {
        val mBundle = AlertDialog.Builder(this@UserActivity)
        val mView = layoutInflater.inflate(R.layout.dialog_change_homephone, null)
        mView.updatehomephone.setText(user.homephone.get())
        mBundle.setView(mView)
        val dialog = mBundle.create()
        mView.submithomephone.setOnClickListener {
            updateHomePhone(mView.updatehomephone.text.toString())
            dialog.dismiss()
        }
        dialog.show()
        return true
    }

    fun dataPickerDialog() {
        val calendar: Calendar = Calendar.getInstance()
        val ngay = calendar.get(Calendar.DATE)
        val thang = calendar.get(Calendar.MONTH)
        val nam = calendar.get(Calendar.YEAR)
        val dialog = DatePickerDialog(this@UserActivity, OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val dob = calendar.time
            updateDob(dob)
        }, nam, thang, ngay)
        dialog.show()
    }

    private fun updateName(name: String) {
        var json = JSONObject()
        var jsonsub = JSONObject()
        jsonsub.put("name", name)
        jsonsub.put("_id", user.id.get())
        json.put("token", token)
        json.put("data", jsonsub)
        emit(json)

    }

    private fun updateHomePhone(homephone: String) {
        var json = JSONObject()
        var jsonsub = JSONObject()
        jsonsub.put("homephone", homephone)
        jsonsub.put("_id", user.id.get())
        json.put("token", token)
        json.put("data", jsonsub)
        emit(json)
    }

    private fun updateAddress(street: String, district: String, city: String, postcode: String) {
        var json = JSONObject()
        var jsonsub = JSONObject()
        jsonsub.put("street", street)
        jsonsub.put("district", district)
        jsonsub.put("city", city)
        jsonsub.put("postcode", postcode)
        jsonsub.put("_id", user.id.get())
        json.put("token", token)
        json.put("data", jsonsub)
        emit(json)
    }

    private fun updatePhoneNumber(phonenumber: String) {
        var json = JSONObject()
        var jsonsub = JSONObject()
        jsonsub.put("phonenumber", phonenumber)
        jsonsub.put("_id", user.id.get())
        json.put("token", token)
        json.put("data", jsonsub)
        emit(json)
    }

    private fun updateDob(dob: Date) {
        var json = JSONObject()
        var jsonsub = JSONObject()
        jsonsub.put("dob", dob)
        jsonsub.put("_id", user.id.get())
        json.put("token", token)
        json.put("data", jsonsub)
        emit(json)
    }

    private fun emit(json: JSONObject) {
        mSocket.emit("client_send_update_user", json)
        Loading.loading(this)
    }

    var onretrieveupdataUser: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data1 = args[0] as JSONObject
            try {
                var correct = data1.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    Toast.makeText(this, "update succes", Toast.LENGTH_LONG).show()
                    mSocket.emit("client_send_data_user", token)
                } else {
                    val err = data1.getString("message")
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {

            }
        }
    }
}
