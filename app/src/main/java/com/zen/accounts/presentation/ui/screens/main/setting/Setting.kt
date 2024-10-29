package com.zen.accounts.presentation.ui.screens.main.setting

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.zen.accounts.R
import com.zen.accounts.data.db.datastore.UserDataStore
import com.zen.accounts.data.db.model.User
import com.zen.accounts.presentation.states.AppState
import com.zen.accounts.presentation.ui.navigation.Screen
import com.zen.accounts.presentation.ui.screens.common.BackupPlan
import com.zen.accounts.presentation.ui.screens.common.GeneralButton
import com.zen.accounts.presentation.ui.screens.common.GeneralDialog
import com.zen.accounts.presentation.ui.screens.common.GeneralDropDown
import com.zen.accounts.presentation.ui.screens.common.GeneralSnackBar
import com.zen.accounts.presentation.ui.screens.common.LoadingDialog
import com.zen.accounts.presentation.ui.screens.common.LoadingState
import com.zen.accounts.presentation.ui.screens.common.TopAppBar
import com.zen.accounts.presentation.ui.screens.common.auth_route
import com.zen.accounts.presentation.ui.screens.common.login_button_label
import com.zen.accounts.presentation.ui.screens.common.login_screen_label
import com.zen.accounts.presentation.ui.screens.common.logout_button_label
import com.zen.accounts.presentation.ui.screens.common.small_logout_button_label
import com.zen.accounts.presentation.ui.theme.Typography
import com.zen.accounts.presentation.ui.theme.buttonDescriptionTextStyle
import com.zen.accounts.presentation.ui.theme.generalPadding
import com.zen.accounts.presentation.ui.theme.halfGeneralPadding
import com.zen.accounts.presentation.ui.theme.red_color
import com.zen.accounts.presentation.ui.theme.roundedCornerShape
import com.zen.accounts.presentation.ui.theme.tweenAnimDuration
import com.zen.accounts.presentation.ui.viewmodels.SettingViewModel
import com.zen.accounts.presentation.utility.LoadingScreen
import com.zen.accounts.presentation.utility.Utility
import com.zen.accounts.presentation.utility.async
import com.zen.accounts.presentation.utility.generalBorder
import com.zen.accounts.presentation.utility.generalCircleBorder
import com.zen.accounts.presentation.utility.io
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class SettingUiState(
    val showBackupDropDown: MutableState<Boolean> = mutableStateOf(false),
    val backupDropDownText: MutableState<BackupPlan> = mutableStateOf(BackupPlan.Off),
    val showSnackBar: MutableState<Boolean> = mutableStateOf(false),
    val showSnackBarJob: MutableState<Job?> = mutableStateOf(null),
    val showSnackBarText: MutableState<String> = mutableStateOf(""),
    val showSnackBarColor: MutableState<Color> = mutableStateOf(red_color),
    val loadingState: MutableState<LoadingState> = mutableStateOf(LoadingState.IDLE),
    val backupLoadingState: MutableState<LoadingState> = mutableStateOf(LoadingState.IDLE),
    val showLogoutPopUp: MutableState<Boolean> = mutableStateOf(false),
    val showConfirmationPopUp: MutableState<Boolean> = mutableStateOf(false),
    val showImagePickerOption: MutableState<Boolean> = mutableStateOf(false),
    val profilePic: MutableState<Bitmap?> = mutableStateOf(null)
)

@Composable
fun Setting(
    settingViewModel: SettingViewModel,
    dataStore: UserDataStore,
    navigateUp: () -> Boolean,
    currentScreen: Screen?
) {
    val uiState = settingViewModel.settingUIState
    val user = settingViewModel.user.collectAsState()

    LaunchedEffect(key1 = Unit) {
        settingViewModel.getBackupPlan()
    }

    LoadingDialog(loadingState = uiState.loadingState)

    MainUI(
        appState = settingViewModel.appState,
        dataStore = dataStore,
        uiState = settingViewModel.settingUIState,
        user = user.value,
        logout = settingViewModel::logout,
        logoutConfirmation = settingViewModel::logoutConfirmation,
        backupPlanChange = settingViewModel::backupPlanChange,
        saveImageToStorage = settingViewModel::saveImageToStorage,
        uploadUserProfilePicture = settingViewModel::uploadUserProfilePicture,
        navigateUp = navigateUp,
        currentScreen
    )
}

