package com.nocturaf.githubfinder.network.repository

import com.nocturaf.githubfinder.network.api.NetworkClient
import com.nocturaf.githubfinder.network.api.UsersApi
import com.nocturaf.githubfinder.network.model.User
import retrofit2.Response

class UsersRepository {

    suspend fun getUsers(): Response<List<User>> {
        val userApi = NetworkClient.client.create(UsersApi::class.java)
        return userApi.getAllUsers()
    }
}