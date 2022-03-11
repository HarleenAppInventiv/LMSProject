package com.selflearningcoursecreationapp.ui.profile.profileDetails

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentProfileDetailsBinding
import com.selflearningcoursecreationapp.ui.dialog.UploadImageOptionsDialog

class ProfileDetailsFragment : BaseFragment<FragmentProfileDetailsBinding>(), View.OnClickListener {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
          profileImageView = binding.imgProfileImage
    }

    fun init() {
        onClickListners()


    }

    fun onClickListners() {
        binding.imgEditProfile.setOnClickListener(this)
        binding.imgEditProfileImage.setOnClickListener(this)
        binding.imgBack.setOnClickListener(this)
    }

    override fun getLayoutRes() = R.layout.fragment_profile_details
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.img_edit_profile -> {
                val action =
                    ProfileDetailsFragmentDirections.actionProfileDetailsFragmentToEditProfileFragment()
                findNavController().navigate(action)
            }
            R.id.img_edit_profile_image -> {
              UploadImageOptionsDialog().apply {
                  setOnDialogClickListener(object :BaseBottomSheetDialog.IDialogClick{
                      override fun onDialogClick(vararg items: Any) {
                         binding.imgProfileImage.setImageURI(Uri.parse(items[1] as String))
                      }

                  })
              }.show(childFragmentManager,"")

            }
            R.id.img_back -> {
                findNavController().popBackStack()
            }
        }
    }


//    private fun showBottomSheetDialog() {
//        val bottomSheetDialog = BottomSheetDialog(requireContext())
//        bottomSheetDialog.setContentView(R.layout.select_media_layout)
//        val camera = bottomSheetDialog.findViewById<TextView>(R.id.txt_take_photo)
//        val gallary = bottomSheetDialog.findViewById<TextView>(R.id.txt_take_from_gallary)
//        val imgClose = bottomSheetDialog.findViewById<ImageView>(R.id.img_close)
//
//        imgClose!!.setOnClickListener() {
//            bottomSheetDialog.dismiss()
//        }
//
//        camera!!.setOnClickListener() {
//            bottomSheetDialog.dismiss()
//            selectImage()
//        }
//
//        gallary!!.setOnClickListener() {
//            bottomSheetDialog.dismiss()
//            selectGallary()
//        }
//        bottomSheetDialog.show()
//    }




}