package br.com.landi.todolist

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.com.landi.todolist.model.ToDo
import br.com.landi.todolist.utils.Utils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {

    private var todoList : MutableList<ToDo>  = mutableListOf()
    private lateinit var db : SQLiteHelper
    private lateinit var listView : ListView
    private lateinit var intentLauncher : ActivityResultLauncher<Intent>
    private var id : Int = 0
    private val datePickerListener =
        DatePickerDialog.OnDateSetListener { view, selectedYear, selectedMonth, selectedDay ->
            var mes = selectedMonth + 1
            var dia = selectedDay
            val year1 = selectedYear.toString()
            var month1 = (selectedMonth + 1).toString()
            var day1 = selectedDay.toString()
            if (dia < 10) {
                day1 = "0$day1"
            }
            if (mes < 10) {
                month1 = "0$month1"
            }
        }

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
        val linearLayoutFilter : LinearLayout = findViewById(R.id.linearLayoutFilter)
        linearLayoutFilter.visibility = GONE
        addItemListView(this.todoList)
    }

    fun filterByDay() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            var date =  LocalDate.now()
            val dateToday = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            val todoListFiltered = todoList.filter { it.date == dateToday }
            addItemListView(todoListFiltered.toMutableList())
            val context = this
            filterLayout(dateToday,
                object : Action {
                    override fun execute() {
                        date = date.minusDays(1)
                        val dateFormatted = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        val todoListFiltered = todoList.filter { it.date == dateFormatted }
                        addItemListView(todoListFiltered.toMutableList())
                        val txv : TextView = findViewById(R.id.txvTodoFilter)
                        txv.text = dateFormatted
                    }
                },
                object : Action {
                    override fun execute() {
                        date = date.plusDays(1)
                        val dateFormatted = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        val todoListFiltered = todoList.filter { it.date == dateFormatted }
                        addItemListView(todoListFiltered.toMutableList())
                        val txv : TextView = findViewById(R.id.txvTodoFilter)
                        txv.text = dateFormatted
                    }
                },
                object : Action {
                    override fun execute() {
                        val cal = Calendar.getInstance()
                        DatePickerDialog(
                            context,
                            { view, selectedYear, selectedMonth, selectedDay ->
                                var mes = selectedMonth + 1
                                var dia = selectedDay
                                val year1 = selectedYear.toString()
                                var month1 = (selectedMonth + 1).toString()
                                var day1 = selectedDay.toString()
                                if (dia < 10) {
                                    day1 = "0$day1"
                                }
                                if (mes < 10) {
                                    month1 = "0$month1"
                                }
                                val dateFormatted = "$day1/$month1/$year1"
                                date = LocalDate.parse(dateFormatted,DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                val todoListFiltered = todoList.filter { it.date == dateFormatted }
                                addItemListView(todoListFiltered.toMutableList())
                                val txv : TextView = findViewById(R.id.txvTodoFilter)
                                txv.text = dateFormatted
                            },
                            cal[Calendar.YEAR],
                            cal[Calendar.MONTH],
                            cal[Calendar.DAY_OF_MONTH]
                        ).show()

                    }
                }
            )
        } else {
            TODO()
        }


    }

    fun filterLayout(text: String, backAction : Action, nextAction: Action, txvAction: Action) {
        val linearLayoutFilter : LinearLayout = findViewById(R.id.linearLayoutFilter)
        linearLayoutFilter.visibility = VISIBLE
        val txv : TextView = findViewById(R.id.txvTodoFilter)
        txv.text = text
        val imgBack : ImageView = findViewById(R.id.imgBackFilterDay)
        val imgNext : ImageView = findViewById(R.id.imgNextFilterDay)
        imgBack.setOnClickListener { backAction.execute() }
        imgNext.setOnClickListener { nextAction.execute() }
        txv.setOnClickListener { txvAction.execute() }
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