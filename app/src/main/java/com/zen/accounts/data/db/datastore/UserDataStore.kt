package com.zen.accounts.data.db.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.zen.accounts.data.db.model.User
import com.zen.accounts.presentation.ui.screens.common.*
import com.zen.accounts.presentation.utility.backupPlanToString
import com.zen.accounts.presentation.utility.stringToBackupPlan
import com.zen.accounts.presentation.utility.stringToUser
import com.zen.accounts.presentation.utility.userToString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserDataStore private constructor(
    private val context : Context,
    private var userDataStoreKey : Preferences.Key<String>,
    private var systemInDarkModeKey : Preferences.Key<Boolean>,
    private var backupPlanKey : Preferences.Key<String>,
    private var profilePicKey : Preferences.Key<ByteArray>
) {
    class Builder(private val context: Context){

        private var userDataStoreKey : Preferences.Key<String> = stringPreferencesKey(user_data_store_key)
        private var systemInDarkModeKey : Preferences.Key<Boolean> = booleanPreferencesKey(system_in_dark_mode)
        private var backupPlanKey : Preferences.Key<String> = stringPreferencesKey(backup_plan)
        private var profilePicKey : Preferences.Key<ByteArray> = byteArrayPreferencesKey(profile_pic)

        fun userDataStoreKey(key : String = user_data_store_key) {
            this.userDataStoreKey = stringPreferencesKey(key)
        }

        fun systemInDarkModeKey(key: String = system_in_dark_mode) {
            this.systemInDarkModeKey = booleanPreferencesKey(key)
        }

        fun backupPlanKey(key: String = backup_plan) {
            this.backupPlanKey = stringPreferencesKey(key)
        }

        fun profilePicKey(key: String = profile_pic) {
            this.profilePicKey = byteArrayPreferencesKey(key)
        }

        fun build() : UserDataStore {
            return UserDataStore(
                context,
                this.userDataStoreKey,
                this.systemInDarkModeKey,
                this.backupPlanKey,
                this.profilePicKey
            )
        }
    }

    val Context.dataStore : DataStore<Preferences> by preferencesDataStore(
        datastore_name
    )

    val getUser : Flow<User?> = context.dataStore.data
        .map { preferences ->
            val userString = preferences[this.userDataStoreKey]
            if (userString != null) stringToUser(userString) else null
        }

    val getBackupPlan: Flow<BackupPlan> = context.dataStore.data
        .map { preference ->
            val backupPlanString = preference[this.backupPlanKey]
            if(backupPlanString == null) BackupPlan.Off else stringToBackupPlan(backupPlanString)
        }

    val getProfilePic : Flow<ByteArray> = context.dataStore.data
        .map { preference ->
            val profilePic = preference[this.profilePicKey]
            profilePic ?: ByteArray(0)
        }

    suspend fun updateBackupPlan(backupPlan: BackupPlan) {
        withContext(Dispatchers.IO) {
            context.dataStore.edit {
                it[backupPlanKey] = backupPlanToString(backupPlan)
            }
        }
    }

    suspend fun getBackupPlan() : BackupPlan {
        return withContext(Dispatchers.IO) {
            val preferences = context.dataStore.data.firstOrNull()
            if (preferences != null) {
                val backupPlanString = preferences[backupPlanKey]
                if(backupPlanString != null)
                    stringToBackupPlan(backupPlanString)
                else
                    BackupPlan.Off
            } else BackupPlan.Off
        }
    }

    suspend fun getUser() : User? {
        return withContext(Dispatchers.IO) {
            val preferences = context.dataStore.data.firstOrNull()
            if (preferences != null) {
                val userString = preferences[this@UserDataStore.userDataStoreKey]
                if (userString != null) stringToUser(userString) else null
            } else null
        }
    }

    suspend fun saveUser(user : User) {
        context.dataStore.edit { preferences ->
            preferences[this.userDataStoreKey] = userToString(user) // profilePic = null because gonna remove this property in future. so that already existing user's app should not crash.
        }
    }

    suspend fun saveProfilePic(image: ByteArray) {
        context.dataStore.edit {preferences ->
            preferences[this.profilePicKey] = image
        }
    }

    suspend fun getProfilePic() : ByteArray? {
        return withContext(Dispatchers.IO) {
            val preferences = context.dataStore.data.firstOrNull()
            if (preferences != null) {
                preferences[this@UserDataStore.profilePicKey]
            } else null
        }
    }

    suspend fun removeProfilePic() {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { pref ->
                pref[this@UserDataStore.profilePicKey] = ByteArray(0)
            }
        }
    }


    suspend fun saveIsDarkMode(data : Boolean) {
        context.dataStore.edit {preferences ->
            preferences[this.systemInDarkModeKey] = data
        }
    }

    suspend fun logoutUser() {
        context.dataStore.edit { preferences ->
            val userString = preferences[this.userDataStoreKey]
            if(userString != null) {
                preferences[this.userDataStoreKey] = userToString(User())
            }
        }
        removeProfilePic()
    }

}