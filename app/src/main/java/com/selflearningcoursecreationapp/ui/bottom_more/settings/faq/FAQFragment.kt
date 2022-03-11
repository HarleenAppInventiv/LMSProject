package com.selflearningcoursecreationapp.ui.bottom_more.settings.faq

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseAdapter
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentFAQBinding
import com.selflearningcoursecreationapp.utils.HandleClick

class FAQFragment : BaseFragment<FragmentFAQBinding>(), HandleClick, BaseAdapter.IViewClick {
    var faqAdapter: FAQAdapter? = null
    var list = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        binding.faq = this
        baseActivity.supportActionBar?.hide()

        list.add(WHY_DO_WE)
        list.add(WHERE_CAN_I)
        list.add(WHY_DO_WE)
        list.add(WHERE_CAN_I)
        list.add(WHY_DO_WE)

        setAdapter()

    }

    override fun getLayoutRes() = R.layout.fragment_f_a_q
    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            var view = items[0] as View
            when (view?.id) {
                R.id.img_back -> {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun setAdapter() {


        faqAdapter?.notifyDataSetChanged() ?: kotlin.run {
            faqAdapter = FAQAdapter(list, requireContext())
            binding.rvFaqQuestion.adapter = faqAdapter
            faqAdapter!!.setOnAdapterItemClickListener(this)
        }
    }

    override fun onItemClick(vararg items: Any) {
        Toast.makeText(requireContext(), "" + items[1].toString(), Toast.LENGTH_SHORT).show()

    }


    companion object {
        const val WHY_DO_WE = "Why do we use it?"
        const val WHERE_CAN_I = "Where can I get some?"
    }


}