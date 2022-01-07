package br.com.landi.todolist

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import br.com.landi.todolist.utils.Utils.Companion.TODO_DATE
import br.com.landi.todolist.utils.Utils.Companion.TODO_NAME
import br.com.landi.todolist.utils.Utils.Companion.TODO_TAGS
import java.util.*
import kotlin.collections.ArrayList


class AddItemActivity : AppCompatActivity() {

    private val datePickerListener =
        OnDateSetListener { view, selectedYear, selectedMonth, selectedDay ->
            var mes = selectedMonth + 1
            var dia = selectedDay
            val year1 = selectedYear.toString()
            var month1 = (selectedMonth + 1).toString()
            var day1 = selectedDay.toString()
            val txvData : EditText = findViewById(R.id.edtDate)
            if (dia < 10) {
                day1 = "0$day1"
            }
            if (mes < 10) {
                month1 = "0$month1"
            }
            txvData.setText("$day1/$month1/$year1")
            val edtTags : EditText = findViewById(R.id.edtTags)
            edtTags.requestFocus()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun init() {
        val addButton : RelativeLayout = findViewById(R.id.btnAddItem)
        val edtName : EditText = findViewById(R.id.edtName)
        val edtDate : EditText = findViewById(R.id.edtDate)
        val edtTags : EditText = findViewById(R.id.edtTags)
        addButton.setOnClickListener {

            if (edtName.text.toString().isEmpty() || edtDate.text.toString().isEmpty()) {

            } else {
                var tags = if (edtTags.text.toString().trim().isEmpty()) {
                    ArrayList()
                } else {
                    ArrayList(edtTags.text.toString().split(";"))
                }
                with(Intent()) {
                    putExtra(TODO_NAME, edtName.text.toString())
                    putExtra(TODO_DATE, edtDate.text.toString())
                    putStringArrayListExtra(TODO_TAGS, tags)
                    setResult(Activity.RESULT_OK, this)
                }
                finish()
            }

        }

        edtDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                datePickerListener,
                cal[Calendar.YEAR],
                cal[Calendar.MONTH],
                cal[Calendar.DAY_OF_MONTH]
            ).show()
        }
        edtDate.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val cal = Calendar.getInstance()
                DatePickerDialog(
                    this,
                    datePickerListener,
                    cal[Calendar.YEAR],
                    cal[Calendar.MONTH],
                    cal[Calendar.DAY_OF_MONTH]
                ).show()
            }
        }
    }
}