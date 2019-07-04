package com.bck.simpleword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
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

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val words = Words()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            val intent = Intent(this, AddWordActivity::class.java)
            startActivity(intent)

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)


        //rvTest()



    }
    private fun rvTest(){
        words.setRecyclerView(recyclerView_MainWord)
        words.setLinearLayoutManager(this)

        for (i in 1..11){
            words.add("C1C3")
            words.add("C2C3")
            words.add("C3C3")
        }
        words.redraw()
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> {

            }
            R.id.action_import_xls -> {
                // Request External Storage Read Permission.
                getPermission(this,10)

                val intent = Intent(this, SelectFileActivity::class.java)
                startActivity(intent)
            }
            else -> super.onOptionsItemSelected(item)
        }
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
}
class Words: ArrayList<Word>(){
    private var recyclerView: RecyclerView? = null
    //var innerArrayList: ArrayList<Word>()
    fun add(name : String) {

        this.add(Word(name))
    }
    fun redraw() {
        recyclerView!!.adapter = WordAdapterClass(this)
    }

    fun setLinearLayoutManager(context: Context) {
        recyclerView!!.layoutManager = LinearLayoutManager(context)
    }

    fun setRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }
}
class Word(var name: String) {
    var mean: String? = ""
    var pronunciation: String? = ""
    val correct: Int = 0
    val wrong: Int = 0
}
class WordAdapterClass (wordList : Words) : RecyclerView.Adapter<WordAdapterClass.WordViewHolder>() {
    private var words : Words? = wordList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_word,parent,false)
        return WordViewHolder(view)
    }
    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val musicVO = words!![position]
        holder.textViewName.text = musicVO.name
    }
    override fun getItemCount(): Int {
        return words!!.size
    }
    class WordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewName = view.findViewById(R.id.textView_FileName) as TextView
    }
}