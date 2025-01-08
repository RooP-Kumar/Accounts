package com.zen.accounts.data.repositoryImpl

import com.zen.accounts.data.api.AuthApi
import com.zen.accounts.data.api.ProfileApi
import com.zen.accounts.data.api.resource.Resource
import com.zen.accounts.data.api.resource.Response
import com.zen.accounts.data.db.model.User
import com.zen.accounts.domain.repository.AuthRepository
import com.zen.accounts.domain.repository.DataStoreRepository
import com.zen.accounts.presentation.utility.io
import com.zen.accounts.retrofit.BackendService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.random.Random

class AuthRepositoryImpl @Inject constructor (
    private val authApi: AuthApi,
    private val profileApi: ProfileApi,
    private val dataStoreRepo: DataStoreRepository,
    private val backendService : BackendService
) : AuthRepository {

    override suspend fun registerUser(user: User, pass: String) : Resource<Response<String>> {
        return withContext(Dispatchers.IO) {
            val res = authApi.registerUsingEmailAndPassword(generateUID(user), pass, user)
            if(res.status) Resource.SUCCESS(res)
            else Resource.FAILURE(res.message)
        }
    }

    override suspend fun loginUser(email: String, pass: String) : Resource<Response<User>> {
        return withContext(Dispatchers.IO) {
            val res = authApi.loginUsingEmailAndPassword(email, pass)
            if(res.status) {
                if(res.value.second.isNotEmpty()) {dataStoreRepo.saveProfilePicToLocalDataSource(res.value.second)}
                Resource.SUCCESS(
                    Response(
                        value = res.value.first,
                        status = res.status,
                        res.message
                    )
                )
            }
            else Resource.FAILURE(res.message)
        }
    }

    override suspend fun logout() {
        io {
            authApi.logout()
        }
    }
    
    override suspend fun signUpUsingRetrofit(user : User) : Resource<Response<String>> {
        return withContext(Dispatchers.IO) {
            val result = backendService.signup(user)
            return@withContext if(result.isNotEmpty()) {
                Resource.SUCCESS(Response(result["msg"] ?: "", true, "Successful"))
            } else {
                Resource.FAILURE("Failure")
            }
        }
    }
    
    override suspend fun uploadProfilePic(): Resource<Response<Unit>> {
        return withContext(Dispatchers.IO){
           val pairOfUserAndProfile = userAndProfileFlow().lastOrNull()
            return@withContext if (pairOfUserAndProfile == null) {
                Resource.FAILURE("Invalid Data.")
            } else {
                val res = profileApi.updateProfilePic(
                    pairOfUserAndProfile.first,
                    pairOfUserAndProfile.second
                )
                return@withContext if (res.status) Resource.SUCCESS(value = res)
                else Resource.FAILURE(res.message)
            }
        }
    }

    private fun userAndProfileFlow() : Flow<Pair<User, ByteArray>?> {
       return combine(dataStoreRepo.userFromLocalDataSource, dataStoreRepo.profilePicFromLocalDataSource) {user, profile ->
           if(user == null || profile.isEmpty()) {
               null
           } else {
               Pair(user, profile)
           }
       }
    }

    override fun generateUID(user: User): String   {
        val alphabets = "abcdefghijklmnopqrstuvwxyz"
        var first = ""
        var end = ""
        end = ""
        if(user.phone.isNotEmpty()) {
            first = user.phone.substring(0, 5)
            end = user.phone.substring(5, 10)
        } else {
            for (i in 1..5) {
                first += Random.nextInt(1, 10).toString()
                end += Random.nextInt(1, 10).toString()
            }
        }
        var middleOne = ""
        if(user.name.isNotEmpty()) {
            for (i in 0..<user.name.length) {
                if (user.name[i] == ' ') {
                    break;
                }
                middleOne += user.name[i]
            }
        } else{
            for (i in 0..4) {
                middleOne += alphabets[Random.nextInt(0, 25)]
            }
        }

        var middleTwo = ""
        for (i in 0..<user.email.length) {
            if (user.email[i] == '@'){
                break;
            }
            middleTwo += user.email[i]
        }

        return "$first$middleOne@$middleTwo$end"
    }
    
    
    

}
