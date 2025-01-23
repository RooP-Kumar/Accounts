package com.zen.accounts.domain.repository

import com.zen.accounts.data.api.resource.Resource
import com.zen.accounts.data.api.resource.Response
import com.zen.accounts.data.db.model.User
import com.zen.accounts.presentation.ui.screens.common.BackupPlan
import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val userFromLocalDataSource : Flow<User?>
    val backupPlanFromLocalDataSource : Flow<BackupPlan>
    val profilePicFromLocalDataSource : Flow<ByteArray>

    suspend fun saveUserToLocalDataSource(user: User) : Resource<Response<Unit>>

    suspend fun saveProfilePicToLocalDataSource(picture: ByteArray) : Resource<Response<Unit>>

    suspend fun updateBackupPlanInLocalDataSource(backupPlan: BackupPlan) : Resource<Response<Unit>>

    suspend fun logoutUserFromLocalDataSource() : Resource<Response<Unit>>

    suspend fun removeProfilePicFromLocalDataSource(): Resource<Response<Unit>>

}
