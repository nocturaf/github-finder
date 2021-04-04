package com.nocturaf.githubfinder.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nocturaf.githubfinder.network.repository.UsersRepository

class UsersViewModelProviderFactory(
    private val usersRepository: UsersRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UsersViewModel(usersRepository) as T
    }
}