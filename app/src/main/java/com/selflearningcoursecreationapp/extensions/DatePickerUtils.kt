package com.selflearningcoursecreationapp.extensions

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.view.View
import android.widget.DatePicker
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.utils.customViews.ThemeUtils
import java.lang.reflect.Field
import java.util.*


@SuppressLint("RestrictedApi")
fun Context.openDatePickerDialog(
    setMinDate: Boolean = false,
    setMaxDate: Boolean = false,
    setMaxYear: Int = 0,
    minYear: Calendar? = null,
    onDateSelect: (calendar: Calendar) -> Unit
) {
    val calendar = if (setMinDate && minYear != null) minYear else Calendar.getInstance()
    val datePickerDialog = MaterialStyledDatePickerDialog(
        this,
        R.style.MaterialDialog,
        { _, year, monthOfYear, dayOfMonth ->
            val selectedCal = Calendar.getInstance()
            selectedCal.set(Calendar.YEAR, year)
            selectedCal.set(Calendar.MONTH, monthOfYear)
            selectedCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            onDateSelect(selectedCal)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    if (setMinDate) {
        if (minYear != null) {
            datePickerDialog.datePicker.minDate = minYear.timeInMillis
        } else {
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        }
    }
    if (setMaxDate) {
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        if (setMaxYear != 0) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.YEAR, -setMaxYear)
            datePickerDialog.datePicker.maxDate = cal.timeInMillis - 1000
        }
    }

    val headerId: Int =
        Resources.getSystem().getIdentifier("date_picker_header", "id", "android")
    datePickerDialog.datePicker.findViewById<LinearLayout>(headerId)
        ?.setBackgroundColor(ThemeUtils.getAppColor(this))
    datePickerDialog.show()

    datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
        .setTextColor(ThemeUtils.getAppColor(this))
    datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
        .setTextColor(ContextCompat.getColor(this, R.color.text_color_black_131414))
}

private fun setDatePickerHeaderBackgroundColor(dpd: DatePickerDialog, color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        try {
            val mDatePickerField: Field
            mDatePickerField = DatePickerDialog::class.java.getDeclaredField("mDatePicker")
            mDatePickerField.setAccessible(true)
            val mDatePicker = mDatePickerField.get(dpd) as DatePicker
            val headerId: Int? =
                Resources.getSystem().getIdentifier("day_picker_selector_layout", "id", "android")
            val header: View? = headerId?.let { mDatePicker.findViewById(it) }
            header?.setBackgroundColor(color)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }
}