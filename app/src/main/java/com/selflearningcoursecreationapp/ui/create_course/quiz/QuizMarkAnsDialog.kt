package com.selflearningcoursecreationapp.ui.create_course.quiz

import android.annotation.SuppressLint
import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseResponse
import com.selflearningcoursecreationapp.databinding.BottomDialogMarkAnswerBinding
import com.selflearningcoursecreationapp.extensions.getCharString
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.QUIZ
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("NotifyDataSetChanged")

class QuizMarkAnsDialog : BaseBottomSheetDialog<BottomDialogMarkAnswerBinding>(),
    View.OnClickListener,
    BaseAdapter.IViewClick {
    private var adapter: MarkColumnOptionAdapter? = null

    private val viewModel: AddQuizAnsVM by viewModel()
    override fun getLayoutRes() = R.layout.bottom_dialog_mark_answer
    override fun initUi() {
        viewModel.hashmap.clear()
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.ivClose.setOnClickListener {
            dismiss()
        }
        var optionList = ArrayList<QuizOptionData>()
        arguments?.let {

            if (it.containsKey("position")) {
                viewModel.adapterPosition = it.getInt("position")

            }
            if (it.containsKey("quizData")) {
                viewModel.quizData = it.getParcelable("quizData") ?: QuizData()
                optionList = viewModel.getListData()?.optionList ?: ArrayList()
                viewModel.quizType = viewModel.getListData()?.questionType ?: QUIZ.MULTIPLE_CHOICE
            }

        }

        when (viewModel.quizType) {
            QUIZ.MATCH_COLUMN -> {
                binding.columnG.visible()
                optionList.forEachIndexed { index, quizOptionData ->
                    viewModel.hashmap[String.format(
                        baseActivity.getString(R.string.option_no),
                        index.getCharString()
                    )] =
                        optionList.map { it.copy(isSelected = it.id == quizOptionData.ansId) } as ArrayList<QuizOptionData>
                }
            }
            else -> {
                binding.columnG.gone()

                viewModel.hashmap[baseActivity.getString(R.string.answer_is)] = optionList
            }
        }
        setAdapter()
        binding.btSave.setOnClickListener(this)
    }

    private fun setAdapter() {
        adapter?.notifyDataSetChanged() ?: run {
            adapter = MarkColumnOptionAdapter(
                viewModel.hashmap,
                viewModel.getListData()?.questionType ?: QUIZ.MATCH_COLUMN
            )
            binding.rvColumn.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)

        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.bt_save -> {
                viewModel.isAnswerValid()
            }
        }
    }

    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val position = items[1] as Int
            val childPos = items[2] as Int
            when (type) {
                Constant.CLICK_VIEW -> {
                    val keyList = viewModel.hashmap.map { it.key }
                    when (viewModel.quizType) {
                        QUIZ.MULTIPLE_CHOICE -> {

                            viewModel.hashmap[keyList[position]]?.get(childPos)?.isSelected =
                                !(viewModel.hashmap[keyList[position]]?.get(childPos)?.isSelected
                                    ?: false)

                            setAdapter()
                        }

                        QUIZ.MATCH_COLUMN -> {
                            viewModel.hashmap[keyList[position]]?.forEach { it.isSelected = false }
                            keyList.forEach {
                                viewModel.hashmap[it]?.get(childPos)?.isSelected = false
                            }
                            viewModel.hashmap[keyList[position]]?.get(childPos)?.isSelected = true
                            setAdapter()
                        }
                        else -> {
                            viewModel.hashmap[keyList[position]]?.forEach { it.isSelected = false }
                            viewModel.hashmap[keyList[position]]?.get(childPos)?.isSelected = true
                            setAdapter()
                        }
                    }
                }
            }
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        showToastShort((value as BaseResponse<*>).message)
        onDialogClick(DialogType.QUIZ_ANSWER, viewModel.hashmap)
        dismiss()
    }


    override fun onApiRetry(apiCode: String) {
        super.onApiRetry(apiCode)
        viewModel.onApiRetry(apiCode)
    }
}