package com.selflearningcoursecreationapp.ui.course_details.lessons

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCourseSectionBinding
import com.selflearningcoursecreationapp.extensions.getLessonCount
import com.selflearningcoursecreationapp.extensions.getTime
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.ResizeableUtils

class CourseSectionAdapter(
    private var list: ArrayList<SectionModel>,
    private val token: String,
) :
    BaseAdapter<AdapterCourseSectionBinding>() {
    override fun getLayoutRes() = R.layout.adapter_course_section

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterCourseSectionBinding
        val context = binding.root.context
        val data = list[position]
        binding.sectionData = data
        binding.executePendingBindings()

        binding.tvTotalTime.text = data.sectionDuration.getTime(context)
        binding.tvLectureList.text = data.lessonList.getLessonCount(context).joinToString(", ")
        binding.tvLessonName.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }
        binding.clLesson.visibleView(data.isVisible)


        if (data.isVisible) {
            binding.icExtend.setImageResource(R.drawable.ic_arrow_top)
        } else {
            binding.icExtend.setImageResource(R.drawable.ic_arrow_bottom)
        }
        binding.icExtend.setOnClickListener {
//            if (!token.isEmpty()) {
            list[position].isVisible = !list[position].isVisible
            notifyItemChanged(position)
//              -  onItemClick(Constant.CLICK_LESSON, position)

//            } else {
//                Toast.makeText(binding.root.context,
//                    "This feature is accessible after you'll enroll the course",
//                    Toast.LENGTH_SHORT).show()
//
//            }

        }

        binding.rvLessons.adapter = CourseLessonAdapter(data.lessonList).apply {
            setOnAdapterItemClickListener(object : BaseAdapter.IViewClick {
                override fun onItemClick(vararg items: Any) {
                    if (items.isNotEmpty()) {
                        this@CourseSectionAdapter.onItemClick(
                            items[0] as Int,
                            holder.bindingAdapterPosition,
                            items[1] as Int
                        )
                    }
                }

            })
        }
        ResizeableUtils.builder(binding.tvDescription).isBold(false)
            .isUnderline(false)
            .setFullText(data.sectionDescription)
            .setFullText(R.string.read_more_arrow)
            .setLessText(R.string.read_less_arrow)
            .setSpanSize(0.9f)
            .showDots(true)
//            .setSpanColor(ContextCompat.getColor(context, R.color.accent_color_fc6d5b))
            .build()
    }

    override fun getItemCount() = list.size
}