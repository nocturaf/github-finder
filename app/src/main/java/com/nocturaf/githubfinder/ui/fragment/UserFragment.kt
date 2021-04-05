package com.nocturaf.githubfinder.ui.fragment

import android.app.SearchManager
import android.content.Context
import android.net.ConnectivityManager
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
import androidx.recyclerview.widget.RecyclerView
import com.nocturaf.githubfinder.R
import com.nocturaf.githubfinder.network.api.ApiConstant.MAX_PAGE
import com.nocturaf.githubfinder.network.repository.UsersRepository
import com.nocturaf.githubfinder.network.util.Result.Success
import com.nocturaf.githubfinder.network.util.Result.Fail
import com.nocturaf.githubfinder.network.util.Result.Loading
import com.nocturaf.githubfinder.ui.adapter.UsersAdapter
import com.nocturaf.githubfinder.ui.viewmodel.UsersViewModel
import com.nocturaf.githubfinder.ui.viewmodel.UsersViewModelProviderFactory
import com.nocturaf.githubfinder.util.gone
import com.nocturaf.githubfinder.util.visible
import kotlinx.android.synthetic.main.error_network_view.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.no_internet_view.*

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
    private var isOnSearchMode: Boolean = false
    private var currentPage = 1
    private var searchKeyword = ""

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

            // set listener when search view perform searching
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    isOnSearchMode = true
                    currentPage = 1
                    query?.let { username ->
                        // search users
                        searchKeyword = username
                        usersViewModel.searchUsersList.clear()
                        searchUsers(searchKeyword)
                    }
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })

            // set listener when search view on close
            searchView.setOnCloseListener {
                // reset state
                searchView.clearFocus()
                loadUsersData()
                false
            }
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
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (isOnSearchMode && !canScrollVertically(1)) {
                        // do pagination
                        currentPage++
                        if (currentPage <= MAX_PAGE) {
                            showLoadMore()
                            searchUsers(searchKeyword, currentPage)
                        }
                    }
                }
            })
        }
    }

    private fun loadUsersData() {
        // load initial users data
        if (isHasInternetConnection()) {
            usersViewModel.getUsers()
        } else {
            showNoInternetView()
        }
    }

    private fun searchUsers(username: String, page: Int = 1) {
        // search github user by username
        if (isHasInternetConnection()) {
            usersViewModel.searchUsers(username, page)
        } else {
            showNoInternetView()
        }
    }

    private fun showErrorNetworkView() {
        rvUser?.gone()
        loader?.gone()
        loader_footer?.gone()
        errorNetworkView?.visible()
        btnTryAgainErrorNetwork?.setOnClickListener {
            if (isOnSearchMode) {
                currentPage = 1
                usersViewModel.searchUsersList.clear()
                searchUsers(searchKeyword)
            } else {
                loadUsersData()
            }
        }
    }

    private fun hideErrorNetworkView() {
        errorNetworkView?.gone()
    }

    private fun showNoInternetView() {
        rvUser?.gone()
        loader?.gone()
        loader_footer?.gone()
        noInternetView?.visible()
        btnTryAgain?.setOnClickListener {
            loadUsersData()
        }
    }

    private fun hideNoInternetView() {
        noInternetView?.gone()
    }

    private fun showEmptyView() {
        rvUser?.gone()
        loader?.gone()
        emptyView?.visible()
    }

    private fun hideEmptyView() {
        emptyView?.gone()
    }

    private fun showLoader() {
        loader?.visible()
        rvUser?.gone()
        hideNoInternetView()
        hideErrorNetworkView()
        hideEmptyView()
    }

    private fun hideLoader() {
        loader?.gone()
        rvUser?.visible()
    }

    private fun showLoadMore() {
        loader_footer?.visible()
    }

    private fun hideLoadMore() {
        loader_footer?.gone()
    }

    private fun isHasInternetConnection(): Boolean {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    private fun observeLiveData() {
        // observe get users live data
        usersViewModel.users.observe(viewLifecycleOwner, Observer { usersResponse ->
            when (usersResponse) {
                is Success -> {
                    hideLoader()
                    val userList = usersResponse.data
                    userList?.let { list ->
                        if (list.isEmpty()) {
                            showEmptyView()
                        } else {
                            usersAdapter?.differ?.submitList(list)
                        }
                    }
                }
                is Fail -> {
                    hideLoader()
                    showErrorNetworkView()
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
                    if (currentPage == 1) {
                        hideLoader()
                    } else {
                        hideLoadMore()
                    }
                    val searchUsersList = searchUsersResponse.data
                    searchUsersList?.let { list ->
                        if (list.isEmpty()) {
                            showEmptyView()
                        } else {
                            usersAdapter?.differ?.submitList(list)
                        }
                    }
                }
                is Fail -> {
                    hideLoader()
                    hideLoadMore()

                    searchUsersResponse.message?.let {
                        Log.d(TAG, it)
                    }
                }
                is Loading -> {
                    if (currentPage == 1) {
                        showLoader()
                    } else {
                        showLoadMore()
                    }
                }
            }
        })
    }
}