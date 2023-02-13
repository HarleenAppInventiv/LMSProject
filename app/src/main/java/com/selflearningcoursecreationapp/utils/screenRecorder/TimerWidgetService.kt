package com.selflearningcoursecreationapp.utils.screenRecorder

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.utils.ACTION_RECORDER_BROADCAST
import com.selflearningcoursecreationapp.utils.customViews.PulseCountDown
import com.selflearningcoursecreationapp.utils.screenRecorder.home.FloatingWidgetService
import com.selflearningcoursecreationapp.utils.screen_recording_v1.ScreenRecordService
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp

class TimerWidgetService : Service() {
    private var mWindowManager: WindowManager? = null
    private var mFloatingWidgetView: View? = null
    private val szWindow = Point()
    private var removeFloatingWidgetView: View? = null


    private lateinit var intentIs: Intent
    private var width: Int? = null
    private var height: Int? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        //init WindowManager
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager


        //Init LayoutInflater
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        addRemoveView(inflater)
        addFloatingWidgetView(inflater)
        //        implementClickListeners();
//        implementTouchListenerToFloatingWidgetView();
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intentIs = intent?.getParcelableExtra("data") ?: Intent()
        height = intent?.getIntExtra("height", 1080) ?: 1024
        width = intent?.getIntExtra("width", 720) ?: 720

        return super.onStartCommand(intent, flags, startId)
    }


    /*  Add Floating Widget View to Window Manager  */
    private fun addFloatingWidgetView(inflater: LayoutInflater) {
        //Inflate the floating view layout we created
        mFloatingWidgetView = inflater.inflate(R.layout.floating_widget_timer, null)

        //Add the view to the window.
        mFloatingWidgetView?.findViewById<PulseCountDown>(R.id.pulseCountDown)?.start {

            stopSelf()


        }


        val params = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
        }


        //Specify the view position
        params.gravity = Gravity.CENTER

        //Initially view will be added to top-left corner, you change x-y coordinates according to your need
//        params.x = 50
//        params.y = 50

        //Add the view to the window
        try {
            mWindowManager
                ?.addView(mFloatingWidgetView, params)

        } catch (e: Exception) {

        }

    }

    private val windowManagerDefaultDisplay: Unit
        get() {
            mWindowManager
                ?.defaultDisplay?.getSize(szWindow)
        }


    /*  Reset position of Floating Widget view on dragging  */
    private fun resetPosition(x_cord_now: Int) {
        //Variable to check if the Floating widget view is on left side or in right side
        // initially we are displaying Floating widget view to Left side so set it to true
        var isLeft = true
        if (x_cord_now <= szWindow.x / 2) {
            isLeft = true
            moveToLeft(x_cord_now)
        } else {
            isLeft = false
            moveToRight(x_cord_now)
        }
    }

    /*  Method to move the Floating widget view to Left  */
    private fun moveToLeft(current_x_cord: Int) {
        val x = szWindow.x - current_x_cord
        object : CountDownTimer(500, 5) {
            //get params of Floating Widget view
            val mParams = mFloatingWidgetView?.layoutParams as WindowManager.LayoutParams
            override fun onTick(t: Long) {
                val step = (500 - t) / 5
                mParams.x = 0 - (current_x_cord * current_x_cord * step).toInt()

                //If you want bounce effect uncomment below line and comment above line
                // mParams.x = 0 - (int) (double) bounceValue(step, x);


                //Update window manager for Floating Widget
                mWindowManager?.updateViewLayout(mFloatingWidgetView, mParams)
            }

            override fun onFinish() {
                mParams.x = 0

                //Update window manager for Floating Widget
                mWindowManager?.updateViewLayout(mFloatingWidgetView, mParams)
            }
        }.start()
    }

    /*  Method to move the Floating widget view to Right  */
    private fun moveToRight(current_x_cord: Int) {
        object : CountDownTimer(500, 5) {
            //get params of Floating Widget view
            val mParams = mFloatingWidgetView?.layoutParams as WindowManager.LayoutParams?
            override fun onTick(t: Long) {
                val step = (500 - t) / 5
                if (mParams != null && mFloatingWidgetView != null) {
                    mParams.x =
                        (szWindow.x + current_x_cord * current_x_cord * step - mFloatingWidgetView!!.width).toInt()

                    //If you want bounce effect uncomment below line and comment above line
                    //  mParams.x = szWindow.x + (int) (double) bounceValue(step, x_cord_now) - mFloatingWidgetView.getWidth();

                    //Update window manager for Floating Widget
                    mWindowManager?.updateViewLayout(mFloatingWidgetView, mParams)
                }
            }

            override fun onFinish() {
                if (mParams != null && mFloatingWidgetView != null) {
                    mParams.x = szWindow.x - mFloatingWidgetView!!.width

                    //Update window manager for Floating Widget
                    mWindowManager?.updateViewLayout(mFloatingWidgetView, mParams)
                }
            }
        }.start()
    }

    /*  Get Bounce value if you want to make bounce effect to your Floating Widget */
    private fun bounceValue(step: Long, scale: Long): Double {
        return scale * exp(-0.055 * step) * cos(0.08 * step)
    }

    /*  return status bar height on basis of device display metrics  */
    private val statusBarHeight: Int
        get() = ceil((25 * applicationContext.resources.displayMetrics.density).toDouble())
            .toInt()

    /*  Update Floating Widget view coordinates on Configuration change  */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        windowManagerDefaultDisplay
        val layoutParams = mFloatingWidgetView?.layoutParams as WindowManager.LayoutParams?
        if (layoutParams != null && mFloatingWidgetView != null) {

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (layoutParams.y + (mFloatingWidgetView!!.height + statusBarHeight) > szWindow.y) {
                    layoutParams.y = szWindow.y - (mFloatingWidgetView!!.height + statusBarHeight)
                    mWindowManager?.updateViewLayout(mFloatingWidgetView, layoutParams)
                }
                if (layoutParams.x != 0 && layoutParams.x < szWindow.x) {
                    resetPosition(szWindow.x)
                }
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (layoutParams.x > szWindow.x) {
                    resetPosition(szWindow.x)
                }
            }
        }
    }

    override fun onDestroy() {
        try {
            val intent2 = Intent(applicationContext, ScreenRecordService::class.java)
            intent2.putExtra("command", 1)
            intent2.putExtra("largeIconId", R.drawable.ic_notification_icon)
            intent2.putExtra("smallIconId", R.drawable.ic_notification_icon)
            intent2.putExtra("width", width)
            intent2.putExtra("height", height)
            intent2.putExtra("data", intentIs)
            startService(intent2)
            startService(
                Intent(
                    applicationContext,
                    FloatingWidgetService::class.java
                )
            )
//        val recorderIntent = Intent(ACTION_RECORDER_BROADCAST_NOTIFY)
//        recorderIntent.putExtras(bundleOf("recordingStatus" to 4))

            try {

                val recorderIntent = Intent(ACTION_RECORDER_BROADCAST)
                recorderIntent.putExtras(bundleOf("recordingStatus" to 4))
                LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(recorderIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (mFloatingWidgetView != null) mWindowManager?.removeView(
                mFloatingWidgetView
            )
            if (removeFloatingWidgetView != null) mWindowManager?.removeView(
                removeFloatingWidgetView
            )


        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }


}