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
import com.example.josephpham.smarhome.fragment.Tab_Weather
import com.example.josephpham.smarhome.fragment.Tab_Mode
import com.example.josephpham.smarhome.interfaces.Loading
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
    var user: User? = null
    var mSocket: Socket = Connect.connect()
    var binding: ActivityMainBinding? = null
    var token: String = ""
    val db = DatabaseHandler(this@MainActivity)
    private val REQ_CODE_SPEECH_INPUT = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        setSupportActionBar(binding!!.appBar!!.toolbar)
        mSocket.on("server_send_data_user", onretrieveDataUser)
        emit()
        binding!!.appBar!!.fab.setOnClickListener {
            fabOnClick()
        }
        binding!!.appBar!!.voice.setOnClickListener {
            voice()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    val result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val key: String = result[0]

                    mSocket.emit("client_send_key", key)

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
        when (item.itemId) {
            R.id.action_list_device -> {
                val intent = Intent(this@MainActivity, ListDeviceActivity::class.java)
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
        initViews()

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
                    customHeadđer()
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
    }
    fun voice(){
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
        binding!!.appBar!!.tabLayout.getTabAt(2)!!.setIcon(R.drawable.ico_weather)

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
                    val tab3 = Tab_Weather()
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
