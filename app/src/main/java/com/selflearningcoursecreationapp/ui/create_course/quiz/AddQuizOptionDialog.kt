package com.selflearningcoursecreationapp.ui.create_course.quiz

import android.net.Uri
import android.view.View
import android.view.WindowManager
import android.widget.CompoundButton
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BottomDialogQuizOptionBinding
import com.selflearningcoursecreationapp.extensions.content
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.ui.dialog.UploadImageOptionsDialog
import com.selflearningcoursecreationapp.utils.DialogType

class AddQuizOptionDialog : BaseBottomSheetDialog<BottomDialogQuizOptionBinding>(),
    CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private var selectedUri: String? = null
    private var selectedType: Int = 0

    override fun getLayoutRes() = R.layout.bottom_dialog_quiz_option
    override fun initUi() {
        dialog?.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        binding.ivClose.setOnClickListener {
            dismiss()
        }
        arguments?.let {

            if (it.containsKey("type")) {
                selectedType = it.getInt("type")
            }

        }

        binding.rbImage.setOnCheckedChangeListener(this)
        binding.rbText.setOnCheckedChangeListener(this)
        binding.btSave.setOnClickListener(this)
        binding.ivHeader.setOnClickListener(this)
        binding.tvHeader.setOnClickListener(this)
    }

    private fun isValid(): Boolean {
        when {
            selectedType == 0 -> {
                showToastShort(baseActivity.getString(R.string.plz_select_answer_type))
            }
            selectedType == 1 && binding.etText.content().isEmpty() -> {
                showToastShort(baseActivity.getString(R.string.plz_enter_your_ans))
            }
            selectedType == 2 && selectedUri.isNullOrEmpty() -> {
                showToastShort(baseActivity.getString(R.string.plz_upload_image))
            }
            else -> return true

        }
        return false
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        when (p0?.id) {
            R.id.rb_image -> {
                binding.ivHeader.visibleView(p1)
                binding.tvHeader.visibleView(p1)
                if (p1) {
                    selectedType = 2
                    binding.rbText.isChecked = false
                }
            }
            R.id.rb_text -> {
                binding.etText.visibleView(p1)
                if (p1) {
                    selectedType = 1
                    binding.rbImage.isChecked = false
                }
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.bt_save -> {
                if (isValid()) {
                    onDialogClick(
                        DialogType.CLICK_QUIZ_OPTION,
                        selectedType,
                        if (selectedType == 1) binding.etText.content() else selectedUri!!
                    )
                    dismiss()
                }
            }
            R.id.iv_header, R.id.tv_header -> {
                UploadImageOptionsDialog().apply {
                    setOnDialogClickListener(object : BaseBottomSheetDialog.IDialogClick {
                        override fun onDialogClick(vararg items: Any) {
                            selectedUri = items[1] as String
                            binding.ivHeader.setImageURI(Uri.parse(items[1] as String))
                            binding.tvHeader.gone()
                        }

                    })
                }.show(childFragmentManager, "")

            }
        }
    }
}