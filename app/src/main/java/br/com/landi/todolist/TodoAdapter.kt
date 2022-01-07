package br.com.landi.todolist

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import android.widget.RelativeLayout
import br.com.landi.todolist.dialog.AlertDialog
import br.com.landi.todolist.dialog.Process
import br.com.landi.todolist.model.ToDo
import br.com.landi.todolist.utils.Utils


class TodoAdapter(
    val context: Context,
    var todoList: MutableList<ToDo>
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v: View = convertView
            ?: (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.model_todo,
                null
            )

        v.setOnClickListener {
            showToast(position)
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
                showDialog(object : Process {
                    override fun execute() {
                        val db = SQLiteHelper(context)
                        db.deleteItemById(getItemId(position))
                        todoList.removeAt(position)
                        refresh(todoList)
                        Utils.toastMessage(context, "Item deletado com sucesso")
                    }
                })
            }
        }

        val txvTodoName =
            v.findViewById<View>(R.id.txvTodoName) as TextView
        val txvTodoDate =
            v.findViewById<View>(R.id.txvTodoItemDate) as TextView
        txvTodoName.text = c.name
        txvTodoDate.text = c.date
        addTags(v,c)
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

    private fun addTags(v: View,c: ToDo) {
        val relativeLayout =
            v.findViewById<View>(R.id.rlLayoutModelToDo) as RelativeLayout
        if (c.tags.size == 0) {
            relativeLayout.visibility = GONE
            return
        }
        relativeLayout.visibility = VISIBLE
        relativeLayout.removeAllViewsInLayout()
        var txSize = 0f
        var id = 0
        var firstId = 0
        var line = 0
        var belowId = 0
        for (i in c.tags) {
            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val tv = TextView(context)
            tv.text = i
            tv.id = View.generateViewId()
            tv.textSize = 15F
            tv.background = v.resources.getDrawable(
                R.drawable.bordered_rectangle_rounded_corners,
                null
            )
            val textSize = (tv.textSize * i.length)
            txSize += textSize
            if (txSize >= Resources.getSystem().displayMetrics.widthPixels) {
                belowId = firstId
                firstId = tv.id
                params.addRule(RelativeLayout.BELOW, belowId)
                params.setMargins(5, 5, 0, 0)
                txSize = 0f
                line++
            } else {
                params.addRule(RelativeLayout.RIGHT_OF, id)
                params.setMargins(5, 5, 0, 0)
                if (line != 0) {
                    params.addRule(RelativeLayout.BELOW, belowId)
                }
            }
            tv.layoutParams = params
            relativeLayout.addView(tv)
            if (id == 0) {
                firstId = tv.id
            }
            id = tv.id
        }
    }

    fun refresh(todoList: MutableList<ToDo>) {
        this.todoList = todoList
        notifyDataSetChanged()
    }

    private fun showToast(position: Int) {
        Utils.toastMessage(
            context, getItem(position).toString()
        )
    }

}