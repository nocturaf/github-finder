package com.nocturaf.githubfinder.util

import com.nocturaf.githubfinder.network.model.SearchUser
import com.nocturaf.githubfinder.network.model.User
import com.nocturaf.githubfinder.ui.model.UserUiModel

object UserMapper {

    private const val SEARCH_USER_MAPPER_SIGNATURE = "mapperForSearchUsers"

    fun mapToUserUiModel(list: List<User>) = list.map { item ->
        UserUiModel().apply {
            id = item.id
            username = item.login
            avatar = item.avatarUrl
        }
    }

    @JvmName(SEARCH_USER_MAPPER_SIGNATURE)
    fun mapToUserUiModel(list: List<SearchUser>) = list.map { item ->
        UserUiModel().apply {
            id = item.id
            username = item.login
            avatar = item.avatarUrl
        }
    }

}