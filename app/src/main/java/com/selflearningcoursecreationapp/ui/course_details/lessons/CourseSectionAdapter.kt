package com.selflearningcoursecreationapp.ui.course_details.lessons

import androidx.core.view.isVisible
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterCourseSectionBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.utils.builderUtils.ResizeableUtils
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils

class CourseSectionAdapter(
    private var list: ArrayList<SectionModel>,
    private val token: String,
    private var showComment: Boolean = true,
    val courseMandatory: Boolean?,
    val modType: Boolean = false
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
//        binding.tvLessonName.setOnClickListener {
//            onItemClick(Constant.CLICK_VIEW, position)
//        }
        binding.ivCompleted.visibleView(
            (list[position].sectionPercentageCompleted?.toInt() ?: 0) == 100 && !data.isVisible
        )
        binding.pbProgress.setOnTouchListener { _, _ ->
            return@setOnTouchListener true
        }


        binding.pbProgress.apply {
            visibleView(
                (list[position].sectionPercentageCompleted?.toDouble()
                    ?: 0.0) > 0.0 && (list[position].sectionPercentageCompleted?.toDouble()
                    ?: 0.0) < 100.0 && !data.isVisible
            )
            max = 100
            progress = list[position].sectionPercentageCompleted?.toInt() ?: 0
//                if (data.sectionPercentageCompleted?.toInt() == 100) list[position].sectionDuration.toInt()
//               else data.sectionDuration.minus(data.sectionTotalDurationLeft ?: 0L).toInt()
        }
        binding.clLesson.visibleView(data.isVisible)

        binding.tvModComment.apply {
            setSpanString(
                SpanUtils.with(
                    context,
                    context.getString(R.string.mod_comment) + " " + data.moderatorComment
                ).endPos(
                    21
                ).isBold().getSpanString()
            )
            visibleView((data.moderatorComment?.isNotEmpty() == true) && showComment)
        }
        if (data.isVisible) {
            binding.icExtend.setImageResource(R.drawable.ic_arrow_top)
            binding.view.visible()
            binding.tvLessonName.isSingleLine = false
        } else {
            binding.icExtend.setImageResource(R.drawable.ic_arrow_bottom)
            binding.view.gone()
            binding.tvLessonName.isSingleLine = true
        }
        binding.constTitleBar.setOnClickListener {
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
        if (binding.ivCompleted.isVisible) {
            binding.cvLeeson.contentDescription =
                "Section ${data.sectionTitle} is already completed"
        }

        binding.icExtend.contentDescription = "Expand Session ${position + 1} from list"

        binding.rvLessons.adapter =
            CourseLessonAdapter(data.lessonList, showComment, modType).apply {
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