package com.nocturaf.githubfinder.network.api

import com.nocturaf.githubfinder.network.api.ApiConstant.USERS
import com.nocturaf.githubfinder.network.model.User
import retrofit2.Response
import retrofit2.http.GET

interface UsersApi {
    @GET(USERS)
    suspend fun getAllUsers(): Response<List<User>>
}