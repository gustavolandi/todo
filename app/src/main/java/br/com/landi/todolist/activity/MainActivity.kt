package br.com.landi.todolist.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import br.com.landi.todolist.utils.Action
import br.com.landi.todolist.R
import br.com.landi.todolist.repository.SQLiteHelper
import br.com.landi.todolist.adapter.TodoAdapter
import br.com.landi.todolist.model.ToDo
import br.com.landi.todolist.utils.Utils
import br.com.landi.todolist.utils.Utils.Companion.validateBuildSdk
import com.whiteelephant.monthpicker.MonthPickerDialog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity() {

    private var todoList : MutableList<ToDo>  = mutableListOf()
    private lateinit var db : SQLiteHelper
    private lateinit var listView : ListView
    private lateinit var intentLauncher : ActivityResultLauncher<Intent>
    private var id : Int = 0
    private var spinnerSelected = 0
    private var tagSelected = 0

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
            dialogFilter()
            return true
        }
        return super.onOptionsItemSelected(item)
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
        if (validateBuildSdk()) {
            todoList.sortBy { LocalDate.parse(it.date, DATE_PATTERN) }
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

    fun addItemListView(todoList: MutableList<ToDo> = this.todoList){
        if (listView.adapter != null) {
            (listView.adapter as TodoAdapter).refresh(todoList)
        } else {
            listView.adapter = TodoAdapter(this, todoList)
        }
    }

    fun dialogFilter() {
        val listFilter = listOf(NO_FILTER, FILTER_DAY, FILTER_MONTH, FILTER_TAG)
        val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.spinner_layout, listFilter
        )
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(Dialog(this)) {
            setContentView(R.layout.dialog_filter)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            val spinner = findViewById<RelativeLayout>(R.id.spinnerFilterTodo) as Spinner
            spinner.adapter = dataAdapter
            spinner.setSelection(spinnerSelected)
            val btnOk = findViewById<RelativeLayout>(R.id.btnSubmitFilterTodo) as RelativeLayout
            btnOk.setOnClickListener {
                spinnerSelected = spinner.selectedItemPosition
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
        if (validateBuildSdk()) {
            var date =  LocalDate.now()
            val dateToday = dateFormmated(date)
            val todoListFiltered = todoList.filter { it.date == dateToday }
            addItemListView(todoListFiltered.toMutableList())
            val context = this
            filterLayout(dateToday,
                object : Action {
                    override fun execute() {
                        date = date.minusDays(1)
                        val dateFormatted = dateFormmated(date)
                        val todoListFiltered = todoList.filter { it.date == dateFormatted }
                        addItemListView(todoListFiltered.toMutableList())
                        val txv: TextView = findViewById(R.id.txvTodoFilter)
                        txv.text = dateFormatted
                    }
                },
                object : Action {
                    override fun execute() {
                        date = date.plusDays(1)
                        val dateFormatted = dateFormmated(date)
                        val todoListFiltered = todoList.filter { it.date == dateFormatted }
                        addItemListView(todoListFiltered.toMutableList())
                        val txv: TextView = findViewById(R.id.txvTodoFilter)
                        txv.text = dateFormatted
                    }
                },
                object : Action {
                    override fun execute() {
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
                                date = LocalDate.parse(
                                    dateFormatted,
                                    DATE_PATTERN
                                )
                                val todoListFiltered = todoList.filter { it.date == dateFormatted }
                                addItemListView(todoListFiltered.toMutableList())
                                val txv: TextView = findViewById(R.id.txvTodoFilter)
                                txv.text = dateFormatted
                            },
                            date.year,
                            date.monthValue-1,
                            date.dayOfMonth
                        ).show()

                    }
                }
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }


    }

    fun filterLayout(text: String, backAction: Action, nextAction: Action, txvAction: Action) {
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
        if (validateBuildSdk()) {
            var date = LocalDate.now()
            val todoListFiltered = todoList.filter {
                it.date.substring(3, 5).toInt() == date.month.value &&
                    it.date.substring(6).toInt() == date.year
            }
            addItemListView(todoListFiltered.toMutableList())
            val context = this
            filterLayout("${getMonth(date.month.value)} / ${date.year}",
                object : Action {
                    override fun execute() {
                        date = date.minusMonths(1)
                        val todoListFiltered = todoList.filter {
                            it.date.substring(3, 5).toInt() == date.month.value &&
                                    it.date.substring(6).toInt() == date.year
                        }
                        addItemListView(todoListFiltered.toMutableList())
                        val txv: TextView = findViewById(R.id.txvTodoFilter)
                        txv.text = "${getMonth(date.month.value)} / ${date.year}"
                    }
                },
                object : Action {
                    override fun execute() {
                        date = date.plusMonths(1)
                        val todoListFiltered = todoList.filter {
                            it.date.substring(3, 5).toInt() == date.month.value &&
                                    it.date.substring(6).toInt() == date.year
                        }
                        addItemListView(todoListFiltered.toMutableList())
                        val txv: TextView = findViewById(R.id.txvTodoFilter)
                        txv.text = "${getMonth(date.month.value)} / ${date.year}"
                    }
                },
                object : Action {
                    override fun execute() {
                        val calendar = Calendar.getInstance()
                        MonthPickerDialog.Builder(
                            context,
                            { selectedMonth, selectedYear ->
                                date = LocalDate.of(selectedYear,selectedMonth+1,1)
                                val todoListFiltered = todoList.filter {
                                    it.date.substring(3, 5).toInt() == date.month.value &&
                                            it.date.substring(6).toInt() == date.year
                                }
                                addItemListView(todoListFiltered.toMutableList())
                                val txv: TextView = findViewById(R.id.txvTodoFilter)
                                txv.text = "${getMonth(date.month.value)} / ${date.year}"
                            },
                            calendar[Calendar.YEAR],
                            calendar[Calendar.MONTH]
                        )
                            .setMinYear(1990)
                            .setActivatedYear(date.year)
                            .setActivatedMonth(date.monthValue - 1)
                            .setMaxYear(2030)
                            .setTitle("Selecione o mês")
                            .build()
                            .show()

                    }
                }
            )
        } else {
            TODO("VERSION.SDK_INT < O")
        }

    }

    fun getMonth(month: Int) : String {
        return when(month) {
            1 -> "Janeiro"
            2 -> "Fevereiro"
            3 -> "Março"
            4 -> "Abril"
            5 -> "Maio"
            6 -> "Junho"
            7 -> "Julho"
            8 -> "Agosto"
            9 -> "Setembro"
            10 -> "Outubro"
            11 -> "Novembro"
            12 -> "Dezembro"
            else -> ""
         }
    }

    fun filterByTag(){
        var tags : List<String> = db.getTags()
        if (tags.size > 0) {
            val tagsSortedBy: List<String> = tags.sortedWith( compareBy(String.CASE_INSENSITIVE_ORDER) { it })
            val todoListFiltered = todoList.filter {it.tags.contains(tagsSortedBy[tagSelected])}
            addItemListView(todoListFiltered.toMutableList())
            filterLayout(tagsSortedBy[tagSelected],
                nextAction = object : Action {
                    override fun execute() {
                        if (++tagSelected >= tagsSortedBy.size) {
                            tagSelected = 0
                        }
                        filterTag(tagsSortedBy)
                    }
                },
                backAction = object : Action {
                    override fun execute() {
                        if (--tagSelected < 0) {
                            tagSelected = tagsSortedBy.size - 1
                        }
                        filterTag(tagsSortedBy)
                    }
                },
                txvAction = object : Action {
                    override fun execute() {
                        dialogFilterTag(tagsSortedBy)
                    }
                }
            )
        }
    }

    fun filterTag(tags: List<String>) {
        val todoListFiltered = todoList.filter {it.tags.contains(tags[tagSelected])}
        addItemListView(todoListFiltered.toMutableList())
        val txv : TextView = findViewById(R.id.txvTodoFilter)
        txv.text = tags[tagSelected]
    }

    fun dialogFilterTag(listFilter: List<String>) {
        val dataAdapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            R.layout.spinner_layout, listFilter
        )
        val context = this
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        with(Dialog(context)) {
            setContentView(R.layout.dialog_filter)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
            val spinner = findViewById<RelativeLayout>(R.id.spinnerFilterTodo) as Spinner
            spinner.adapter = dataAdapter
            spinner.setSelection(tagSelected)
            val btnOk = findViewById<RelativeLayout>(R.id.btnSubmitFilterTodo) as RelativeLayout
            btnOk.setOnClickListener {
                tagSelected = spinner.selectedItemPosition
                filterTag(listFilter)
                dismiss()
            }
            show()
        }
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

    companion object {
        private const val NO_FILTER = "Sem Filtro"
        private const val FILTER_DAY = "Dia"
        private const val FILTER_MONTH = "Mês"
        private const val FILTER_TAG = "Tag"
        @RequiresApi(Build.VERSION_CODES.O)
        private val DATE_PATTERN = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        @RequiresApi(Build.VERSION_CODES.O)
        fun dateFormmated(date : LocalDate) : String {
            return date.format(DATE_PATTERN)
        }

    }

}