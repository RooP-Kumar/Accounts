package com.zen.accounts.data.repositoryImpl

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.zen.accounts.data.api.resource.Resource
import com.zen.accounts.data.api.resource.Response
import com.zen.accounts.data.db.model.User
import com.zen.accounts.domain.repository.DataStoreRepository
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class DataStoreRepositoryImplTest {
    private lateinit var dataStoreRepository : DataStoreRepository
    @Before
    fun startUp() {
        dataStoreRepository = mock(DataStoreRepositoryImpl::class.java)
    }
    
    @Test
    fun saveUserToLocalDataSource_returnSuccess() = runTest {
        val response = Response(Unit, true, "Task is successfully done!")
        val user = User(
            "asdfasdfasf",
            "Roop Kumar",
            "9528865314",
            "roopkm12@outlook.com",
            true,
            null
        )
        `when`(dataStoreRepository.saveUserToLocalDataSource(user)).thenReturn(
            Resource.SUCCESS(response)
        )
        
        val result = dataStoreRepository.saveUserToLocalDataSource(user)
        assertThat(result).isInstanceOf(Resource.SUCCESS::class.java)
    }
    
    @Test
    fun userFromLocalDataSource_returnUser() = runTest {
        val user = User(
            "asdfasdfasf",
            "Roop Kumar",
            "9528865314",
            "roopkm12@outlook.com",
            true,
            null
        )
        
        
        
        
    }
    
    
    @After
    fun tearDown() {
    
    }
}
