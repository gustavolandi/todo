package br.com.landi.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import br.com.landi.todolist.model.ToDo
import br.com.landi.todolist.utils.Utils

class TodoAdapter(val context: Context, var todoList : MutableList<ToDo>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View = if (convertView == null) {
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.model_todo, null)
        } else {
            convertView
        }

        v.setOnClickListener {
        }
        v.setOnLongClickListener {
            return@setOnLongClickListener(true)
        }

        val c: ToDo = getItem(position)

        val cbTodoStatus = v.findViewById<View>(R.id.cbTodoStatus) as CheckBox
        cbTodoStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            c.status = isChecked
        }

        val imgDelete = v.findViewById<View>(R.id.imgDeleteItem) as ImageView
        imgDelete.setOnClickListener {
            with(AlertDialog(context)) {
                cancelable = true
                message = "Deseja deletar o item selecionado?"
                title = "Deletar Item"
                showDialog(object : Process{
                        override fun execute() {
                            todoList.removeAt(position)
                            refresh(todoList)
                            Utils.toastMessage(context,"Item deletado com sucesso")
                        }
                    })
            }
        }

        val txvTodoName =
            v.findViewById<View>(R.id.txvTodoName) as TextView
        val txvTodoDate =
            v.findViewById<View>(R.id.txvTodoItemDate) as TextView
        val txvTodoTags =
            v.findViewById<View>(R.id.txvTodoItemTags) as TextView
        txvTodoName.setText(c.name)
        txvTodoDate.setText(c.date)
        if (c.tags.size > 0) {
            txvTodoTags.setVisibility(View.VISIBLE)
            txvTodoTags.setText(c.tags.joinToString(";"))
        } else {
            txvTodoTags.setVisibility(View.GONE)
        }

        return v
    }

    override fun getItem(position: Int): ToDo {
        return todoList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return todoList.get(position).id.toLong()
    }

    override fun getCount(): Int {
        return todoList.size
    }

    fun refresh(todoList: MutableList<ToDo>) {
        this.todoList = todoList
        notifyDataSetChanged()
    }

    private fun showToast(position: Int) {
        Utils.toastMessage(context,"Nome = ${getItem(position).name} e Id = ${getItem(position).id} e status = ${getItem(position).status}")
    }
}