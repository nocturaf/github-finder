package com.nocturaf.githubfinder.network.api

import com.nocturaf.githubfinder.network.api.ApiConstant.DEFAULT_PER_PAGE
import com.nocturaf.githubfinder.network.api.ApiConstant.DEFAULT_START_PAGE
import com.nocturaf.githubfinder.network.api.ApiConstant.PAGE_QUERY
import com.nocturaf.githubfinder.network.api.ApiConstant.PER_PAGE_QUERY
import com.nocturaf.githubfinder.network.api.ApiConstant.SEARCH_QUERY
import com.nocturaf.githubfinder.network.api.ApiConstant.SEARCH_USERS
import com.nocturaf.githubfinder.network.api.ApiConstant.USERS
import com.nocturaf.githubfinder.network.model.SearchUsersResponse
import com.nocturaf.githubfinder.network.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UsersApi {

    @GET(USERS)
    suspend fun getAllUsers(): Response<List<User>>

    @GET(SEARCH_USERS)
    suspend fun searchUsers(
        @Query(SEARCH_QUERY) username: String,
        @Query(PAGE_QUERY) page: Int = DEFAULT_START_PAGE,
        @Query(PER_PAGE_QUERY) perPage: Int = DEFAULT_PER_PAGE
    ): Response<SearchUsersResponse>

}