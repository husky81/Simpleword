package com.bck.simpleword

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v4.widget.DrawerLayout
import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import kotlinx.android.synthetic.main.content_main.*

const val REQUEST_CODE_ADD_A_WORD: Int = 1921
const val REQUEST_CODE_EDIT_A_WORD: Int = 1923
const val RESULT_CODE_WORD: Int = 1952
const val RESULT_CODE_WORD_DELETE: Int = 1953

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
            val intent = Intent(this, WordEditingActivity::class.java)
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
                        words.update(positionEditingWord, word)
                        // words.update(word)
                        redrawWords(words)
                    }
                    RESULT_CODE_WORD_DELETE -> {
                        val word = data!!.getParcelableExtra<Word>("Word")
                        words.delete(word)
                        words.removeAt(positionEditingWord)
                        redrawWords(words)
                    }
                }
            }
        }
    }

    fun openEditWordActivity(word: Word){
        val intent = Intent(this, WordEditingActivity::class.java)
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


