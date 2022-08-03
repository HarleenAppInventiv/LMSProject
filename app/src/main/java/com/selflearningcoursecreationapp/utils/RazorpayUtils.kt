package com.selflearningcoursecreationapp.utils

import android.app.Activity
import android.widget.Toast
import com.razorpay.Checkout
import com.razorpay.ExternalWalletListener
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.models.course.OrderData
import org.json.JSONObject

class RazorpayUtils() : PaymentResultWithDataListener, ExternalWalletListener {
    private var activity: Activity? = null
    private var orderData: OrderData? = null
    private var razorPayCallback: RazorPayCallback? = null
    fun isInitialized() = activity != null
    fun initRazorPay(activity: Activity) {
        this.activity = activity
        Checkout.preload(activity)
        razorPayCallback = (activity as RazorPayCallback)

    }

    fun startPayment(data: OrderData?) {
        this.orderData = data

        razorPayCallback?.orderDetail(data)
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = activity!!
        val co = Checkout()
        co.setKeyID(orderData?.razorpayKey)
        try {
            val options = JSONObject()
            options.put("name", orderData?.name)
            options.put("order_id", orderData?.orderId)
            options.put("description", orderData?.description)
            //You can omit the image option to fetch the image from dashboard
            options.put("image", orderData?.image)
            options.put("currency", orderData?.currency)
            options.put("amount", orderData?.amount)
            options.put("send_sms_hash", true);

            val theme = JSONObject()
            theme.put("color", orderData?.themeId)

            val notes = JSONObject()
            notes.put("address", orderData?.notes)

            val prefill = JSONObject()
            prefill.put("email", orderData?.prefillEmail)
            prefill.put("name", orderData?.prefillEmail)
            prefill.put("contact", orderData?.prefillContact)

            options.put("notes", notes)
            options.put("prefill", prefill)
            options.put("theme", theme)
            showLog("RAZORPAY_DATA", options.toString())
            co.open(activity, options)

        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }


    override fun onExternalWalletSelected(p0: String?, p1: PaymentData?) {
        showLog(tag = "RAZORPAY", msg = "Payment onExternalWalletSelected $p0 >>> $p1")

    }

//    override fun onPaymentSuccess(razorpayPaymentId: String?) {
//        showLog(tag = "RAZORPAY", msg = "Payment onPaymentSuccess $razorpayPaymentId ")
//
//    }
//
//    override fun onPaymentError(errorCode: Int, response: String?) {
//        showLog(tag = "RAZORPAY", msg = "Payment onPaymentError $errorCode >>> $response")
//    }


    override fun onPaymentSuccess(razorpayPaymentId: String?, p1: PaymentData?) {
        showLog(tag = "RAZORPAY", msg = "Payment onPaymentSuccess $razorpayPaymentId ... $p1 ")
    }

    override fun onPaymentError(errorCode: Int, response: String?, p2: PaymentData?) {
        showLog(tag = "RAZORPAY", msg = "Payment onPaymentError $errorCode >>> $response ...?? $p2")
    }

    interface RazorPayCallback {
        fun orderDetail(orderData: OrderData?)
    }
}