@Composable
private fun MainUI(
    appState: AppState = AppState(context = LocalContext.current),
    dataStore : UserDataStore,
    uiState: SettingUiState = SettingUiState(),
    user : User? = null,
    logout: () -> Unit = {},
    logoutConfirmation: (Boolean) -> Unit = {},
    backupPlanChange : (BackupPlan) -> Unit = {},
    saveImageToStorage : (Uri) -> Deferred<Bitmap?>? = { async { return@async Bitmap.createBitmap(0, 0, Bitmap.Config.ALPHA_8) } },
    uploadUserProfilePicture: suspend (Bitmap) -> Unit = {},
    navigateUp : () -> Boolean,
    currentScreen: Screen?
) {
    val coroutineScope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenWidthDp
    BackHandler(uiState.showImagePickerOption.value) {
        uiState.showImagePickerOption.value = false
    }

    var rotation by remember { mutableFloatStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition(label = "backup indicator label")

    if(uiState.backupLoadingState.value == LoadingState.LOADING){
        rotation = infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                tween(1000),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation label"
        ).value
    }

    ScreenDialogs(
        uiState = uiState,
        logout,
        logoutConfirmation
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopAppBar(
                drawerState = appState.drawerState,
                navigateUp = navigateUp,
                currentScreen = currentScreen
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Crossfade(
                    targetState = user,
                    animationSpec = tween(500),
                    label = "main screen"
                ) {
                    if(it == null) {
                        LoadingScreen()
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if(screenWidth <= 500) {
                                ProfileSection(
                                    user!!,
                                    uiState.showImagePickerOption,
                                    uiState.profilePic
                                )
                            }
                            // <-------------------------------------------- Dark Mode Switch (Will add later) --------------------------->
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(generalPadding),
//                                horizontalArrangement = Arrangement.SpaceBetween,
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Text(
//                                    text = "Dark Mode",
//                                    style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
//                                )
//
//                                Switch(
//                                    checked = darkSwitchOn,
//                                    onCheckedChange = {
//                                        coroutineScope.launch {
//                                            appState.dataStore.saveIsDarkMode(!darkSwitchOn)
//                                        }
//                                    },
//                                    colors = SwitchDefaults.colors().copy(
//                                        checkedBorderColor = MaterialTheme.colorScheme.onBackground,
//                                        checkedThumbColor = MaterialTheme.colorScheme.onBackground,
//                                        checkedTrackColor = enabled_color,
//                                        uncheckedBorderColor = enabled_color,
//                                        uncheckedThumbColor = enabled_color,
//                                        uncheckedTrackColor = disabled_color
//                                    )
//                                )
//                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = generalPadding, bottom = generalPadding),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Backup",
                                    style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                                )

                                Spacer(modifier = Modifier.weight(1f))

                                AnimatedVisibility(
                                    visible = uiState.backupLoadingState.value == LoadingState.LOADING,
                                    modifier = Modifier,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_sync),
                                        "backup indicator",
                                        modifier = Modifier
                                            .size(20.dp)
                                            .graphicsLayer {
                                                rotationZ = rotation
                                            }
                                    )
                                }

                                GeneralDropDown(
                                    modifier = Modifier.width(160.dp),
                                    value = uiState.backupDropDownText,
                                    showDropDown = uiState.showBackupDropDown,
                                    valueList = BackupPlan.getAllBackupPlan(),
                                    enable = user!!.isAuthenticated,
                                    onClick = {
                                        if (user.uid.isEmpty() && !user.isAuthenticated) {
                                            uiState.showSnackBarText.value =
                                                "Please login to use backup"
                                            coroutineScope.launch {
                                                if (uiState.showSnackBarJob.value == null || (uiState.showSnackBarJob.value != null && !uiState.showSnackBarJob.value!!.isActive)) {
                                                    uiState.showSnackBarJob.value =
                                                        coroutineScope.launch(Dispatchers.IO) {
                                                            Utility.showSnackBar(uiState.showSnackBar)
                                                        }
                                                }
                                            }
                                        }
                                    },
                                    onItemClick = backupPlanChange
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = generalPadding, bottom = generalPadding),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (user!!.isAuthenticated) small_logout_button_label else login_screen_label,
                                    style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                                )

                                GeneralButton(
                                    text = if (user.isAuthenticated) logout_button_label else login_button_label,
                                    modifier = Modifier
                                        .width(160.dp)
                                        .padding(
                                            horizontal = generalPadding,
                                            vertical = halfGeneralPadding
                                        )
                                ) {
                                    coroutineScope.launch {
                                        val currentUser = dataStore.getUser()
                                        if (currentUser != null && currentUser.isAuthenticated)
                                            uiState.showLogoutPopUp.value = true
                                        else
                                            appState.navController.navigate(auth_route)
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = uiState.showImagePickerOption.value,
            modifier = Modifier
                .align(Alignment.BottomCenter),
            enter = slideInVertically(tween(tweenAnimDuration)) { it },
            exit = slideOutVertically(tween(tweenAnimDuration)) { it }
        ) {
            ImagePickerSection(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface),
                saveImageToStorage,
                uploadUserProfilePicture
            )
        }

        GeneralSnackBar(
            visible = uiState.showSnackBar,
            text = uiState.showSnackBarText.value,
            modifier = Modifier.align(Alignment.TopCenter),
            containerColor = uiState.showSnackBarColor.value
        )
    }
}

