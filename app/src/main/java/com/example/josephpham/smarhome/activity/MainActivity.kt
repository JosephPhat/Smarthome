package com.example.josephpham.smarhome.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.example.josephpham.smarhome.model.User
import com.example.josephpham.smarhome.viewmodel.UserViewModel
import com.example.josephpham.smarhome.connect.Connect
import com.example.josephpham.smarhome.fragment.Tab_Room
import com.example.josephpham.smarhome.fragment.Tab_Camera
import com.example.josephpham.smarhome.fragment.Tab_Mode
import com.example.josephpham.smarhome.interfaces.Loading
import com.example.josephpham.smarhome.model.Mode
import com.example.josephpham.smarhome.model.Room
import com.example.josephpham.smarhome.R
import com.example.josephpham.smarhome.databinding.ActivityMainBinding
import com.example.josephpham.smarhome.databinding.NavHeaderMainBinding
import com.example.josephpham.smarhome.database.DatabaseHandler
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.fablayout.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        var user: User? = null
        var listMode: ArrayList<Mode>? = null
        var listRoom: ArrayList<Room>? = null
    }

    var mSocket: Socket = Connect.connect()
    var binding: ActivityMainBinding? = null
    var token: String = ""
    val db = DatabaseHandler(this@MainActivity)
    private val REQ_CODE_SPEECH_INPUT = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        setSupportActionBar(binding!!.appBar!!.toolbar)
        mSocket.on("server_send_data_user", onretrieveDataUser)
        mSocket.on("server_send_mode", onretrieveDataMode)
        mSocket.on("server_send_room", onretrieveDataRoom)
        emit()
        binding!!.appBar!!.fab.setOnClickListener {
            fabOnClick()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    val result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    mSocket.emit("clent_send_key", result[0])
                }
            }
        }
    }

    fun emit() {
        token = db.readData()
        Loading.loading(this@MainActivity)
        mSocket.emit("client_send_data_user", token)

    }

    override fun onBackPressed() {
        if (binding!!.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding!!.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_list_device -> {
                val intent = Intent(this@MainActivity, ListDeviceActivity::class.java)
                intent.putExtra("id_user", user!!.id.get())
                startActivity(intent)

            }
            R.id.action_add_device -> {
                val intent = Intent(this@MainActivity, AddDeviceActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_camera -> {

            }
            R.id.profile -> {
                val intent = Intent(this@MainActivity, UserActivity::class.java)
                startActivity(intent)
            }

            R.id.logout -> {
                Connect.disConnect()
                db.deletes()
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        binding!!.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun customHeadđer() {
        val toggle = ActionBarDrawerToggle(
                this@MainActivity, binding!!.drawerLayout, binding!!.appBar!!.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding!!.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        var userBinding: NavHeaderMainBinding = DataBindingUtil.inflate(layoutInflater, R.layout.nav_header_main, binding!!.navView, false)
        binding!!.navView.setNavigationItemSelectedListener(this@MainActivity)
        binding!!.navView.removeHeaderView(binding!!.navView.getHeaderView(0))
        binding!!.navView.addHeaderView(userBinding.root)
        var userViewModel = UserViewModel(this@MainActivity, user!!)
        userBinding.user = userViewModel

    }

    var onretrieveDataUser: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                val correct = data.getBoolean("success")
                if (correct == true) {
                    val result = data.getJSONObject("result")
                    user = User.parseJson(result)
                    customHeadđer()
                    mSocket.emit("client_send_mode")
                } else {
                    val err = data.getString("message")
                    val intent: Intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }
    var onretrieveDataRoom: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data = args[0] as JSONObject
            try {
                var correct = data.getBoolean("success")
                listRoom = ArrayList()
                if (correct == true) {
                    val result = data.getJSONArray("result")
                    for (i in 0..result.length() - 1) {
                        val jsonObject = result.getJSONObject(i)
                        val room = Room.parseJson(jsonObject)
                        listRoom!!.add(room)
                    }
                    initViews()
                } else {
                    val err = data.getString("message")
                    val intent: Intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                Log.d("EEEE", e.toString())
            }
        }
    }
    var onretrieveDataMode: Emitter.Listener = Emitter.Listener { args ->
        runOnUiThread {
            val data1 = args[0] as JSONObject
            try {
                listMode = ArrayList()
                val correct = data1.getBoolean("success")
                Loading.dismiss()
                if (correct == true) {
                    val modeJson = data1.getJSONArray("result")
                    for (i in 0..modeJson.length() - 1) {
                        val json: JSONObject = modeJson.getJSONObject(i)
                        val mode = Mode.parseJson(json)
                        listMode!!.add(mode)
                    }
                    mSocket.emit("client_send_room")
                } else {
                    val err = data1.getString("message")
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, err.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {

            }
        }
    }

    fun fabOnClick() {
        val mHideButton = AnimationUtils.loadAnimation(this, R.anim.hide_button)
        val mShowButton = AnimationUtils.loadAnimation(this, R.anim.show_button)
        val mHideLayout = AnimationUtils.loadAnimation(this, R.anim.hide_layout)
        val mShowLayout = AnimationUtils.loadAnimation(this, R.anim.show_layout)

        if (binding!!.appBar!!.fab_sup_main.addRoomLayout.visibility == View.VISIBLE &&
                binding!!.appBar!!.fab_sup_main.addModeLayout.visibility == View.VISIBLE) {
            binding!!.appBar!!.fab_sup_main.addRoomLayout.visibility = View.GONE
            binding!!.appBar!!.fab_sup_main.addModeLayout.visibility = View.GONE
            binding!!.appBar!!.fab_sup_main.addRoomLayout.startAnimation(mHideLayout)
            binding!!.appBar!!.fab_sup_main.addModeLayout.startAnimation(mHideLayout)
            binding!!.appBar!!.fab.startAnimation(mHideButton)
        } else {
            binding!!.appBar!!.fab_sup_main.addRoomLayout.visibility = View.VISIBLE
            binding!!.appBar!!.fab_sup_main.addModeLayout.visibility = View.VISIBLE
            binding!!.appBar!!.fab_sup_main.addRoomLayout.startAnimation(mShowLayout)
            binding!!.appBar!!.fab_sup_main.addModeLayout.startAnimation(mShowLayout)
            binding!!.appBar!!.fab.startAnimation(mShowButton)
        }
        binding!!.appBar!!.fab_sup_main.addRoom.setOnClickListener { view ->
            val intent = Intent(this@MainActivity, AddRoomActivity::class.java)
            startActivity(intent)
        }
        binding!!.appBar!!.fab_sup_main.addMode.setOnClickListener { view ->
            val intent: Intent = Intent(this@MainActivity, AddModeActivity::class.java)
            intent.putExtra("id_user", user!!.id.get())
            startActivity(intent)
        }
        binding!!.appBar!!.voice.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    getString(R.string.speech_prompt))
            try {
                startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
            } catch (a: ActivityNotFoundException) {
                Toast.makeText(applicationContext,
                        getString(R.string.speech_not_supported),
                        Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun initViews() {
        val viewPager = binding!!.appBar!!.view_pager
        viewPager.adapter = SectionsPagerAdapter(supportFragmentManager)
        binding!!.appBar!!.tabLayout.setupWithViewPager(viewPager)
        binding!!.appBar!!.tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_room_key_while)
        binding!!.appBar!!.tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_mode_while)
        binding!!.appBar!!.tabLayout.getTabAt(2)!!.setIcon(R.drawable.ic_camera_alt_while_24dp)

    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> {
                    val tab1 = Tab_Room()
                    return tab1
                }
                1 -> {
                    val tab2 = Tab_Mode()
                    return tab2
                }
                2 -> {
                    val tab3 = Tab_Camera()
                    return tab3
                }

            }
            return Tab_Room()
        }

        override fun getCount(): Int {
            return 3
        }
    }
}
