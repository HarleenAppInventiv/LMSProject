package com.selflearningcoursecreationapp.ui.profile.profileThumb

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentProfileThumbBinding
import com.selflearningcoursecreationapp.ui.authentication.InitialActivity
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.HandleClick
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileThumbFragment : BaseFragment<FragmentProfileThumbBinding>(), HandleClick {

    private val viewModel: ProfileThumbViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity.supportActionBar?.hide()
        init()

    }

    fun init() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.profileThumb = this
        setContentDesctiption()


    }

    private fun setContentDesctiption() {
        binding.txtUserName.contentDescription = "user name alisi nikolson"
        binding.tvUserMail.contentDescription = "user mail @limadecell"
    }


    override fun getLayoutRes() = R.layout.fragment_profile_thumb


    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {

                R.id.txt_profile_details -> {

                    findNavController().navigate(R.id.action_profileThumbFragment_to_profileDetailsFragment)
                }
                R.id.txt_wishlist -> {

                    findNavController().navigate(R.id.action_profileThumbFragment_to_bookmarkedCoursesFragment)
                }
                R.id.txt_dashboard -> {

                    findNavController().navigate(R.id.action_profileThumbFragment_to_dashboardBaseFragment)
                }
                R.id.txt_courses -> {
                    (baseActivity as HomeActivity).setSelected(R.id.action_course)
                }
                R.id.txt_logout -> {

                    CommonAlertDialog.builder(baseActivity)
                        .title(getString(R.string.come_back_soon))
                        .description(getString(R.string.are_you_sure_you_want_to_logout))
                        .positiveBtnText(getString(R.string.log_out))
                        .icon(R.drawable.ic_fogot_password)
                        .getCallback {
                            if (it) {
                                viewModel.callLogout()

                            }
                        }.build()
                }
                R.id.img_back -> {
                    findNavController().popBackStack()
                }
                R.id.txt_my_bank -> {
                    findNavController().navigate(R.id.action_profileThumbFragment_to_myBankFragment)
                }
                R.id.txt_delete_account -> {
                    CommonAlertDialog.builder(baseActivity)
                        .title(getString(R.string.are_you_sure))
                        .description(getString(R.string.do_you_really_want_to_delete_your_account))
                        .positiveBtnText(getString(R.string.delete_acc))
                        .icon(R.drawable.ic_fogot_password)
                        .getCallback {
                            if (it) {
                                viewModel.deleteAccount()

                            }
                        }.build()

                }

            }
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_LOGOUT -> {
                commanLoginIntent()
            }
            ApiEndPoints.API_DELETE_ACCOUNT -> {
                commanLoginIntent()
            }
        }
    }

    fun commanLoginIntent() {
        baseActivity.startActivity(
            Intent(
                baseActivity,
                InitialActivity::class.java
            )
        )
        baseActivity.finish()
    }

    override fun onResume() {
        super.onResume()

        viewModel.getUserData()
        binding.txtUserName.text = viewModel.userProfile?.name
        binding.tvUserMail.text = viewModel.userProfile?.email
    }
}