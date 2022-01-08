package br.com.landi.todolist

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.com.landi.todolist.model.ToDo
import br.com.landi.todolist.utils.Utils
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {

    private var todoList : MutableList<ToDo>  = mutableListOf()
    private lateinit var db : SQLiteHelper
    private lateinit var listView : ListView
    private lateinit var intentLauncher : ActivityResultLauncher<Intent>
    private var id : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_adiciona, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        } else if (id == R.id.addItem) {
            activityAddItem()
            return true
        } else if (id == R.id.filterItem) {
            filterOptions()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun filterOptions(){
        dialogFilter()
    }

    fun dialogFilter() {
        val listFilter = listOf(NO_FILTER, FILTER_DAY, FILTER_MONTH, FILTER_TAG)
        val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.spinner_layout, listFilter
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        var context = this
        with(Dialog(this)) {
            setContentView(R.layout.dialog_filter)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            val spinner = findViewById<RelativeLayout>(R.id.spinnerFilterTodo) as Spinner
            spinner.adapter = dataAdapter
            val btnOk = findViewById<RelativeLayout>(R.id.btnSubmitFilterTodo) as RelativeLayout
            btnOk.setOnClickListener {
                when(spinner.selectedItem.toString()) {
                     NO_FILTER -> noFilter()
                     FILTER_DAY -> filterByDay()
                     FILTER_MONTH -> filterByMonth()
                     FILTER_TAG -> filterByTag()
                }
                dismiss()
            }
            show()
        }
    }

    fun noFilter(){
        addItemListView(this.todoList)
    }

    fun filterByDay() {
        val date = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val todoList = todoList.filter { it.date == date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}
        addItemListView(todoList.toMutableList())
    }

    fun filterByMonth() {
        val date = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate.now().month.value
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val todoList = todoList.filter { it.date.substring(3,5).toInt() == date}
        addItemListView(todoList.toMutableList())
    }

    fun filterByTag(){

    }

    fun initComponents() {
        this.listView  = findViewById(R.id.lwtTodoView)
        db = SQLiteHelper(this)
        getTodosDb()
        addItemListView()
        intentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.getStringArrayListExtra(Utils.TODO_TAGS)?.let {
                        buildToDo(
                            result.data?.getStringExtra(Utils.TODO_NAME),
                            result.data?.getStringExtra(Utils.TODO_DATE),
                            it
                        )
                    }
                    addItemListView()
                }
            }
    }

    fun activityAddItem() {
        intentLauncher.launch(Intent(this, AddItemActivity::class.java))
    }

    fun buildToDo(name: String?, date: String?, tags: MutableList<String> = mutableListOf()) {
        val toDo = ToDo(++id, name ?: "", false, date ?: "", tags)
        saveTodo(toDo)
    }

    fun saveTodo(toDo: ToDo) {
        db.saveTodo(toDo)
        getTodosDb()
    }

    fun getTodosDb() {
        todoList = db.getToDo
        todoList.sortBy { it.date }
    }

    fun addItemListView(todoList : MutableList<ToDo> = this.todoList){
        if (listView.adapter != null) {
            (listView.adapter as TodoAdapter).refresh(todoList)
        } else {
            listView.adapter = TodoAdapter(this, todoList)
        }
    }

    companion object {
        private const val NO_FILTER = "Sem Filtro"
        private const val FILTER_DAY = "Dia"
        private const val FILTER_MONTH = "Mês"
        private const val FILTER_TAG = "Tag"
    }

}