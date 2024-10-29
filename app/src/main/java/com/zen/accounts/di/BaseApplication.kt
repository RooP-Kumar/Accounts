package com.zen.accounts.di

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.zen.accounts.data.repositoryImpl.worker_repository.WorkRepository
import com.zen.accounts.data.repositoryImpl.worker_repository.WorkerRepository
import com.zen.accounts.data.workmanager.DeleteExpenseWorker
import com.zen.accounts.data.workmanager.PeriodicWorker
import com.zen.accounts.data.workmanager.ProfileUpdateWorker
import com.zen.accounts.data.workmanager.UpdateExpenseWorker
import com.zen.accounts.data.workmanager.UploadExpenseWorker
import com.zen.accounts.domain.repository.AuthRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication: Application(), Configuration.Provider {
    @Inject
    lateinit var workRepository : WorkRepository
    @Inject
    lateinit var authRepository: AuthRepository

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(
                CombineWorkerFactory(workRepository, authRepository)
            )
            .build()
}

class CombineWorkerFactory(
    private val workRepository: WorkRepository,
    private val authRepository: AuthRepository
): WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when(workerClassName) {
            UploadExpenseWorker::class.java.name -> {
                UploadExpenseWorkerFactory(workRepository).createWorker(appContext, workerClassName, workerParameters)
            }
            UpdateExpenseWorker::class.java.name -> {
                UpdateExpenseWorkerFactory(workRepository).createWorker(appContext, workerClassName, workerParameters)
            }
            DeleteExpenseWorker::class.java.name -> {
                DeleteExpenseWorkerFactory(workRepository).createWorker(appContext, workerClassName, workerParameters)
            }
            PeriodicWorker::class.java.name -> {
                PeriodicWorkerFactory(WorkerRepository(appContext)).createWorker(appContext, workerClassName, workerParameters)
            }
            ProfileUpdateWorker::class.java.name -> {
                UpdateProfileWorkerFactory(authRepository).createWorker(appContext, workerClassName, workerParameters)
            }
            else -> null
        }
    }

}

class PeriodicWorkerFactory(
    private val workRepository: WorkerRepository
) : WorkerFactory(){
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return PeriodicWorker(
            appContext,
            workerParameters,
            workRepository
        )
    }

}

class UploadExpenseWorkerFactory(
    private val workRepository : WorkRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return UploadExpenseWorker(
            appContext,
            workerParameters,
            workRepository
        )
    }
}

class UpdateExpenseWorkerFactory(
    private val workRepository: WorkRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return UpdateExpenseWorker(
            appContext,
            workerParameters,
            workRepository
        )
    }

}
class DeleteExpenseWorkerFactory(
    private val workRepository: WorkRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return DeleteExpenseWorker(
            appContext,
            workerParameters,
            workRepository
        )
    }

}

class UpdateProfileWorkerFactory(
    private val repo: AuthRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return ProfileUpdateWorker(appContext, workerParameters, repo)
    }

}
