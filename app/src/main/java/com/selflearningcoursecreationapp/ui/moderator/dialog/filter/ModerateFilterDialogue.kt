package com.selflearningcoursecreationapp.ui.moderator.dialog

import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseBottomSheetDialog
import com.selflearningcoursecreationapp.databinding.DialogeModeratorFilterBinding
import com.selflearningcoursecreationapp.ui.moderator.dialog.filter.ModeratorFilterChildAdapter
import com.selflearningcoursecreationapp.utils.Constant

class ModerateFilterDialogue : BaseBottomSheetDialog<DialogeModeratorFilterBinding>() {

    private var catArrayList = arrayListOf<Payload>()
    private var catItemArrayList = arrayListOf<Payload>()
    private lateinit var parentAdapter: ModeratorFilterParentAdapter
    private lateinit var childAdapter: ModeratorFilterChildAdapter

    override fun getLayoutRes(): Int {

        return R.layout.dialoge_moderator_filter
    }

    override fun initUi() {


        catArrayList.add(Payload("category", true, Constant.TYPE_CATEGORY))
        catArrayList.add(Payload("REQUEST DATE", false, Constant.TYPE_REQUEST_DATE))
        catArrayList.add(Payload("FEE RANGE", false, Constant.TYPE_FEE_RANGE))
        catArrayList.add(Payload("CREATOR’S NAME", false, Constant.TYPE_CREATOR_NAME))

        parentAdapter = ModeratorFilterParentAdapter { type: Int, position: Int ->
            catArrayList.forEachIndexed { index, _ ->
                catArrayList[index].isSelected = index == position
                parentAdapter.setAdapterList(catArrayList)
                childAdapter.setAdapterList(catItemArrayList, type)
            }


        }
        parentAdapter.setAdapterList(catArrayList)
        binding.recyclerFilterCat.adapter = parentAdapter



        catItemArrayList.add(Payload("Arts", false, Constant.TYPE_CATEGORY))
        catItemArrayList.add(Payload("Business", false, Constant.TYPE_CATEGORY))
        catItemArrayList.add(Payload("Maths", false, Constant.TYPE_CATEGORY))
        catItemArrayList.add(Payload("Chemistry", false, Constant.TYPE_CATEGORY))
        catItemArrayList.add(Payload("Biology", false, Constant.TYPE_CATEGORY))

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


}

data class Payload(var name: String, var isSelected: Boolean, var type: Int)