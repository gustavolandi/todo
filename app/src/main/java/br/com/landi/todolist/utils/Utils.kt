package br.com.landi.todolist.utils

import android.content.Context
import android.os.Build
import android.widget.Toast
import br.com.landi.todolist.model.ToDo

class Utils {

    companion object {
        val TODO_NAME = "todoName"
        val TODO_DATE = "todoDate"
        val TODO_TAGS = "todoTags"

        fun toastMessage(context: Context, message: String, duration: Int = Toast.LENGTH_LONG) {
            Toast.makeText(context,message,duration).show()
        }


        fun validateBuildSdk() : Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }

        fun exampleTodo() : MutableList<ToDo> {
            var list=  mutableListOf<ToDo>()
            list.add(ToDo(1,
                "teste",
                false,
                "01/01/2022",
                mutableListOf(
                    "tag1",
                    "tag2",
                    "tag3",
                    "tag4",
                    "tag5",
                    "tag6",
                    "tag7",
                    "tag8",
                    "tag9",
                    "tag10",
                    "tag11",
                    "tag12",
                    "tag13",
                    "tag14",
                    "tag15",
                    "tag16",
                    "tag17"
                )
            ))
            list.add(
                ToDo(2,"teste2", false,"01/01/2022", mutableListOf())
            )
            list.add(
                ToDo(2,"teste3", false,"01/01/2022", mutableListOf("tag1"))
            )
            return list
        }
    }

}