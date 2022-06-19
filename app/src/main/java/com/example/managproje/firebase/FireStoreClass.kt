package com.example.managproje.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.managproje.activities.*
import com.example.managproje.models.Board
import com.example.managproje.models.Card
import com.example.managproje.models.User
import com.example.managproje.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUserFireStore(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                e->
                Log.e(activity.javaClass.simpleName,
                    "Error writing document",e)
            }
    }

    fun getCurrentUserId(): String{

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if(currentUser != null){
            currentUserId = currentUser.uid
        }
        return currentUserId

    }

    fun addUpdateTaskList(activity: Activity, board: Board){
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "TaskList Updated Successfully")
                if(activity is TaskListActivity){
                    activity.addUpdateTaskListSuccess()
                }
                else if(activity is CardDetailsActivity){
                    activity.addUpdateTaskListSuccess()
                }

            }
            .addOnFailureListener {
                exception->
                if(activity is TaskListActivity){
                    activity.hideProgressDialog()
                }

                else if(activity is CardDetailsActivity){
                    activity.hideProgressDialog()
                }

                Log.e(activity.javaClass.simpleName, "Error while creating the board", exception)
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity,
                              userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile Data Updated Successfully")
                Toast.makeText(activity,
                    "Profile Data Updated Successfully",
                    Toast.LENGTH_SHORT).show()

                activity.profileUpdateSuccess()
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board",e)

                Toast.makeText(activity,
                    "Error when updating the Profile",
                    Toast.LENGTH_SHORT).show()
            }
    }


    fun loadUserData(activity: Activity, readBoardsList: Boolean = false){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {  document ->
                val loggedInUser = document.toObject(User::class.java)

                when(activity){
                    is SignInActivity ->{
                        if(loggedInUser !=null){
                            activity.signInSuccess(loggedInUser)
                        }
                    }
                    is MainActivity ->{
                        if (loggedInUser != null) {
                            activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                        }
                    }
                    is MyProfileActivity ->{
                        if (loggedInUser != null) {
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }
                }



            }.addOnFailureListener {
                    e->
                when(activity){
                    is SignInActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MainActivity ->{
                        activity.hideProgressDialog()
                    }
                    is MyProfileActivity->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e("SignInUser",
                    "Error writing document",e)
            }
    }

    fun createBoard(activity: CreateBoardActivity, board: Board){
        mFireStore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board Created Successfully")
                Toast.makeText(activity,
                    "Board Created Successfully",
                    Toast.LENGTH_SHORT).show()

                activity.boardCreatedSuccessfully()
            }
            .addOnFailureListener {
                exception->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                "Error while created Board"
                ,exception)
            }
    }

    fun getBoardsList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())

                val boardList: ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)
                    board!!.documentId = i.id
                    boardList.add(board)
                }

                activity.populateBoardListToUI(boardList)
            }
            .addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a Board",e)
            }
    }

    fun getBoardDetails(activity: TaskListActivity, documentId: String) {
        mFireStore.collection(Constants.BOARDS)
            .document(documentId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                val boardList: ArrayList<Board> = ArrayList()
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                activity.boardDetails(board)

            }
            .addOnFailureListener {
                    e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a Board",e)
            }
    }

    fun getAssignedMembersListDetails(
        activity: MembersActivity, assignedTo: ArrayList<String>){
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener {
                document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val usersList: ArrayList<User> = ArrayList()

                for(i in document.documents){
                    val user = i.toObject(User::class.java)!!
                    usersList.add(user)
                }

                activity.setupMembersList(usersList)
            }
            .addOnFailureListener {
                    e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while loading the Members",e)
            }
    }

    fun getMemberDetails(activity: MembersActivity, email: String){
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL,email)
            .get()
            .addOnSuccessListener {
                document->
                if(document.size() > 0){
                    val user = document.documents[0].toObject(User::class.java)
                    activity.memberDetails(user!!)
                }else{
                    activity.hideProgressDialog()
                    activity.showErrorSnackBar("No Such Member Found")
                }
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting User Details",
                    e
                )
            }
    }

    fun assignMemberToBoard(
        activity: MembersActivity, board: Board, user: User){

        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo

        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignedSuccess(user)
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                    "Error while assigning to board", e)
            }

    }
}