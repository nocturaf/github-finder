package com.nocturaf.githubfinder.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.nocturaf.githubfinder.R
import com.nocturaf.githubfinder.network.repository.UsersRepository
import com.nocturaf.githubfinder.network.util.Result.Success
import com.nocturaf.githubfinder.network.util.Result.Fail
import com.nocturaf.githubfinder.network.util.Result.Loading
import com.nocturaf.githubfinder.ui.viewmodel.UsersViewModel
import com.nocturaf.githubfinder.ui.viewmodel.UsersViewModelProviderFactory

class UserFragment : Fragment(LAYOUT) {

    companion object {
        @LayoutRes
        val LAYOUT = R.layout.fragment_user
    }

    lateinit var usersViewModel: UsersViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        observeLiveData()
        loadUsersData()
    }

    private fun initViewModel() {
        // init usersViewModel using view model provider factory
        UsersViewModelProviderFactory(UsersRepository()).apply {
            usersViewModel = ViewModelProvider(
                this@UserFragment,
                this
            ).get(UsersViewModel::class.java)
        }
    }

    private fun loadUsersData() {
        usersViewModel.getUsers()
    }

    private fun observeLiveData() {
        // observe get users live data
        usersViewModel.users.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Success -> {
                    val users = response.data
                    users?.forEach {
                        Log.d("USERGITHUB", it.login)
                    }
                }
                is Fail -> {
                    Log.d("USERGITHUBERROR", response.message)
                }
                is Loading -> {
                }
            }
        })
    }
}