package com.zen.accounts.ui.screens.main.myexpense

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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.zen.accounts.R
import com.zen.accounts.db.dao.ExpenseWithOperation
import com.zen.accounts.ui.navigation.Screen
import com.zen.accounts.ui.screens.common.LoadingDialog
import com.zen.accounts.ui.screens.common.LoadingState
import com.zen.accounts.ui.screens.common.TopAppBar
import com.zen.accounts.ui.screens.common.date_formatter_pattern_without_time
import com.zen.accounts.ui.screens.common.getRupeeString
import com.zen.accounts.ui.screens.main.expense_detail.ExpenseItemDeleteDialog
import com.zen.accounts.ui.theme.Typography
import com.zen.accounts.ui.theme.generalPadding
import com.zen.accounts.ui.theme.halfGeneralPadding
import com.zen.accounts.ui.theme.primary_color
import com.zen.accounts.ui.theme.secondary_color
import com.zen.accounts.ui.theme.tweenAnimDuration
import com.zen.accounts.ui.viewmodels.MyExpenseViewModel
import com.zen.accounts.utility.generalBorder
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date

data class MyExpenseUiStateHolder(
    var expenseItemListAmountTextWidth: Dp = 0.dp,
    var showSelectCheckbox: Boolean = false,
    var checkBoxMap: HashMap<Long, Boolean> = hashMapOf(),
    var selectAll: Boolean = false,
    var loadingState: LoadingState = LoadingState.IDLE,
    var showDeleteDialog: Boolean = false,
    var showExpenseList: Boolean = false,
)

const val MyExpenseUiStateHolderAmountTextWidth = "expenseItemListAmountTextWidth"
const val MyExpenseUiStateHolderShowSelectCheckBox = "showSelectCheckbox"
const val MyExpenseUiStateHolderCheckBoxList = "checkBoxMap"
const val MyExpenseUiStateHolderSelectAll = "selectAll"
const val MyExpenseUiStateHolderLoadingState = "loadingState"
const val MyExpenseUiStateHolderShowDeleteDialog = "showDeleteDialog"
const val MyExpenseUiStateHolderShowExpenseList = "showExpenseList"

@Composable
fun MyExpense(
    drawerState: MutableState<DrawerState?>?,
    viewModel: MyExpenseViewModel,
    isMonthlyExpense: Boolean,
    navigateUp: () -> Boolean,
    currentScreen: Screen?,
    navigateTo: (String) -> Unit
) {
    viewModel.isMonthlyCalled = isMonthlyExpense
    val myExpenseUiState by viewModel.myExpenseUiStateFlow.collectAsState()

    val allExpense =
        if (!isMonthlyExpense) viewModel.allExpense.collectAsState()
        else viewModel.allExpense.collectAsState()

    BackHandler(
        myExpenseUiState.showSelectCheckbox
    ) {
        viewModel.updateMyExpenseUiState(false, MyExpenseUiStateHolderShowSelectCheckBox)
    }

    LoadingDialog(loadingState = myExpenseUiState.loadingState)
    
    LaunchedEffect(key1 = Unit) {
        viewModel.getAllExpenses()
    }

    LaunchedEffect(key1 = allExpense.value) {
        delay(500)
        viewModel.updateMyExpenseUiState(true, MyExpenseUiStateHolderShowExpenseList)
    }

    MainUi(
        myExpenseUiState = myExpenseUiState,
        updateMyExpenseUiState = viewModel::updateMyExpenseUiState,
        drawerState = drawerState,
        deleteExpenses = viewModel::deleteExpenses,
        allExpense = allExpense.value,
        navigateUp = navigateUp,
        currentScreen = currentScreen,
        navigateTo = navigateTo
    )

}

