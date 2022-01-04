package br.com.landi.todolist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import br.com.landi.todolist.model.ToDo

class TodoAdapter(val context: Context, var todoList : MutableList<ToDo>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v: View? = null

        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = inflater.inflate(R.layout.model_todo, null)
        } else {
            v = convertView
        }

        v!!.setOnClickListener {
            Toast.makeText(context," Nome = ${getItem(position).name} e Id = ${getItem(position).id} e status = ${getItem(position).status}" , Toast.LENGTH_LONG).show() }

        v!!.setOnLongClickListener {
            Toast.makeText(context," Nome = ${getItem(position).name} e Id = ${getItem(position).id} e status = ${getItem(position).status}" , Toast.LENGTH_LONG).show()
            return@setOnLongClickListener(true)
        }

        val c: ToDo = getItem(position)

        val txvTodoName =
            v!!.findViewById<View>(R.id.txvTodoName) as TextView
        val txvTodoDate =
            v!!.findViewById<View>(R.id.txvTodoItemDate) as TextView

        val cbTodoStatus = v!!.findViewById<View>(R.id.cbTodoStatus) as CheckBox
        cbTodoStatus.setOnCheckedChangeListener { buttonView, isChecked ->
            c.status = isChecked
        }

        val imgDelete = v!!.findViewById<View>(R.id.imgDeleteItem) as ImageView
        imgDelete.setOnClickListener {
            val dialog = AlertDialog(v!!.context)
            dialog.cancelable = true
            dialog.message = "Deseja deletar o item selecionado?"
            dialog.title = "Deletar Item"
            dialog.showDialog(object : Process{
                override fun execute() {
                    todoList.removeAt(position)
                    refresh(todoList)
                }
            })
        }
        txvTodoName.setText(c.name)
        txvTodoDate.setText(c.date)

        return v!!
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
}