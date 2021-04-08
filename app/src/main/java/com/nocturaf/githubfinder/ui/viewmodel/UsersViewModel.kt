package com.nocturaf.githubfinder.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nocturaf.githubfinder.network.model.SearchUsersResponse
import com.nocturaf.githubfinder.network.model.User
import com.nocturaf.githubfinder.network.repository.UsersRepository
import com.nocturaf.githubfinder.network.util.Result
import com.nocturaf.githubfinder.ui.model.UserUiModel
import com.nocturaf.githubfinder.util.UserMapper
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception

class UsersViewModel(
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _users: MutableLiveData<Result<List<UserUiModel>>> = MutableLiveData()
    val users: LiveData<Result<List<UserUiModel>>> get() = _users

    private val _searchUsers: MutableLiveData<Result<List<UserUiModel>>> = MutableLiveData()
    val searchUsers: LiveData<Result<List<UserUiModel>>> get() = _searchUsers

    var searchUsersList: MutableList<UserUiModel> = mutableListOf()

    fun getUsers() = viewModelScope.launch {
        _users.postValue(Result.Loading())
        try {
            val response = getUsersCall(usersRepository.getUsers())
            _users.postValue(response)
        } catch (e: Exception) {
            _users.postValue(Result.Fail(e.message))
        }
    }

    fun searchUsers(username: String, page: Int = 1) = viewModelScope.launch {
        _searchUsers.postValue(Result.Loading())
        try {
            val searchUsersResponse = searchUsersCall(usersRepository.searchUsers(username, page))
            _searchUsers.postValue(searchUsersResponse)
        } catch (e: Exception) {
            _searchUsers.postValue(Result.Fail(e.message))
        }
    }

    private fun getUsersCall(response: Response<List<User>>): Result<List<UserUiModel>> {
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                return Result.Success(
                    UserMapper.mapToUserUiModel(responseBody)
                )
            }
        }
        return Result.Fail(response.message())
    }

    private fun searchUsersCall(response: Response<SearchUsersResponse>): Result<List<UserUiModel>> {
        if (response.isSuccessful) {
            response.body()?.let { responseBody ->
                val responseUsersList = UserMapper.mapToUserUiModel(responseBody.items)
                if (searchUsersList.isEmpty()) {
                    searchUsersList = responseUsersList.toMutableList()
                } else {
                    searchUsersList.addAll(responseUsersList)
                }
                return Result.Success(searchUsersList.toList())
            }
        }
        return Result.Fail(response.message())
    }

}