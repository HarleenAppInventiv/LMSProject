package com.selflearningcoursecreationapp.utils.screenRecorder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.utils.screenRecorder.settings.PreferenceHelper


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            onFirstCreate()
        }
        // super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        try {
//            navController = findNavController(R.id.main_nav_host_fragment)
//            findViewById<Toolbar?>(R.id.toolbar)?.let {
//                setSupportActionBar(it)
//                val appBarConfiguration = AppBarConfiguration(
//                    setOf(R.id.home, R.id.navigation_dialog, R.id.more_settings_dialog)
//                )
//                NavigationUI.setupActionBarWithNavController(
//                    this,
//                    navController,
//                    appBarConfiguration
//                )
//            }
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        addFragment()

    }

    fun addFragment() {
//    val fragmentManager = this.parentFragmentManager

        val manager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.add(
            R.id.main_nav_host_fragment,
            com.selflearningcoursecreationapp.utils.screenRecorder.home.HomeFragment()
        )
        transaction.addToBackStack(null)
        transaction.commit()
    }


    /**
     * Called the first time the activity is created.
     */
    private fun onFirstCreate() {
        PreferenceHelper(this).apply {
            // Apply theme before onCreate
            applyNightMode(nightMode)
            initIfFirstTimeAnd {
                createNotificationChannels()
            }
        }
    }

    override fun onSupportNavigateUp() = navController.navigateUp()

    companion object {
        const val ACTION_TOGGLE_RECORDING = "com.ibashkimi.screenrecorder.TOGGLE_RECORDING"
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


}