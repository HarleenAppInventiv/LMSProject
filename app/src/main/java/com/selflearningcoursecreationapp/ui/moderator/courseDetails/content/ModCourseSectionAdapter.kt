package com.selflearningcoursecreationapp.ui.moderator.courseDetails.content

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterModCourseSectionBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.create_course.review.LectureViewAdapter
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.ResizeableUtils
import com.selflearningcoursecreationapp.utils.SpanUtils

class ModCourseSectionAdapter(private var list: ArrayList<SectionModel>) :
    BaseAdapter<AdapterModCourseSectionBinding>() {
    override fun getLayoutRes() = R.layout.adapter_mod_course_section

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterModCourseSectionBinding
        val context = binding.root.context
        val data = list[position]
        binding.sectionData = data
        binding.executePendingBindings()

        binding.tvTotalTime.text = data.sectionDuration.getTime(context)
        binding.tvLectureList.text = data.lessonList.getLessonCount(context).joinToString(", ")

        binding.tvComment.setSpanString(
            SpanUtils.with(
                context,
                "Comment : Lorem ipsum dolor sitamet, consecte tur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            ).endPos(7).isBold().getSpanString()
        )

        binding.tvLessonName.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }
        binding.clLesson.visibleView(data.isVisible)
        binding.commentG.visibleView(position % 2 != 0)
        if (data.isVisible) {
            binding.ivExtend.setImageResource(R.drawable.ic_arrow_top)
            binding.ivComment.visibleView(position % 2 == 0)
        } else {
            binding.ivExtend.setImageResource(R.drawable.ic_arrow_bottom)
            binding.ivComment.gone()
        }
        binding.ivExtend.setOnClickListener {
            list[position].isVisible = !list[position].isVisible
            notifyItemChanged(position)
        }
        binding.rvLessons.adapter = LectureViewAdapter(data.lessonList, true).apply {
            setOnAdapterItemClickListener(object : BaseAdapter.IViewClick {
                override fun onItemClick(vararg items: Any) {
                    if (items.isNotEmpty()) {
                        this@ModCourseSectionAdapter.onItemClick(
                            items[0] as Int,
                            holder.bindingAdapterPosition,
                            items[1] as Int
                        )
                    }
                }

            })
        }
        ResizeableUtils.builder(binding.tvDescription)
            .isUnderline(false)
            .setFullText(data.sectionDescription)
            .setFullText(R.string.read_more)
            .showDots(true)
            .build()
    }

    override fun getItemCount() = list.size
}