package com.selflearningcoursecreationapp.ui.bottom_home

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseViewHolder
import com.selflearningcoursecreationapp.databinding.AdapterHomeBinding
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.models.CourseTypeModel
import com.selflearningcoursecreationapp.utils.Constant


class HomeAdapter(private var courseTypeList: ArrayList<CourseTypeModel>?) :
    BaseAdapter<AdapterHomeBinding>() {

    //    private var coursesList = ArrayList<CourseTempData>()
    override fun getLayoutRes() = R.layout.adapter_home

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val binding = holder.binding as AdapterHomeBinding
//        coursesList.apply {
//            add(CourseTempData("UI/UX courses", R.drawable.ic_uiux))
//            add(CourseTempData("Science courses", R.drawable.ic_science))
//            add(CourseTempData("UX research part", R.drawable.ic_research))
//            add(CourseTempData("Color Pallete", R.drawable.ic_color_palatte))
//        }

        binding.tvSeeAll.visibleView(
            !courseTypeList?.get(position)?.courses.isNullOrEmpty() && (courseTypeList?.get(
                position
            )?.courses?.size ?: 0) == 9
        )
        binding.rvCourses.adapter =
            HomeCoursesAdapter(courseTypeList?.get(position)?.courses).apply {
                setOnAdapterItemClickListener(object : IViewClick {
                    override fun onItemClick(vararg items: Any) {
                        this@HomeAdapter.onItemClick(
                            items[0] as Int,
                            holder.bindingAdapterPosition,
                            items[1] as Int
                        )
                    }
                })
            }
        binding.tvTitle.text = courseTypeList?.get(position)?.title ?: ""
        binding.tvSeeAll.setOnClickListener {
            onItemClick(Constant.CLICK_SEE_ALL, position)
        }
    }

    override fun getItemCount() = courseTypeList?.size ?: 0


}