package com.selflearningcoursecreationapp.ui.create_course.quiz

import android.annotation.SuppressLint
import android.view.View
import androidx.databinding.library.baseAdapters.BR
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterAddQuizViewBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.QUIZ
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants

class AddQuizViewAdapter(private var list: ArrayList<QuizQuestionData>) :
    BaseAdapter<AdapterAddQuizViewBinding>(), View.OnTouchListener {

    override fun getLayoutRes() = R.layout.adapter_add_quiz_view

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterAddQuizViewBinding
        val context = binding.root.context
        val data = list[position]
        binding.tvQuizNumber.text =
            String.format(context.getString(R.string.question), position + 1)
        binding.quizData = data
        binding.executePendingBindings()
        binding.tvQuesType.apply {
            text =
                if (data.questionTypeTitle.isNullOrEmpty()) context.getQuizTypeTitle(
                    data.questionType ?: QUIZ.MULTIPLE_CHOICE
                ) else data.questionTypeTitle

            contentDescription = "$text double tab to see other question types"
        }

        if ((data.questionImage != null || !data.questionImageId.isNullOrEmpty()))
            binding.ivHeader.loadImage(data.questionImage, null)
        else
            binding.ivHeader.background = context.getDrawable(R.drawable.cornered_stroked_bg)

        binding.ivEditImage.visibleView(!data.questionImageId.isNullOrEmpty() && data.questionType == QUIZ.IMAGE_BASED)

//        binding.etQuestion.doEnable(data.isEnabled ?: true)
//        binding.tvQuesType.doEnable(data.isEnabled ?: true)
        if (data.questionId.isNullOrZero()) {
            binding.tvQuesType.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                R.drawable.ic_arrow_bottom,
                0
            )
        } else {
            binding.tvQuesType.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        }
        setOptionAdapter(position, binding, data, list[position].optionSelected)
        binding.tvExpand.setOnClickListener {
            list[position].isExpanded = !(list[position].isExpanded ?: false)
            notifyDataSetChanged()
        }
        if (list[position].isExpanded != false) {
            binding.tvExpand.setImageResource(R.drawable.ic_arrow_top)
        } else {
            binding.tvExpand.setImageResource(R.drawable.ic_arrow_bottom)

        }

        binding.btSave.setOnClickListener {
            onItemClick(Constant.CLICK_SAVE, position)

        }
        binding.ivDelete.setOnClickListener {
            onItemClick(Constant.CLICK_DELETE, position)

        }
        binding.btEdit.setOnClickListener {
            list[position].isEnabled = true
            notifyItemChanged(position)

        }
        binding.tvHeader.setOnClickListener {
            if (data.questionImageId.isNullOrEmpty())
                onItemClick(DialogType.CLICK_PORTRAIT_QUES, position)

        }
        binding.ivEditImage.setOnClickListener {
            onItemClick(DialogType.CLICK_PORTRAIT_QUES, position)

        }
        binding.ivHeader.setOnClickListener {
            if (data.questionImageId.isNullOrEmpty())
                onItemClick(DialogType.CLICK_PORTRAIT_QUES, position)

        }

        binding.tvAdd.setOnClickListener {

            if (list[position].optionList.size < 4) {
                list[position].optionList.add(QuizOptionData(quizType = data.questionType))

            }



            if (list[position].optionList.size == 4) {
                binding.tvAdd.gone()
            }

            list[position].notifyPropertyChanged(BR.dataEntered)
            binding.rvOptions.adapter?.notifyDataSetChanged()
        }

        binding.tvColumn1.setOnClickListener {
            setOptionAdapter(position, binding, data, 0)

        }
        binding.tvColumn2.setOnClickListener {
            setOptionAdapter(position, binding, data, 1)

        }

        binding.btAnswer.setOnClickListener {
            onItemClick(DialogType.QUIZ_ANSWER, position)
        }
        binding.tvQuesType.setOnClickListener {
            onItemClick(DialogType.CLICK_QUIZ_TYPE, position)
        }

        binding.etQuestion.setOnTouchListener(this)
    }

    private fun setOptionAdapter(
        position: Int,
        binding: AdapterAddQuizViewBinding,
        data: QuizQuestionData,
        optionType: Int
    ) {

        if (list[position].optionList.isNullOrEmpty()) {
            list[position].optionList = ArrayList()
        }


        list[position].optionSelected = optionType


        binding.tvAdd.visibleView(list[position].optionList.size < 4 && data.isEnabled != false)



        binding.rvOptions.adapter = AddQuizOptionAdapter(
            list[position].optionList,
            data.isEnabled ?: false, data.questionType, optionType
        ).apply {
            setOnAdapterItemClickListener(object : IViewClick {
                override fun onItemClick(vararg items: Any) {
                    this@AddQuizViewAdapter.onItemClick(items[0] as Int, position, items[1] as Int)
                    when (items[0] as Int) {
                        Constant.CLICK_OPTION_DELETE -> {

                            binding.tvAdd.visibleView(list[position].optionList.size < 4)
                            list[position].notifyPropertyChanged(BR.dataEntered)

                        }
                    }

                }
            })
        }


        if (optionType == 0) {
            binding.tvColumn1.changeBackgroundTint(ThemeConstants.TYPE_THEME)
            binding.tvColumn2.backgroundTintList = null
            binding.tvColumn1.changeTextColor(ThemeConstants.TYPE_PRIMARY)
            binding.tvColumn2.changeTextColor(ThemeConstants.TYPE_BODY)
        } else {
            binding.tvColumn1.backgroundTintList = null
            binding.tvColumn2.changeBackgroundTint(ThemeConstants.TYPE_THEME)
            binding.tvColumn1.changeTextColor(ThemeConstants.TYPE_BODY)
            binding.tvColumn2.changeTextColor(ThemeConstants.TYPE_PRIMARY)
        }
    }

    override fun getItemCount() = list.size


}