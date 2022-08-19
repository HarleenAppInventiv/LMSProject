package com.selflearningcoursecreationapp.ui.moderator.dialog.filter

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogeModeratorFilterBinding
import com.selflearningcoursecreationapp.ui.create_course.add_courses_steps.AddCourseViewModel
import com.selflearningcoursecreationapp.ui.moderator.dialog.ModeratorFilterParentAdapter
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class ModerateFilterDialogue : BaseBottomSheetDialog<DialogeModeratorFilterBinding>() {

    private val viewModel: AddCourseViewModel by viewModel()

    private var catArrayList = arrayListOf<Payload>()
    private var catItemArrayList = arrayListOf<Payload>()
    private lateinit var parentAdapter: ModeratorFilterParentAdapter
    private lateinit var childAdapter: ModeratorFilterChildAdapter

    override fun getLayoutRes(): Int {

        return R.layout.dialoge_moderator_filter
    }

    override fun initUi() {

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)
        if (!viewModel.masterData.isDataAdded()) {
            viewModel.getMasterData()
        }

        catArrayList.add(
            Payload(
                getString(R.string.language).uppercase(Locale.getDefault()),
                true,
                Constant.TYPE_CATEGORY
            )
        )
        catArrayList.add(
            Payload(
                getString(R.string.request_date),
                false,
                Constant.TYPE_REQUEST_DATE
            )
        )
        catArrayList.add(Payload(getString(R.string.fee_range), false, Constant.TYPE_FEE_RANGE))
        catArrayList.add(
            Payload(
                getString(R.string.creator_name),
                false,
                Constant.TYPE_CREATOR_NAME
            )
        )

        parentAdapter = ModeratorFilterParentAdapter { type: Int, position: Int ->
            catArrayList.forEachIndexed { index, _ ->
                catArrayList[index].isSelected = index == position
                parentAdapter.setAdapterList(catArrayList)
                childAdapter.setAdapterList(catItemArrayList, type)
            }


        }
        parentAdapter.setAdapterList(catArrayList)
        binding.recyclerFilterCat.adapter = parentAdapter


//        catItemArrayList.add(Payload("Business", false, Constant.TYPE_CATEGORY))
//        catItemArrayList.add(Payload("Maths", false, Constant.TYPE_CATEGORY))
//        catItemArrayList.add(Payload("Chemistry", false, Constant.TYPE_CATEGORY))
//        catItemArrayList.add(Payload("Biology", false, Constant.TYPE_CATEGORY))

        childAdapter = ModeratorFilterChildAdapter(baseActivity) { clickType, position ->
            when (clickType) {
                Constant.TYPE_CATEGORY -> {

                    catItemArrayList.forEachIndexed { index, _ ->
                        if (index == position) {
                            catItemArrayList[position].isSelected = true
                        } else {
                            catItemArrayList[index].isSelected = false
                        }

                    }
                    childAdapter.setAdapterList(catItemArrayList, clickType)

                }
            }


        }
        childAdapter.setAdapterList(catItemArrayList, 0)
        binding.recyclerFilterCatItem.adapter = childAdapter


        binding.imgClose.setOnClickListener {
            dismiss()
        }
        binding.btApply.setOnClickListener {
            dismiss()
        }


    }

    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_MASTER_DATA -> {

                viewModel.masterData.languages?.list?.forEach { categoryData ->
                    catItemArrayList.add(
                        Payload(
                            categoryData.name.toString(),
                            false,
                            Constant.TYPE_CATEGORY
                        )
                    )


                }

                childAdapter.setAdapterList(catItemArrayList, 0)
            }
        }


    }

}

data class Payload(var name: String, var isSelected: Boolean, var type: Int)