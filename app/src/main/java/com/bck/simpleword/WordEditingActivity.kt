package com.bck.simpleword

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat

import kotlinx.android.synthetic.main.activity_word_editing.*
import kotlinx.android.synthetic.main.content_word_editing.*

class WordEditingActivity : AppCompatActivity() {
    var word = Word("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_editing)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            saveWordAndFinish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getWordFromFirstActivity()
        showWord(word)
    }
    override fun onBackPressed() {
        val drawerLayout: androidx.drawerlayout.widget.DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.edit_word, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_word -> {
                deleteCallAndFinish()
            }
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }
    private fun showWord(word: Word) {
        textView_Word.setText(word.name)
        textView_Pronunciation.setText(word.pronunciation)
        textView_Text.setText(word.text)
        textView_Memo.setText(word.memo)
    }
    private fun getWordFromFirstActivity(){
        if (intent.hasExtra("Word")){
            val itemWord = intent.getParcelableExtra<Word>("Word")
            this.word = itemWord
        }
    }
    private fun saveWordAndFinish(){
        word.name = textView_Word.text.toString()
        word.pronunciation = textView_Pronunciation.text.toString()
        word.text = textView_Text.text.toString()
        word.memo = textView_Memo.text.toString()

        intent = Intent()
        intent.putExtra("Word", word)
        setResult(RESULT_CODE_WORD, intent)

        finish()
    }
    private fun deleteCallAndFinish(){
        word.deleteCall = 1
        intent = Intent()
        intent.putExtra("Word", word)
        setResult(RESULT_CODE_WORD_DELETE, intent)
        finish()
    }
}
