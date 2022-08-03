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
        windowManagerDefaultDisplay

        //Init LayoutInflater
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        addRemoveView(inflater)
        addFloatingWidgetView(inflater)
        //        implementClickListeners();
//        implementTouchListenerToFloatingWidgetView();
    }

    /*  Add Remove View to Window Manager  */
    private fun addRemoveView(inflater: LayoutInflater): View {
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
        return remove_image_view!!
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
        params.gravity = Gravity.TOP or Gravity.LEFT

        //Initially view will be added to top-left corner, you change x-y coordinates according to your need
        params.x = 0
        params.y = 100

        //Add the view to the window
        mWindowManager
            ?.addView(mFloatingWidgetView, params)
    }

    private val windowManagerDefaultDisplay: Unit
        get() {
            mWindowManager
                ?.defaultDisplay?.getSize(szWindow)
        }

    //    /*  Implement Touch Listener to Floating Widget Root View  */
    //    private void implementTouchListenerToFloatingWidgetView() {
    //        //Drag and move floating view using user's touch action.
    //        mFloatingWidgetView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
    //
    //            long time_start = 0, time_end = 0;
    //
    //            boolean isLongClick = false;//variable to judge if user click long press
    //            boolean inBounded = false;//variable to judge if floating view is bounded to remove view
    //            int remove_img_width = 0, remove_img_height = 0;
    //
    //            final Handler handler_longClick = new Handler();
    //            final Runnable runnable_longClick = new Runnable() {
    //                @Override
    //                public void run() {
    //                    //On Floating Widget Long Click
    //
    //                    //Set isLongClick as true
    //                    isLongClick = true;
    //
    //                    //Set remove widget view visibility to VISIBLE
    //                    removeFloatingWidgetView.setVisibility(View.VISIBLE);
    //
    //                    onFloatingWidgetLongClick();
    //                }
    //            };
    //
    //            @Override
    //            public boolean onTouch(View v, MotionEvent event) {
    //
    //                //Get Floating widget view params
    //                WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) mFloatingWidgetView.getLayoutParams();
    //
    //                //get the touch location coordinates
    //                int x_cord = (int) event.getRawX();
    //                int y_cord = (int) event.getRawY();
    //
    //                int x_cord_Destination, y_cord_Destination;
    //
    //                switch (event.getAction()) {
    //                    case MotionEvent.ACTION_DOWN:
    //                        time_start = System.currentTimeMillis();
    //
    //                        handler_longClick.postDelayed(runnable_longClick, 600);
    //
    //                        remove_img_width = remove_image_view.getLayoutParams().width;
    //                        remove_img_height = remove_image_view.getLayoutParams().height;
    //
    //                        x_init_cord = x_cord;
    //                        y_init_cord = y_cord;
    //
    //                        //remember the initial position.
    //                        x_init_margin = layoutParams.x;
    //                        y_init_margin = layoutParams.y;
    //
    //                        return true;
    //                    case MotionEvent.ACTION_UP:
    //                        isLongClick = false;
    //                        removeFloatingWidgetView.setVisibility(View.GONE);
    //                        remove_image_view.getLayoutParams().height = remove_img_height;
    //                        remove_image_view.getLayoutParams().width = remove_img_width;
    //                        handler_longClick.removeCallbacks(runnable_longClick);
    //
    //                        //If user drag and drop the floating widget view into remove view then stop the service
    //                        if (inBounded) {
    //                            stopSelf();
    //                            inBounded = false;
    //                            break;
    //                        }
    //
    //
    //                        //Get the difference between initial coordinate and current coordinate
    //                        int x_diff = x_cord - x_init_cord;
    //                        int y_diff = y_cord - y_init_cord;
    //
    //                        //The check for x_diff <5 && y_diff< 5 because sometime elements moves a little while clicking.
    //                        //So that is click event.
    //                        if (Math.abs(x_diff) < 5 && Math.abs(y_diff) < 5) {
    //                            time_end = System.currentTimeMillis();
    //
    //
    //
    //                        }
    //
    //                        y_cord_Destination = y_init_margin + y_diff;
    //
    //                        int barHeight = getStatusBarHeight();
    //                        if (y_cord_Destination < 0) {
    //                            y_cord_Destination = 0;
    //                        } else if (y_cord_Destination + (mFloatingWidgetView.getHeight() + barHeight) > szWindow.y) {
    //                            y_cord_Destination = szWindow.y - (mFloatingWidgetView.getHeight() + barHeight);
    //                        }
    //
    //                        layoutParams.y = y_cord_Destination;
    //
    //                        inBounded = false;
    //
    //                        //reset position if user drags the floating view
    //                        resetPosition(x_cord);
    //
    //                        return true;
    //                    case MotionEvent.ACTION_MOVE:
    //                        int x_diff_move = x_cord - x_init_cord;
    //                        int y_diff_move = y_cord - y_init_cord;
    //
    //                        x_cord_Destination = x_init_margin + x_diff_move;
    //                        y_cord_Destination = y_init_margin + y_diff_move;
    //
    //                        //If user long click the floating view, update remove view
    //                        if (isLongClick) {
    //                            int x_bound_left = szWindow.x / 2 - (int) (remove_img_width * 1.5);
    //                            int x_bound_right = szWindow.x / 2 + (int) (remove_img_width * 1.5);
    //                            int y_bound_top = szWindow.y - (int) (remove_img_height * 1.5);
    //
    //                            //If Floating view comes under Remove View update Window Manager
    //                            if ((x_cord >= x_bound_left && x_cord <= x_bound_right) && y_cord >= y_bound_top) {
    //                                inBounded = true;
    //
    //                                int x_cord_remove = (int) ((szWindow.x - (remove_img_height * 1.5)) / 2);
    //                                int y_cord_remove = (int) (szWindow.y - ((remove_img_width * 1.5) + getStatusBarHeight()));
    //
    //                                if (remove_image_view.getLayoutParams().height == remove_img_height) {
    //                                    remove_image_view.getLayoutParams().height = (int) (remove_img_height * 1.5);
    //                                    remove_image_view.getLayoutParams().width = (int) (remove_img_width * 1.5);
    //
    //                                    WindowManager.LayoutParams param_remove = (WindowManager.LayoutParams) removeFloatingWidgetView.getLayoutParams();
    //                                    param_remove.x = x_cord_remove;
    //                                    param_remove.y = y_cord_remove;
    //
    //                                    mWindowManager.updateViewLayout(removeFloatingWidgetView, param_remove);
    //                                }
    //
    //                                layoutParams.x = x_cord_remove + (Math.abs(removeFloatingWidgetView.getWidth() - mFloatingWidgetView.getWidth())) / 2;
    //                                layoutParams.y = y_cord_remove + (Math.abs(removeFloatingWidgetView.getHeight() - mFloatingWidgetView.getHeight())) / 2;
    //
    //                                //Update the layout with new X & Y coordinate
    //                                mWindowManager.updateViewLayout(mFloatingWidgetView, layoutParams);
    //                                break;
    //                            } else {
    //                                //If Floating window gets out of the Remove view update Remove view again
    //                                inBounded = false;
    //                                remove_image_view.getLayoutParams().height = remove_img_height;
    //                                remove_image_view.getLayoutParams().width = remove_img_width;
    //
    //                            }
    //
    //                        }
    //
    //
    //                        layoutParams.x = x_cord_Destination;
    //                        layoutParams.y = y_cord_Destination;
    //
    //                        //Update the layout with new X & Y coordinate
    //                        mWindowManager.updateViewLayout(mFloatingWidgetView, layoutParams);
    //                        return true;
    //                }
    //                return false;
    //            }
    //        });
    //
    //    }
    /*  on Floating Widget Long Click, increase the size of remove view as it look like taking focus */
    private fun onFloatingWidgetLongClick() {
        //Get remove Floating view params
        val removeParams = removeFloatingWidgetView?.layoutParams as WindowManager.LayoutParams

        //get x and y coordinates of remove view
        val x_cord = (szWindow.x - removeFloatingWidgetView!!.width) / 2
        val y_cord = szWindow.y - (removeFloatingWidgetView!!.height + statusBarHeight)
        removeParams.x = x_cord
        removeParams.y = y_cord

        //Update Remove view params
        mWindowManager!!.updateViewLayout(removeFloatingWidgetView, removeParams)
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
            val mParams = mFloatingWidgetView!!.layoutParams as WindowManager.LayoutParams
            override fun onTick(t: Long) {
                val step = (500 - t) / 5
                mParams.x = 0 - (current_x_cord * current_x_cord * step).toInt()

                //If you want bounce effect uncomment below line and comment above line
                // mParams.x = 0 - (int) (double) bounceValue(step, x);


                //Update window manager for Floating Widget
                mWindowManager!!.updateViewLayout(mFloatingWidgetView, mParams)
            }

            override fun onFinish() {
                mParams.x = 0

                //Update window manager for Floating Widget
                mWindowManager!!.updateViewLayout(mFloatingWidgetView, mParams)
            }
        }.start()
    }

    /*  Method to move the Floating widget view to Right  */
    private fun moveToRight(current_x_cord: Int) {
        object : CountDownTimer(500, 5) {
            //get params of Floating Widget view
            val mParams = mFloatingWidgetView!!.layoutParams as WindowManager.LayoutParams
            override fun onTick(t: Long) {
                val step = (500 - t) / 5
                mParams.x =
                    (szWindow.x + current_x_cord * current_x_cord * step - mFloatingWidgetView!!.width).toInt()

                //If you want bounce effect uncomment below line and comment above line
                //  mParams.x = szWindow.x + (int) (double) bounceValue(step, x_cord_now) - mFloatingWidgetView.getWidth();

                //Update window manager for Floating Widget
                mWindowManager!!.updateViewLayout(mFloatingWidgetView, mParams)
            }

            override fun onFinish() {
                mParams.x = szWindow.x - mFloatingWidgetView!!.width

                //Update window manager for Floating Widget
                mWindowManager!!.updateViewLayout(mFloatingWidgetView, mParams)
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
        val layoutParams = mFloatingWidgetView!!.layoutParams as WindowManager.LayoutParams
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

    override fun onDestroy() {
        super.onDestroy()
        if (mFloatingWidgetView != null) mWindowManager!!.removeView(
            mFloatingWidgetView
        )
        if (removeFloatingWidgetView != null) mWindowManager?.removeView(removeFloatingWidgetView)
    }
}