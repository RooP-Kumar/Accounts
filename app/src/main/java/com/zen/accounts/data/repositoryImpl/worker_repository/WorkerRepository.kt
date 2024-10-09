package com.zen.accounts.data.repositoryImpl.worker_repository

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.zen.accounts.presentation.ui.screens.common.*
import com.zen.accounts.presentation.utility.io
import com.zen.accounts.data.workmanager.DeleteExpenseWorker
import com.zen.accounts.data.workmanager.PeriodicWorker
import com.zen.accounts.data.workmanager.ProfileUpdateWorker
import com.zen.accounts.data.workmanager.UpdateExpenseWorker
import com.zen.accounts.data.workmanager.UploadExpenseWorker
import com.zen.accounts.presentation.ui.screens.common.daily_work_request_tag
import com.zen.accounts.presentation.ui.screens.common.daily_worker_name
import com.zen.accounts.presentation.ui.screens.common.single_work_request_tag
import com.zen.accounts.presentation.ui.screens.common.update_profile_work_request_tag
import com.zen.accounts.presentation.ui.screens.common.update_profile_worker_name
import com.zen.accounts.presentation.ui.screens.common.work_manager_input_data
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkerRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private lateinit var workManager: WorkManager
    }

    init {
        workManager = WorkManager.getInstance(context)
    }

    // Cancel All Worker
    fun cancelAllWorker() {
        workManager.cancelAllWork()
    }

    fun updateProfile(): UUID {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val request = OneTimeWorkRequestBuilder<ProfileUpdateWorker>()
            .addTag(update_profile_work_request_tag)
            .setConstraints(constraints)
            .build()

        workManager.beginUniqueWork(
            update_profile_worker_name,
            ExistingWorkPolicy.REPLACE,
            request
        ).enqueue()

        return request.id
    }

    // Single Work Request
    fun startUploadingNow(uid: String, calledFromTimePeriod: Boolean = false): List<UUID> {
        val inputData = workDataOf(work_manager_input_data to uid)

        val customConstraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .build()

        val createRequest = OneTimeWorkRequestBuilder<UploadExpenseWorker>()
            .setConstraints(customConstraints)
            .addTag(single_work_request_tag)
            .setInputData(inputData)
            .build()

        val updateRequest = OneTimeWorkRequestBuilder<UpdateExpenseWorker>()
            .setConstraints(customConstraints)
            .addTag(single_work_request_tag)
            .setInputData(inputData)
            .build()

        val deleteRequest = OneTimeWorkRequestBuilder<DeleteExpenseWorker>()
            .setConstraints(customConstraints)
            .addTag(single_work_request_tag)
            .setInputData(inputData)
            .build()

        if (!calledFromTimePeriod) {
            workManager.cancelAllWork()
        }

        // Not recommended. Because create, update and delete can be done in single transaction.
        // I am doing it because of learning purpose
        // I wanted to learning chaining the work
        workManager.beginWith(createRequest)
            .then(updateRequest)
            .then(deleteRequest)
            .enqueue()

        return listOf(createRequest.id, updateRequest.id, deleteRequest.id)

    }

    // Daily Work Request
    fun startUploadingDaily(uid: String) {
        val inputData = Data.Builder()
            .putString(work_manager_input_data, uid)
            .build()

        val customConstraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .build()

        io {
            workManager.getWorkInfosForUniqueWorkFlow(daily_worker_name).collectLatest {
                var createNewRequest = false
                it?.let {list ->
                    for(i in 0..<list.size) {
                        val workInfo = list[i]
                        if(workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING){
                            createNewRequest = false
                            break
                        } else {
                            createNewRequest = true
                        }
                    }
                }
                if (it == null || it.isEmpty() || createNewRequest) {
                    val request = PeriodicWorkRequestBuilder<PeriodicWorker>(
                        repeatInterval = 24,
                        repeatIntervalTimeUnit = TimeUnit.HOURS
                    )
                        .addTag(daily_work_request_tag)
                        .setConstraints(customConstraints)
                        .setInputData(inputData)
                        .build()

                    workManager
                        .enqueueUniquePeriodicWork(
                            daily_worker_name,
                            ExistingPeriodicWorkPolicy.UPDATE,
                            request
                        )
                }
            }

        }


    }

    // Weekly Work Request
    fun startUploadingWeekly(uid: String) {
        val inputData = Data.Builder()
            .putString(work_manager_input_data, uid)
            .build()

        val customConstraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .build()

        io {
            workManager.getWorkInfosForUniqueWorkFlow(weekly_work_request_tag).collectLatest {
                var createNewRequest = false
                it?.let { list ->
                    for (i in 0..<list.size) {
                        val workInfo = list[i]
                        if (workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING) {
                            createNewRequest = false
                            break
                        } else {
                            createNewRequest = true
                        }
                    }
                }
                if (it == null || it.isEmpty() || createNewRequest) {
                    val request = PeriodicWorkRequestBuilder<PeriodicWorker>(
                        repeatInterval = 7 * 24,
                        repeatIntervalTimeUnit = TimeUnit.HOURS
                    )
                        .addTag(weekly_work_request_tag)
                        .setConstraints(customConstraints)
                        .setInputData(inputData)
                        .build()
                    workManager.enqueueUniquePeriodicWork(
                        weekly_worker_name,
                        ExistingPeriodicWorkPolicy.UPDATE,
                        request
                    )
                }

            }
        }


    }

    // Monthly Work Request
    fun startUploadingMonthly(uid: String) {
        val inputData = Data.Builder()
            .putString(work_manager_input_data, uid)
            .build()

        val customConstraints = Constraints
            .Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .build()

        io {
            workManager.getWorkInfosForUniqueWorkFlow(monthly_work_request_tag).collectLatest {
                var createNewRequest = false
                it?.let { list ->
                    for (i in 0..<list.size) {
                        val workInfo = list[i]
                        if (workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING) {
                            createNewRequest = false
                            break
                        } else {
                            createNewRequest = true
                        }
                    }
                }
                if (it == null || it.isEmpty() || createNewRequest) {
                    val request = PeriodicWorkRequestBuilder<PeriodicWorker>(
                        repeatInterval = 30 * 24,
                        repeatIntervalTimeUnit = TimeUnit.HOURS
                    )
                        .addTag(monthly_work_request_tag)
                        .setConstraints(customConstraints)
                        .setInputData(inputData)
                        .build()

                    workManager.enqueueUniquePeriodicWork(
                        monthly_worker_name,
                        ExistingPeriodicWorkPolicy.UPDATE,
                        request
                    )
                }

            }
        }

    }

    fun getWorkInfoById(id: UUID): Flow<WorkInfo?> {
        return workManager.getWorkInfoByIdFlow(id)
    }
}