package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentAddSectionOrLectureBinding
import com.selflearningcoursecreationapp.ui.dialog.SectionMoreDialog
import com.selflearningcoursecreationapp.ui.dialog.UploadDocOptionsDialog
import com.selflearningcoursecreationapp.utils.Constant


class AddSectionOrLectureFragment : BaseFragment<FragmentAddSectionOrLectureBinding>(),
    BaseAdapter.IViewClick, BaseBottomSheetDialog.IDialogClick {
    private var adapter: AddSectionAdapter? = null
    private var adapterPosition: Int = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {

        setAdapter()
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }

    override fun getLayoutRes() = R.layout.fragment_add_section_or_lecture
    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            adapterPosition = items[1] as Int
            val childPosition = items[2] as Int
            when (type) {
                Constant.CLICK_VIEW -> {
                    Toast.makeText(requireContext(), "${adapterPosition}", Toast.LENGTH_SHORT)
                        .show()
                }
                Constant.CLICK_DELETE -> {
                    showToastShort("${childPosition}delete")
                }
                Constant.CLICK_MORE -> {
                    showToastShort("${adapterPosition} more")
                    SectionMoreDialog().apply {
                        setOnDialogClickListener(this@AddSectionOrLectureFragment)
                    }.show(childFragmentManager, "")
                }
                Constant.CLICK_UPLOAD -> {
                    UploadDocOptionsDialog().show(childFragmentManager, "")
                }
            }
        }
    }


    private fun setAdapter() {

        adapter?.notifyDataSetChanged() ?: kotlin.run {
            adapter = AddSectionAdapter()
            binding.rvSections.adapter = adapter
            adapter!!.setOnAdapterItemClickListener(this)
        }
    }


    override fun onDialogClick(vararg items: Any) {
        if (items.isNotEmpty()) {
//            val type = items[0] as Int
//            when (type) {
//                Constant.CLICK_ADD -> {
//
//
//                }
//                Constant.CLICK_DELETE -> {
//
//                }
//            }
        }
    }


}