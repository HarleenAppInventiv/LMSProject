package com.selflearningcoursecreationapp.ui.create_course.add_sections_lecture

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentAddSectionOrLectureBinding
import com.selflearningcoursecreationapp.utils.Constant


class AddSectionOrLectureFragment : BaseFragment<FragmentAddSectionOrLectureBinding>(),
    BaseAdapter.IViewClick {
    private var adapter: AddSectionAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {

        setAdapter()


    }

    override fun getLayoutRes() = R.layout.fragment_add_section_or_lecture
    override fun onItemClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val type = items[0] as Int
            val position = items[1] as Int
            val childPosition = items[2] as Int
            when (type) {
                Constant.CLICK_VIEW -> {
                    Toast.makeText(requireContext(), "${position}", Toast.LENGTH_SHORT).show()
                }
                Constant.CLICK_DELETE -> {
                    showToastShort("${childPosition}delete")
                }
                Constant.CLICK_MORE -> {
                    showToastShort("${position}more")
                    showBottomSheetDialogmore()
                }
                Constant.CLICK_UPLOAD -> {
                    showBottomSheetDialog()
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

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.botttom_dialog_upload_vdo_ado)
        val audio = bottomSheetDialog.findViewById<TextView>(R.id.tv_take_audio)
        val video = bottomSheetDialog.findViewById<TextView>(R.id.tv_take_video)
        val imgClose = bottomSheetDialog.findViewById<ImageView>(R.id.iv_close)

        imgClose!!.setOnClickListener() {
            bottomSheetDialog.dismiss()
        }

        audio!!.setOnClickListener() {
            bottomSheetDialog.dismiss()

        }

        video!!.setOnClickListener() {
            bottomSheetDialog.dismiss()

        }
        bottomSheetDialog.show()
    }


    private fun showBottomSheetDialogmore() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_dialog_more_options)
        val addLecture = bottomSheetDialog.findViewById<TextView>(R.id.tv_add_lecture)
        val rmvSection = bottomSheetDialog.findViewById<TextView>(R.id.tv_remove_section)
        val imgClose = bottomSheetDialog.findViewById<ImageView>(R.id.iv_close)

        imgClose!!.setOnClickListener() {
            bottomSheetDialog.dismiss()
        }

        rmvSection!!.setOnClickListener() {
            bottomSheetDialog.dismiss()

        }

        addLecture!!.setOnClickListener() {
            bottomSheetDialog.dismiss()

        }
        bottomSheetDialog.show()
    }


}