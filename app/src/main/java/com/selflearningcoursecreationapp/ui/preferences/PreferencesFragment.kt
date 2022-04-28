package com.selflearningcoursecreationapp.ui.preferences

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.databinding.FragmentPreferencesBinding
import com.selflearningcoursecreationapp.di.getAppContext
import com.selflearningcoursecreationapp.extensions.gone
import com.selflearningcoursecreationapp.extensions.showLog
import com.selflearningcoursecreationapp.extensions.visible
import com.selflearningcoursecreationapp.extensions.visibleView
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.ui.preferences.category.CategoryFragment
import com.selflearningcoursecreationapp.ui.preferences.font.SelectFontFragment
import com.selflearningcoursecreationapp.ui.preferences.language.SelectLanguageFragment
import com.selflearningcoursecreationapp.ui.preferences.theme.SelectThemeFragment
import com.selflearningcoursecreationapp.ui.splash.intro_slider.DotAdapter
import com.selflearningcoursecreationapp.utils.*
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class PreferencesFragment : BaseFragment<FragmentPreferencesBinding>(), View.OnClickListener {
    private var adapter: ScreenSlidePagerAdapter? = null
    private val viewModel: PreferenceViewModel by viewModel()
    private var dotAdapter: DotAdapter? = null
    private var dotList: ArrayList<Boolean> = ArrayList()
    private var type: Int = PREFERENCES.TYPE_ALL
    private var currentSelection: Int = 0
    private var screenType: Int = PREFERENCES.SCREEN_APP
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            PreferencesFragmentArgs.fromBundle(it).let { args ->
                type = args.type
                screenType = args.screenType
                currentSelection =
                    if (args.currentSelection == -1) {
                        if (args.type == -1) 0 else args.type
                    } else args.currentSelection
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
//        if (type == PREFERENCES.TYPE_ALL) {
        setHasOptionsMenu(true)
//        }
        initUi()
    }

    private fun initUi() {

        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

        initViewPager()
        observeListeners()
        if (screenType == PREFERENCES.SCREEN_APP && type == PREFERENCES.TYPE_ALL) {
            lifecycleScope.launch {
                delay(500)
                binding.viewpager.currentItem = currentSelection

                binding.viewpager.adapter?.notifyDataSetChanged()
                baseActivity.runOnUiThread {
//                setData()
                }
            }

        }
        binding.cbSelectAll.setOnClickListener(this)
        binding.btContinue.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (type == PREFERENCES.TYPE_ALL) {
            inflater.inflate(R.menu.preference_menu, menu)

        } else {
            inflater.inflate(R.menu.course_menu, menu)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (type == PREFERENCES.TYPE_ALL) {
            val item = menu.findItem(R.id.action_skip)
            val s = SpannableString(item.title)
            s.setSpan(ForegroundColorSpan(ThemeUtils.getAppColor(baseActivity)), 0, s.length, 0)
            item.title = s
        } else {
//            val item = menu.findItem(R.id.action_read)
//            item.icon.colorFilter = PorterDuffColorFilter(ThemeUtils.getAppColor(requireContext()),
//                PorterDuff.Mode.SRC_IN)

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_skip -> {
                when (binding.viewpager.currentItem) {
                    0 -> {
                        viewModel.categoryListLiveData.value?.forEachIndexed { index, _ ->
                            viewModel.categoryListLiveData.value!![index].isSelected = false
                        }

                    }
                    1 -> {
                        viewModel.themeListLiveData.value?.forEach { themeData ->
                            themeData.isSelected = themeData.id == viewModel.userProfile?.theme?.id
                        }

                    }
                    2 -> {
                        viewModel.fontListData.value?.forEach { themeData ->
                            themeData.isSelected = viewModel.userProfile?.font?.id == themeData.id

                        }


                    }

                    3 -> {
                        viewModel.languageListLiveData.value?.forEach { data ->
                            data.isSelected = viewModel.userProfile?.language?.code == data.code

                        }


                    }
                }
                saveData()


            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveData() {
//        viewModel.getApiResponse().observe(viewLifecycleOwner, this)

        viewModel.savePrefernce(currentSelection)
    }

    private fun initViewPager() {
        val list = ArrayList<Fragment>()
        val fragList = arrayListOf(
            CategoryFragment(),
            SelectThemeFragment(),
            SelectFontFragment(),
            SelectLanguageFragment()
        )


        if (type == PREFERENCES.TYPE_ALL) {
            list.addAll(fragList)
        } else {
            list.add(fragList[type])
        }
        binding.rvPagerDot.visibleView(type == PREFERENCES.TYPE_ALL)
        setData()

        adapter = ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)
        binding.viewpager.adapter = adapter
        dotList.clear()
        dotList.addAll(list.map { false })
        dotList.set(0, true)
        binding.viewpager.isUserInputEnabled = false
        setDotAdapter()

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                dotList.forEachIndexed { index, _ ->
                    dotList[index] = index == position
                }
                setDotAdapter()
                if (position != 3) {
                    setHasOptionsMenu(true)
                } else {
                    setHasOptionsMenu(false)

                }
                setData()

            }
        })


    }

    private fun observeListeners() {
        if (type == PREFERENCES.TYPE_ALL || type == PREFERENCES.TYPE_CATEGORY) {
            viewModel.categoryListLiveData.observe(viewLifecycleOwner, Observer {
                if (currentSelection == PREFERENCES.TYPE_CATEGORY) {
                    resetData()
                    setCategoryData()

                    binding.cbSelectAll.isChecked = it.filter { !it.isSelected }.isNullOrEmpty()
                }
            })
        }
        if (type == PREFERENCES.TYPE_ALL || type == PREFERENCES.TYPE_THEME) {
            viewModel.themeListLiveData.observe(viewLifecycleOwner, Observer {
                if (currentSelection == PREFERENCES.TYPE_THEME) {
                    resetData()
                    setThemeData()
                }
            })
        }
        if (type == PREFERENCES.TYPE_ALL || type == PREFERENCES.TYPE_FONT) {
            viewModel.fontListData.observe(viewLifecycleOwner, {
//                if (currentSelection==PREFERENCES.TYPE_FONT) {
                setData()
            })
        }
        if (type == PREFERENCES.TYPE_ALL || type == PREFERENCES.TYPE_LANGUAGE) {
            viewModel.languageListLiveData.observe(viewLifecycleOwner, Observer {
                if (currentSelection == PREFERENCES.TYPE_LANGUAGE) {
                    setData()
                }
            })
        }

    }

    private fun setCategoryData() {
        val total = viewModel.categoryListLiveData.value?.filter { it.isSelected }?.size ?: 0
        binding.tvSelectedValue.text = "${if (total <= 9) "0$total" else total}"
        binding.tvSelectedValue.changeTextColor(ThemeConstants.TYPE_THEME)
    }

    private fun setThemeData() {
        val themeData = viewModel.themeListLiveData.value?.singleOrNull { it.isSelected }
        themeData?.let { theme ->
            binding.tvSelectedValue.text = theme.name
            binding.ivSelectedTheme.visible()
            binding.ivSelectedTheme.setBackgroundColor(
                Color.parseColor(theme.code)
            )
        }
    }

    private fun setFontData() {
        showLog("PREFERENCES", "setFontData")
        val themeData = viewModel.fontListData.value?.singleOrNull { it.isSelected }
        themeData?.let { theme ->
            binding.tvSelectedValue.text = theme.name
            binding.tvSelectedValue.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_font_preview,
                0,
                0,
                0
            )

        }
    }

    private fun setLanguageData() {
        showLog("PREFERENCES", "language")
        val themeData = viewModel.languageListLiveData.value?.singleOrNull { it.isSelected }
        themeData?.let { theme ->
            binding.tvSelectedValue.text = theme.name
        }
    }

    private fun resetData() {
        binding.tvSelectedValue.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        binding.tvSelectedValue.changeTextColor(ThemeConstants.TYPE_HEADING)
        binding.tvSelectedValue.text = ""
        binding.ivSelectedTheme.gone()
    }


    private fun setData() {
        val position = if (type == PREFERENCES.TYPE_ALL) binding.viewpager.currentItem else type
        val selectedTitleList =
            baseActivity.resources.getStringArray(R.array.preference_selected_title)
        binding.tvSelectedTitle.text = selectedTitleList[position]
        resetData()
        binding.cbSelectAll.gone()
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


        if (type != PREFERENCES.TYPE_ALL) {
            if (screenType == PREFERENCES.SCREEN_APP)
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
                if (screenType == PREFERENCES.SCREEN_APP)
                    saveData()
                else sendSelectedData()

            }
        }
    }

    private fun sendSelectedData() {
        val list = when (type) {
            PREFERENCES.TYPE_LANGUAGE -> {
                viewModel.languageListLiveData.value
            }

            PREFERENCES.TYPE_FONT -> {
                viewModel.fontListData.value
            }
            PREFERENCES.TYPE_THEME -> {
                viewModel.themeListLiveData.value
            }
            else -> {
                viewModel.categoryListLiveData.value
            }
        }?.filter { it.isSelected }

        setFragmentResult("preferenceData", bundleOf("type" to type, "data" to list))
        findNavController().navigateUp()
    }


    @SuppressLint("ResourceType")
    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_SAVE_PREFRENCE -> {
                showLoading()
                lifecycleScope.launch {
                    viewModel.savePrefDataInDB(type)
                    delay(2000)
                    baseActivity.runOnUiThread {
                        hideLoading()
                        if (type == PREFERENCES.TYPE_ALL) {
                            pagerMoveOrUpdateThemeFile()
                        } else if (type != PREFERENCES.TYPE_CATEGORY) {

                            var changedText = ""
                            var themeText = ""

                            when (type) {
                                PREFERENCES.TYPE_LANGUAGE -> {
                                    changedText =
                                        baseActivity.getString(R.string.language_changed_to)
                                    themeText =
                                        viewModel.languageListLiveData.value?.singleOrNull { it.isSelected }
                                            ?.let {
                                                it.name
                                            } ?: ""

                                }
                                PREFERENCES.TYPE_FONT -> {
                                    changedText = baseActivity.getString(R.string.font_changed_to)
                                    themeText =
                                        viewModel.fontListData.value?.singleOrNull { it.isSelected }
                                            ?.let {
                                                it.name
                                            } ?: ""
                                }
                                PREFERENCES.TYPE_THEME -> {
                                    lifecycleScope.launch {
                                        (getAppContext() as SelfLearningApplication).updatedThemeFile()
                                    }
                                    val themeData =
                                        viewModel.themeListLiveData.value?.singleOrNull { it.isSelected }
                                    changedText = baseActivity.getString(R.string.theme_changed_to)
                                    themeText = "${themeData?.name} ${getString(R.string.theme)}"

                                }
                            }

                            val msg =
                                SpanUtils.with(baseActivity, "$changedText \"$themeText\"")
                                    .apply {
                                        isBold()
                                        startPos(changedText.length)
                                        themeColor()
                                    }.getSpanString()

                            closePopUp(msg)


                        } else {
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    private fun closePopUp(msg: SpannableString) {
        CommonAlertDialog.builder(baseActivity)
            .spannedText(msg)
            .notCancellable()
            .positiveBtnText(getString(R.string.ok_close))
            .hideNegativeBtn(true)
            .getCallback {
                if (it) {
                    showLog(
                        "THEME",
                        SelfLearningApplication.themeFile ?: "no gg"
                    )
                    baseActivity.startActivity(
                        Intent(
                            baseActivity,
                            HomeActivity::class.java
                        )
                    )
                    baseActivity.finish()

//                                        baseActivity.recreate()
                }
            }.build()
    }

    fun onClickBack() {
        if (type != PREFERENCES.TYPE_ALL) {
            findNavController().navigateUp()
        } else {
            if (currentSelection == PreferencesFragmentArgs?.fromBundle(requireArguments())?.currentSelection) {
                findNavController().navigateUp()
            } else {
                when (binding.viewpager.currentItem) {
                    0 -> {
                        findNavController().navigateUp()
                    }
                    else -> {
                        currentSelection -= 1
                        binding.viewpager.currentItem -= 1
                    }
                }
            }
        }
    }

    fun pagerMoveOrUpdateThemeFile() {
        currentSelection += 1
        showLog("CURRENT_SELECTION", "$currentSelection")
        when (binding.viewpager.currentItem) {

            3 -> {
                lifecycleScope.launch {
                    (getAppContext() as SelfLearningApplication).updatedThemeFile()
                }
                findNavController().navigate(R.id.homeActivity)
            }
            else -> {
                binding.viewpager.currentItem += 1
            }
        }
    }


}