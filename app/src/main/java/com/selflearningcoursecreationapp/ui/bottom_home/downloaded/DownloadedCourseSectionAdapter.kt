package com.selflearningcoursecreationapp.ui.bottom_home.downloaded

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterDownloadedCourseSectionBinding
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.models.offline.OfflineSectionData

class DownloadedCourseSectionAdapter(
    private var list: ArrayList<OfflineSectionData>
) :
    BaseAdapter<AdapterDownloadedCourseSectionBinding>() {
    override fun getLayoutRes() = R.layout.adapter_downloaded_course_section

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterDownloadedCourseSectionBinding
        val context = binding.root.context
        val data = list[position]
        binding.executePendingBindings()
        binding.tvLessonName.text = list[position].title



        binding.rvLessons.adapter = DownloadedCourseLectureAdapter(data.lessonList!!).apply {
            setOnAdapterItemClickListener(object : BaseAdapter.IViewClick {
                override fun onItemClick(vararg items: Any) {
                    if (items.isNotEmpty()) {
                        this@DownloadedCourseSectionAdapter.onItemClick(
                            items[0] as Int,
                            holder.bindingAdapterPosition, //section position
                            items[1] as Int //lecture position
                        )
                    }
                }

            })
        }

        if (data.isVisible) {
            binding.icExtend.setImageResource(R.drawable.ic_arrow_top)
            binding.view.visible()
            binding.rvLessons.visible()
            binding.clLesson.visible()
        } else {
            binding.icExtend.setImageResource(R.drawable.ic_arrow_bottom)
            binding.rvLessons.gone()
            binding.clLesson.gone()
        }
        binding.icExtend.setOnClickListener {
            list[position].isVisible = !list[position].isVisible
            notifyItemChanged(position)
        }

//        ResizeableUtils.builder(binding.tvDescription).isBold(false)
//            .isUnderline(false)
//            .setFullText(data.description)
//            .setFullText(R.string.read_more_arrow)
//            .setLessText(R.string.read_less_arrow)
//            .setSpanSize(0.9f)
//            .showDots(true)
////            .setSpanColor(ContextCompat.getColor(context, R.color.accent_color_fc6d5b))
//            .build()
    }

    override fun getItemCount() = list.size
}