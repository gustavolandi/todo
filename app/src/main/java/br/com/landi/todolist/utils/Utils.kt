package br.com.landi.todolist.utils

import android.content.Context
import android.widget.Toast

class Utils {

    companion object {
        val TODO_NAME = "todoName"
        val TODO_DATE = "todoDate"
        val TODO_TAGS = "todoTags"

        fun toastMessage(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
            Toast.makeText(context,message,duration).show()
        }
    }

}