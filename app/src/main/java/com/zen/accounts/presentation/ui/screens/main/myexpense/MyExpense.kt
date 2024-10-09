package com.zen.accounts.presentation.ui.screens.main.myexpense

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.zen.accounts.R
import com.zen.accounts.data.db.dao.ExpenseWithOperation
import com.zen.accounts.data.db.model.Expense
import com.zen.accounts.presentation.ui.navigation.Screen
import com.zen.accounts.presentation.ui.screens.common.LoadingDialog
import com.zen.accounts.presentation.ui.screens.common.TopAppBar
import com.zen.accounts.presentation.ui.screens.common.date_formatter_pattern_without_time
import com.zen.accounts.presentation.ui.screens.common.getRupeeString
import com.zen.accounts.presentation.ui.screens.main.expense_detail.ExpenseItemDeleteDialog
import com.zen.accounts.presentation.ui.theme.Typography
import com.zen.accounts.presentation.ui.theme.generalPadding
import com.zen.accounts.presentation.ui.theme.halfGeneralPadding
import com.zen.accounts.presentation.ui.theme.primary_color
import com.zen.accounts.presentation.ui.theme.secondary_color
import com.zen.accounts.presentation.ui.theme.tweenAnimDuration
import com.zen.accounts.presentation.ui.viewmodels.MyExpenseViewModel
import com.zen.accounts.presentation.utility.generalBorder
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date

private class LoadingState(initialList : List<ExpenseWithOperation>) {
    var list : SnapshotStateList<ExpenseWithOperation> =
        mutableStateListOf<ExpenseWithOperation>().apply { addAll(initialList) }
        private set
    var errorMsg : String? by mutableStateOf(null)
        private set
    fun updateErrorMsg(value: String) {
        errorMsg = value
    }
    fun updateList(newValue : List<ExpenseWithOperation>) {
        list.clear()
        list.addAll(newValue)
    }
    val isLoading = list.isEmpty()
    val isSuccess = list.isNotEmpty()
    val isError = errorMsg != null
    companion object {
        val Saver : Saver<LoadingState, *> = mapSaver(
            save = { mapOf("list" to it.list) },
            restore = { LoadingState((it["list"] as List<ExpenseWithOperation>))) }
        )
    }
}

@Composable
private fun getLoadingState(list : List<ExpenseWithOperation>) : LoadingState =
    rememberSaveable(list) {
        LoadingState(list)
    }

@Composable
fun MyExpense(
    drawerState : MutableState<DrawerState?>?,
    viewModel : MyExpenseViewModel,
    isMonthlyExpense : Boolean,
    navigateUp : () -> Boolean,
    currentScreen : Screen?,
    navigateTo : (Expense) -> Unit
) {
    viewModel.isMonthlyCalled = isMonthlyExpense
    val allExpense =
        if (!isMonthlyExpense) viewModel.allExpense
        else viewModel.monthlyExpense
    val loadingState = getLoadingState(list = allExpense)
    LoadingDialog(showDialog = getLoadingState(list = allExpense).isLoading)
    MyExpense(
        loadingState,
        drawerState,
        viewModel::deleteExpenses,
        navigateTo,
        navigateUp
    )
    
}

@Composable
private fun MyExpense(
    loadingState : LoadingState,
    drawerState : MutableState<DrawerState?>?,
    deleteExpenses : (List<Long>) -> Unit,
    navigateTo : (Expense) -> Unit,
    navigateUp : () -> Boolean
) {
    Crossfade(targetState = loadingState.list, label = "Main screen cross-fade.") { target ->
        if(loadingState.isLoading) {
            // Handle the loading activity.
            CircularProgressIndicator()
        }
        else if (loadingState.isSuccess) {
            // Handle Success activity.
            // Use target as list.
            SuccessScreen(
                list = target,
                drawerState,
                deleteExpenses,
                navigateUp,
                navigateTo
            )
        }
        else if (loadingState.isError) {
            // Handle Error activity.
            ErrorComp()
        }
    }
}

