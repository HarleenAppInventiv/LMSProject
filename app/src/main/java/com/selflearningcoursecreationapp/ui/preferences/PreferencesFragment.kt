package com.selflearningcoursecreationapp.ui.preferences

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.databinding.FragmentPreferencesBinding
import com.selflearningcoursecreationapp.di.getAppContext
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.AppThemeFile
import com.selflearningcoursecreationapp.models.ThemeData
import com.selflearningcoursecreationapp.ui.authentication.InitialActivity
import com.selflearningcoursecreationapp.ui.preferences.category.CategoryFragment
import com.selflearningcoursecreationapp.ui.preferences.font.SelectFontFragment
import com.selflearningcoursecreationapp.ui.preferences.language.SelectLanguageFragment
import com.selflearningcoursecreationapp.ui.preferences.theme.SelectThemeFragment
import com.selflearningcoursecreationapp.ui.splash.intro_slider.DotAdapter
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.text.style.ForegroundColorSpan

import android.text.SpannableString





class PreferencesFragment : BaseFragment<FragmentPreferencesBinding>(), View.OnClickListener {
    private val viewModel: PreferenceViewModel by viewModels()
    private var dotAdapter: DotAdapter? = null
    private var dotList: ArrayList<Boolean> = ArrayList()
    private var type: Int = TYPE_ALL
    private var screenType: Int = SCREEN_APP
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            PreferencesFragmentArgs.fromBundle(it).let { args ->
                type = args.type
                screenType = args.screenType
                if (!args.title.isNullOrEmpty())
                    baseActivity.setToolbar(args.title)
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_preferences
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = parentFragment
        if (type == TYPE_ALL) {
            setHasOptionsMenu(true)
        }
        initUi()
    }

    private fun initUi() {
        initViewPager()
        observeListeners()
        binding.cbSelectAll.setOnClickListener(this)
        binding.btContinue.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.preference_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item= menu.findItem(R.id.action_skip)
        val s = SpannableString(item.getTitle())
        s.setSpan(ForegroundColorSpan(ThemeUtils.getAppColor(baseActivity)), 0, s.length, 0)
        item.setTitle(s)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_skip -> {
                when (binding.viewpager.currentItem) {
                    0 -> {
                        viewModel.categoryListLiveData.value?.forEachIndexed { index, _ ->
                            viewModel.categoryListLiveData.value!![index].isSelected = false
                        }
                        binding.viewpager.currentItem += 1
                    }
                    1 -> {
                        viewModel.themeListLiveData.value?.forEach { themeData ->
                            themeData.isSelected = themeData.id == viewModel.selectedThemeId
                        }
                        binding.viewpager.currentItem += 1
                    }
                    2 -> {
                        viewModel.fontListData.value?.forEach { themeData ->
                            themeData.isSelected = viewModel.selectedThemeId == themeData.id

                        }

                        binding.viewpager.currentItem += 1
                    }

                    3 -> {
                        findNavController().navigate(R.id.homeActivity)

                    }
                }

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViewPager() {
        val list = ArrayList<Fragment>()
        val fragList = arrayListOf(
            CategoryFragment(),
            SelectThemeFragment(),
            SelectFontFragment(),
            SelectLanguageFragment()
        )


        if (type == TYPE_ALL) {
            list.addAll(fragList)
        } else {
            list.add(fragList[type])
        }
        binding.rvPagerDot.visibleView(type == TYPE_ALL)


//        if (type == TYPE_ALL || type == TYPE_CATEGORY)
//            list.add(CategoryFragment())
//        if (type == TYPE_ALL || type == TYPE_THEME)
//            list.add(SelectThemeFragment())
//        if (type == TYPE_ALL || type == TYPE_FONT)
//            list.add(SelectFontFragment())
//        if (type == TYPE_ALL || type == TYPE_LANGUAGE)
//            list.add(SelectLanguageFragment())
//

        val adapter = ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)
        binding.viewpager.adapter = adapter
        dotList.clear()
        dotList.addAll(list.map { false })
        dotList.set(0, true)

        setDotAdapter()
        setData()

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                dotList.forEachIndexed { index, _ ->
                    dotList[index] = index == position
                }
                setDotAdapter()

                setData()

            }
        })
    }

    private fun observeListeners() {
        if (type == TYPE_ALL || type == TYPE_CATEGORY) {
            viewModel.categoryListLiveData.observe(viewLifecycleOwner, Observer {
                setCategoryData()
            })
        }
        if (type == TYPE_ALL || type == TYPE_THEME) {
            viewModel.themeListLiveData.observe(viewLifecycleOwner, Observer {
                setThemeData()
            })
        }
        if (type == TYPE_ALL || type == TYPE_FONT) {
            viewModel.fontListData.observe(viewLifecycleOwner, Observer {
                setFontData()
            })
        }
        if (type == TYPE_ALL || type == TYPE_LANGUAGE) {
            viewModel.languageListLiveData.observe(viewLifecycleOwner, Observer {
                setLanguageData()
            })
        }

    }

    private fun setCategoryData() {
        val total = viewModel.categoryListLiveData.value?.filter { it.isSelected }?.size ?: 0
//        if (binding.viewpager.currentItem == 0) {
        binding.tvSelectedValue.text = total.toString()
        binding.tvSelectedValue.setColor(R.attr.colorPrimary)
        binding.tvSelectedValue.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
//        }
    }

    private fun setThemeData() {
        val themeData = viewModel.themeListLiveData.value?.singleOrNull { it.isSelected }
//        if (binding.viewpager.currentItem == 1) {
        themeData?.let { theme ->
            binding.tvSelectedValue.text = baseActivity.getString(theme.themeName)
            binding.ivSelectedTheme.visible()
            binding.ivSelectedTheme.setBackgroundColor(
                ContextCompat.getColor(
                    baseActivity,
                    theme.themeColor
                )
            )
        } ?: kotlin.run {
            binding.tvSelectedValue.text = ""
            binding.ivSelectedTheme.gone()
        }

        binding.tvSelectedValue.setColor(R.attr.headingTextColor)
        binding.tvSelectedValue.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
//        }
    }

    private fun setFontData() {
        val themeData = viewModel.fontListData.value?.singleOrNull { it.isSelected }
//        if (binding.viewpager.currentItem == 2) {
        themeData?.let { theme ->
            binding.tvSelectedValue.text = baseActivity.getString(theme.themeName)
            binding.tvSelectedValue.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_font_preview,
                0,
                0,
                0
            )

        } ?: kotlin.run {
            binding.tvSelectedValue.text = ""
            binding.tvSelectedValue.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

        }

        binding.tvSelectedValue.setColor(R.attr.headingTextColor)
