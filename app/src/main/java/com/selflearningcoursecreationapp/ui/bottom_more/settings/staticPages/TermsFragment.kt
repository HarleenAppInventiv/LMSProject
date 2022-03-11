package com.selflearningcoursecreationapp.ui.bottom_more.settings.staticPages

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentTermsBinding
import com.selflearningcoursecreationapp.utils.HandleClick


class TermsFragment : BaseFragment<FragmentTermsBinding>(), HandleClick {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    fun init() {
        binding.terms = this
        baseActivity.supportActionBar?.hide()

    }

    override fun getLayoutRes() = R.layout.fragment_terms
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


}