package com.example.managproje.activities

import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.managproje.R
import com.example.managproje.firebase.FireStoreClass
import com.example.managproje.models.User
import kotlinx.android.synthetic.main.activity_my_profile.*

class MyProfileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        setupActionBar()

        FireStoreClass().loadUserData(this)
    }


    private fun setupActionBar(){
        setSupportActionBar(toolbar_my_profile_activity)

        val actionBar = supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }
        toolbar_my_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun setUserDataInUI(loggedInUser: User){
        Glide
            .with(this@MyProfileActivity)
            .load(loggedInUser.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_user_image_my_profile)

        et_name_my_profile.setText(loggedInUser.name)
        et_email_my_profile.setText(loggedInUser.email)
        if(loggedInUser.mobile != 0L){
            et_mobile_my_profile.setText(loggedInUser.mobile.toString())
        }

    }
}