@Composable
private fun SuccessScreen(
    list: List<ExpenseWithOperation>,
    drawerState : MutableState<DrawerState?>?,
    deleteExpenses : (List<Long>) -> Unit,
    navigateUp : () -> Boolean,
    navigateTo : (Expense) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val isShowList by rememberSaveable {
        derivedStateOf {
            list.isNotEmpty()
        }
    }
    var showDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var selectAll by rememberSaveable {
        mutableStateOf(false)
    }
    var showCheckBoxes by remember {
        mutableStateOf(false)
    }
    val checkBoxSet = rememberSaveable {
        mutableSetOf<Long>()
    }
    
    BackHandler(
        selectAll
    ) {
        showCheckBoxes = false
    }
    
    val interactionSource = remember { MutableInteractionSource() }
    val longPressed =
        interactionSource.collectIsPressedAsState()
    if (longPressed.value) {
        showCheckBoxes = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            drawerState = drawerState,
            navigateUp = navigateUp,
            currentScreen = Screen.MyExpenseScreen
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {}
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isShowList) {
                var rotation by remember { mutableFloatStateOf(0f) }
                val infiniteTransition = rememberInfiniteTransition(label = "")
                rotation = infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ), label = ""
                ).value
                
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_sync),
                            contentDescription = "Rotating Icon",
                            tint = primary_color,
                            modifier = Modifier
                                .size(48.dp)
                                .graphicsLayer {
                                    rotationZ = rotation
                                }
                        )
                        
                        Text(
                            text = "Syncing.....",
                            textAlign = TextAlign.Center,
                            style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                        )
                    }
                }
            } else {
                Crossfade(
                    targetState = list.isNotEmpty(),
                    label = "my expense screen",
                ) { allExpenseIsNoEmpty ->
                    when (allExpenseIsNoEmpty) {
                        true -> {
                            val animateShowList = remember { mutableStateOf(false) }
                            val screenHeight = LocalConfiguration.current.screenHeightDp
                            LaunchedEffect(key1 = Unit) {
                                delay(100)
                                animateShowList.value = true
                            }
                            Column {
                                ExpenseItemDeleteDialog(
                                    showDialog = showDeleteDialog,
                                    onYes = {
                                        deleteExpenses(emptyList())
                                        showDeleteDialog = false
                                    },
                                    onNo = {
                                        showDeleteDialog = false
                                    }
                                )
                                
                                Column {
                                    AnimatedVisibility(
                                        visible = showCheckBoxes,
                                        enter = fadeIn() + slideInVertically(tween(300)) { value -> -1 * value },
                                        exit = fadeOut() + slideOutVertically(tween(300)) { value -> -1 * value }
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = generalPadding),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            
                                            Checkbox(
                                                checked = selectAll,
                                                onCheckedChange = {
                                                    if (it) {
                                                        checkBoxSet.addAll(list.map { expenseWithOperation ->  expenseWithOperation.id })
                                                    } else {
                                                        checkBoxSet.clear()
                                                    }
                                                    selectAll = false
                                                },
                                                colors = CheckboxDefaults.colors().copy(
                                                    checkedBoxColor = primary_color,
                                                    uncheckedBoxColor = MaterialTheme.colorScheme.background,
                                                    checkedCheckmarkColor = Color.White,
                                                    uncheckedCheckmarkColor = Color.Gray,
                                                    checkedBorderColor = MaterialTheme.colorScheme.background,
                                                    uncheckedBorderColor = Color.Gray
                                                )
                                            )
                                            
                                            Text(
                                                text = if (selectAll) "Deselect All" else "Select All",
                                                style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                                            )
                                            
                                            Spacer(modifier = Modifier.weight(1f))
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .clickable {
                                                        showDeleteDialog = true
                                                    }
                                                    .background(secondary_color)
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_bin),
                                                    contentDescription = "delete icon",
                                                    modifier = Modifier
                                                        .align(Alignment.Center)
                                                        .padding(8.dp),
                                                    tint = primary_color
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(generalPadding))
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .clickable {
                                                        showCheckBoxes = false
                                                        selectAll = false
                                                    }
                                                    .background(secondary_color)
                                            ) {
                                                Icon(
                                                    painter = painterResource(id = R.drawable.ic_close),
                                                    contentDescription = "delete icon",
                                                    modifier = Modifier
                                                        .align(Alignment.Center)
                                                        .padding(6.dp),
                                                    tint = primary_color
                                                )
                                            }
                                        }
                                    }
                                    
                                    AnimatedVisibility(
                                        visible = animateShowList.value,
                                        enter = slideInVertically(tween(tweenAnimDuration - 100)) { it + screenHeight },
                                        exit = slideOutVertically(tween(tweenAnimDuration)) { screenHeight }
                                    ) {
                                        val tempModifier = Modifier
                                            .fillMaxSize()
                                            .padding(generalPadding)
                                            .generalBorder()
                                            .padding(
                                                end = generalPadding,
                                                top = halfGeneralPadding,
                                                bottom = halfGeneralPadding
                                            )
                                        if (screenWidth <= 500) {
                                            LazyColumn(
                                                modifier = tempModifier
                                            ) {
                                                items(
                                                    list,
                                                    key = { it.id }) { expense ->
                                                    ListItemLayout(
                                                        interactionSource,
                                                        expense = expense,
                                                        showCheckBox = showCheckBoxes,
                                                        onItemClick = {
                                                            if (showCheckBoxes) {
                                                                if (checkBoxSet.contains(expense.id)) {
                                                                    checkBoxSet.remove(expense.id)
                                                                } else {
                                                                    checkBoxSet.add(expense.id)
                                                                }
                                                            } else {
                                                                navigateTo(expense.toExpense())
                                                            }
                                                        },
                                                        onCheckChange =  { value ->
                                                            if (!value) {
                                                                checkBoxSet.remove(expense.id)
                                                            } else {
                                                                checkBoxSet.add(expense.id)
                                                            }
                                                        },
                                                        navigateTo = navigateTo,
                                                    )
                                                }
                                                
                                            }
                                        } else {
                                            LazyVerticalGrid(
                                                columns = GridCells.Fixed(2),
                                                modifier = tempModifier
                                            ) {
                                                items(
                                                    list,
                                                    key = { it.id }) { expense ->
                                                    ListItemLayout(
                                                        interactionSource,
                                                        expense = expense,
                                                        showCheckBox = showCheckBoxes,
                                                        onItemClick = {
                                                            if (showCheckBoxes) {
                                                                if (checkBoxSet.contains(expense.id)) {
                                                                    checkBoxSet.remove(expense.id)
                                                                } else {
                                                                    checkBoxSet.add(expense.id)
                                                                }
                                                            } else {
                                                                navigateTo(expense.toExpense())
                                                            }
                                                        },
                                                        onCheckChange = {value ->
                                                            if (!value) {
                                                                checkBoxSet.remove(expense.id)
                                                            } else {
                                                                checkBoxSet.add(expense.id)
                                                            }
                                                        },
                                                        navigateTo = navigateTo,
                                                    )
                                                }
                                                
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        false -> {
                            val lottieComposition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(
                                    R.raw.empty_list
                                )
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                
                                LottieAnimation(
                                    composition = lottieComposition,
                                    modifier = Modifier
                                        .size(350.dp, 350.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Text(
                                    text = "No items added!",
                                    style = Typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorComp() {
    // doing something with list.
}

private fun dateString(date : Date) : String {
    val formatter = SimpleDateFormat(date_formatter_pattern_without_time, java.util.Locale.UK)
    return formatter.format(date)
}

@Composable
private fun ListItemLayout(
    interactionSource : MutableInteractionSource,
    expense : ExpenseWithOperation,
    showCheckBox : Boolean,
    onItemClick : () -> Unit,
    onCheckChange : (Boolean) -> Unit,
    navigateTo : (Expense) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(
                top = halfGeneralPadding,
                start = generalPadding,
                bottom = halfGeneralPadding
            )
            .generalBorder()
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = {
                    onItemClick()
                }
            )
            .background(MaterialTheme.colorScheme.secondary)
            .padding(vertical = generalPadding)
            .padding(end = generalPadding),
        verticalAlignment = Alignment.CenterVertically
    
    ) {
        
        AnimatedVisibility(
            visible = showCheckBox
        ) {
            Checkbox(
                checked = showCheckBox,
                onCheckedChange = { value ->
                    onCheckChange(value)
                },
                colors = CheckboxDefaults.colors()
                    .copy(
                        checkedBoxColor = MaterialTheme.colorScheme.background,
                        uncheckedBoxColor = MaterialTheme.colorScheme.background,
                        checkedCheckmarkColor = primary_color,
                        uncheckedCheckmarkColor = Color.Gray,
                        checkedBorderColor = primary_color,
                        uncheckedBorderColor = Color.Gray
                    )
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = if (showCheckBox) 0.dp else generalPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_clock),
                        contentDescription = "sync icon",
                        modifier = Modifier
                            .size(12.dp),
                        tint = if (isSystemInDarkTheme()) Color.White else Color(0xFF8C8C8C)
                    )
                    Spacer(modifier = Modifier.width(halfGeneralPadding))
                    Text(
                        text = dateString(expense.date),
                        style = Typography.bodySmall.copy(
                            color = if (isSystemInDarkTheme()) Color.White else Color.Gray
                        )
                    )
                }
                
                
                Row(
                    modifier = Modifier
                        .alpha(if (expense.operation != null && expense.operation?.isNotEmpty()!!) 1f else 0f)
                        .then(
                            if (isSystemInDarkTheme())
                                Modifier.border(0.5.dp, color = primary_color, shape = CircleShape)
                            else
                                Modifier
                        )
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = generalPadding, vertical = halfGeneralPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sync),
                        contentDescription = "sync icon",
                        modifier = Modifier
                            .size(12.dp),
                        tint = secondary_color
                    )
                    Spacer(modifier = Modifier.width(halfGeneralPadding))
                    Text(
                        text = expense.operation.toString().capitalize(Locale.current),
                        style = Typography.bodySmall.copy(color = Color.White)
                    )
                }
                
            }
            
            Spacer(modifier = Modifier.height(halfGeneralPadding))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                
                Text(
                    text = expense.title,
                    style = Typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                )
                
                Text(
                    text = getRupeeString(expense.totalAmount),
                    style = Typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                
            }
        }
        
    }
}
