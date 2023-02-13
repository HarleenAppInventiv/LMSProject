package com.selflearningcoursecreationapp.ui.dialog.singleChoice

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.databinding.DialogRequestFailedBinding
import com.selflearningcoursecreationapp.extensions.loadImage
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.utils.Constant
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class NetworkFailDialogue : BaseDialog<DialogRequestFailedBinding>() {

    private var isNetworkError: Boolean = false
    private var apiError: ApiError? = null
    private var fromHome: Boolean = false

    override fun getLayoutRes(): Int {
        return R.layout.dialog_request_failed
    }

    @SuppressLint("ResourceType")
    override fun initUi() {

        arguments?.let {
            isNetworkError = arguments?.getBoolean("networkError", false) ?: false
            fromHome = arguments?.getBoolean("fromHome", false) ?: false
            apiError = arguments?.getParcelable("exception")
        }

        binding.tvGoToDownlaod.visibleView(isNetworkError && fromHome)
        if (isNetworkError) {
            binding.btnSubmit.text = baseActivity.getString(R.string.try_again)
            binding.tvErrorSubHeading.text =
                baseActivity.getString(R.string.not_stable_internet_text)
            binding.tvErrorHeading.text = baseActivity.getString(R.string.no_internet)
            binding.ivError.setImageResource(R.drawable.ic__network_error)
        } else {
            when (apiError?.exception) {
                is SocketTimeoutException, is TimeoutException, is IOException -> {
                    binding.btnSubmit.text = baseActivity.getString(R.string.retry)
                    binding.tvErrorSubHeading.text =
                        baseActivity.getString(R.string.request_failed_retry)
                    binding.tvErrorHeading.text = baseActivity.getString(R.string.oops)
                    binding.ivError.setImageResource(R.drawable.ic_request_failed)

                }
                else -> {
                    binding.btnSubmit.text = baseActivity.getString(R.string.try_again)
                    binding.tvErrorSubHeading.text =
                        baseActivity.getString(R.string.error_on_screen_text)
                    binding.tvErrorHeading.text = baseActivity.getString(R.string.oh_snap)
                    binding.ivError.setImageResource(R.drawable.ic_no_page)

                }
            }
        }


        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        binding.btnSubmit.setOnClickListener {
            onDialogClick(
                Constant.CLICK_ADD,
                arguments?.getString("apiCode", "").toString(),
                arguments?.getBoolean("networkError", false) ?: false
            )
            dismiss()


        }
        binding.tvGoToDownlaod.setOnClickListener {
            onDialogClick(Constant.CLICK_VIEW)


            dismiss()
        }

        changeImage(arguments?.getBoolean("networkError", false) ?: false)

    }

    /*
    No Use
    */
    override fun onRetry(apiCode: String, networkError: Boolean, exception: ApiError) {

    }

    override fun onApiRetry(apiCode: String) {

    }

    private fun changeImage(isNetworkFailDialogue: Boolean) {
        if (isNetworkFailDialogue)
            binding.ivError.loadImage(R.drawable.ic__network_error)


    }


}