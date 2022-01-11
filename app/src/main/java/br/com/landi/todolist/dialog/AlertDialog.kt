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
        with(AlertDialog.Builder(context)) {
            setTitle(title)
            setMessage(message)
            setCancelable(false)
            setNegativeButton(textNegativeButton) { dialog, id -> dialog.cancel() }
            setPositiveButton(textPositiveButton) { dialog, id -> action.execute() }
            create().show()
        }
    }

}
