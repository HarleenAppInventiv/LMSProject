package com.selflearningcoursecreationapp.base

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.LiveDataObserver
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.extensions.showLog


abstract class BaseFragment<DB : ViewDataBinding> : Fragment(), LiveDataObserver {
    protected lateinit var binding: DB
    lateinit var mProfileUri: Uri
    var profileImageView: ImageView? = null

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!

                mProfileUri = fileUri
                profileImageView?.setImageURI(fileUri)
                Toast.makeText(requireContext(), "" + mProfileUri.toString(), Toast.LENGTH_SHORT)
                    .show()
            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    @LayoutRes
    abstract fun getLayoutRes(): Int
    lateinit var baseActivity: BaseActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutRes(), container, false)
        binding.lifecycleOwner = this
        return binding.root

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            baseActivity = context
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        hideLoading()
        showLog("API_RESPONSE", "base response")

    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {
        hideLoading()
        baseActivity.handleOnException(isNetworkAvailable, exception, apiCode)
    }

    override fun onError(error: ToastData) {
        hideLoading()
        baseActivity.handleOnError(error)
    }

    override fun onLoading(message: String) {
        showLoading()
    }

    fun showLoading() {
        baseActivity.showProgressBar()
    }

    fun hideLoading() {
        baseActivity.hideProgressBar()
    }


    fun showToastShort(message: String) {
        baseActivity.showToastShort(message)
    }

    fun showToastLong(message: String) {
        baseActivity.showToastLong(message)
    }


    fun selectImage() {
        ImagePicker.with(this)
            .cameraOnly()
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    fun selectGallary() {
        ImagePicker.with(this)
            .galleryOnly()
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }

    }

    fun showBottomSheetDialogMedia() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.dialog_upload_image)
        val camera = bottomSheetDialog.findViewById<TextView>(R.id.txt_take_photo)
        val gallary = bottomSheetDialog.findViewById<TextView>(R.id.txt_take_from_gallary)
        val imgClose = bottomSheetDialog.findViewById<ImageView>(R.id.img_close)

        imgClose!!.setOnClickListener() {
            bottomSheetDialog.dismiss()
        }

        camera!!.setOnClickListener() {
            bottomSheetDialog.dismiss()
            selectImage()
        }

        gallary!!.setOnClickListener() {
            bottomSheetDialog.dismiss()
            selectGallary()
        }
        bottomSheetDialog.show()
    }
}