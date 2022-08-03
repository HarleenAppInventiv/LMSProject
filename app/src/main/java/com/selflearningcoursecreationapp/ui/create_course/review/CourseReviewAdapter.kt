package com.selflearningcoursecreationapp.ui.create_course.review

import android.annotation.SuppressLint
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterSectionReviewBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel

@SuppressLint("NotifyDataSetChanged")
class CourseReviewAdapter(
    private var list: ArrayList<SectionModel>, private var courseCreatorId: Int
) : BaseAdapter<AdapterSectionReviewBinding>() {
    override fun getLayoutRes() = R.layout.adapter_section_review
    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterSectionReviewBinding
        val context = binding.root.context
        val data = list[position]
        binding.ivUserImage.loadImage(
            data.sectionCreatedByProfileURL,
            R.drawable.ic_default_user_grey
        )
        binding.ivUserLogo.loadImage(data.sectionLogoURL, R.drawable.ic_logo_default)

        binding.logoGroup.visibleView(courseCreatorId != data.sectionCreatedById)

        binding.tvSectionNumber.text = data.sectionTitle
        binding.tvDescription.text = data.sectionDescription
        var millis: Long = 0
        data.lessonList.forEach {
//            millis += if (it.mediaType != MediaType.QUIZ) {
//                it.lectureContentDuration?.div(10000) ?: 0
//            } else {
            millis += it.lectureContentDuration ?: 0
//            }
        }
        binding.tvTotalTime.text = millis.getTime(context)
        if (data.isVisible) {
            binding.ivVisible.setImageResource(R.drawable.ic_arrow_top)
            binding.llChild.visible()
            binding.tvSectionNumber.isSingleLine = false
        } else {
            binding.ivVisible.setImageResource(R.drawable.ic_arrow_bottom)
            binding.llChild.gone()
            binding.tvSectionNumber.isSingleLine = true

        }

        binding.tvLectureList.text = data.lessonList.getLessonCount(context).joinToString(", ")



        binding.ivVisible.setOnClickListener {
            list.forEachIndexed { index, sectionModel ->
                if (index != position) {
                    sectionModel.isVisible = false
                } else {
                    sectionModel.isVisible = !sectionModel.isVisible
                }
            }
            notifyDataSetChanged()

        }

        binding.rvLecture.adapter = LectureViewAdapter(data.lessonList).apply {
            setOnAdapterItemClickListener(object : IViewClick {
                override fun onItemClick(vararg items: Any) {
                    if (items.isNotEmpty()) {
                        val type = items[0] as Int
                        val childPosition = items[1] as Int
                        this@CourseReviewAdapter.onItemClick(
                            type,
                            holder.adapterPosition,
                            childPosition
                        )
                    }
                }
            })
        }

    }
}