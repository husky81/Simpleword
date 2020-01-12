package com.bck.simpleword

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Parcel
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class Words(activity: Activity, recyclerView: RecyclerView): ArrayList<Word>(){
    private val dbHandler = MyDBHandler(activity, null, null, 1)
    init {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        dbHandler.queryAll(this)
    }
    override fun add(element: Word): Boolean {
        dbHandler.insert(element)
        return super.add(element)
    }
    fun load(element: Word): Boolean {
        return super.add(element)
    }
    fun update(rowRecyclerView: Int, word: Word){
        dbHandler.update(word)
        this[rowRecyclerView] = word
    }
    fun deleteAll() {
        dbHandler.deleteAll()
        super.clear()
    }
    fun delete(word: Word){
        dbHandler.delete(word.id)
    }
}
class Word(var name: String?) : Parcelable {
    //파셀러블로 만들면 클래스를 엑티비티간에 전송할 수 있다.
    //자동생성 기능이 좋으므로 변수가 변경되면 constructor 아래를 지우고 자동생성기능을 이용할 것. 190705
    //https://blog.yena.io/studynote/2017/11/28/Android-Kotlin-putExtra.html

    var id: Int = 0 // database id
    var pronunciation: String? = ""
    var text: String? = ""
    var memo: String? = ""
    var correct: Int = 0
    var wrong: Int = 0
    var deleteCall: Int = 0

    constructor(parcel: Parcel) : this(parcel.readString()) {
        id = parcel.readInt()
        pronunciation = parcel.readString()
        text = parcel.readString()
        memo = parcel.readString()
        correct = parcel.readInt()
        wrong = parcel.readInt()
        deleteCall = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(id)
        parcel.writeString(pronunciation)
        parcel.writeString(text)
        parcel.writeString(memo)
        parcel.writeInt(correct)
        parcel.writeInt(wrong)
        parcel.writeInt(deleteCall)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Word> {
        override fun createFromParcel(parcel: Parcel): Word {
            return Word(parcel)
        }

        override fun newArray(size: Int): Array<Word?> {
            return arrayOfNulls(size)
        }
    }

}

class WordAdapter (private val words : Words) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {
    interface ItemClick{ fun onClick(view: View, position: Int) }
    var itemClick: ItemClick? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_word,parent,false)
        return WordViewHolder(view)
    }
    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val item = words[position]
        holder.textViewName.text = item.name
        holder.textViewText.text = item.text
        if(itemClick != null) holder.itemView.setOnClickListener { v -> itemClick?.onClick(v, position) }
    }
    override fun getItemCount(): Int {
        return words.size
    }
    class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName = view.findViewById(R.id.textView_FileName) as TextView
        val textViewText = view.findViewById(R.id.textView_WordText) as TextView

    }
}
class MyDBHandler(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int): SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "Simpleword3"

        const val TABLE_PRODUCTS = "words_main"
        const val colId = "id"
        const val colName = "name"
        const val colPronunciation = "pronunciation"
        const val colText = "text"
        const val colMemo = "memo"
        const val colCorrect = "correct"
        const val colWrong = "wrong"
    }
    override fun onCreate(db: SQLiteDatabase) {
        val tableCreatingCommand =
            "CREATE TABLE IF NOT EXISTS $TABLE_PRODUCTS (" +
                    "$colId INTEGER PRIMARY KEY, " +
                    "$colName TEXT, " +
                    "$colPronunciation TEXT, " +
                    "$colText TEXT, " +
                    "$colMemo TEXT, " +
                    "$colCorrect INTEGER, " +
                    "$colWrong INTEGER" +
                    ")"
        val createRlt = db.execSQL(tableCreatingCommand)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        onCreate(db)
    }
    private fun insert(values: ContentValues): Long {
        val db = this.writableDatabase
        val index = db.insert(TABLE_PRODUCTS, null, values)

        db.close()
        return index
    }
    fun insert(word: Word): Long {
        return this.insert(word2contentValues(word)!!)
    }
    fun update(word: Word): Int {
        val db = this.writableDatabase
        val index = db.update(TABLE_PRODUCTS,word2contentValues(word),"id=" + word.id,null)
        db.close()
        return index

    }
    fun queryAll(words: Words): Words{
        val db = this.readableDatabase
        val cursor = db.rawQuery("select * from $TABLE_PRODUCTS", null)

        if (cursor.moveToFirst()) {
            val ciId = cursor.getColumnIndex(colId)
            val ciName = cursor.getColumnIndex(colName)
            val ciPronunciation = cursor.getColumnIndex(colPronunciation)
            val ciText = cursor.getColumnIndex(colText)
            val ciMemo = cursor.getColumnIndex(colMemo)
            val ciCorrect = cursor.getColumnIndex(colCorrect)
            val ciWrong = cursor.getColumnIndex(colWrong)

            do {
                val word = Word("")
                word.id = Integer.parseInt(cursor.getString(ciId))
                word.name = cursor.getString(ciName)
                word.pronunciation = cursor.getString(ciPronunciation)
                word.text = cursor.getString(ciText)
                word.memo = cursor.getString(ciMemo)
                word.correct = cursor.getInt(ciCorrect)
                word.wrong = cursor.getInt(ciWrong)
                words.load(word)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return words
    }
    fun delete(wordName: String): Boolean {
        var result = false
        val query = "SELECT * FROM $TABLE_PRODUCTS WHERE $colName = wo$wordName\""
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            val id = Integer.parseInt(cursor.getString(0))
            db.delete(TABLE_PRODUCTS, colId + " = ?", arrayOf(id.toString()))
            cursor.close()
            result = true
        }
        db.close()
        return result
    }
    fun delete(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_PRODUCTS, "$colId = ?", arrayOf(id.toString()))
        db.close()
        return
    }
    fun deleteAll() {
        val db = this.writableDatabase
        db.execSQL("delete from $TABLE_PRODUCTS")
        db.close()
        return
    }
    //fun update(values: ContentValues, selection: String, selectionargs: Array<String>): Int {
    //    val count = db!!.update(TABLE_PRODUCTS, values, selection, selectionargs)
    //            return count
    //            }
    //fun update(word: Word){
    //    val result = db!!.update(TABLE_PRODUCTS, word2contentValues(word), "$colId = ?", arrayOf(word.id.toString()))
    //        }

    private fun word2contentValues(word: Word): ContentValues? {
        val values = ContentValues()
        values.put(colName,word.name)
        values.put(colPronunciation,word.pronunciation)
        values.put(colText,word.text)
        values.put(colMemo,word.memo)
        values.put(colCorrect,word.correct)
        values.put(colWrong,word.wrong)
        return values
    }

}
