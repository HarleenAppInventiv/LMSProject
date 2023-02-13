package com.selflearningcoursecreationapp.ui.dialog

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogDonwloadSampleCertificateBinding
import com.selflearningcoursecreationapp.utils.Constant

class DownloadSampleOptionDialog : BaseBottomSheetDialog<DialogDonwloadSampleCertificateBinding>() {
    override fun getLayoutRes() = R.layout.dialog_donwload_sample_certificate

    override fun initUi() {

        binding.imgClose.setOnClickListener {
            dismiss()
        }

        binding.tvDownloadComp.setOnClickListener {
            dismiss()
            onDialogClick(Constant.DOWNLOAD_COMPLETIION)
        }

        binding.tvDownloadAppr.setOnClickListener {
            dismiss()
            onDialogClick(Constant.DOWNLOAD_APPRECIATION)

        }
    }
}