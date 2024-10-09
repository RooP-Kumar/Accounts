package com.zen.accounts.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.zen.accounts.data.db.AppDatabase
import com.zen.accounts.data.db.dao.BackupTrackerDao
import com.zen.accounts.data.db.dao.ExpenseDao
import com.zen.accounts.data.db.dao.ExpenseItemDao
import com.zen.accounts.data.db.datastore.UserDataStore
import com.zen.accounts.data.repositoryImpl.AuthRepositoryImpl
import com.zen.accounts.data.repositoryImpl.DataStoreRepositoryImpl
import com.zen.accounts.domain.repository.AuthRepository
import com.zen.accounts.domain.repository.DataStoreRepository
import com.zen.accounts.presentation.states.AppState
import com.zen.accounts.presentation.utility.DateDeSerializerForApi
import com.zen.accounts.presentation.utility.DateSerializerForApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiModule {
    @Provides
    fun getDatabase(@ApplicationContext context : Context) : AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "App Database",
        ).build()
    }

    @Provides
    fun getExpenseDao(db : AppDatabase) : ExpenseDao {
        return db.getExpenseDao()
    }

    @Provides
    fun getExpenseItemDao(db : AppDatabase) : ExpenseItemDao {
        return db.getExpenseItemDao()
    }

    @Provides
    fun getBackupTrackerDao(db : AppDatabase) : BackupTrackerDao {
        return db.getBackupTrackerDao()
    }

    @Provides
    fun getGson() : Gson{
        return GsonBuilder()
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .registerTypeAdapter(Date::class.java, DateSerializerForApi())
            .registerTypeAdapter(Date::class.java, DateDeSerializerForApi())
            .create()
    }

    @Provides
    fun getRetrofit(gson : Gson) : Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://192.168.31.14:9090")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun getAppState(@ApplicationContext context: Context) : AppState {
        return AppState(context)
    }
    
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun getAuthRepo(repo : AuthRepositoryImpl) : AuthRepository
    
    @Binds
    abstract fun getDataStore(dataStore : DataStoreRepositoryImpl) : DataStoreRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Singleton
    @Provides
    fun getUerDataStore(@ApplicationContext context: Context) : UserDataStore {
        return UserDataStore.Builder(context)
            .build()
    }
}
