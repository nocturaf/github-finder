package com.nocturaf.githubfinder.network.repository

import com.nocturaf.githubfinder.network.api.NetworkClient
import com.nocturaf.githubfinder.network.api.UsersApi
import com.nocturaf.githubfinder.network.model.SearchUsersResponse
import com.nocturaf.githubfinder.network.model.User
import retrofit2.Response

class UsersRepository {

    private val usersApi: UsersApi by lazy {
        NetworkClient.client.create(UsersApi::class.java)
    }

    suspend fun getUsers(): Response<List<User>> {
        return usersApi.getAllUsers()
    }

    suspend fun searchUsers(username: String): Response<SearchUsersResponse> {
        return usersApi.searchUsers(username)
    }
}