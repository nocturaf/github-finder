package com.nocturaf.githubfinder.network.api

object ApiConstant {
    // url & paths
    const val BASE_URL = "https://api.github.com/"
    const val USERS = "users"
    const val SEARCH_USERS = "/search/users"

    // query & params
    const val SEARCH_QUERY = "q"
    const val PAGE_QUERY = "page"
    const val PER_PAGE_QUERY = "per_page"

    // default params
    const val DEFAULT_START_PAGE = 1
    const val DEFAULT_PER_PAGE = 15
    const val MAX_PAGE = 8
}