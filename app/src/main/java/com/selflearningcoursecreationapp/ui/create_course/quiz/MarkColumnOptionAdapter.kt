package com.selflearningcoursecreationapp.ui.create_course.quiz

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterColumnOptionBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.course.quiz.QuizOptionData


class MarkColumnOptionAdapter(
    private var list: HashMap<String, ArrayList<QuizOptionData>>,
    private var type: Int
) :
    BaseAdapter<AdapterColumnOptionBinding>() {

    override fun getLayoutRes() = R.layout.adapter_column_option

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterColumnOptionBinding
        val keyList = list.keys.asIterable()
        binding.tvAnswer.text = keyList.elementAt(position)
        binding.lineV.visibleView(position + 1 != list.size)
        val recyclerViewState = binding.rvList.layoutManager?.onSaveInstanceState()
        binding.rvList.layoutManager?.onRestoreInstanceState(recyclerViewState)
        binding.rvList.adapter =
            MarkAnsOptionAdapter(list[keyList.elementAt(position)] ?: ArrayList()).apply {
                setOnAdapterItemClickListener(object : IViewClick {
                    override fun onItemClick(vararg items: Any) {
                        this@MarkColumnOptionAdapter.onItemClick(
                            items[0] as Int,
                            holder.adapterPosition,
                            items[1] as Int
                        )
                    }
                })
            }
    }

    override fun getItemCount() = list.size


}