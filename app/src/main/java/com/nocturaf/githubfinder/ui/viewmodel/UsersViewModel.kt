package com.nocturaf.githubfinder.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nocturaf.githubfinder.network.model.User
import com.nocturaf.githubfinder.network.repository.UsersRepository
import com.nocturaf.githubfinder.network.util.Result
import kotlinx.coroutines.launch
import retrofit2.Response

class UsersViewModel(
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _users: MutableLiveData<Result<List<User>>> = MutableLiveData()
    val users: LiveData<Result<List<User>>> get() = _users

    fun getUsers() = viewModelScope.launch {
        _users.postValue(Result.Loading())
        val response = getUsersCall(usersRepository.getUsers())
        _users.postValue(response)
    }

    private fun getUsersCall(response: Response<List<User>>): Result<List<User>> {
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                return Result.Success(responseBody)
            }
        }
        return Result.Fail(response.message())
    }

}