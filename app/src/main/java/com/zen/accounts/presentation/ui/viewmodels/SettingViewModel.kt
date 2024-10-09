package com.zen.accounts.presentation.ui.viewmodels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.zen.accounts.data.db.model.User
import com.zen.accounts.data.repositoryImpl.ExpenseItemRepository
import com.zen.accounts.data.repositoryImpl.ExpenseRepository
import com.zen.accounts.data.repositoryImpl.MediaStoreRepository
import com.zen.accounts.presentation.utility.async
import com.zen.accounts.presentation.ui.screens.common.BackupPlan
import com.zen.accounts.presentation.ui.screens.common.LoadingState
import com.zen.accounts.presentation.ui.screens.common.work_manager_output_data
import com.zen.accounts.presentation.ui.screens.main.setting.SettingUiState
import com.zen.accounts.presentation.ui.theme.green_color
import com.zen.accounts.presentation.ui.theme.red_color
import com.zen.accounts.presentation.utility.Utility
import com.zen.accounts.presentation.utility.io
import com.zen.accounts.presentation.utility.main
import com.zen.accounts.data.repositoryImpl.worker_repository.WorkerRepository
import com.zen.accounts.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val expenseRepository: ExpenseRepository,
    private val expenseItemRepository: ExpenseItemRepository,
    private val workerRepository: WorkerRepository,
    private val mediaStoreRepository: MediaStoreRepository
) : BaseViewmodel() {
    val settingUIState by lazy { SettingUiState() }
    private val _user = MutableStateFlow<User?>(null)
    val user : StateFlow<User?> get() = _user


    init {
        viewModelScope.launch {
            expenseRepository.dataStore.getUser
                .flowOn(Dispatchers.IO)
                .map {
                    it ?: User()
                }
                .collect {
                    delay(1000)
                    _user.value = it
                }
        }

        viewModelScope.launch {
            expenseRepository.dataStore.getProfilePic
                .flowOn(Dispatchers.IO)
                .map {
                    it?.let {
                        BitmapFactory.decodeByteArray(it, 0, it.size)
                    }
                }
                .collect {
                    settingUIState.profilePic.value = it
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            settingUIState.apply {
                loadingState.value = LoadingState.LOADING
                if (expenseRepository.isBackupTableEmpty()) {
                    expenseRepository.clearExpenseTable()
                    expenseItemRepository.clearExpenseItemTable()
                    authRepository.logout()
                    expenseRepository.dataStore.logoutUser()
                    profilePic.value = null

                    delay(500)
                    loadingState.value = LoadingState.SUCCESS
                    showSnackBarText.value = "Logout Successfully"
                    showSnackBarColor.value = green_color
                    Utility.showSnackBar(showSnackBar)
                } else {
                    loadingState.value = LoadingState.IDLE
                    showConfirmationPopUp.value = true
                }
            }
        }
    }

    fun logoutConfirmation(logoutWithoutBackup: Boolean) {
        viewModelScope.launch {
            settingUIState.apply {
                if (!logoutWithoutBackup) {
                    startSingleUploadRequest(true)
                } else {
                    expenseRepository.clearBackupTable()
                    expenseRepository.clearExpenseTable()
                    expenseItemRepository.clearExpenseItemTable()
                    authRepository.logout()
                    expenseRepository.dataStore.logoutUser()
                }
            }
        }
    }

    fun getBackupPlan() {
        viewModelScope.launch {
            settingUIState.apply {
                val backupPlan = expenseRepository.dataStore.getBackupPlan()
                if (backupDropDownText.value == BackupPlan.Off && backupPlan != BackupPlan.Off) {
                    backupDropDownText.value = backupPlan
                }
            }
        }
    }

    suspend fun updateBackupPlan() {
        viewModelScope.launch(Dispatchers.IO) {
            settingUIState.apply {
                expenseRepository.dataStore.updateBackupPlan(if (backupDropDownText.value == BackupPlan.Now) BackupPlan.Off else backupDropDownText.value)
            }
        }
    }

    private fun startSingleUploadRequest(fromLogoutConfirmation: Boolean = false) {
        viewModelScope.launch {
            expenseRepository.dataStore.getUser()?.let { user ->
                settingUIState.backupLoadingState.value = LoadingState.LOADING
                val requestIds =
                    workerRepository.startUploadingNow(user.uid)
                workerRepository.getWorkInfoById(requestIds[2]).collectLatest {
                    it?.let {
                        when (it.state) {
                            WorkInfo.State.SUCCEEDED -> {
                                settingUIState.backupLoadingState.value = LoadingState.SUCCESS
                                settingUIState.showSnackBarText.value = it.outputData.getString(work_manager_output_data).toString()
                                settingUIState.showSnackBarColor.value = green_color
                                Utility.showSnackBar(settingUIState.showSnackBar)
                                if (fromLogoutConfirmation) {
                                    settingUIState.loadingState.value = LoadingState.LOADING
                                    expenseRepository.clearExpenseTable()
                                    expenseItemRepository.clearExpenseItemTable()
                                    authRepository.logout()
                                    expenseRepository.dataStore.logoutUser()
                                    delay(200)
                                    settingUIState.loadingState.value = LoadingState.SUCCESS
                                }
                                settingUIState.backupDropDownText.value = BackupPlan.Off
                            }

                            WorkInfo.State.FAILED -> {
                                delay(500)
                                settingUIState.backupDropDownText.value = BackupPlan.Off
                                settingUIState.backupLoadingState.value = LoadingState.FAILURE
                                settingUIState.showSnackBarText.value = it.outputData.getString(work_manager_output_data).toString()
                                settingUIState.showSnackBarColor.value = green_color
                                Utility.showSnackBar(settingUIState.showSnackBar)
                            }

                            WorkInfo.State.RUNNING -> {
                                settingUIState.backupLoadingState.value = LoadingState.LOADING
                            }

                            WorkInfo.State.BLOCKED -> {
                                settingUIState.backupLoadingState.value = LoadingState.LOADING
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }


    fun startDailyUploadRequest() {
        viewModelScope.launch {
            expenseRepository.dataStore.getUser()?.let { user ->
                workerRepository.startUploadingDaily(user.uid)
            }
        }
    }

    fun startWeeklyUploadRequest() {
        viewModelScope.launch {
            expenseRepository.dataStore.getUser()?.let { user ->
                workerRepository.startUploadingWeekly(user.uid)
            }
        }
    }

    fun startMonthlyUploadRequest() {
        viewModelScope.launch {
            expenseRepository.dataStore.getUser()?.let { user ->
                workerRepository.startUploadingMonthly(user.uid)
            }
        }
    }

    suspend fun cancelAllWork() {
        io {
            workerRepository.cancelAllWorker()
            updateBackupPlan()
        }
    }

    fun saveImageToStorage(uri: Uri): Deferred<Bitmap?> {
        return async {
            settingUIState.loadingState.value = LoadingState.LOADING
            return@async mediaStoreRepository.saveImageToStorage(uri).await()
        }
    }

    suspend fun uploadUserProfilePicture(imageBitmap: Bitmap) {
        io {
            val bos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            expenseRepository.dataStore.getUser()?.let {
                expenseRepository.dataStore.saveProfilePic(bos.toByteArray())
                settingUIState.profilePic.value = imageBitmap
                settingUIState.loadingState.value = LoadingState.SUCCESS
                settingUIState.showImagePickerOption.value = false
                val requestId = workerRepository.updateProfile()
                workerRepository.getWorkInfoById(requestId)
                    .collectLatest {workInfo ->
                        workInfo?.let {
                            val outputData = workInfo.outputData.getString(work_manager_output_data)
                            when (workInfo.state) {

                                WorkInfo.State.SUCCEEDED -> {
                                    settingUIState.showSnackBarText.value = outputData.toString()
                                    settingUIState.showSnackBarColor.value = green_color
                                    Utility.showSnackBar(settingUIState.showSnackBar)
                                }

                                WorkInfo.State.FAILED -> {
                                    settingUIState.showSnackBarText.value = outputData.toString()
                                    settingUIState.showSnackBarColor.value = red_color
                                    Utility.showSnackBar(settingUIState.showSnackBar)
                                }

                                else -> {}
                            }
                        }
                    }
            }
        }
    }

    // <----------------------------------- UI updates starts ------------------------------------->
    fun backupPlanChange(backupPlan: BackupPlan) {
        settingUIState.backupDropDownText.value = backupPlan
        main {
            when (backupPlan) {
                is BackupPlan.Off -> {
                    cancelAllWork()
                }

                is BackupPlan.Now -> {
                    startSingleUploadRequest()
                    updateBackupPlan()
                }

                is BackupPlan.Daily -> {
                    startDailyUploadRequest()
                    updateBackupPlan()
                }

                is BackupPlan.Weekly -> {
                    startWeeklyUploadRequest()
                    updateBackupPlan()
                }

                is BackupPlan.Monthly -> {
                    startMonthlyUploadRequest()
                    updateBackupPlan()
                }
            }
        }
    }
}