@Composable
private fun MainUi(
    myExpenseUiState: MyExpenseUiStateHolder,
    updateMyExpenseUiState: (Any, String) -> Unit,
    drawerState: MutableState<DrawerState?>?,
    deleteExpenses: (List<Long>) -> Unit,
    allExpense: List<ExpenseWithOperation>,
    navigateUp: () -> Boolean,
    currentScreen: Screen?,
    navigateTo: (String) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            drawerState = drawerState,
            navigateUp = navigateUp,
            currentScreen = currentScreen
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {}
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (!myExpenseUiState.showExpenseList) {
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
                    targetState = allExpense.isNotEmpty(),
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
                                    showDialog = myExpenseUiState.showDeleteDialog,
                                    onYes = {
                                        deleteExpenses(
                                            myExpenseUiState.checkBoxMap.keys.toList()
                                        )
                                        updateMyExpenseUiState(
                                            false,
                                            MyExpenseUiStateHolderShowDeleteDialog
                                        )
                                    },
                                    onNo = {
                                        updateMyExpenseUiState(
                                            false,
                                            MyExpenseUiStateHolderShowDeleteDialog
                                        )
                                    }
                                )

                                Column {
                                    AnimatedVisibility(
                                        visible = myExpenseUiState.showSelectCheckbox,
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
                                                checked = myExpenseUiState.selectAll,
                                                onCheckedChange = {
                                                    if (it) {
                                                        allExpense.forEach { exp ->
                                                            val temp = myExpenseUiState.checkBoxMap
                                                            temp[exp.id] = true
                                                            updateMyExpenseUiState(
                                                                temp,
                                                                MyExpenseUiStateHolderCheckBoxList
                                                            )
                                                        }
                                                    } else {
                                                        updateMyExpenseUiState(
                                                            hashMapOf<Long, String>(),
                                                            MyExpenseUiStateHolderCheckBoxList
                                                        )
                                                    }
                                                    updateMyExpenseUiState(
                                                        it,
                                                        MyExpenseUiStateHolderSelectAll
                                                    )
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
                                                text = if (myExpenseUiState.selectAll) "Deselect All" else "Select All",
                                                style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                                            )

                                            Spacer(modifier = Modifier.weight(1f))
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .clip(CircleShape)
                                                    .clickable {
                                                        updateMyExpenseUiState(
                                                            true,
                                                            MyExpenseUiStateHolderShowDeleteDialog
                                                        )
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
                                                        updateMyExpenseUiState(
                                                            false,
                                                            MyExpenseUiStateHolderShowSelectCheckBox
                                                        )
                                                        updateMyExpenseUiState(
                                                            false,
                                                            MyExpenseUiStateHolderSelectAll
                                                        )
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
                                                    allExpense,
                                                    key = { it.id }) { expense ->
                                                    ListItemLayout(
                                                        expense = expense,
                                                        navigateTo = navigateTo,
                                                        updateMyExpenseUiState = updateMyExpenseUiState,
                                                        myExpenseUiState = myExpenseUiState
                                                    )
                                                }

                                            }
                                        } else {
                                            LazyVerticalGrid(
                                                columns = GridCells.Fixed(2),
                                                modifier = tempModifier
                                            ) {
                                                items(
                                                    allExpense,
                                                    key = { it.id }) { expense ->
                                                    ListItemLayout(
                                                        expense = expense,
                                                        navigateTo = navigateTo,
                                                        updateMyExpenseUiState = updateMyExpenseUiState,
                                                        myExpenseUiState = myExpenseUiState
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

private fun dateString(date: Date): String {
    val formatter = SimpleDateFormat(date_formatter_pattern_without_time, java.util.Locale.UK)
    return formatter.format(date)
}

@Composable
private fun ListItemLayout(
    myExpenseUiState: MyExpenseUiStateHolder,
    expense: ExpenseWithOperation,
    navigateTo: (String) -> Unit,
    updateMyExpenseUiState: (Any, String) -> Unit
) {
    val interactionSource =
        remember { MutableInteractionSource() }
    val longPressed =
        interactionSource.collectIsPressedAsState()
    if (longPressed.value) {
        updateMyExpenseUiState(true, MyExpenseUiStateHolderShowSelectCheckBox)
    }
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
                    if (myExpenseUiState.showSelectCheckbox) {
                        if (myExpenseUiState.checkBoxMap[expense.id] == true) {
                            val temp = myExpenseUiState.checkBoxMap
                            temp.remove(expense.id)
                            updateMyExpenseUiState(temp, MyExpenseUiStateHolderCheckBoxList)
                        } else {
                            val temp = myExpenseUiState.checkBoxMap
                            temp[expense.id] = true
                            updateMyExpenseUiState(temp, MyExpenseUiStateHolderCheckBoxList)
                        }
                    } else {
                        navigateTo(Screen.ExpenseDetailScreen.getRoute(expense.toExpense()))
                    }
                }
            )
            .background(MaterialTheme.colorScheme.secondary)
            .padding(vertical = generalPadding)
            .padding(end = generalPadding),
        verticalAlignment = Alignment.CenterVertically

    ) {

        AnimatedVisibility(
            visible = myExpenseUiState.showSelectCheckbox
        ) {
            Checkbox(
                checked = myExpenseUiState.checkBoxMap[expense.id] ?: false,
                onCheckedChange = {
                    if (myExpenseUiState.checkBoxMap[expense.id] == true) {
                        val temp = myExpenseUiState.checkBoxMap
                        temp.remove(expense.id)
                        updateMyExpenseUiState(temp, MyExpenseUiStateHolderCheckBoxList)
                    } else {
                        val temp = myExpenseUiState.checkBoxMap
                        temp[expense.id] = true
                        updateMyExpenseUiState(temp, MyExpenseUiStateHolderCheckBoxList)
                    }
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
                .padding(start = if (myExpenseUiState.showSelectCheckbox) 0.dp else generalPadding)
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
                        tint = if(isSystemInDarkTheme()) Color.White else Color(0xFF8C8C8C)
                    )
                    Spacer(modifier = Modifier.width(halfGeneralPadding))
                    Text(
                        text = dateString(expense.date),
                        style = Typography.bodySmall.copy(
                            color = if(isSystemInDarkTheme()) Color.White else Color.Gray
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