@Composable
fun ScreenDialogs(
    uiState: SettingUiState = SettingUiState(),
    logout : () -> Unit,
    logoutConfirmation : (Boolean) -> Unit
) {
    val localDensity = LocalDensity.current
    val confirmationPopupButtonsHeight = remember { mutableStateOf(0.dp) }


    // Only for getting equal height of the button
    // Start --->
    Button(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = generalPadding)
            .onGloballyPositioned {
                confirmationPopupButtonsHeight.value = with(localDensity) {
                    it.size.height.toDp()
                }
            }
            .zIndex(-100f) // because should not be visible on the screen
            .alpha(0f)
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = logout_button_label,
                style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .padding(top = generalPadding)
            )

            Text(
                text = "Not backed up expenses will be removed permanently.",
                textAlign = TextAlign.Center,
                style = buttonDescriptionTextStyle.copy(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .padding(bottom = generalPadding, top = halfGeneralPadding)
            )
        }
    }

    // <--- End

    GeneralDialog(
        showDialog = uiState.showLogoutPopUp,
        dialogProperties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .generalBorder()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "You want to Logout\nAre you sure?",
                textAlign = TextAlign.Center,
                maxLines = 2,
                style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .padding(generalPadding)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = generalPadding)
                    .padding(bottom = generalPadding)
            ) {
                GeneralButton(text = "CANCEL", modifier = Modifier.weight(1f)) {
                    uiState.showLogoutPopUp.value = false
                }

                Spacer(modifier = Modifier.width(generalPadding))

                GeneralButton(text = "YES", modifier = Modifier.weight(1f)) {
                    uiState.showLogoutPopUp.value = false
                    logout()
                }
            }
        }
    }

    GeneralDialog(
        showDialog = uiState.showConfirmationPopUp,
        dialogProperties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .generalBorder()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Warning!\nYou have some expense which are not backed up yet.",
                textAlign = TextAlign.Center,
                style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .padding(generalPadding)
            )

            Button(
                onClick = {
                    uiState.showConfirmationPopUp.value = false
                    logoutConfirmation(true)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = generalPadding),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = Color(0xFFFF204E)
                ),
                shape = roundedCornerShape
            ) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = logout_button_label,
                        style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier
                            .padding(top = generalPadding)
                    )

                    Text(
                        text = "Not backed up expenses will be removed permanently.",
                        textAlign = TextAlign.Center,
                        style = buttonDescriptionTextStyle.copy(color = MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier
                            .padding(bottom = generalPadding, top = halfGeneralPadding)
                    )
                }
            }

            Spacer(modifier = Modifier.height(generalPadding))

            if (confirmationPopupButtonsHeight.value != 0.dp) {
                Button(
                    onClick = {
                        uiState.showConfirmationPopUp.value = false
                        logoutConfirmation(false)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(confirmationPopupButtonsHeight.value)
                        .padding(horizontal = generalPadding),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = Color(0xFFA2FF86).copy(alpha = 0.75f)
                    ),
                    shape = roundedCornerShape
                ) {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "BACKUP",
                            style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                            modifier = Modifier
                                .padding(top = generalPadding)
                        )

                        Text(
                            text = "Backup before logout.",
                            style = buttonDescriptionTextStyle.copy(color = MaterialTheme.colorScheme.onBackground),
                            modifier = Modifier
                                .padding(bottom = generalPadding, top = halfGeneralPadding)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(generalPadding))

        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileSection(
    user: User? = null,
    showImagePickerOption: MutableState<Boolean>,
    profilePicBitmap: MutableState<Bitmap?>
) {
    var name = "John Doe"
    var mobile = "1234567890"
    var email = "johndoe@example.com"

    if (user != null && user.name.isNotEmpty()) {
        name = user.name
    }
    if (user != null && user.phone.isNotEmpty()) {
        mobile = user.phone
    }
    if (user != null && user.email.isNotEmpty()) {
        email = user.email
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(bottom = generalPadding, top = halfGeneralPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(halfGeneralPadding)
                .clip(shape = CircleShape)
                .clickable {
                    showImagePickerOption.value = true
                }
                .background(Color.LightGray)
        ) {
            // Profile Picture using Glide.
//            GlideImage(
//                model = profilePicBitmap.value,
//                contentDescription = "profile picture",
//                loading = placeholder(R.drawable.ic_person),
//                failure = placeholder(R.drawable.ic_person),
//                transition = CrossFade,
//                modifier = Modifier
//                    .size(120.dp)
//            )
            
            // Profile Picture using Coil.
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profilePicBitmap.value)
                    .crossfade(true)
                    .build(),
                contentDescription = "profile image",
                placeholder = painterResource(id = R.drawable.ic_person),
                error = painterResource(id = R.drawable.ic_person),
                modifier = Modifier
                    .size(120.dp)
            )

            // Image changing button.
            Text(
                text = "Edit",
                style = Typography.bodySmall.copy(color = MaterialTheme.colorScheme.background),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(120.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color.LightGray.copy(alpha = 0.5f))
                    .padding(bottom = 5.dp)
            )
        }

        // User Name
        Text(
            text = name,
            textAlign = TextAlign.Center,
            style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
        )
        
        // User Phone number
        Text(
            text = "+91 $mobile",
            textAlign = TextAlign.Center,
            style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
        )
        
        // User Email
        Text(
            text = email,
            textAlign = TextAlign.Center,
            style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
        )
    }
}

