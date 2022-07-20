package com.victorloveday.leavemanager.database

import android.content.Context
import androidx.datastore.preferences.clear
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserInfoManager(context: Context) {
    private val dataStore = context.createDataStore(name = "user_preferences")

    companion object {
        val NAME_KEY = preferencesKey<String>("NAME")
        val USER_ID_KEY = preferencesKey<String>("USER_ID")
        val AGE_KEY = preferencesKey<String>("AGE")
        val ROLE_KEY = preferencesKey<String>("ROLE")
        val USER_TYPE_KEY = preferencesKey<String>("USER_TYPE")
        val USER_ON_LEAVE_KEY = preferencesKey<Boolean>("USER_ON_LEAVE")
    }

    suspend fun clearUser() {
        dataStore.edit {
            it.clear()
        }
    }

    suspend fun storeUser(name: String, userId: String, age: String, role: String, userType: String, isOnLeave: Boolean) {
        dataStore.edit {
            it[NAME_KEY] = name
            it[USER_ID_KEY] = userId
            it[AGE_KEY] = age
            it[ROLE_KEY] = role
            it[USER_TYPE_KEY] = userType
            it[USER_ON_LEAVE_KEY] = isOnLeave
        }
    }

    val nameFlow: Flow<String> = dataStore.data.map {
        it[NAME_KEY] ?: ""
    }
    val userIdFlow: Flow<String> = dataStore.data.map {
        it[USER_ID_KEY] ?: ""
    }
    val ageFlow: Flow<String> = dataStore.data.map {
        it[AGE_KEY] ?: ""
    }
    val roleFlow: Flow<String> = dataStore.data.map {
        it[ROLE_KEY] ?: ""
    }
    val typeFlow: Flow<String> = dataStore.data.map {
        it[USER_TYPE_KEY] ?: ""
    }
    val onLeave: Flow<Boolean> = dataStore.data.map {
        it[USER_ON_LEAVE_KEY] ?: false
    }

}