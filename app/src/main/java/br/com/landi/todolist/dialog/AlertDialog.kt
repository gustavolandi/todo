package br.com.landi.todolist.dialog

import android.app.AlertDialog
import android.content.Context
import br.com.landi.todolist.utils.Action

class AlertDialog(private val context: Context) {

    var title = ""
    var message = ""
    var cancelable = false
    var textNegativeButton = "Não"
    var textPositiveButton = "Sim"

    fun showDialog(action: Action) {
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
