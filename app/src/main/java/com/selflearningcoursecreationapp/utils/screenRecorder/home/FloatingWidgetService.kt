package com.selflearningcoursecreationapp.utils.screenRecorder.home

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
import android.widget.ImageView
import com.selflearningcoursecreationapp.R
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.exp

class FloatingWidgetService : Service() {
    private var mWindowManager: WindowManager? = null
    private var mFloatingWidgetView: View? = null
    private val szWindow = Point()
    private var removeFloatingWidgetView: View? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        //init WindowManager
        mWindowManager = getSystemService(WINDOW_SERVICE) as WindowManager


        //Init LayoutInflater
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
       addRemoveView(inflater)
        addFloatingWidgetView(inflater)
        //        implementClickListeners();
//        implementTouchListenerToFloatingWidgetView();
    }

    /*  Add Remove View to Window Manager  */
    private fun addRemoveView(inflater: LayoutInflater): View? {
        //Inflate the removing view layout we created
        removeFloatingWidgetView = inflater.inflate(R.layout.remove_floating_widget_layout, null)

        //Add the view to the window.
        val paramRemove: WindowManager.LayoutParams =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
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
        paramRemove.gravity = Gravity.TOP or Gravity.LEFT

        //Initially the Removing widget view is not visible, so set visibility to GONE
        removeFloatingWidgetView?.visibility = View.GONE
        val remove_image_view = removeFloatingWidgetView?.findViewById<ImageView>(R.id.remove_img)

        //Add the view to the window
        mWindowManager?.addView(removeFloatingWidgetView, paramRemove)
        return remove_image_view
    }

    /*  Add Floating Widget View to Window Manager  */
    private fun addFloatingWidgetView(inflater: LayoutInflater) {
        //Inflate the floating view layout we created
        mFloatingWidgetView = inflater.inflate(R.layout.floating_widget_layout, null)

        //Add the view to the window.


        val params = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT
            )
        } else {
            WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT
            )
        }


        //Specify the view position
        params.gravity = Gravity.TOP or Gravity.LEFT

        //Initially view will be added to top-left corner, you change x-y coordinates according to your need
        params.x = 50
        params.y = 50

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

    /*  Method to move the Floating widget video Right  */
    private fun moveToRight(current_x_cord: Int) {
        object : CountDownTimer(500, 5) {
            //get params of Floating Widget view
            val mParams = mFloatingWidgetView?.layoutParams as WindowManager.LayoutParams?
            override fun onTick(t: Long) {
                val step = (500 - t) / 5
                mParams?.x =
                    (szWindow.x + current_x_cord * current_x_cord * step - (mFloatingWidgetView?.width
                        ?: 0)).toInt()

                //If you want bounce effect uncomment below line and comment above line
                //  mParams.x = szWindow.x + (int) (double) bounceValue(step, x_cord_now) - mFloatingWidgetView.getWidth();

                //Update window manager for Floating Widget
                mWindowManager?.updateViewLayout(mFloatingWidgetView, mParams)
            }

            override fun onFinish() {
                mParams?.x = szWindow.x - (mFloatingWidgetView?.width ?: 0)

                //Update window manager for Floating Widget
                mWindowManager?.updateViewLayout(mFloatingWidgetView, mParams)
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
        super.onDestroy()
        if (mFloatingWidgetView != null) mWindowManager?.removeView(
            mFloatingWidgetView
        )
        if (removeFloatingWidgetView != null) mWindowManager?.removeView(removeFloatingWidgetView)
    }
}