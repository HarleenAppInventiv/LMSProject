package com.selflearningcoursecreationapp.ui.profile.profileDetails

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentProfileDetailsBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.dialog.UploadImageOptionsDialog
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileDetailsFragment : BaseFragment<FragmentProfileDetailsBinding>(), View.OnClickListener {
    private val viewModel: ProfileDetailViewModel by viewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        onClickListners()

        viewModel.viewProfile()

    }

    fun onClickListners() {
        binding.imgEditProfile.setOnClickListener(this)
        binding.imgEditProfileImage.setOnClickListener(this)
        binding.imgBack.setOnClickListener(this)
        binding.tvAddEmail.setOnClickListener(this)
    }

    override fun getLayoutRes() = R.layout.fragment_profile_details
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_add_email -> {
                findNavController().navigate(
                    R.id.action_profileDetailsFragment_to_addEmailFragment,
                    bundleOf("isEmailAdded" to binding.txtContactMail.content().isNotEmpty())
                )
            }
            R.id.img_edit_profile -> {
                val action =
                    ProfileDetailsFragmentDirections.actionProfileDetailsFragmentToEditProfileFragment()
                findNavController().navigate(action)
            }
            R.id.img_edit_profile_image -> {
                UploadImageOptionsDialog().apply {
                    setOnDialogClickListener(object : BaseBottomSheetDialog.IDialogClick {
                        override fun onDialogClick(vararg items: Any) {
                            binding.imgProfileImage.setImageURI(Uri.parse(items[1] as String))
                        }

                    })
                }.show(childFragmentManager, "")

            }
            R.id.img_back -> {
                findNavController().popBackStack()
            }

        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        if (apiCode == ApiEndPoints.API_VIEW_PROFILE) {

            viewModel.userProfile?.let { data ->
                //                binding.imgProfileImage.s

                Log.d("Profile", "onResponseSuccess: $data")

                var location =
                    "${
                        if (data.city.isNullOrEmpty()) {
                            ""
                        } else {
                            data.city + ", "
                        }
                    }${
                        if (data.state.isNullOrEmpty()) {
                            ""
                        } else {
                            data.state
                        }
                    }"

                setBio(data)
                binding.txtAddress.text = location
                binding.txtUserName.text = data.name
                binding.txtDob.text = data.dob?.changeDateFormat()
                binding.txtContactMail.text = data.email
                setEmailData(data)
                binding.txtContactNumber.text = data.countryCode + " " + data.number
                binding.txtProfession.text = data.profession?.name
                binding.txtGender.text = data.genderName


            }

        }
    }

    private fun setEmailData(data: UserProfile) {
        binding.txtContactMail.isEnabled = !data.email.isNullOrEmpty()

        if (data.email.isNullOrEmpty()) {
            //    binding.txtContactMail.setDrawableColor(0,ThemeConstants.TYPE_TINT)
            binding.tvAddEmail.text = baseActivity.getString(R.string.add_email)
            binding.txtContactMail.setCompoundDrawablesRelativeWithIntrinsicBounds(
                binding.txtContactMail.compoundDrawables[0],
                null,
                null,
                null
            )
            binding.tvEmailVerified.gone()
            val params = binding.tvAddEmail.layoutParams as ConstraintLayout.LayoutParams
            params.topToTop = binding.txtContactMail.id
            params.bottomToBottom = binding.txtContactMail.id
            params.topMargin = 0
            binding.tvAddEmail.layoutParams = params
        } else {
            //    binding.txtContactMail.setDrawableColor(0,ThemeConstants.TYPE_THEME)
            binding.tvAddEmail.text = baseActivity.getString(R.string.change_email)
            binding.tvEmailVerified.visible()


            val params = binding.tvAddEmail.layoutParams as ConstraintLayout.LayoutParams
            params.topToBottom = binding.txtContactMail.id
            params.topMargin = baseActivity.resources.getDimensionPixelOffset(R.dimen._5sdp)
            binding.tvAddEmail.layoutParams = params
//            binding.txtContactMail.setCompoundDrawablesRelativeWithIntrinsicBounds(
//                binding.txtContactMail.compoundDrawables[0],
//                null,
//                null,
//                ContextCompat.getDrawable(baseActivity, R.drawable.ic_check_green)
//            )

        }
    }

    private fun setBio(data: UserProfile) {
        if (!data.bio.isNullOrEmpty()) {
            binding.tvUserBio.setTextResizable(data.bio)
            binding.tvUserBio.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                null,
                null
            )
        } else {
            binding.tvUserBio.setOnClickListener {
                val action =
                    ProfileDetailsFragmentDirections.actionProfileDetailsFragmentToEditProfileFragment()
                findNavController().navigate(action)
            }
            binding.tvUserBio.text = baseActivity.getString(R.string.add_bio)
            binding.tvUserBio.setCompoundDrawablesRelativeWithIntrinsicBounds(
                binding.tvUserBio.compoundDrawables[0],
                null,
                null,
                null
            )

        }
    }

}