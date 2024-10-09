package com.zen.accounts.data.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.zen.accounts.data.api.resource.Resource
import com.zen.accounts.presentation.ui.screens.common.work_manager_input_data
import com.zen.accounts.presentation.ui.screens.common.work_manager_output_data
import com.zen.accounts.data.repositoryImpl.worker_repository.WorkRepository

class UploadExpenseWorker(
    context : Context,
    workParam : WorkerParameters,
    private val repo : WorkRepository
) : CoroutineWorker(context, workParam) {
    override suspend fun doWork(): Result {
        val uid = inputData.getString(work_manager_input_data)
        val outputData = Data.Builder()
        return if (uid != null) {
            return when(val res = repo.uploadExpenseToFirebase(uid)) {
                is Resource.SUCCESS -> {
                    repo.clearCreatedExpenseFromBackupTable()
                    Result.success(workDataOf(work_manager_input_data to uid))
                }
                is Resource.FAILURE -> {
                    Result.failure(outputData.putString(work_manager_output_data, res.message).build())
                }
            }
        } else {
            Result.failure(outputData.putString(work_manager_output_data, "No user found.").build())
        }
    }

}