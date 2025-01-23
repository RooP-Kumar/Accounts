package com.zen.accounts.data.repositoryImpl

import com.zen.accounts.data.api.resource.Resource
import com.zen.accounts.data.api.resource.Response
import com.zen.accounts.data.db.datastore.UserDataStore
import com.zen.accounts.data.db.model.User
import com.zen.accounts.domain.repository.DataStoreRepository
import com.zen.accounts.presentation.ui.screens.common.BackupPlan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val dataStore: UserDataStore
): DataStoreRepository {
    override val userFromLocalDataSource : Flow<User?> = dataStore.getUser
    override val profilePicFromLocalDataSource : Flow<ByteArray> = dataStore.getProfilePic
    override val backupPlanFromLocalDataSource : Flow<BackupPlan> = dataStore.getBackupPlan

    override suspend fun saveUserToLocalDataSource(user: User) : Resource<Response<Unit>> {
        return withContext(Dispatchers.IO) {
            dataStore.saveUser(user)
            delay(200)
            return@withContext Resource.SUCCESS(Response(Unit))
        }
    }

    override suspend fun saveProfilePicToLocalDataSource(picture: ByteArray) : Resource<Response<Unit>> {
        return withContext(Dispatchers.IO) {
            dataStore.saveProfilePic(picture)
            delay(200)
            return@withContext Resource.SUCCESS(Response(Unit))
        }
    }

    override suspend fun updateBackupPlanInLocalDataSource(backupPlan: BackupPlan) : Resource<Response<Unit>> {
        return withContext(Dispatchers.IO) {
            dataStore.updateBackupPlan(backupPlan)
            delay(200)
            return@withContext Resource.SUCCESS(Response(Unit))
        }

    }

    override suspend fun removeProfilePicFromLocalDataSource(): Resource<Response<Unit>> {
        return withContext(Dispatchers.IO) {
            dataStore.removeProfilePic()
            delay(200)
            return@withContext Resource.SUCCESS(Response(Unit))
        }
    }
    
    override suspend fun logoutUserFromLocalDataSource() : Resource<Response<Unit>> {
        return withContext(Dispatchers.IO) {
            dataStore.logoutUser()
            delay(200)
            return@withContext Resource.SUCCESS(Response(Unit))
        }
    }

}