//        }
    }

    private fun setLanguageData() {
        val themeData = viewModel.languageListLiveData.value?.singleOrNull { it.isSelected }
//        if (binding.viewpager.currentItem == 3) {
        themeData?.let { theme ->
            binding.tvSelectedValue.text = baseActivity.getString(theme.themeName)


        } ?: kotlin.run {
            binding.tvSelectedValue.text = ""

        }
        binding.tvSelectedValue.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)

        binding.tvSelectedValue.setColor(R.attr.headingTextColor)
//        }
    }

    private fun setData() {
        val position = if (type == TYPE_ALL) binding.viewpager.currentItem else type


        val selectedTitleList =
            baseActivity.resources.getStringArray(R.array.preference_selected_title)
        binding.tvSelectedTitle.text = selectedTitleList[position]
        binding.tvSelectedValue.setColor(R.attr.colorPrimary)

        binding.cbSelectAll.gone()
        binding.ivSelectedTheme.gone()
        binding.tvSelectedValue.setColor(R.attr.headingTextColor)
        binding.tvSelectedValue.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        binding.btContinue.text = baseActivity.getString(R.string.next)


        when (position) {
            0 -> {
                binding.cbSelectAll.visible()
                setCategoryData()
            }
            1 -> {
                setThemeData()
            }
            2 -> {
                setFontData()
            }
            3 -> {
                binding.btContinue.text = baseActivity.getString(R.string.continue_text)
                setLanguageData()
            }
        }


        if (type != TYPE_ALL) {
            if (screenType == SCREEN_APP)
                binding.btContinue.text = baseActivity.getString(R.string.apply)
            else binding.btContinue.text = baseActivity.getString(R.string.select)

        }
    }

    private fun setDotAdapter() {
        dotAdapter?.notifyDataSetChanged() ?: kotlin.run {
            dotAdapter = DotAdapter(Constant.TYPE_ROUND, dotList)
            binding.rvPagerDot.adapter = dotAdapter
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.cb_selectAll -> {
                viewModel.categoryListLiveData.value?.let { list ->
                    list.forEachIndexed { index, _ ->
                        list[index].isSelected = binding.cbSelectAll.isChecked
                    }

                    viewModel.categoryListLiveData.value = list
                }
            }
            R.id.bt_continue -> {
                when (type) {
                    TYPE_ALL -> {
                        if (binding.viewpager.currentItem == 3) {
                            showLoading()

                            lifecycleScope.launch {
                                val operation = async {
                                    viewModel.setAllValue()
                                }
                                operation.await()
                                delay(1000)
                                baseActivity.runOnUiThread {
                                    hideLoading()
                                    findNavController().navigate(R.id.homeActivity)
                                }
                            }
                        } else {
                            binding.viewpager.currentItem += 1
                            setData()
                        }
                    }

                    TYPE_CATEGORY -> {
                        if (screenType == SCREEN_APP)
                            goToNext()
                        else sendSelectedData()

                    }

                    else -> {
                        if (screenType == SCREEN_APP)
                            saveAppData()
                        else sendSelectedData()
                    }

                }
            }
        }
    }

    private fun sendSelectedData() {
        val list = when (type) {
            TYPE_LANGUAGE -> {
                viewModel.languageListLiveData.value
            }

            TYPE_FONT -> {
                viewModel.fontListData.value
            }
            TYPE_THEME -> {
                viewModel.themeListLiveData.value
            }
            else -> {
                viewModel.categoryListLiveData.value
            }
        }?.filter { it.isSelected }

        setFragmentResult("preferenceData", bundleOf("type" to type, "data" to list))
        findNavController().navigateUp()
    }

    private fun saveAppData() {
        var changedText = ""
        var themeText = ""
        showLoading()
        lifecycleScope.launch {
            val operation = async {
                when (type) {
                    TYPE_LANGUAGE -> {

                        viewModel.saveLanguage()
                        changedText = baseActivity.getString(R.string.language_changed_to)
                        themeText =
                            viewModel.languageListLiveData.value?.singleOrNull { it.isSelected }
                                ?.let {
                                    getString(it.themeName)
                                } ?: ""

                    }
                    TYPE_FONT -> {
                        viewModel.saveFont()
                        changedText = baseActivity.getString(R.string.font_changed_to)
                        themeText =
                            viewModel.fontListData.value?.singleOrNull { it.isSelected }
                                ?.let {
                                    getString(it.themeName)
                                } ?: ""
                    }
                    TYPE_THEME -> {
                        viewModel.saveTheme()
                        viewModel.themeListLiveData.value?.singleOrNull { it.isSelected }
                            ?.let { baseActivity.saveThemeFile(baseActivity.getThemeFile(baseActivity.getString(it.themeColor))) }
                        delay(1000)
                        (getAppContext() as SelfLearningApplication).updatedThemeFile()
                        changedText = baseActivity.getString(R.string.theme_changed_to)
                        themeText =
                            viewModel.themeListLiveData.value?.singleOrNull { it.isSelected }
                                ?.let {
                                    "${getString(it.themeName)} ${getString(R.string.theme)}"
                                } ?: ""
                    }
                }
            }
            operation.await()
            delay(1000)
            baseActivity.runOnUiThread {
                hideLoading()
                val msg = baseActivity.getSpanString(
                    "$changedText \"$themeText\"",
                    isBold = true,
                    startPos = changedText.length
                )
                baseActivity.showAlertDialog(
                    spanText = msg,
                    buttonText = baseActivity.getString(R.string.ok_close),
                    isCancellable = false,
                    onClick = {
                        baseActivity.recreate()
                    })


            }
        }
    }

    private fun goToNext() {
        if (baseActivity is InitialActivity) {
            findNavController().navigate(R.id.homeActivity)
        } else {
            findNavController().navigateUp()
        }
    }



    companion object {
        const val TYPE_CATEGORY = 0
        const val TYPE_THEME = 1
        const val TYPE_FONT = 2
        const val TYPE_LANGUAGE = 3
        const val TYPE_ALL = -1
        const val SCREEN_APP = 1
        const val SCREEN_COURSE = 2
    }

}