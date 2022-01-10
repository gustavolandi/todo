package br.com.landi.todolist

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import br.com.landi.todolist.model.ToDo
import java.lang.Exception

class SQLiteHelper(private val context: Context) :
    SQLiteOpenHelper(context, NAME_DB, null, version_db) {
    override fun onCreate(db: SQLiteDatabase) {
        createTables(db)
    }

    private fun createTables(db: SQLiteDatabase) {
        val SQL_TODO = "CREATE TABLE IF NOT EXISTS $TBX_TODO ($ID_PK, $NAME VARCHAR(255), $DATE VARCHAR(10), $STATUS INTEGER(1))"
        val SQL_TAGS = "CREATE TABLE IF NOT EXISTS $TBX_TAGS ($ID_PK, $NAME_TAG)"
        val SQL_TAGS_TODO = "CREATE TABLE IF NOT EXISTS $TBX_TAGS_TODO ($ID_PK, $ID_TAG INTEGER(10), $ID_TODO INTEGER(10), " +
                "CONSTRAINT fk_tag_todo FOREIGN KEY ($ID_TODO) REFERENCES $TBX_TODO($ID)," +
                "CONSTRAINT fk_tag FOREIGN KEY ($ID_TAG) REFERENCES $TBX_TAGS($ID))"
        db.createTable(SQL_TODO)
        db.createTable(SQL_TAGS)
        db.createTable(SQL_TAGS_TODO)
    }

    private fun SQLiteDatabase.createTable(query: String) {
        try {
            this.execSQL(query)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    fun saveTodo(toDo: ToDo) {
        val db = this.writableDatabase
        val ctv = ContentValues()
        ctv.put(NAME, toDo.name)
        ctv.put(DATE, toDo.date)
        ctv.put(STATUS, toDo.status)
        val id = db.insert(TBX_TODO, ID, ctv)
        if (toDo.tags.size > 0) {
            saveTagsToDo(
                getTagsAndSave(toDo.tags),
                id
            )
        }
    }

    fun getTagsAndSave(tags: MutableList<String>) : MutableList<Long> {
        val db = this.readableDatabase
        val tagMap: MutableList<Long> = mutableListOf()
        tags.forEach {
            val cursor = db.rawQuery("SELECT * FROM $TBX_TAGS WHERE $NAME_TAG = '$it'", null)
            if (!cursor.moveToNext()) {
                tagMap.add(saveTag(it))
            } else {
                tagMap.add(cursor.getLong(cursor.getColumnIndex(ID)))
            }
        }
        return tagMap
    }

    fun getTags() : List<String> {
        val db = this.readableDatabase
        val tagList: MutableList<String> = mutableListOf()
        val cursor = db.rawQuery("SELECT * FROM $TBX_TAGS", null)
        while(cursor.moveToNext()) {
            tagList.add(cursor.getString(cursor.getColumnIndex(NAME_TAG)))
        }
        return tagList
    }
    
    fun saveTagsToDo(tagMap: MutableList<Long>, id: Long) {
        val db = this.writableDatabase
        tagMap.forEach {
            var ctv = ContentValues()
            ctv.put(ID_TAG, it)
            ctv.put(ID_TODO,id)
            db.insert(TBX_TAGS_TODO, ID, ctv)
        }
    }

    fun saveTag(tag: String): Long {
        val db = this.writableDatabase
        var ctv = ContentValues()
        ctv.put(NAME_TAG, tag)
        val id = db.insert(TBX_TAGS, ID, ctv)
        return id
    }

    val getToDo: MutableList<ToDo>
        get() {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $TBX_TODO", null)
            val todoList: MutableList<ToDo> = mutableListOf()
            while (cursor.moveToNext()) {
                todoList.add(
                    ToDo(cursor.getInt(cursor.getColumnIndex(ID)),
                        cursor.getString(cursor.getColumnIndex(NAME)),
                        cursor.getInt(cursor.getColumnIndex(NAME)) == 1,
                        cursor.getString(cursor.getColumnIndex(DATE)),
                        getTagsByItem(db,cursor.getLong(cursor.getColumnIndex(ID))
                        )
                    )
                )
            }
            return todoList
        }

    fun getTagsByItem(db: SQLiteDatabase,id : Long) : MutableList<String> {
        val cursor = db.rawQuery("SELECT $ID_TAG FROM $TBX_TAGS_TODO WHERE $ID_TODO = $id", null)
        val idTagList : MutableList<Long> = mutableListOf()
        while (cursor.moveToNext()) {
            idTagList.add(cursor.getLong(cursor.getColumnIndex(ID_TAG)))
        }
        val tagList: MutableList<String> = mutableListOf()
        if (idTagList.size > 0) {
            val cursorTags = db.rawQuery("SELECT $NAME_TAG FROM $TBX_TAGS WHERE ${getIdQueryTags(idTagList)}", null)
            while (cursorTags.moveToNext()) {
                tagList.add(cursorTags.getString(cursorTags.getColumnIndex(NAME_TAG)))
            }
        }
        return tagList
    }

    fun getIdQueryTags(idList: List<Long>) : String {
        var query = "$ID ="
        idList.forEachIndexed { index,item ->
            if (index > 0) {
                query = "$query or $ID = $item"
            } else {
                query = "$query $item"
            }
        }
        return query
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun deleteItemById(id: Long) {
        val db = this.writableDatabase
        db.delete(TBX_TAGS_TODO, "$ID_TODO = $id", null)
        db.delete(TBX_TODO, "$ID = $id", null)
    }

    fun truncateAllTables() {
        val db = this.writableDatabase
        db.delete(TBX_TODO, null, null)
        db.delete(TBX_TAGS, null, null)
        db.delete(TBX_TAGS_TODO, null, null)
    }

    companion object {
        private const val NAME_DB = "ToDoList_Landi.db"
        private const val version_db = 1
        private const val TBX_TODO = "tbx_todo"
        private const val TBX_TAGS = "tbx_tags"
        private const val TBX_TAGS_TODO = "tbx_tags_todo"
        private const val ID_PK = "id integer primary key autoincrement"

        private const val ID = "id"
        private const val NAME = "name"
        private const val DATE = "date"
        private const val STATUS = "status"
        private const val NAME_TAG = "name_tag"
        private const val ID_TAG = "id_tag"
        private const val ID_TODO = "id_todo"
    }
}
