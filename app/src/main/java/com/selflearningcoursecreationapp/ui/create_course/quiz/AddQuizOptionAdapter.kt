package com.selflearningcoursecreationapp.ui.create_course.quiz

import android.annotation.SuppressLint
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterAddQuizOptionBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.QUIZ

class AddQuizOptionAdapter(
    private var list: ArrayList<QuizOptionData>,
    private var isEnabled: Boolean,
    private var questionType: Int?,
    private var optionType: Int,
) :
    BaseAdapter<AdapterAddQuizOptionBinding>(), View.OnTouchListener {


    override fun getLayoutRes() = R.layout.adapter_add_quiz_option

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterAddQuizOptionBinding
        val context = binding.root.context
        val data = list[position]

        binding.root.isEnabled = isEnabled
        binding.optionData = data
        binding.isEnabled = isEnabled
        binding.type = optionType
        binding.tvName.text = position.getCharString()



        binding.etOption1.gone()
        binding.etOption2.gone()
        binding.etOption2.textHelper(position + 1 == list.size)
        binding.etOption1.textHelper(position + 1 == list.size)
        binding.etOption1.setOnTouchListener(this)
        binding.etOption2.setOnTouchListener(this)
//        binding.etOption1.setWordLimit()
//        binding.etOption2.setWordLimit()


        when (questionType) {
            QUIZ.MATCH_COLUMN -> {
                if (optionType == 0) {
                    binding.etOption1.visible()
                } else {
                    binding.etOption2.visible()

                }
                binding.etOption1.hint =
                    "${context.getString(R.string.tap_here_to_enter_option)} ${position.getCharString()}"
                binding.etOption2.hint =
                    "${context.getString(R.string.tap_here_to_enter_option)} ${position.getCharString()}"
            }
            QUIZ.DRAG_DROP, QUIZ.IMAGE_BASED -> {
                if (!data.option1.isNullOrEmpty()) {
                    binding.etOption1.visible()
                } else if (!data.imageId.isNullOrEmpty()) {
                    binding.ivOption.visible()
                    binding.ivOption.loadImage(data.image, R.drawable.ic_default_banner)
                } else {
                    binding.tvSelect.visible()
                }
                binding.tvSelect.text = binding.root.context.getString(R.string.add_answer)

            }
            else -> {
                binding.etOption1.visible()
                binding.etOption1.hint =
                    "${context.getString(R.string.tap_here_to_enter_option)} ${position.getCharString()}"
            }
        }

        binding.ivDelete.setOnClickListener {
            list.removeAt(position)
            notifyDataSetChanged()
            onItemClick(Constant.CLICK_OPTION_DELETE, position)
        }

        binding.tvSelect.setOnClickListener {
            onItemClick(DialogType.CLICK_QUIZ_OPTION, position)
        }
        if (isEnabled) {
            binding.ivDelete.visible()
            binding.ivMark.gone()
        } else {
            binding.ivDelete.gone()
            binding.ivMark.visibleView(data.isSelected == true)

        }

    }

    override fun getItemCount() = list.size


}