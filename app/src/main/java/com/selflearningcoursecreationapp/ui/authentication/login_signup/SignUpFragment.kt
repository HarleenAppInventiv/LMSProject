package com.selflearningcoursecreationapp.ui.authentication.login_signup

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hbb20.CountryCodePicker
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.databinding.FragmentSignUpBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.navigateTo
import com.selflearningcoursecreationapp.models.SingleChoiceData
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.authentication.viewModel.OnBoardingViewModel
import com.selflearningcoursecreationapp.ui.dialog.singleChoice.SingleChoiceBottomDialog
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.OtpType
import com.selflearningcoursecreationapp.utils.STATIC_PAGES_TYPE
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils

class SignUpFragment : BaseFragment<FragmentSignUpBinding>(), BaseBottomSheetDialog.IDialogClick,
    CountryCodePicker.OnCountryChangeListener {
    val viewModel: OnBoardingViewModel by viewModels({ if (parentFragment != null) requireParentFragment() else this })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }

    fun init() {
        binding.signup = viewModel
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.countryCodePicker.apply {
            setOnCountryChangeListener(this@SignUpFragment)
        }
        viewModel.signUpLiveData.value?.countryCode =
            binding.countryCodePicker.selectedCountryCodeWithPlus

        viewModel.signUpLiveData.value?.viMode =
            SelfLearningApplication.isViOn
        setSpanString()
        binding.evChooseProfession.setOnClickListener {
            SingleChoiceBottomDialog().apply {
                arguments = bundleOf(
                    "type" to DialogType.PROFESSION,
                    "title" to this@SignUpFragment.baseActivity.getString(R.string.profession),
                    "id" to if (!viewModel.signUpLiveData.value?.professionId.isNullOrEmpty()) {
                        viewModel.signUpLiveData.value?.professionId?.toIntOrNull() ?: 0
                    } else 0
                )

                setOnDialogClickListener(this@SignUpFragment)
            }.show(childFragmentManager, "")
        }
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        Log.d("SignUpFragment", "onResponseSuccess: $apiCode")
        val response = value as? BaseResponse<*>
        response?.let {

            if (apiCode == ApiEndPoints.API_SIGNUP) {
                val result = response.resource as? UserProfile
                result?.let {
                    findNavController().navigateTo(
                        LoginSignUpFragmentDirections.actionLoginSignUpFragmentToOTPVerifyFragment(
                            phone = binding.edtRegPhone.content(),
                            email = "",
                            type = OtpType.TYPE_SIGNUP,
                            countryCode = binding.countryCodePicker.selectedCountryCodeWithPlus
                        )
                    )
                }
            }
        }
    }

    fun setSpanString() {
        val msg =
            baseActivity.getString(R.string.by_registering_you_are_accepting_privacy_policy_and_terms_of_use)
        val ss = SpannableString(msg)
        val privacySpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                viewModel.isMovedToPrivacy = true
                val action =
                    LoginSignUpFragmentDirections.actionLoginSignUpFragmentToPrivacyFragment(
//                        baseActivity.getString(R.string.privacy_policy),
//                        ApiEndPoints.LINK_PRIVACY_POL
                        STATIC_PAGES_TYPE.PRIVACY
                    )
                findNavController().navigateTo(action)

            }

            @SuppressLint("ResourceType")
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ThemeUtils.getAppColor(baseActivity)

            }
        }
        val termsSpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                viewModel.isMovedToPrivacy = true
                val action =
                    LoginSignUpFragmentDirections.actionLoginSignUpFragmentToPrivacyFragment(
//                        baseActivity.getString(R.string.terms_amp_conditions),
//                        ApiEndPoints.LINK_TERM_COND
                        STATIC_PAGES_TYPE.TERMS
                    )
                findNavController().navigateTo(action)


            }

            @SuppressLint("ResourceType")
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false

                ds.color = ThemeUtils.getAppColor(baseActivity)
            }
        }

        ss.setSpan(privacySpan, 33, 49, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(termsSpan, 54, msg.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvTerms.text = ss
        binding.tvTerms.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTerms.highlightColor =
            ContextCompat.getColor(baseActivity, android.R.color.transparent)
    }


    override fun getLayoutRes(): Int {
        return R.layout.fragment_sign_up
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("signup", "onDestroy: ")
    }


    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val value = items[1]

            when (type) {
                DialogType.PROFESSION -> {
                    value as SingleChoiceData
                    binding.evChooseProfession.setText(value.title)
                    viewModel.signUpLiveData.value?.professionId = value.id.toString()
                }
            }
        }
    }

    override fun onCountrySelected() {
        viewModel.signUpLiveData.value?.countryCode =
            binding.countryCodePicker.selectedCountryCodeWithPlus
    }

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }


}