package com.bck.simpleword

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_add_word.*
import kotlinx.android.synthetic.main.content_add_word.*

class EditWordActivity : AppCompatActivity() {
    var word = Word("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_word)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            saveWordAndFinish()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getWordFromFirstActivity()
        showWord(word)
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
        setResult(RESULT_CODE_EDITED_WORD, intent)

        finish()
    }
}
