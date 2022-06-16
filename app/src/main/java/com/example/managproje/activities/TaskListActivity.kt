package com.example.managproje.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.managproje.R
import com.example.managproje.adapters.TaskListItemsAdapter
import com.example.managproje.firebase.FireStoreClass
import com.example.managproje.models.Board
import com.example.managproje.models.Task
import com.example.managproje.utils.Constants
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : BaseActivity() {


    private lateinit var mBoardDetails: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        var boardDocumentId = ""
        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this, boardDocumentId)
    }

    fun boardDetails(board: Board){

        mBoardDetails = board

        hideProgressDialog()
        setupActionBar()

        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)
        rv_task_list.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false)

        rv_task_list.setHasFixedSize(true)

        val adapter = TaskListItemsAdapter(this, board.taskList)
        rv_task_list.adapter = adapter

    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        //this is for FireStore when we are getting the data
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this, mBoardDetails.documentId)
    }

    fun createTaskList(taskListName: String){
        val task  = Task(taskListName, FireStoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)

        showProgressDialog(resources.getString(R.string.please_wait))

        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_task_list_activity)

        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.name
        }
        toolbar_task_list_activity.setNavigationOnClickListener { onBackPressed() }
    }
}