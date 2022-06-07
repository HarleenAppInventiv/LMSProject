package com.selflearningcoursecreationapp.ui.create_course.co_author

import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseDialog
import com.selflearningcoursecreationapp.data.network.ApiError
import com.selflearningcoursecreationapp.data.network.HTTPCode
import com.selflearningcoursecreationapp.data.network.ToastData
import com.selflearningcoursecreationapp.databinding.LayoutEditTextDialogBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.hideKeyboard
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.SpanUtils
import com.selflearningcoursecreationapp.utils.customViews.LMSEditText
import org.koin.androidx.viewmodel.ext.android.viewModel

class InviteCoAuthorDialog : BaseDialog<LayoutEditTextDialogBinding>() {
    var sendVia = 0
    var isEmail = true
    override fun getLayoutRes() = R.layout.layout_edit_text_dialog
    private val viewModel: CoAuthorViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTransparent_95)

    }

    override fun initUi() {
        arguments?.let {
            viewModel.courseId = it.getInt("courseId")
        }
        binding.viewModel = viewModel
        enablePhone()
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

        binding.btnSend.setOnClickListener {
            viewModel.validate(binding.countryCodePicker.selectedCountryCodeWithPlus, isEmail)
        }
        binding.ivCross.setOnClickListener {
            dismiss()
        }
        textChangeListener()
    }

    private fun textChangeListener() {
        binding.edtUserPhone.doOnTextChanged { text, start, before, count ->
            binding.tvError.gone()
            binding.llPhone.setBackgroundResource(R.drawable.edt_bg)

        }
        binding.edtUserEmail.doOnTextChanged { text, start, before, count ->
            binding.tvErrorEmail.gone()
            binding.edtUserEmail.setBackgroundResource(R.drawable.edt_bg)
        }
    }

    private fun setBackgroundManuallyll(edtBgOutline: Int, ll: LinearLayout) {
        ll.setBackgroundResource(edtBgOutline)

    }

    fun enablePhone() {
        binding.edtUserPhone.hideKeyboard()
        sendVia = 1
        isEmail = true
        binding.edtUserEmail.apply {
            visible()
        }
        binding.edtUserEmail.text?.clear()
        binding.edtUserPhone.text?.clear()
        binding.tvErrorEmail.gone()
        binding.llPhone.gone()
        binding.tvError.gone()
        binding.tvInvite.setSpanString(
            SpanUtils.with(
                binding.root.context,
                binding.root.context.getString(R.string.or_send_invitation_via_phone_number)
            ).startPos(
                22
            ).isBold().themeColor().getCallback {
                baseActivity.hideKeyboard()
                enableMail()
            }.getSpanString()
        )
    }

    fun enableMail() {
        binding.edtUserEmail.hideKeyboard()
        sendVia = 2
        binding.edtUserEmail.gone()
        binding.edtUserPhone.text?.clear()
        binding.edtUserEmail.text?.clear()
        isEmail = false

        binding.llPhone.apply {
            visible()
        }
        binding.tvErrorEmail.gone()
        binding.tvError.gone()
        binding.tvInvite.setSpanString(
            SpanUtils.with(
                binding.root.context,
                binding.root.context.getString(R.string.or_send_via_email)
            ).startPos(
                22
            ).isBold().themeColor().getCallback {
                baseActivity.hideKeyboard()
                enablePhone()

            }.getSpanString()
        )
    }


    private fun setBackgroundManually(bg: Int, editTExt: LMSEditText) {
        editTExt.setBackgroundResource(bg)

    }

    fun commanAlert() {

        CommonAlertDialog.builder(baseActivity)
            .title(getString(R.string.success))
            .description(getString(R.string.your_invitation_sent))
            .positiveBtnText(getString(R.string.okay))
            .hideNegativeBtn(true)
            .icon(R.drawable.ic_celebration)
            .getCallback {
                if (it) {
                    dismiss()
                }
            }.build()
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        dismiss()
        commanAlert()

    }

    override fun onException(isNetworkAvailable: Boolean, exception: ApiError, apiCode: String) {


        if (apiCode == ApiEndPoints.API_INVITE_COAUTHOR && exception.statusCode == HTTPCode.UN_SUCESS) {
            baseActivity.hideProgressBar()
            if (isEmail) {
                binding.edtUserEmail.setBackgroundResource(R.drawable.edt_bg_outline)
                binding.tvErrorEmail.apply {
                    visible()
                    text = exception.message
                }
            } else {
                binding.llPhone.setBackgroundResource(R.drawable.edt_bg_outline)
                binding.tvError.apply {
                    visible()
                    text = exception.message
                }
            }
        } else {
            super.onException(isNetworkAvailable, exception, apiCode)

        }

    }


    override fun onError(error: ToastData, apiCode: String?) {
        when (apiCode) {
            "phone" -> {
                binding.llPhone.setBackgroundResource(R.drawable.edt_bg_outline)
                binding.tvError.apply {
                    visible()
                    text = baseActivity.getString(error.errorCode!!)
                }
            }
            "email" -> {
                binding.edtUserEmail.setBackgroundResource(R.drawable.edt_bg_outline)
                binding.tvErrorEmail.apply {
                    visible()
                    text = baseActivity.getString(error.errorCode!!)
                }
            }
        }
    }

}
