package com.bck.simpleword

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_select_file.*
import kotlinx.android.synthetic.main.content_select_file.*

import java.io.File
import android.annotation.SuppressLint
import android.os.Build
import android.support.annotation.RequiresApi
import android.widget.ImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SelectFileActivity : AppCompatActivity() {


    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_file)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val items = FileItems()
        items.setRecyclerView(recyclerView_SelectFile)
        items.setLinearLayoutManager(this)

        val pathDownload : String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()

        items.openDirectory(pathDownload)



    }
}
class FileItems: ArrayList<ItemFile>(){
    private var recyclerView: RecyclerView? = null
    private val storageRootDirectory = Environment.getExternalStorageDirectory().toString() // "/storage/emulated/0"

    private fun add(name : String) {
        this.add(ItemFile(name))
    }
    private fun add(file: File) {
        this.add(ItemFile(file))
    }

    private fun redraw() {
        val fileItems = this
        val adapter = SelectFileAdapter(fileItems)
        adapter.itemClick = object : SelectFileAdapter.ItemClick{
            override fun onClick(view: View, position: Int) {
                if(fileItems[position].file.isDirectory){
                    openDirectory(fileItems[position].path)
                }else{
                    if(fileItems[position].file.extension == "xlsx"){

                    }
                }
            }
        }
        recyclerView!!.adapter = adapter
    }
    fun setLinearLayoutManager(context: Context) {
        recyclerView!!.layoutManager = LinearLayoutManager(context)
    }
    fun setRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }
    fun openDirectory(mPath: String) {
        val f = File(mPath)
        //val fs = f.listFiles().toList()

        val fs = f.listFiles()
        this.clear()

        if (mPath != storageRootDirectory) {
            this.add("../")
            this.last().path = f.parent
        }

        if(fs!=null){

            //sortFileListByModifiedDate(fs)
            fs.reverse()
            sortFileListByIsDirectory(fs)

            for (file in fs) {
                if(file.isHidden) continue
                this.add(file)
            }
        }
        this.redraw()
    }

}
class ItemFile(val file : File) {
    var path: String = ""
    var name: String = ""

    init {
        this.name = file.name
        this.path = file.path
    }
    constructor(name : String) : this(File(name)) {
        this.name = name
    }


}
class SelectFileAdapter (itemList : FileItems) : RecyclerView.Adapter<SelectFileAdapter.ViewHolder>() {
    private var items : FileItems? = itemList
    interface ItemClick{
        fun onClick(view: View, position: Int)
    }
    var itemClick: ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_file,parent,false)
        return ViewHolder(view)
    }
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items!![position]
        holder.textViewName.text = item.name
        holder.textViewModifiedDate.text = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss").format(Date(item.file.lastModified()))

        if(item.file.isDirectory){
            holder.imageView.setImageResource(R.drawable.ic_folder_24dp)
        }else{
            if(item.file.extension == "xls"){
                holder.imageView.setImageResource((R.drawable.ic_note_add_black_24dp))
            }else{
                holder.imageView.setImageResource(R.drawable.ic_insert_drive_file_24dp)
            }
        }

        if(itemClick != null)
        {
            holder.itemView.setOnClickListener { v -> itemClick?.onClick(v, position)
            }
        }
    }
    override fun getItemCount(): Int {
        return items!!.size
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var textViewName = view.findViewById(R.id.textView_FileName) as TextView
        var textViewModifiedDate = view.findViewById(R.id.textView_FileModifiedDate) as TextView
        var imageView = view.findViewById(R.id.imageView_fileIcon) as ImageView
    }
}