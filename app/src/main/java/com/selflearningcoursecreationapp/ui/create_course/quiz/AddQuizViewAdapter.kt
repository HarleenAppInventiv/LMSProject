package com.selflearningcoursecreationapp.ui.create_course.quiz

import android.net.Uri
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterAddQuizViewBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.models.course.quiz.QuizQuestionData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants

class AddQuizViewAdapter(private var list: ArrayList<QuizQuestionData>) :
    BaseAdapter<AdapterAddQuizViewBinding>() {

    override fun getLayoutRes() = R.layout.adapter_add_quiz_view

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterAddQuizViewBinding
        val context = binding.root.context
        val data = list[position]
        binding.tvQuizNumber.text =
            String.format(context.getString(R.string.question), position + 1)
        binding.quizData = data

        binding.tvQuesType.text = data.questionTypeTitle

        data.questionImage?.let {

            binding.ivHeader.setImageURI(Uri.parse(it))
        }

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
//        binding.btAnswer.visibleView(!(data.isEnabled ?: true))
//        binding.tvAdd.visibleView(data.isEnabled ?: true)
//        binding.btSave.visibleView(data.isEnabled ?: true)
//        binding.btEdit.visibleView(!(data.isEnabled ?: true))
        setOptionAdapter(position, binding, data, list[position].optionSelected)
        binding.tvExpand.setOnClickListener {
            list[position].isExpanded = !(list[position].isExpanded ?: false)
            notifyDataSetChanged()
        }
//        binding.clQuiz.visibleView(list[position].isExpanded ?: true)
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
            onItemClick(DialogType.CLICK_BANNER, position)

        }
        binding.ivHeader.setOnClickListener {
            onItemClick(DialogType.CLICK_BANNER, position)

        }

        binding.tvAdd.setOnClickListener {

            if (list[position].optionList.size < 4) {
                list[position].optionList.add(QuizOptionData(quizType = data.questionType))

            }



            if (list[position].optionList.size == 4) {
                binding.tvAdd.gone()
            }


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
//        when (data.questionType) {
//            QUIZ.MATCH_COLUMN -> {
//                binding.columnG.visible()
//                binding.tvOptions.gone()
//            }
//            else -> {
//
//                binding.columnG.gone()
//                binding.tvOptions.visible()
//            }
//        }


    }

    private fun setOptionAdapter(
        position: Int,
        binding: AdapterAddQuizViewBinding,
        data: QuizQuestionData,
        optionType: Int
    ) {

        if (list[position].optionList.isNullOrEmpty()) {
            list[position].optionList = ArrayList<QuizOptionData>()
        }


        list[position].optionSelected = optionType


        binding.tvAdd.visibleView(list[position].optionList.size < 4 && data.isEnabled != false)



        binding.rvOptions.adapter = AddQuizOptionAdapter(
            list[position].optionList,
            data.isEnabled ?: false, data.questionType, optionType
        ).apply {
            setOnAdapterItemClickListener(object : BaseAdapter.IViewClick {
                override fun onItemClick(vararg items: Any) {
                    this@AddQuizViewAdapter.onItemClick(items[0] as Int, position, items[1] as Int)
                    when (items[0] as Int) {
                        Constant.CLICK_OPTION_DELETE -> {

                            binding.tvAdd.visibleView(list[position].optionList.size ?: 0 < 4)

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