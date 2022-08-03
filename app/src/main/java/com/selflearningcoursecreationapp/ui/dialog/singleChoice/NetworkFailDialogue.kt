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
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class NetworkFailDialogue : BaseDialog<DialogRequestFailedBinding>() {

    private var isNetworkError: Boolean = false
    private var apiError: ApiError? = null

    override fun getLayoutRes(): Int {
        return R.layout.dialog_request_failed
    }

    @SuppressLint("ResourceType")
    override fun initUi() {

        arguments?.let {
            isNetworkError = arguments?.getBoolean("networkError", false) ?: false
            apiError = arguments?.getParcelable("exception")
        }

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
                arguments?.getString("apiCode", "").toString(),
                arguments?.getBoolean("networkError", false) ?: false
            )
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