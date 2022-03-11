package com.selflearningcoursecreationapp.ui.authentication.login_signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentLoginBinding
import com.selflearningcoursecreationapp.extensions.showHidePassword
import com.selflearningcoursecreationapp.ui.authentication.viewModel.OnBoardingViewModel
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment<FragmentLoginBinding>(),
    View.OnClickListener/*,GoogleSignCallback*/ {
    //    private var mGoogleSignInAI: GoogleSignInAI? = null
    private val GOOGLE_LOGIN_REQUEST_CODE = 1001
    private val viewModel: OnBoardingViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

    }

    fun init() {
        binding.edtLoginPassword.showHidePassword()
            binding.login = viewModel
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        setListners()
        initializeGoogle()
//        viewModel.allStates()


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
        Log.e("API_RESPONSE", "response")
//        when (apiCode) {
//            ApiEndPoints.API_COUNTRIES -> {
//                Log.e("API_RESPONSE", "county")
//                (value as BaseResponse<StateData>).let {
//                    Log.e("API_RESPONSE", "done ttt")
//                }
//            }
//
//        }

        activity?.startActivity(Intent(activity!!, HomeActivity::class.java))


    }


    fun setListners() {
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
                var action =
                    LoginSignUpFragmentDirections.actionLoginSignUpFragmentToLoginOTPFragment()
                findNavController().navigate(action)
            }
            R.id.txt_forgot_pass -> {
                var action =
                    LoginSignUpFragmentDirections.actionLoginSignUpFragmentToForgotPassFragment()
                findNavController().navigate(action)
            }
            R.id.tv_guest -> {
                activity?.startActivity(Intent(activity!!, HomeActivity::class.java))
            }

            R.id.btn_google_login -> {
//                mGoogleSignInAI!!.doSignIn()


            }

        }


    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_login
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (GOOGLE_LOGIN_REQUEST_CODE == requestCode) {
//            mGoogleSignInAI!!.onActivityResult(data)
        }
    }
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