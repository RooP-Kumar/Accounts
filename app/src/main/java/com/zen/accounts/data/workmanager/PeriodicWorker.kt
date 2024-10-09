package com.zen.accounts.data.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zen.accounts.presentation.ui.screens.common.work_manager_input_data
import com.zen.accounts.data.repositoryImpl.worker_repository.WorkerRepository

class PeriodicWorker(
    context : Context,
    workerParameters: WorkerParameters,
    private val workerRepository: WorkerRepository
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        val uid = inputData.getString(work_manager_input_data)
        return if(uid != null) {
            workerRepository.startUploadingNow(uid, true)
            Result.success()
        } else {
            Result.failure()
        }
    }
}