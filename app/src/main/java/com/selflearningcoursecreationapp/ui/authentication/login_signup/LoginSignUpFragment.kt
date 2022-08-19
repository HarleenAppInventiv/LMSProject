package com.selflearningcoursecreationapp.ui.authentication.login_signup

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentLoginSignUpBinding
import com.selflearningcoursecreationapp.extensions.getAttrColor
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.authentication.viewModel.OnBoardingViewModel
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginSignUpFragment : BaseFragment<FragmentLoginSignUpBinding>(), View.OnClickListener {
    private val viewModel: OnBoardingViewModel by viewModel()

    private var type: Int = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

    }


    fun init() {
        binding.textView.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.hello_welcome)).endPos(6)
                .textColor(
                    ContextCompat.getColor(
                        baseActivity,
                        baseActivity.getAttrColor(R.attr.headingTextColor)
                    )
                )
                .isBold().getSpanString()
        )

        binding.txtSingIn.setOnClickListener(this)
        binding.txtSignUp.setOnClickListener(this)

        setSelected()



        setFragmentResultListener("loginData", listener = { _, bundle ->
            type = bundle.getInt("type")

            setSelected()
        })


    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.txt_sing_in -> {
                type = 0
                setSelected()

            }
            R.id.txt_sign_up -> {
                type = 1
                setSelected()

            }
        }
    }

    private fun setSelected() {
        binding.txtSingIn.changeTextColor(if (type == 0) ThemeConstants.TYPE_THEME else ThemeConstants.TYPE_BODY)
        binding.txtSignUp.changeTextColor(if (type == 1) ThemeConstants.TYPE_THEME else ThemeConstants.TYPE_BODY)

        if (type == 0)
            binding.txtSingIn.changeBackgroundTint(ThemeConstants.TYPE_THEME)
        else binding.txtSingIn.backgroundTintList = null

        if (type == 1)
            binding.txtSignUp.changeBackgroundTint(ThemeConstants.TYPE_THEME)
        else binding.txtSignUp.backgroundTintList = null

        if (!viewModel.isMovedToPrivacy) {
            viewModel.signUpLiveData.value = UserProfile()
            viewModel.isPrivacyPolicyChecked.value = false
        } else {
            viewModel.isMovedToPrivacy = false
        }
        if (type == 0) {
            binding.tvSubHeading.text =
                baseActivity.getString(R.string.sign_in_to_continue_your_account)
            childFragmentManager.beginTransaction()
                .replace(R.id.container, LoginFragment()).commit()
        } else {
            binding.tvSubHeading.text = baseActivity.getString(R.string.sign_up_to_get_started)

            childFragmentManager.beginTransaction()
                .replace(R.id.container, SignUpFragment()).commit()
        }

    }

    override fun getLayoutRes() = R.layout.fragment_login_sign_up
    fun onClickBack() {
        if (type == 1) {
            type = 0
            setSelected()
        } else {
            baseActivity.finishAffinity()
        }
    }

    override fun onApiRetry(apiCode: String) {

    }


}