package com.selflearningcoursecreationapp.ui.profile.edit_profile

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentEditProfileBinding
import com.selflearningcoursecreationapp.extensions.getStringDate
import com.selflearningcoursecreationapp.extensions.openDatePickerDialog
import com.selflearningcoursecreationapp.extensions.setSpanString
import com.selflearningcoursecreationapp.utils.SpanUtils
import org.koin.androidx.viewmodel.ext.android.viewModel


class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(), View.OnClickListener {
    private val viewModel: EditProfileViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()


    }

    fun init() {
        binding.editProfile = viewModel
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.edtUserDob.setOnClickListener(this)

        binding.textView6.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.personal_details))
                .endPos(8).isBold()
                .textColor(ContextCompat.getColor(baseActivity, R.color.heading_color_262626))
                .getSpanString()
        )
        binding.textView7.setSpanString(
            SpanUtils.with(baseActivity, baseActivity.getString(R.string.address_details)).endPos(7)
                .isBold()
                .textColor(ContextCompat.getColor(baseActivity, R.color.heading_color_262626))
                .getSpanString()
        )
    }


    override fun getLayoutRes() = R.layout.fragment_edit_profile
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.edt_user_dob -> {
                baseActivity.openDatePickerDialog(setMaxDate = true) {
                    binding.edtUserDob.setText(it.time.getStringDate())
                }


            }
        }
    }


}