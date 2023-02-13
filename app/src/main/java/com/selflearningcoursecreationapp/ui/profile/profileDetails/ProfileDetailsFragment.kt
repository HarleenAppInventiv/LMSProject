package com.selflearningcoursecreationapp.ui.profile.profileDetails

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.FragmentProfileDetailsBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.ImageResponse
import com.selflearningcoursecreationapp.models.user.UserProfile
import com.selflearningcoursecreationapp.ui.dialog.UploadImageOptionsDialog
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.BundleConst
import com.selflearningcoursecreationapp.utils.builderUtils.ImageViewBuilder
import com.selflearningcoursecreationapp.utils.builderUtils.ResizeableUtils
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class ProfileDetailsFragment : BaseFragment<FragmentProfileDetailsBinding>(), View.OnClickListener {
    private val viewModel: ProfileDetailViewModel by viewModel()
    private lateinit var bundle: Bundle
    private var isImageEmpty = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
//        val a = 1 / 0
    }


    fun init() {

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        onClickListeners()

        viewModel.viewProfile()
        binding.profileDetail = viewModel


    }

    private fun onClickListeners() {
        binding.imgEditProfile.setOnClickListener(this)
        binding.imgEditProfileImage.setOnClickListener(this)
        binding.imgBack.setOnClickListener(this)
        binding.tvAddEmail.setOnClickListener(this)
        binding.imgProfileImage.setOnClickListener(this)
        binding.imgTalk.setOnClickListener(this)
    }

    override fun getLayoutRes() = R.layout.fragment_profile_details
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_add_email -> {
                findNavController().navigateTo(
                    R.id.action_profileDetailsFragment_to_addEmailFragment,
                    bundleOf("isEmailAdded" to binding.txtContactMail.content().isNotEmpty())
                )
            }
            R.id.img_edit_profile -> {
                val action =
                    ProfileDetailsFragmentDirections.actionProfileDetailsFragmentToEditProfileFragment()
                findNavController().navigateTo(action)
            }

            R.id.img_edit_profile_image -> {
                val dialogue = UploadImageOptionsDialog()
                dialogue.arguments =
                    bundleOf("from" to if (viewModel.userProfile?.profileUrl.isNullOrEmpty()) 0 else 10)
                dialogue.apply {
                    setOnDialogClickListener(object : BaseBottomSheetDialog.IDialogClick {
                        override fun onDialogClick(vararg items: Any) {
                            if (items[0] == 10) {
                                viewModel.deleteProfile()
                            } else {
                                val uri = items[1] as String
                                val file = File(Uri.parse(uri).path ?: "")
                                binding.imgProfileImage.setImageURI(Uri.parse(uri))
                                viewModel.uploadImage(file)
                            }

                        }

                    })
                }.show(childFragmentManager, "")

            }
            R.id.img_back -> {
                findNavController().popBackStack()
            }

            R.id.img_talk -> {
                baseActivity.checkAccessibilityService()
            }

        }
    }

    override fun onResume() {
        super.onResume()
        activity?.setTransparentStatusBar()

    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_VIEW_PROFILE -> {
                viewModel.userProfile?.let { data ->

                    val location =
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
                    Glide.with(baseActivity).clear(binding.imgProfileImage)
//                    binding.imgProfileImage.loadImage(
//                        data.profileUrl,
//                        R.drawable.ic_default_user_grey,
//                        data.profileBlurHash
//                    )

                    ImageViewBuilder.builder(binding.imgProfileImage).setImageUrl(data.profileUrl)
                        .blurhash(data.profileBlurHash)
                        .placeHolder(R.drawable.ic_default_user_grey)
                        .loadImage()

                    if (!data.profileUrl.isNullOrEmpty()) {
                        isImageEmpty = true
                    }
                    bundle = bundleOf(
                        BundleConst.IMAGE to data.profileUrl,
                        BundleConst.BLUR_HASH to data.profileBlurHash
                    )
//                    Glide.with(requireActivity()).load(data.profileUrl)
//                        .placeholder(R.drawable.ic_default_user_grey)
//                        .into(binding.imgProfileImage)

                    binding.imgProfileImage
                    binding.txtDob.text = data.dob?.changeDateFormat()
                    binding.txtContactMail.text = data.email
                    viewModel.txtEmail.value = data.email
                    setEmailData(data)
                    binding.txtContactNumber.text =
                        String.format("%s %s", data.countryCode, data.number)
                    binding.txtProfession.text = data.profession?.name
                    binding.txtGender.text = data.genderName
                    binding.tvUserBio.contentDescription =
                        if (data.bio.isNullOrEmpty()) getString(R.string.edit_profile) else data.bio


                    contentDescription(data)

                }
            }
            ApiEndPoints.API_UPLOAD_IMAGE -> {
                (value as BaseResponse<ImageResponse>).let {
                    showToastShort(baseActivity.getString(R.string.profile_pic_updated))
                    viewModel.viewProfile()
                }
            }
            ApiEndPoints.API_UPLOAD_IMAGE + "/delete" -> {
                (value as BaseResponse<ImageResponse>).let {
                    showToastShort(it.message)
                    viewModel.viewProfile()
                }
            }
        }


    }

    private fun setEmailData(data: UserProfile) {
        binding.txtContactMail.isEnabled = data.email.isNotEmpty()

        if (data.email.isEmpty()) {
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
            binding.tvAddEmail.text = baseActivity.getString(R.string.change_email)
            binding.tvEmailVerified.visible()


            val params = binding.tvAddEmail.layoutParams as ConstraintLayout.LayoutParams
            params.topToBottom = binding.txtContactMail.id
            params.topMargin = baseActivity.resources.getDimensionPixelOffset(R.dimen._5sdp)
            binding.tvAddEmail.layoutParams = params
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.setTransparentLightStatusBar()

    }

    private fun setBio(data: UserProfile) {
        if (!data.bio.isNullOrEmpty()) {
            ResizeableUtils.builder(binding.tvUserBio).setFullText(data.bio).isUnderline(false)
                .isBold(false).build()
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
                findNavController().navigateTo(action)
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

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }

    fun contentDescription(data: UserProfile) {

        binding.txtContactNumber.contentDescription = "User contact detail is ${data.number}"
        binding.txtUserName.contentDescription = "User name is ${data.name}"
        binding.txtContactMail.contentDescription =
            if (data.email.isEmpty()) "Email" else "User verified email is ${data.email}"
        binding.txtDob.contentDescription =
            if (data.dob?.isEmpty() == true) "Date of birth" else "User DOB  is ${data.dob}"
        binding.txtProfession.contentDescription =
            if (data.professionName.isNullOrEmpty()) "Profession name" else "User profession  is ${data.professionName}"
        binding.txtGender.contentDescription =
            if (data.genderName.isNullOrEmpty()) "Gender" else "User gender  is ${data.genderName}"
        binding.txtAddress.contentDescription =
            if (data.city.isNullOrEmpty() || data.state.isNullOrEmpty()) "Location" else "User location  is ${data.city}, ${data.state} "
    }


}