package br.com.landi.todolist.dialog

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import br.com.landi.todolist.utils.Action
import com.whiteelephant.monthpicker.MonthPickerDialog
import java.time.LocalDate

class CustomDialog(private val context: Context) {

    var title = ""
    var message = ""
    var cancelable = false
    var textNegativeButton = "Não"
    var textPositiveButton = "Sim"

    fun showDialog(action: Action) {
        with(AlertDialog.Builder(context)) {
            setTitle(title)
            setMessage(message)
            setCancelable(cancelable)
            setNegativeButton(textNegativeButton) { dialog, id -> dialog.cancel() }
            setPositiveButton(textPositiveButton) { dialog, id -> action.execute() }
            create().show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showMonthDatePickerDialog(date: LocalDate, action: Action) {
        with(MonthPickerDialog.Builder(
            context,
            { selectedMonth, selectedYear ->
                action.execute(selectedMonth,selectedYear)
            },
            date.year,
            date.monthValue - 1
        )) {
            setMinYear(1990)
            setActivatedYear(date.year)
            setActivatedMonth(date.monthValue - 1)
            setMaxYear(2030)
            setTitle(title)
            build().show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showDatePickerDialog(date: LocalDate, action: Action) {
        DatePickerDialog(
            context,
            { view, selectedYear, selectedMonth, selectedDay ->
                action.execute(selectedYear, selectedMonth, selectedDay)
            },
            date.year,
            date.monthValue-1,
            date.dayOfMonth
        ).show()
    }

}
