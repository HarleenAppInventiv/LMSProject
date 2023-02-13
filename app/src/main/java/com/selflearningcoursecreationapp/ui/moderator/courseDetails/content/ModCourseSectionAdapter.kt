package com.selflearningcoursecreationapp.ui.moderator.courseDetails.content

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterModCourseSectionBinding
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture.SectionModel
import com.selflearningcoursecreationapp.ui.create_course.review.LectureViewAdapter
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.ModHomeConst
import com.selflearningcoursecreationapp.utils.builderUtils.ImageViewBuilder
import com.selflearningcoursecreationapp.utils.builderUtils.ResizeableUtils
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils

class ModCourseSectionAdapter(
    private var list: ArrayList<SectionModel>,
    private var createdById: Int?,
    private var status: Int,
    private val pending: Int
) :
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

        binding.ivUserImage.visibleView(createdById != data.sectionCreatedById)
        binding.ivUserLogo.visibleView(createdById != data.sectionCreatedById)


        ImageViewBuilder.builder(binding.ivUserImage)
            .setImageUrl(data.sectionCreatedByProfileURL)
            .placeHolder(R.drawable.ic_default_user_grey)
            .loadImage()

        ImageViewBuilder.builder(binding.ivUserLogo)
            .setImageUrl(data.sectionLogoURL)
            .placeHolder(R.drawable.ic_logo_default)
            .loadImage()


        binding.tvComment.setSpanString(
            SpanUtils.with(
                context,
                "Comment: ${list.get(position).moderatorComment}"
            ).endPos(7).isBold().getSpanString()
        )

        binding.tvLessonName.setOnClickListener {
            onItemClick(Constant.CLICK_VIEW, position)
        }
        binding.clLesson.visibleView(data.isVisible)
        binding.commentG.visibleView(!list.get(position).moderatorComment.isNullOrEmpty())

        binding.ivEdit.visibleView(status == ModHomeConst.PENDING && !list[position].moderatorComment.isNullOrEmpty())
        binding.ivDelete.visibleView(status == ModHomeConst.PENDING && !list[position].moderatorComment.isNullOrEmpty())

//        binding.tvComment.text = list.get(position).moderatorComment
        binding.tvTime.text = list.get(position).commentCreatedDate.changeDateFormat()
        if (data.isVisible) {
            binding.ivExtend.setImageResource(R.drawable.ic_arrow_top)
            binding.ivComment.visibleView(list.get(position).moderatorComment.isNullOrEmpty() && status == ModHomeConst.PENDING)
            binding.tvLessonName.isSingleLine = false

        } else {
            binding.ivExtend.setImageResource(R.drawable.ic_arrow_bottom)
            binding.ivComment.gone()
            binding.tvLessonName.isSingleLine = true
        }
        binding.ivExtend.setOnClickListener {
            list[position].isVisible = !list[position].isVisible
            notifyItemChanged(position)
        }
        binding.rvLessons.adapter = LectureViewAdapter(data.lessonList, true, pending).apply {
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
        binding.ivComment.setOnClickListener {
            onItemClick(Constant.CLICK_ADD, position)
        }
        binding.ivEdit.setOnClickListener {
            onItemClick(Constant.CLICK_EDIT, position)

        }
        binding.ivDelete.setOnClickListener {
            onItemClick(Constant.CLICK_DELETE, position)

        }
        ResizeableUtils.builder(binding.tvDescription)
            .isBold(false)
            .isUnderline(false)
            .setFullText(data.sectionDescription)
            .setFullText(R.string.read_more_arrow)
            .showDots(true)
            .build()
    }

    override fun getItemCount() = list.size

    fun changeStatus(status: Int) {
        this.status = status
        notifyDataSetChanged()
    }
}