package com.selflearningcoursecreationapp.ui.profile.profileThumb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentProfileThumbBinding
import com.selflearningcoursecreationapp.extensions.showAlertDialog
import com.selflearningcoursecreationapp.ui.authentication.InitialActivity
import com.selflearningcoursecreationapp.utils.HandleClick
import android.graphics.PorterDuff

import android.R.color
import android.content.res.ColorStateList
import android.graphics.Color

import android.graphics.PorterDuffColorFilter

import android.graphics.drawable.Drawable





class ProfileThumbFragment : BaseFragment<FragmentProfileThumbBinding>(), HandleClick {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity?.supportActionBar?.hide()
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
                    baseActivity.showAlertDialog(baseActivity.getString(R.string.come_back_soon),
                        baseActivity.getString(R.string.are_you_sure_you_want_to_logout),
                        baseActivity.getString(R.string.log_out),
                        negativeButtonText = baseActivity.getString(R.string.cancel),
                        icon = R.drawable.ic_fogot_password,
                        onClick = { isPositive ->
                            if (isPositive) {
                                baseActivity.startActivity(
                                    Intent(
                                        baseActivity,
                                        InitialActivity::class.java
                                    )
                                )
                                baseActivity.finish()
                            }
                        })
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