package com.selflearningcoursecreationapp.ui.preferences

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseViewModel
import com.selflearningcoursecreationapp.data.prefrence.PreferenceDataStore
import com.selflearningcoursecreationapp.models.ThemeData
import com.selflearningcoursecreationapp.utils.Constants
import com.selflearningcoursecreationapp.utils.FONT_CONSTANT
import com.selflearningcoursecreationapp.utils.LANGUAGE_CONSTANT
import com.selflearningcoursecreationapp.utils.THEME_CONSTANT
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreferenceViewModel : BaseViewModel() {
    var selectedThemeId = THEME_CONSTANT.BLUE
    var selectedFontId = FONT_CONSTANT.IBM
    var selectedLanguageId = LANGUAGE_CONSTANT.ENGLISH

    init {
        viewModelScope.launch {

            selectedThemeId = PreferenceDataStore.getInt(Constants.APP_THEME) ?: THEME_CONSTANT.BLUE
            selectedLanguageId =
                PreferenceDataStore.getString(Constants.LANGUAGE_THEME) ?: LANGUAGE_CONSTANT.ENGLISH
            selectedFontId = PreferenceDataStore.getInt(Constants.FONT_THEME) ?: FONT_CONSTANT.IBM

            Log.d("main1", "" + selectedLanguageId)
        }
    }


    suspend fun setAllValue() {
        withContext(viewModelScope.coroutineContext) {
            saveTheme()
            saveFont()
            saveLanguage()
        }
    }

    fun saveTheme() {
        viewModelScope.launch {
            themeListLiveData.value?.singleOrNull { it.isSelected }?.let {
                PreferenceDataStore.saveInt(Constants.APP_THEME, it.id)
            }
        }
    }


    fun saveFont() {
        viewModelScope.launch {
            fontListData.value?.singleOrNull { it.isSelected }?.let {
                PreferenceDataStore.saveInt(Constants.FONT_THEME, it.id)
            }
        }
    }


    fun saveLanguage() {
        viewModelScope.launch {
            languageListLiveData.value?.firstOrNull { it.isSelected }?.let {
                Log.d("main", "$it")
                PreferenceDataStore.saveString(
                    Constants.LANGUAGE_THEME,
                    it.languageId ?: LANGUAGE_CONSTANT.ENGLISH
                )
                Log.d(
                    "SavedLanguage",
                    PreferenceDataStore.getString(Constants.LANGUAGE_THEME).orEmpty()
                )

            }
        }
    }

    var categoryListLiveData = MutableLiveData<ArrayList<ThemeData>>().apply {
        value = ArrayList()
        for (i in 0 until 9) {
            value!!.add(ThemeData(languageId = "Category ${i + 1}", isSelected = false))
        }
    }

    var themeListLiveData = MutableLiveData<ArrayList<ThemeData>>().apply {
        value = ArrayList()

        value!!.add(
            ThemeData(
                R.string.blue_theme,
                R.color.blue,
                THEME_CONSTANT.BLUE == selectedThemeId,
                THEME_CONSTANT.BLUE
            )
        )
        value!!.add(
            ThemeData(
                R.string.wine_theme,
                R.color.wine_red,
                THEME_CONSTANT.WINE == selectedThemeId,
                THEME_CONSTANT.WINE
            )
        )
        value!!.add(
            ThemeData(
                R.string.black_theme,
                R.color.heading_color_262626,
                THEME_CONSTANT.BLACK == selectedThemeId,
                THEME_CONSTANT.BLACK
            )
        )
        value!!.add(
            ThemeData(
                R.string.sea_theme,
                R.color.sea,
                THEME_CONSTANT.SEA == selectedThemeId,
                THEME_CONSTANT.SEA
            )
        )
    }
    var languageListLiveData = MutableLiveData<ArrayList<ThemeData>>().apply {
        value = ArrayList()
        value!!.add(
            ThemeData(
                R.string.english_language,
                0,
                LANGUAGE_CONSTANT.ENGLISH.equals(selectedLanguageId),
                languageId = LANGUAGE_CONSTANT.ENGLISH
            )
        )
        value!!.add(
            ThemeData(
                R.string.kannada_language,
                0,
                LANGUAGE_CONSTANT.KANNADA.equals(selectedLanguageId),
                languageId = LANGUAGE_CONSTANT.KANNADA
            )
        )
        value!!.add(
            ThemeData(
                R.string.tamil_language,
                0,
                LANGUAGE_CONSTANT.KANNADA.equals(selectedLanguageId),
                languageId = LANGUAGE_CONSTANT.TAMIL
            )
        )
        value!!.add(
            ThemeData(
                R.string.telugu_language,
                0,
                LANGUAGE_CONSTANT.TELUGU.equals(selectedLanguageId),
                languageId = LANGUAGE_CONSTANT.TELUGU
            )
        )
        value!!.add(
            ThemeData(
                R.string.bengali_language,
                0,
                LANGUAGE_CONSTANT.BENGALI.equals(selectedLanguageId),
                languageId = LANGUAGE_CONSTANT.BENGALI
            )
        )
        value!!.add(
            ThemeData(
                R.string.hindi_language,
                0,
                LANGUAGE_CONSTANT.HINDI.equals(selectedLanguageId),
                languageId = LANGUAGE_CONSTANT.HINDI
            )
        )

    }
    var fontListData = MutableLiveData<ArrayList<ThemeData>>().apply {
        value = ArrayList()
        value!!.add(
            ThemeData(
                R.string.roboto_font,
                R.font.roboto_medium,
                FONT_CONSTANT.ROBOTO == selectedFontId,
                FONT_CONSTANT.ROBOTO
            )
        )
        value!!.add(
            ThemeData(
                R.string.ibm_font,
                R.font.ibm_medium,
                FONT_CONSTANT.IBM == selectedFontId,
                FONT_CONSTANT.IBM
            )
        )
        value!!.add(
            ThemeData(
                R.string.worksans_font,
                R.font.worksans_medium,
                FONT_CONSTANT.WORK_SANS == selectedFontId,
                FONT_CONSTANT.WORK_SANS
            )
        )
    }


}