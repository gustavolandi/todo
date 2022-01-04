package br.com.landi.todolist

import android.app.AlertDialog
import android.content.Context

class AlertDialog(private val context: Context) {

    var title = ""
    var message = ""
    var cancelable = false
    var textNegativeButton = "Não"
    var textPositiveButton = "Sim"

    fun showDialog(action: Process) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder
            .setMessage(message)
            .setCancelable(false)
            .setNegativeButton(
                textNegativeButton
            ) { dialog, id -> dialog.cancel() }
            .setPositiveButton(
                textPositiveButton
            ) { dialog, id ->
                action.execute()
            }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

}

interface Process {
    fun execute()
}