@Composable
fun ImagePickerSection(
    modifier: Modifier,
    saveImageToStorage: (Uri) -> Deferred<Bitmap?>?,
    uploadUserProfilePicture: suspend (Bitmap) -> Unit
    ) {
    val context = LocalContext.current
    val includeGallery = remember { mutableStateOf(false) }
    val launcher = Utility.imagePicker {
        io {
            val bitmap = saveImageToStorage(it)
            bitmap?.await()?.let { it1 -> uploadUserProfilePicture(it1) }
        }
    }

    // Launcher for requesting image from user's gallery or files.
    val requestLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            if (it) {
                Utility.imageCropLauncher(launcher, includeGallery.value)
            }
        }
    
    Column(
        modifier = Modifier
            .height(250.dp)
    ) {
        Spacer(modifier = Modifier
            .height(0.5.dp)
            .fillMaxWidth()
            .background(Color.White))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .then(modifier),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .generalCircleBorder(size = 100.dp)
                    .clickable {
                        includeGallery.value = false
                        if (context.checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestLauncher.launch(android.Manifest.permission.CAMERA)
                        } else {
                            Utility.imageCropLauncher(launcher)
                        }
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(generalPadding.times(2)))
            
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .generalCircleBorder(size = 100.dp)
                    .clickable {
                        includeGallery.value = true
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (context.checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                                requestLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                            } else {
                                Utility.imageCropLauncher(launcher, true)
                            }
                        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                            if (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            } else {
                                Utility.imageCropLauncher(launcher, true)
                            }
                        }
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_gallery),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(24.dp)
                )
            }
        }
    }
    
}


//@Preview(
//    apiLevel = 34,
//    uiMode = Configuration.UI_MODE_NIGHT_YES
//)
//@Preview(
//    apiLevel = 34,
//    uiMode = Configuration.UI_MODE_NIGHT_NO
//)
//@Composable
//private fun PreviewSetting() {
//    AccountsThemes {
//        MainUI(user = User().copy(name = "Roop Kumar"), currentScreen = null, navigateUp = {true})
//    }
//}
