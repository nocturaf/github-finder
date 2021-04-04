package com.nocturaf.githubfinder.ui.fragment

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nocturaf.githubfinder.R
import com.nocturaf.githubfinder.network.repository.UsersRepository
import com.nocturaf.githubfinder.network.util.Result.Success
import com.nocturaf.githubfinder.network.util.Result.Fail
import com.nocturaf.githubfinder.network.util.Result.Loading
import com.nocturaf.githubfinder.ui.adapter.UsersAdapter
import com.nocturaf.githubfinder.ui.viewmodel.UsersViewModel
import com.nocturaf.githubfinder.ui.viewmodel.UsersViewModelProviderFactory
import com.nocturaf.githubfinder.util.gone
import com.nocturaf.githubfinder.util.visible
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : Fragment(LAYOUT) {

    companion object {
        @LayoutRes
        val LAYOUT = R.layout.fragment_user

        @MenuRes
        private const val TOOLBAR_MENU = R.menu.menu_toolbar

        @IdRes
        private const val SEARCH_MENU_ID = R.id.icSearch

        private const val TAG = "UserFragment"
    }

    private lateinit var usersViewModel: UsersViewModel
    private var usersAdapter: UsersAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initViewModel()
        initRecyclerView()
        observeLiveData()
        loadUsersData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        setupSearchView(menu, inflater)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupSearchView(menu: Menu, inflater: MenuInflater) {
        // setup search view in toolbar
        inflater.inflate(TOOLBAR_MENU, menu)
        val searchIcon = menu.findItem(SEARCH_MENU_ID)
        activity?.let {
            val searchManager = it.getSystemService(Context.SEARCH_SERVICE) as SearchManager
            val searchView = searchIcon.actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(it.componentName))
            searchView.queryHint = getString(R.string.search_view_hint)
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                // set search view listener
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { username ->
                        // search users
                        searchUsers(username)
                    }
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })
        }
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

    private fun initRecyclerView() {
        // init recyclerview with linear layout manager
        usersAdapter = UsersAdapter()
        rvUser.apply {
            setHasFixedSize(true)
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun loadUsersData() {
        // load initial users data
        showLoader()
        usersViewModel.getUsers()
    }

    private fun searchUsers(username: String) {
        // search github user by username
        showLoader()
        usersViewModel.searchUsers(username)
    }

    private fun showLoader() {
        loader?.visible()
        rvUser?.gone()
    }

    private fun hideLoader() {
        loader?.gone()
        rvUser?.visible()
    }

    private fun observeLiveData() {
        // observe get users live data
        usersViewModel.users.observe(viewLifecycleOwner, Observer { usersResponse ->
            when (usersResponse) {
                is Success -> {
                    hideLoader()
                    val userList = usersResponse.data
                    userList?.let { list ->
                        usersAdapter?.differ?.submitList(list)
                    }
                }
                is Fail -> {
                    hideLoader()
                    usersResponse.message?.let {
                        Log.d(TAG, it)
                    }
                }
                is Loading -> showLoader()
            }
        })

        // observe search users live data
        usersViewModel.searchUsers.observe(viewLifecycleOwner, Observer { searchUsersResponse ->
            when (searchUsersResponse) {
                is Success -> {
                    hideLoader()
                    val searchUsersList = searchUsersResponse.data
                    searchUsersList?.let { list ->
                        usersAdapter?.differ?.submitList(list)
                    }
                }
                is Fail -> {
                    hideLoader()
                    searchUsersResponse.message?.let {
                        Log.d(TAG, it)
                    }
                }
                is Loading -> showLoader()
            }
        })
    }
}