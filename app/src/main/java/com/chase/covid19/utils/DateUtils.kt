package com.chase.covid19.utils

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.chase.covid19.R
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog
import ir.hamsaa.persiandatepicker.util.PersianCalendar

object DateUtils {
    fun initDatepicker(context:Context) : PersianDatePickerDialog{
        val initDate = PersianCalendar()
        initDate.setPersianDate(1370, 3, 13)

        val typeface = ResourcesCompat.getFont(context, R.font.iransansmedium)

        val picker = PersianDatePickerDialog(context)
            .setPositiveButtonString("باشه")
            .setNegativeButton("بیخیال")
//            .setTodayButton("امروز")
            .setTodayButtonVisible(false)
            .setMinYear(1300)
            .setActionTextColor(ContextCompat.getColor(context, R.color.dim_gray))
            .setTitleColor(ContextCompat.getColor(context, R.color.dim_gray))
            .setActionTextColorResource(R.color.dim_gray)
            .setMaxYear(PersianDatePickerDialog.THIS_YEAR)
            .setInitDate(initDate)
            .setActionTextColor(Color.GRAY)
            .setTypeFace(typeface)
            .setTitleType(PersianDatePickerDialog.WEEKDAY_DAY_MONTH_YEAR)
            .setShowInBottomSheet(true)

        return picker
    }
}
