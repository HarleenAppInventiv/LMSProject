package com.selflearningcoursecreationapp.ui.bottom_more

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.utils.HandleClick


class MoreFragment :
    BaseFragment<com.selflearningcoursecreationapp.databinding.FragmentMoreBinding>(),
    HandleClick {
    override fun getLayoutRes(): Int {
        return R.layout.fragment_more
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        setHasOptionsMenu(true)
    }

    private fun initUi() {
        binding.handleClick = this
        /*  val spannable: SpannableString = SpannableString(baseActivity.getString(R.string.settings))
          spannable.setSpan(baseActivity.localeSpan?: LocaleSpan(Locale.getDefault()), 0, spannable.length, 0)
          binding.tvSettings.text=spannable */
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.course_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onHandleClick(vararg items: Any) {
        if (items.isNotEmpty()) {
            val view = items[0] as View
            when (view.id) {
                R.id.tv_settings -> {
                    findNavController().navigate(R.id.action_moreFragment_to_settingsFragment)
                }
                R.id.tv_payments -> {
                    findNavController().navigate(R.id.action_moreFragment_to_paymentsFragment)
                }
                R.id.tv_cards -> {
                    findNavController().navigate(R.id.action_moreFragment_to_cardsFragment)
                }
                R.id.tv_moderator -> {
//                    findNavController().navigate(R.id.action_moreFragment_to_reviewTabFragment)

                }
                R.id.tv_practice_accent -> {
                    findNavController().navigate(R.id.action_moreFragment_to_assessmentFragment)

                }

            }
        }

    }

}