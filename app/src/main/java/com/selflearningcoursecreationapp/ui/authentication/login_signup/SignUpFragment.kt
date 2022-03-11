package com.selflearningcoursecreationapp.ui.authentication.login_signup

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentSignUpBinding
import com.selflearningcoursecreationapp.extensions.showHidePassword
import com.selflearningcoursecreationapp.ui.authentication.viewModel.OnBoardingViewModel
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpFragment : BaseFragment<FragmentSignUpBinding>() {
    val viewModel: OnBoardingViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    fun init() {
        binding.edtRegPassword.showHidePassword()
        binding.signup = viewModel
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        setSpanString()
    }



    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        findNavController().navigate(R.id.action_loginSignUpFragment_to_preferencesFragment)
    }

    fun setSpanString() {
        val msg =
            baseActivity.getString(R.string.by_registering_you_are_accepting_privacy_policy_and_terms_of_use)
        val ss = SpannableString(msg)
        val privacySpan = object : ClickableSpan() {
            override fun onClick(p0: View) {
                baseActivity.showToastShort("Privacy policy comming soon")

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
//                Toast.makeText(requireContext(), , Toast.LENGTH_SHORT).show()
                baseActivity.showToastShort("Terms comming soon")

            }

            @SuppressLint("ResourceType")
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false

                ds.color = ThemeUtils.getAppColor(baseActivity)
            }
        }

//    if (isBold) {
//        ss.setSpan(StyleSpan(Typeface.BOLD), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//    }
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


}