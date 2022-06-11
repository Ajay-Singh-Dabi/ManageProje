package com.example.managproje.firebase

import android.util.Log
import com.example.managproje.activities.SignInActivity
import com.example.managproje.activities.SignUpActivity
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
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun signInUser(activity: SignInActivity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {  document ->
                val loggedInUser = document.toObject(User::class.java)

                if(loggedInUser !=null){
                    activity.signInSuccess(loggedInUser)
                }

            }.addOnFailureListener {
                    e->
                Log.e("SignInUser",
                    "Error writing document",e)
            }
    }
}