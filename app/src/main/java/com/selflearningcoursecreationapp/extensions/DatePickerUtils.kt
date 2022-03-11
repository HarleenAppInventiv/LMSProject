package com.selflearningcoursecreationapp.extensions

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog
import java.util.*

@SuppressLint("RestrictedApi")
fun Context.openDatePickerDialog(
    setMinDate:Boolean=false,
    setMaxDate:Boolean= false,
    setMaxYear:Int=0,
    minYear:Calendar?=null,
    onDateSelect:(calendar:Calendar)->Unit
){
    val calendar= if (setMinDate && minYear!=null) minYear else Calendar.getInstance()
    val datePickerDialog=MaterialStyledDatePickerDialog(this,{_,year,monthOfYear,dayOfMonth->
        val selectedCal= Calendar.getInstance()
        selectedCal.set(Calendar.YEAR,year)
        selectedCal.set(Calendar.MONTH,monthOfYear)
        selectedCal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
        onDateSelect(selectedCal)
    },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))

    if (setMinDate)
    {
        if (minYear!=null)
        {
            datePickerDialog.datePicker.minDate=minYear.timeInMillis
        }else{
            datePickerDialog.datePicker.minDate=System.currentTimeMillis()-1000
        }
    }

    if (setMaxDate) {
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        if (setMaxYear != 0)
        {
            val cal= Calendar.getInstance()
            cal.add(Calendar.YEAR,-setMaxYear)
            datePickerDialog.datePicker.maxDate=cal.timeInMillis-1000
        }
    }

    datePickerDialog.show()
}