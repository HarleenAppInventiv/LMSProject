package com.selflearningcoursecreationapp.ui.dialog

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.BottomDialogCourceCateBinding
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AdapterCourseCategory
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.DialogType

class CourseCategoriesOptionDialog() : BaseBottomSheetDialog<BottomDialogCourceCateBinding>(),
        (String?) -> Unit, BaseAdapter.IViewClick {

    var list = ArrayList<String>()
    lateinit var adapterCourseCategories: AdapterCourseCategory
    override fun getLayoutRes() = R.layout.bottom_dialog_cource_cate
    override fun initUi() {
        binding.ivClose.setOnClickListener {
            dismiss()
        }

//        arguments?.let {

//            val listAllProfessions: ArrayList<ProfessionModel> = it.getParcelableArrayList("data")!!
//            binding.etSearch.gone()

        list.add("Science")
        list.add("Maths")
        list.add("Geography")
        list.add("Chemistry")
        list.add("Biology")
        list.add("Finance")

        binding.etSearch.visible()
        binding.tvTitle.setText(getString(R.string.course_category))

//            setProfessionalAdapter(list)
//        }

    }

//    fun setProfessionalAdapter(list: ArrayList<String>) {
//        binding.recyclerCourceCategory.apply {
//            adapterCourseCategories = AdapterCourse(list)
//            adapter = adapterCourseCategories
//        }
//        adapterCourseCategories.setOnAdapterItemClickListener(this)
//    }

    override fun onItemClick(vararg items: Any) {
        dismiss()
        if (items.isNotEmpty()) {
            val click = items[0] as Int
            val value = items[1]
            when (click) {
                Constant.CLICK_VIEW -> {
                    onDialogClick(DialogType.PROFESSION, value)
                }
            }

        }
    }

    override fun invoke(p1: String?) {

    }

}