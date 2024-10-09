package com.zen.accounts.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.zen.accounts.data.db.datastore.UserDataStore
import com.zen.accounts.presentation.ui.screens.common.datastore_name
import com.zen.accounts.presentation.ui.viewmodels.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

val Context.store : DataStore<Preferences> by preferencesDataStore(
    datastore_name
)

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var dataStore : UserDataStore
    private val settingViewModel: SettingViewModel by viewModels<SettingViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp(
                settingViewModel = settingViewModel,
                dataStore
            )
        }
    }
}
