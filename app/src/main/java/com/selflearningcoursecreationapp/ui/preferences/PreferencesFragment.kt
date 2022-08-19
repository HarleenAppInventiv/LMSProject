package com.selflearningcoursecreationapp.ui.preferences

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.base.SelfLearningApplication
import com.selflearningcoursecreationapp.databinding.FragmentPreferencesBinding
import com.selflearningcoursecreationapp.di.getAppContext
import com.selflearningcoursecreationapp.extensions.*
import com.selflearningcoursecreationapp.models.user.PreferenceData
import com.selflearningcoursecreationapp.ui.home.HomeActivity
import com.selflearningcoursecreationapp.ui.moderator.ModeratorActivity
import com.selflearningcoursecreationapp.ui.preferences.category.CategoryFragment
import com.selflearningcoursecreationapp.ui.preferences.font.SelectFontFragment
import com.selflearningcoursecreationapp.ui.preferences.language.SelectLanguageFragment
import com.selflearningcoursecreationapp.ui.preferences.theme.SelectThemeFragment
import com.selflearningcoursecreationapp.ui.splash.intro_slider.DotAdapter
import com.selflearningcoursecreationapp.utils.ApiEndPoints
import com.selflearningcoursecreationapp.utils.Constant
import com.selflearningcoursecreationapp.utils.PREFERENCES
import com.selflearningcoursecreationapp.utils.builderUtils.CommonAlertDialog
import com.selflearningcoursecreationapp.utils.builderUtils.SpanUtils
import com.selflearningcoursecreationapp.utils.customViews.ThemeConstants
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

@SuppressLint("NotifyDataSetChanged")

class PreferencesFragment : BaseFragment<FragmentPreferencesBinding>(), View.OnClickListener {
    private var adapter: ScreenSlidePagerAdapter? = null
    private val viewModel: PreferenceViewModel by viewModel()
    private var dotAdapter: DotAdapter? = null
    private var dotList: ArrayList<Boolean> = ArrayList()

