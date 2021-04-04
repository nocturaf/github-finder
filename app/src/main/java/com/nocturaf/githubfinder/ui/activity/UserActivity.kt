package com.nocturaf.githubfinder.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.IdRes
import com.nocturaf.githubfinder.R
import com.nocturaf.githubfinder.ui.fragment.UserFragment

class UserActivity : AppCompatActivity() {

    companion object {
        @IdRes
        val CONTAINER_ID = R.id.activity_container
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        setupFragment()
    }

    private fun setupFragment() {
        supportFragmentManager
            .beginTransaction()
            .add(CONTAINER_ID, UserFragment())
            .commit()
    }
}