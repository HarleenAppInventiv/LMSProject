package com.selflearningcoursecreationapp.ui.profile.profileThumb

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentProfileThumbBinding
import com.selflearningcoursecreationapp.ui.authentication.InitialActivity
import com.selflearningcoursecreationapp.utils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.HandleClick


class ProfileThumbFragment : BaseFragment<FragmentProfileThumbBinding>(), HandleClick {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity.supportActionBar?.hide()
        init()

    }

    fun init() {
        binding.profileThumb = this
    }


    override fun getLayoutRes() = R.layout.fragment_profile_thumb


    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {

                R.id.txt_profile_details -> {

                    findNavController().navigate(R.id.action_profileThumbFragment_to_profileDetailsFragment)
                }
                R.id.txt_logout -> {

                    CommonAlertDialog.builder(baseActivity)
                        .title(getString(R.string.come_back_soon))
                        .description(getString(R.string.are_you_sure_you_want_to_logout))
                        .positiveBtnText(getString(R.string.log_out))
                        .icon(R.drawable.ic_fogot_password)
                        .getCallback {
                            if (it) {

                                baseActivity.startActivity(
                                    Intent(
                                        baseActivity,
                                        InitialActivity::class.java
                                    )
                                )
                                baseActivity.finish()
                            }
                        }.build()
                }
                R.id.img_back -> {
                    findNavController().popBackStack()
                }
                R.id.txt_my_bank -> {
                    findNavController().navigate(R.id.action_profileThumbFragment_to_myBankFragment)
                }
            }
        }
    }


}