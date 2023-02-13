package com.selflearningcoursecreationapp.ui.dialog

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BottomSheetRecordingBinding
import com.selflearningcoursecreationapp.utils.screenRecorder.ScreenRecordingActivity

class ScreenRecordingOptions : BaseBottomSheetDialog<BottomSheetRecordingBinding>() {
    override fun getLayoutRes(): Int {

        return R.layout.bottom_sheet_recording
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initUi() {


        val intent = Intent(baseActivity, ScreenRecordingActivity::class.java)
        binding.tvRecordScreenWithCamera.setOnClickListener {
            intent.putExtra("type", 1)
            startActivity(intent)
            dismiss()

        }
        binding.tvRecordScreenWithoutCamera.setOnClickListener {
            intent.putExtra("type", 2)
            startActivity(intent)
            dismiss()

        }
        binding.ivClose.setOnClickListener {
            dismiss()
        }


    }
}