package com.selflearningcoursecreationapp.ui.authentication.login_signup

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.text.isDigitsOnly
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.databinding.FragmentLoginBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.user.PreferenceData
import com.selflearningcoursecreationapp.models.user.UserResponse
import com.selflearningcoursecreationapp.ui.authentication.viewModel.OnBoardingViewModel
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.OtpType
import com.selflearningcoursecreationapp.utils.ValidationConst
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils

class LoginFragment : BaseFragment<FragmentLoginBinding>(),
    View.OnClickListener {

    private val viewModel: OnBoardingViewModel by viewModels({ if (parentFragment != null) requireParentFragment() else this })
    override fun getLayoutRes(): Int {
        return R.layout.fragment_login
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    fun init() {

        binding.edtLoginPassword.showHidePassword()
        binding.login = viewModel

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        hideLoading()
        setListeners()
        initializeGoogle()

        binding.edtLoginEmail.doAfterTextChanged {
            if (it?.isDigitsOnly() == true) {
                binding.countryCodePicker.visible()
                limitEditText(ValidationConst.MAX_NO_LENGTH)
            } else {
                binding.countryCodePicker.gone()
                limitEditText(ValidationConst.MAX_EMAIL_LENGTH)
            }
            if (it.isNullOrEmpty()) {
                binding.countryCodePicker.gone()
            }
        }

        binding.tvGuest.setSpanString(
            SpanUtils.with(
                baseActivity,
                baseActivity.getString(R.string.or_continue_as_a_guest)
            ).startPos(
                17
            ).isBold().themeColor().getCallback {
                setFragmentResult("loginData", bundleOf("type" to 1))
                findNavController().navigateUp()
            }.getSpanString()
        )

        viewModel.loginLiveData.value?.viMode = SelfLearningApplication.isViOn ?: false

        binding.btnSingIn.setOnClickListener {

            viewModel.token = baseActivity.token
            viewModel.countryCode = binding.countryCodePicker.selectedCountryCodeWithPlus
            viewModel.loginValidation()
        }


        binding.countryCodePicker.apply {

            if (!viewModel.loginLiveData.value?.countryCode.isNullOrEmpty()) {
                setCountryForPhoneCode(
                    viewModel.loginLiveData.value?.countryCode?.subSequence(
                        1, viewModel.loginLiveData.value?.countryCode?.length ?: 0
                    )?.toString()?.toInt() ?: 91
                )
            }
        }


    }

    private fun limitEditText(maxLength: Int) {
        val filters = arrayOfNulls<InputFilter>(1)
        filters[0] = InputFilter.LengthFilter(maxLength)
        binding.edtLoginEmail.filters = filters
    }

    private fun initializeGoogle() {
//        mGoogleSignInAI = GoogleSignInAI()
//        mGoogleSignInAI!!.setActivity(requireActivity())
//        mGoogleSignInAI!!.setCallback(this)
//        mGoogleSignInAI!!.setRequestCode(GOOGLE_LOGIN_REQUEST_CODE,)
//        mGoogleSignInAI!!.setUpGoogleClientForGoogleLogin()
    }


    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_LOGIN -> {


                val userData = (value as BaseResponse<UserResponse>).resource
                showLog("API_RESPONSE", userData?.token.toString())

                when {
                    userData?.user?.phoneNumberVerified == false -> {
                        findNavController().navigateTo(
                            LoginSignUpFragmentDirections.actionLoginSignUpFragmentToOTPVerifyFragment(
                                "",
                                type = OtpType.TYPE_SIGNUP,
                                email = userData.user?.email,
                                countryCode = ""
                            )
                        )

                    }
                    userData?.user?.getPreferenceValue() != 4 -> {
                        val data = PreferenceData(
                            currentSelection = userData?.user?.getPreferenceValue() ?: 0
                        )

                        findNavController().navigateTo(
                            LoginSignUpFragmentDirections.actionLoginSignUpFragmentToPreferencesFragment(
                                preferenceData = data
                            )
                        )

                    }
                    userData.user?.currentMode == 2 -> {
                        baseActivity.goToModeratorActivity()
                    }
                    else -> {
                        baseActivity.goToHomeActivity()
                    }
                }
            }

        }


    }


    private fun setListeners() {
        binding.btnOtpLogin.setOnClickListener(this)
        binding.btnGoogleLogin.setOnClickListener(this)
        binding.btnSingIn.setOnClickListener(this)
        binding.txtForgotPass.setOnClickListener(this)
        binding.tvGuest.setOnClickListener(this)

//        viewModel.allStates()
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_otp_login -> {
                val action =
                    LoginSignUpFragmentDirections.actionLoginSignUpFragmentToLoginOTPFragment()
                findNavController().navigateTo(action)
            }
            R.id.txt_forgot_pass -> {
                val action =
                    LoginSignUpFragmentDirections.actionLoginSignUpFragmentToForgotPassFragment()
                findNavController().navigateTo(action)
            }
            R.id.tv_guest -> {
                activity?.startActivity(Intent(baseActivity, HomeActivity::class.java))
            }

            R.id.btn_google_login -> {
//                mGoogleSignInAI!!.doSignIn()


            }

        }


    }


    //


//
//    override fun googleSignInSuccessResult(googleSignInAccount: GoogleSignInAccount?) {
////        (findViewById(R.id.textView) as TextView).text =
////            "Hello " + googleSignInAccount!!.displayName
//        Toast.makeText(context, "Hello " + googleSignInAccount?.displayName, Toast.LENGTH_LONG).show()
//    }
//
//    override fun googleSignInFailureResult(message: String?) {
//        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
//    }


}