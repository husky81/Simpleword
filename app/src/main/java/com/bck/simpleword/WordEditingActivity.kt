package com.bck.simpleword

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_edit_word.*
import kotlinx.android.synthetic.main.content_edit_word.*

class WordEditingActivity : AppCompatActivity() {
    var word = Word("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_word)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            saveWordAndFinish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getWordFromFirstActivity()
        showWord(word)
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
