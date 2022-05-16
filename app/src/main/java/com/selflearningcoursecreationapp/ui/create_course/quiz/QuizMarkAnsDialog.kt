package com.selflearningcoursecreationapp.ui.create_course.quiz

import android.view.View
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BottomDialogMarkAnswerBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.course.quiz.QuizData
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType
import com.selflearningcoursecreationapp.utils.QUIZ
import org.json.JSONArray
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class QuizMarkAnsDialog : BaseBottomSheetDialog<BottomDialogMarkAnswerBinding>(),
    View.OnClickListener,
    BaseAdapter.IViewClick {
    private var adapter: MarkColumnOptionAdapter? = null
    private var hashmap = HashMap<String, ArrayList<QuizOptionData>>()

    private val viewModel: AddQuizVM by viewModel()
    private var adapterPosition: Int = 0
    private var quizType: Int = QUIZ.MULTIPLE_CHOICE
    override fun getLayoutRes() = R.layout.bottom_dialog_mark_answer
    override fun initUi() {
        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        binding.ivClose.setOnClickListener {
            dismiss()
        }
        var optionList = ArrayList<QuizOptionData>()
        arguments?.let {

            if (it.containsKey("position")) {
                adapterPosition = it.getInt("position")

            }
            if (it.containsKey("quizData")) {
                viewModel.quizData = it.getParcelable("quizData") ?: QuizData()
                optionList = viewModel.getListData(adapterPosition)?.optionList ?: ArrayList()
                quizType =
                    viewModel.getListData(adapterPosition)?.questionType ?: QUIZ.MULTIPLE_CHOICE
            }

        }

        when (quizType) {
            QUIZ.MATCH_COLUMN -> {
                binding.columnG.visible()
                optionList.forEachIndexed { index, quizOptionData ->
                    val charText = (index + 65).toChar()
                    hashmap.put(
                        charText.toString(),
                        optionList.map { it.copy(isSelected = it.id == quizOptionData.ansId) } as ArrayList<QuizOptionData>
                    )
                }
            }
            else -> {
                binding.columnG.gone()

                hashmap.put(baseActivity.getString(R.string.answer_is), optionList)
            }
        }
        setAdapter()
        binding.btSave.setOnClickListener(this)
    }

    private fun setAdapter() {
        adapter?.notifyDataSetChanged() ?: run {
            adapter = MarkColumnOptionAdapter(
                hashmap,
                viewModel.getListData(adapterPosition)?.questionType ?: QUIZ.MATCH_COLUMN
            )
            binding.rvColumn.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)

        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.bt_save -> {
                var isValid = true
                val ansList = JSONArray()
                hashmap.map { it.key }.forEachIndexed { index, s ->

                    val list = hashmap[s]?.filter { it.isSelected == true }
                    if (list.isNullOrEmpty()) {
                        isValid = false
                        showToastShort(baseActivity.getString(R.string.plz_select_ans))
                        return
                    } else {
                        list.forEach {
                            val ansObj = JSONObject()

                            if (quizType == QUIZ.MATCH_COLUMN) {
                                ansObj.put(
                                    "answere1",
                                    viewModel.getListData(adapterPosition)?.optionList?.get(index)?.id
                                )
                                ansObj.put("answere2", it.id)
                            } else {
                                ansObj.put("answere1", it.id)
                            }
                            ansList.put(ansObj)
                        }
                    }
                }
                if (isValid) {
                    viewModel.saveAnswer(ansList)
                }
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
                    val keyList = hashmap.map { it.key }
                    when (quizType) {
                        QUIZ.MULTIPLE_CHOICE -> {

                            hashmap[keyList[position]]?.get(childPos)?.isSelected =
                                !(hashmap[keyList[position]]?.get(childPos)?.isSelected ?: false)

                            setAdapter()
                        }

                        QUIZ.MATCH_COLUMN -> {
                            hashmap[keyList[position]]?.forEach { it.isSelected = false }
                            keyList.forEach {
                                hashmap[it]?.get(childPos)?.isSelected = false
                            }
                            hashmap[keyList[position]]?.get(childPos)?.isSelected = true
                            setAdapter()
                        }
                        else -> {
                            hashmap[keyList[position]]?.forEach { it.isSelected = false }
                            hashmap[keyList[position]]?.get(childPos)?.isSelected = true
                            setAdapter()
                        }
                    }
                }
            }
        }
    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        onDialogClick(DialogType.QUIZ_ANSWER, hashmap)
        dismiss()
    }
}