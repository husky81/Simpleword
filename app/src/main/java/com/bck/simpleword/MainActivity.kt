package com.bck.simpleword

import android.content.Context
import android.content.Intent
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
const val RESULT_CODE_EDITED_WORD: Int = 1952

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val words = Words()

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
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        words.setRecyclerView(recyclerView_MainWord)
        words.setLinearLayoutManager(this)

        words.add("apple")
        words.last().text = "사과"
        words.add("peach")
        words.last().text = "복숭아"
        words.add("potato")
        words.last().text = "감자"
        redrawRecyclerView(words)




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
                    RESULT_CODE_EDITED_WORD -> {
                        val word = data!!.getParcelableExtra<Word>("Word")
                        words.add(word)
                        redrawRecyclerView(words)
                    }
                }
            }
            REQUEST_CODE_EDIT_A_WORD -> {
                when(resultCode){
                    RESULT_CODE_EDITED_WORD -> {
                        val word = data!!.getParcelableExtra<Word>("Word")
                        words[word.index] = word
                        redrawRecyclerView(words)
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
    private fun redrawRecyclerView(words: Words) {
        val adapter = WordAdapter(words)
        adapter.itemClick = object : WordAdapter.ItemClick{
            override fun onClick(view: View, position: Int) {
                words[position].index = position
                openEditWordActivity(words[position])
            }
        }
        recyclerView_MainWord!!.adapter = adapter
    }
    fun importXlsFile(){
        // Request External Storage Read Permission.
        getPermission(this,10)

        val intent = Intent(this, SelectFileActivity::class.java)
        startActivity(intent)
    }
}
class Words: ArrayList<Word>(){
    private var recyclerView: RecyclerView? = null
    //var innerArrayList: ArrayList<Word>()
    fun add(name : String) {
        this.add(Word(name))
    }
    fun setLinearLayoutManager(context: Context) {
        recyclerView!!.layoutManager = LinearLayoutManager(context)
    }
    fun setRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }
}
class Word(var name: String?) : Parcelable {
    var index: Int = 0
    var pronunciation: String? = ""
    var text: String? = ""
    var memo: String? = ""
    val correct: Int = 0
    val wrong: Int = 0

    constructor(parcel: Parcel) : this(parcel.readString()) {
        index = parcel.readInt()
        pronunciation = parcel.readString()
        text = parcel.readString()
        memo = parcel.readString()
    }

    //파셀러블로 만들면 클래스를 엑티비티간에 전송할 수 있다.
    //자동생성 기능이 좋으므로 위 변수가 변경되면 아래를 지우고 자동생성기능을 이용할 것. 190705
    //https://blog.yena.io/studynote/2017/11/28/Android-Kotlin-putExtra.html
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(index)
        parcel.writeString(pronunciation)
        parcel.writeString(text)
        parcel.writeString(memo)
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

