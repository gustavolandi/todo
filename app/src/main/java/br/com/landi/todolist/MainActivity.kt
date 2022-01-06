package br.com.landi.todolist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.com.landi.todolist.model.ToDo
import br.com.landi.todolist.utils.Utils


class MainActivity : AppCompatActivity() {

    private var todoList : MutableList<ToDo>  = mutableListOf()
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
        }
        return super.onOptionsItemSelected(item)
    }

    fun initComponents() {
        this.listView  = findViewById(R.id.lwtTodoView)
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
        todoList.add(ToDo(++id, name ?: "", false, date ?: "", tags))
    }

    fun exampleTodo() {
        buildToDo(
            "teste", "01/01/2022", mutableListOf(
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
        )
    }

    fun addItemListView(){
        if (listView.getAdapter() != null) {
            (listView.getAdapter() as TodoAdapter).refresh(this.todoList)
        } else {
            listView.setAdapter(TodoAdapter(this, this.todoList))
        }
    }

}