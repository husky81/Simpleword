package com.bck.simpleword

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v4.widget.DrawerLayout
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.TextView
import kotlinx.android.synthetic.main.content_main.*

const val REQUEST_CODE_ADD_A_WORD: Int = 1921
const val REQUEST_CODE_EDIT_A_WORD: Int = 1923
const val RESULT_CODE_WORD: Int = 1952

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var words: Words
    private var positionEditingWord: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, EditWordActivity::class.java)
            //startActivity(intent)
            startActivityForResult(intent,REQUEST_CODE_ADD_A_WORD)
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        //drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)


        words = Words(this, recyclerView_MainWord)
        // words!!.add("aa")
        // words!!.last().text = "사과"
        // words!!.add("peach")
        // words!!.last().text = "복숭아"
        // words!!.add("potato")
        // words!!.last().text = "감자"

        //words.deleteAll()
        redrawWords(words)


    }
    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> {

            }
            R.id.action_import_xls -> {
                importXlsFile()
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            REQUEST_CODE_ADD_A_WORD -> {
                when(resultCode){
                    RESULT_CODE_WORD -> {
                        val word = data!!.getParcelableExtra<Word>("Word")
                        words.add(word)
                        redrawWords(words)
                    }
                }
            }
            REQUEST_CODE_EDIT_A_WORD -> {
                when(resultCode){
                    RESULT_CODE_WORD -> {
                        val word = data!!.getParcelableExtra<Word>("Word")
                        words[positionEditingWord] = word
                        redrawWords(words)
                    }
                }
            }
        }
    }

    fun openEditWordActivity(word: Word){
        val intent = Intent(this, EditWordActivity::class.java)
        intent.putExtra("Word",word)
        startActivityForResult(intent, REQUEST_CODE_EDIT_A_WORD)
    }
    private fun redrawWords(words: Words) {
        val adapter = WordAdapter(words)
        adapter.itemClick = object : WordAdapter.ItemClick{
            override fun onClick(view: View, position: Int) {
                positionEditingWord = position
                openEditWordActivity(words[position])
            }
        }
        recyclerView_MainWord!!.adapter = adapter
    }
    private fun importXlsFile(){
        // Request External Storage Read Permission.
        getPermission(this,10)

        val intent = Intent(this, SelectFileActivity::class.java)
        startActivity(intent)
    }
}

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
    fun update(index: Int, word: Word){
        dbHandler.update(word)
        this[index] = word
    }
    fun deleteAll() {
        dbHandler.deleteAll()
        super.clear()
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

    constructor(parcel: Parcel) : this(parcel.readString()) {
        id = parcel.readInt()
        pronunciation = parcel.readString()
        text = parcel.readString()
        memo = parcel.readString()
        correct = parcel.readInt()
        wrong = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(id)
        parcel.writeString(pronunciation)
        parcel.writeString(text)
        parcel.writeString(memo)
        parcel.writeInt(correct)
        parcel.writeInt(wrong)
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

    fun update(word: Word) {

    }
}