    private var currentSelection: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            PreferencesFragmentArgs.fromBundle(it).let { args ->
                viewModel.preferenceArgData = args.preferenceData ?: PreferenceData()
                viewModel.type = viewModel.preferenceArgData.type
                viewModel.screenType = viewModel.preferenceArgData.screenType
                viewModel.languageSingleSelection =
                    viewModel.preferenceArgData.languageSingleSelection
                viewModel.categorySingleSelection =
                    viewModel.preferenceArgData.categorySingleSelection
                currentSelection =
                    if (viewModel.preferenceArgData?.currentSelection.isNullOrNegative()) {
                        if (viewModel.type == -1) 0 else viewModel.type
                    } else viewModel.preferenceArgData?.currentSelection
                if (!viewModel.preferenceArgData?.title.isNullOrEmpty()) {

                    baseActivity.setToolbar(
                        viewModel.preferenceArgData?.title,
                        backIcon = if (viewModel.screenType == PREFERENCES.SCREEN_SELECT) R.drawable.ic_cross_grey else R.drawable.ic_arrow_left
                    )
                }
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
        if (viewModel.screenType == PREFERENCES.SCREEN_APP && viewModel.type == PREFERENCES.TYPE_ALL) {
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
        if (viewModel.type == PREFERENCES.TYPE_ALL) {
            inflater.inflate(R.menu.preference_menu, menu)

        } else {
            inflater.inflate(R.menu.course_menu, menu)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (viewModel.type == PREFERENCES.TYPE_ALL) {
            val item = menu.findItem(R.id.action_skip)
            val s = SpannableString(item.title)
            s.setSpan(ForegroundColorSpan(ThemeUtils.getAppColor(baseActivity)), 0, s.length, 0)
            s.setSpan(
                StyleSpan(Typeface.NORMAL),
                0,
                s.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            item.title = s

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_skip -> {
                when (binding.viewpager.currentItem) {
                    0 -> {
                        binding.btContinue.setBtnDisabled(true)
                        binding.btContinue.isEnabled = (true)
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

        viewModel.savePreference(currentSelection)
    }

    private fun initViewPager() {
        val list = ArrayList<Fragment>()
        val fragList = arrayListOf(
            CategoryFragment(),
            SelectThemeFragment(),
            SelectFontFragment(),
            SelectLanguageFragment()
        )


        if (viewModel.type == PREFERENCES.TYPE_ALL) {
            list.addAll(fragList)
        } else {
            list.add(fragList[viewModel.type])
        }
        binding.rvPagerDot.visibleView(viewModel.type == PREFERENCES.TYPE_ALL)
        setData()

        adapter = ScreenSlidePagerAdapter(childFragmentManager, list, this.lifecycle)
        binding.viewpager.adapter = adapter
        dotList.clear()
        dotList.addAll(list.map { false })
        dotList[0] = true
        binding.viewpager.isUserInputEnabled = false
        setDotAdapter()

        binding.viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                dotList.forEachIndexed { index, _ ->
                    dotList[index] = index == position
                }
                setDotAdapter()
//                if (position != 3) {
//                    setHasOptionsMenu(true)
//                } else {
//                    setHasOptionsMenu(false)
//
//                }
                setData()

            }
        })


    }

    private fun observeListeners() {
        viewModel.categoryListLiveData.observe(viewLifecycleOwner) {
            if (currentSelection == PREFERENCES.TYPE_CATEGORY) {
                resetData()
                setCategoryData()

                binding.cbSelectAll.isChecked =
                    it.filter { data -> !data.isSelected }.isNullOrEmpty()
            }
        }
        viewModel.themeListLiveData.observe(viewLifecycleOwner) {
            if (currentSelection == PREFERENCES.TYPE_THEME) {
                resetData()
                setThemeData()
            }
        }
        viewModel.fontListData.observe(viewLifecycleOwner) {
            setData()
        }
        viewModel.languageListLiveData.observe(viewLifecycleOwner) {
            if (currentSelection == PREFERENCES.TYPE_LANGUAGE) {
                setData()
            }
        }


    }

    private fun setCategoryData() {
        val total = viewModel.categoryListLiveData.value?.filter { it.isSelected }?.size ?: 0
        binding.tvSelectedValue.text = "${if (total <= 9) "0$total" else total}"
        binding.tvSelectedValue.changeTextColor(ThemeConstants.TYPE_THEME)
        binding.btContinue.setBtnDisabled(total >= 1)
        binding.btContinue.isEnabled = (total >= 1)
    }

    private fun setThemeData() {
        val themeData = viewModel.themeListLiveData.value?.singleOrNull { it.isSelected }
        themeData?.let { theme ->
            binding.tvSelectedValue.text = theme.name
            binding.ivSelectedTheme.visible()
            try {
                binding.ivSelectedTheme.setBackgroundColor(
                    Color.parseColor(theme.code)
                )
            } catch (e: Exception) {
                showException(e)
            }

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
        val themeData = viewModel.languageListLiveData.value?.filter { it.isSelected }
//        themeData?.let { theme ->
//            binding.tvSelectedValue.text = theme.name
//        }
        val name = themeData?.map { it.name }?.joinToString(",")?.toString()?.let {
            if (it.endsWith(",")) {
                it.dropLast(it.length - 1).toString()
            } else {
                it
            }
        }
        binding.tvSelectedValue.text = name
    }

    private fun resetData() {
        binding.tvSelectedValue.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        binding.tvSelectedValue.changeTextColor(ThemeConstants.TYPE_HEADING)
        binding.tvSelectedValue.text = ""
        binding.ivSelectedTheme.gone()
        binding.btContinue.setBtnDisabled(true)
        binding.btContinue.isEnabled = (true)
    }


    private fun setData() {
        val position =
            if (viewModel.type == PREFERENCES.TYPE_ALL) binding.viewpager.currentItem else viewModel.type
        val selectedTitleList =
            baseActivity.resources.getStringArray(R.array.preference_selected_title)
        binding.tvSelectedTitle.text = selectedTitleList[position]
        resetData()
        binding.cbSelectAll.gone()
        binding.btContinue.text = baseActivity.getString(R.string.next)

        when (position) {
            0 -> {
                binding.cbSelectAll.visibleView(!viewModel.categorySingleSelection)
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


        if (viewModel.type != PREFERENCES.TYPE_ALL) {
            if (viewModel.screenType == PREFERENCES.SCREEN_APP)
                binding.btContinue.text = baseActivity.getString(R.string.apply)
            else binding.btContinue.text = baseActivity.getString(R.string.done)

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
                if (viewModel.screenType == PREFERENCES.SCREEN_APP)
                    saveData()
                else sendSelectedData()

            }
        }
    }

    private fun sendSelectedData() {
        val list = when (viewModel.type) {
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

        setFragmentResult("preferenceData", bundleOf("type" to viewModel.type, "data" to list))
        findNavController().navigateUp()
    }


    @SuppressLint("ResourceType")
    override fun <T> onResponseSuccess(value: T, apiCode: String) {
        super.onResponseSuccess(value, apiCode)
        when (apiCode) {
            ApiEndPoints.API_SAVE_PREFERENCES -> {
                showLoading()
                lifecycleScope.launch {
                    viewModel.savePrefDataInDB(viewModel.type)
                    delay(2000)
                    baseActivity.runOnUiThread {
                        hideLoading()
                        when {
                            viewModel.type == PREFERENCES.TYPE_ALL -> {
                                pagerMoveOrUpdateThemeFile()
                            }
                            viewModel.type != PREFERENCES.TYPE_CATEGORY -> {

                                var changedText = ""
                                var themeText = ""

                                when (viewModel.type) {
                                    PREFERENCES.TYPE_LANGUAGE -> {
                                        changedText =
                                            baseActivity.getString(R.string.language_changed_to)
                                        themeText =
                                            viewModel.languageListLiveData.value?.singleOrNull { it.isSelected }?.name
                                                ?: ""

                                    }
                                    PREFERENCES.TYPE_FONT -> {
                                        changedText =
                                            baseActivity.getString(R.string.font_changed_to)
                                        themeText =
                                            viewModel.fontListData.value?.singleOrNull { it.isSelected }?.name
                                                ?: ""
                                    }
                                    PREFERENCES.TYPE_THEME -> {
                                        lifecycleScope.launch {
                                            (getAppContext() as SelfLearningApplication).updatedThemeFile()
                                        }
                                        val themeData =
                                            viewModel.themeListLiveData.value?.singleOrNull { it.isSelected }
                                        changedText =
                                            baseActivity.getString(R.string.theme_changed_to)
                                        themeText =
                                            "${themeData?.name} ${getString(R.string.theme)}"

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

                            }
                            else -> {
                                showToastLong(baseActivity.getString(R.string.categories_updated_successfully))
                                findNavController().navigateUp()
                            }
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
                    if (baseActivity is HomeActivity) {
                        baseActivity.startActivity(
                            Intent(
                                baseActivity,
                                HomeActivity::class.java
                            )
                        )
                    } else {
                        baseActivity.startActivity(
                            Intent(
                                baseActivity,
                                ModeratorActivity::class.java
                            )
                        )
                    }
                    baseActivity.finish()

//                                        baseActivity.recreate()
                }
            }.build()
    }

    fun onClickBack() {
        if (viewModel.type != PREFERENCES.TYPE_ALL) {
            findNavController().navigateUp()
        } else {
            /* if (currentSelection == PreferencesFragmentArgs?.fromBundle(requireArguments())?.currentSelection) {
                 findNavController().navigateUp()
             } else {*/
            when (binding.viewpager.currentItem) {
                0 -> {
                    findNavController().navigateUp()
                }
                else -> {
                    currentSelection -= 1
                    binding.viewpager.currentItem -= 1
                }
            }
//            }
        }
    }

    private fun pagerMoveOrUpdateThemeFile() {
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

    override fun onApiRetry(apiCode: String) {
        viewModel.onApiRetry(apiCode)
    }


}