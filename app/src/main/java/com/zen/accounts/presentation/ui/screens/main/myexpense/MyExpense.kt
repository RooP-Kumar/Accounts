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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.zen.accounts.R
import com.zen.accounts.data.db.dao.ExpenseWithOperation
import com.zen.accounts.data.db.model.Expense
import com.zen.accounts.presentation.ui.navigation.Screen
import com.zen.accounts.presentation.ui.screens.common.TopBarBackButton
import com.zen.accounts.presentation.ui.screens.common.date_formatter_pattern_without_time
import com.zen.accounts.presentation.ui.screens.common.getRupeeString
import com.zen.accounts.presentation.ui.screens.main.expense_detail.ExpenseItemDeleteDialog
import com.zen.accounts.presentation.ui.theme.Typography
import com.zen.accounts.presentation.ui.theme.generalPadding
import com.zen.accounts.presentation.ui.theme.halfGeneralPadding
import com.zen.accounts.presentation.ui.theme.primary_color
import com.zen.accounts.presentation.ui.theme.roundedCornerShape
import com.zen.accounts.presentation.ui.theme.secondary_color
import com.zen.accounts.presentation.ui.theme.topBarHeight
import com.zen.accounts.presentation.ui.viewmodels.MyExpenseViewModel
import com.zen.accounts.presentation.utility.generalBorder
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date

sealed class ScreenLoadingState<out T>() {
    data object Loading : ScreenLoadingState<Nothing>()
    data class Success<T>(var value : T) : ScreenLoadingState<T>()
    data class Error(var msg : String) : ScreenLoadingState<Nothing>()
}

data class MyExpenseState(
    val screenLoadingState : ScreenLoadingState<List<ExpenseWithOperation>>,
    var showDeleteDialog : Boolean = false,
    var selectAll : Boolean = false,
    var showCheckBoxes : Boolean = false,
    var checkBoxSet : Set<Long> = setOf(),
)

@Composable
fun MyExpense(
    drawerState : MutableState<DrawerState?>?,
    viewModel : MyExpenseViewModel,
    isMonthlyExpense : Boolean = false,
    navigateUp : () -> Boolean,
    navigateTo : (Expense) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.fetchList(isMonthlyExpense)
    }
    val uiState = viewModel.myExpenseUiState.collectAsStateWithLifecycle()
    
    BackHandler(
        uiState.value.showCheckBoxes
    ) {
        viewModel.updateShowCheckBoxes(false)
        viewModel.updateSelectAll(false)
        viewModel.updateCheckBoxSet(setOf())
    }
    
    MyExpense(
        drawerState,
        uiState.value,
        viewModel::deleteExpenses,
        navigateTo,
        navigateUp
    ) { selectAll : Boolean?, showCheckBoxes : Boolean?, showDeleteDialog : Boolean?, checkBoxSet : Set<Long>? ->
        selectAll?.let { viewModel.updateSelectAll(it) }
        showCheckBoxes?.let { viewModel.updateShowCheckBoxes(it) }
        showDeleteDialog?.let { viewModel.updateShowDeleteDialog(it) }
        checkBoxSet?.let { viewModel.updateCheckBoxSet(it) }
    }
}

@Composable
private fun MyExpense(
    drawerState : MutableState<DrawerState?>?,
    uiState : MyExpenseState,
    deleteExpenses : (List<Long>) -> Unit,
    navigateTo : (Expense) -> Unit,
    navigateUp : () -> Boolean,
    updateFunction : (Boolean?, Boolean?, Boolean?, Set<Long>?) -> Unit
) {
    Crossfade(
        targetState = uiState.screenLoadingState,
        label = "Cross-fade animation layout"
    ) { target ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            MyExpenseTopAppBar(
                if (!uiState.showCheckBoxes) Screen.MyExpenseScreen.title else if (uiState.checkBoxSet.isNotEmpty()) "${uiState.checkBoxSet.size} Selected" else "Select Expenses",
                uiState.selectAll,
                uiState.showCheckBoxes,
                navigateUp,
                onCheckChange = {
                    if (it) {
                        updateFunction(
                            true,
                            null,
                            null,
                            uiState.checkBoxSet + ( (uiState.screenLoadingState as ScreenLoadingState.Success).value ).map { expenseWithOperation -> expenseWithOperation.id })
                    } else {
                        updateFunction(false, null, null, setOf())
                    }
                }
            )
            
            when (target) {
                is ScreenLoadingState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            RotatingSyncImage()
                            Text(
                                text = "Syncing.....",
                                textAlign = TextAlign.Center,
                                style = Typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                            )
                        }
                    }
                }
                
                is ScreenLoadingState.Success -> {
                    // Handle Success State
                    if (target.value.isEmpty()) {
                        // Handle Empty List
                        val lottieComposition by rememberLottieComposition(
                            LottieCompositionSpec.RawRes(
                                R.raw.empty_list
                            )
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LottieAnimation(
                                composition = lottieComposition,
                                modifier = Modifier
                                    .size(350.dp, 350.dp),
                                contentScale = ContentScale.Fit
                            )
                            Text(
                                text = "List is Empty.",
                                style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground)
                            )
                        }
                    } else {
                        // Handle Non-empty list
                        SuccessScreen(
                            list = target.value,
                            drawerState,
                            uiState,
                            deleteExpenses,
                            navigateUp,
                            navigateTo,
                            updateFunction
                        )
                    }
                }
                
                is ScreenLoadingState.Error -> {
                    ErrorComp()
                }
            }
        }
    }
}

@Composable
private fun SuccessScreen(
    list : List<ExpenseWithOperation>,
    drawerState : MutableState<DrawerState?>?,
    uiState : MyExpenseState,
    deleteExpenses : (List<Long>) -> Unit,
    navigateUp : () -> Boolean,
    navigateTo : (Expense) -> Unit,
    updateFunction : (Boolean?, Boolean?, Boolean?, Set<Long>?) -> Unit
) {
    val screenWidth = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp
    }
    val layoutType = if (screenWidth >= 500) "grid" else "list"
    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {}
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center
    ) {
        fun handleSelects(id : Long) {
            val temp = uiState.checkBoxSet.toMutableSet()
            if (!temp.contains(id)) {
                temp.add(id)
            } else {
                temp.remove(id)
            }
            updateFunction(temp.size == list.size, null, null, temp)
        }
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
                            showDialog = uiState.showDeleteDialog,
                            onYes = {
                                if (uiState.checkBoxSet.size == list.size) {
                                    updateFunction(false, false, false, null)
                                } else {
                                    updateFunction(null, null, false, null)
                                }
                                deleteExpenses(uiState.checkBoxSet.toList())
                            },
                            onNo = {
                                updateFunction(null, null, false, null)
                            }
                        )
                        
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            val tempModifier = Modifier
                                .padding(generalPadding)
                                .weight(1f)
                                .generalBorder()
                                .padding(
                                    end = generalPadding,
                                    top = halfGeneralPadding,
                                    bottom = halfGeneralPadding
                                )
                            when (layoutType) {
                                "list" -> {
                                    LazyColumn(
                                        modifier = tempModifier
                                    ) {
                                        items(
                                            list,
                                            key = { it.id }) { expense ->
                                            val interactionSource =
                                                remember { MutableInteractionSource() }
                                            val longPressed =
                                                interactionSource.collectIsPressedAsState()
                                            if (longPressed.value) {
                                                updateFunction(null, true, null, null)
                                            }
                                            
                                            ListItemLayout(
                                                interactionSource,
                                                expense = expense,
                                                showCheckBox = uiState.showCheckBoxes,
                                                selected = uiState.checkBoxSet.contains(
                                                    expense.id
                                                ),
                                                onItemClick = {
                                                    if (uiState.showCheckBoxes) {
                                                        handleSelects(expense.id)
                                                    } else {
                                                        navigateTo(expense.toExpense())
                                                    }
                                                },
                                                onCheckChange = {
                                                    handleSelects(expense.id)
                                                },
                                                navigateTo = navigateTo,
                                            )
                                        }
                                        
                                    }
                                }
                                
                                "grid" -> {
                                    LazyVerticalGrid(
                                        columns = GridCells.Fixed(2),
                                        modifier = tempModifier
                                    ) {
                                        items(
                                            list,
                                            key = { it.id }) { expense ->
                                            val interactionSource =
                                                remember { MutableInteractionSource() }
                                            val longPressed =
                                                interactionSource.collectIsPressedAsState()
                                            if (longPressed.value) {
                                                updateFunction(null, true, null, null)
                                            }
                                            
                                            ListItemLayout(
                                                interactionSource,
                                                expense = expense,
                                                showCheckBox = uiState.showCheckBoxes,
                                                selected = uiState.checkBoxSet.contains(
                                                    expense.id
                                                ),
                                                onItemClick = {
                                                    if (uiState.showCheckBoxes) {
                                                        handleSelects(expense.id)
                                                    } else {
                                                        navigateTo(expense.toExpense())
                                                    }
                                                },
                                                onCheckChange = {
                                                    handleSelects(expense.id)
                                                },
                                                navigateTo = navigateTo,
                                            )
                                        }
                                        
                                    }
                                }
                            }
                            
                            AnimatedVisibility(visible = uiState.showCheckBoxes) {
                                Row {
                                    Text(
                                        text = "Cancel",
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                updateFunction(
                                                    false,
                                                    false,
                                                    null,
                                                    setOf()
                                                )
                                            }
                                            .background(MaterialTheme.colorScheme.secondary)
                                            .padding(vertical = generalPadding)
                                    )
                                    Spacer(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .background(MaterialTheme.colorScheme.background)
                                    )
                                    Text(
                                        text = "Delete",
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                updateFunction(
                                                    null,
                                                    null,
                                                    true,
                                                    null
                                                )
                                            }
                                            .background(MaterialTheme.colorScheme.secondary)
                                            .padding(vertical = generalPadding)
                                    )
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
                            .fillMaxWidth(),
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
    selected : Boolean,
    onItemClick : (ExpenseWithOperation) -> Unit,
    onCheckChange : () -> Unit,
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
                    onItemClick(expense)
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
            MyExpenseCheckBox(
                modifier = Modifier
                    .padding(horizontal = generalPadding),
                checked = selected
            ) {
                onCheckChange()
            }
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

@Composable
private fun RotatingSyncImage() {
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
}

@Composable
fun BoxAnimatedVisibility(
    visible : Boolean,
    content : @Composable () -> Unit
) {
    AnimatedVisibility(
        modifier = Modifier
            .zIndex(100f)
            .padding(horizontal = generalPadding)
            .padding(generalPadding)
            .clip(roundedCornerShape),
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -300 }) + fadeIn(),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -300 })
    ) {
        content()
    }
}

@Composable
fun MyExpenseTopAppBar(
    titleText : String,
    selectAll : Boolean,
    showCheckBoxes : Boolean,
    navigateUp : () -> Boolean,
    onCheckChange : (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(topBarHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!showCheckBoxes) {
            TopBarBackButton(navigateUp = navigateUp)
        } else {
            MyExpenseCheckBox(
                modifier = Modifier
                    .padding(start = 24.dp),
                checked = selectAll
            ) {
                onCheckChange(it)
            }
        }
        
        Text(
            text = titleText,
            style = Typography.bodyLarge.copy(MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .padding(generalPadding)
                .weight(1f)
        )
    }
}

@Composable
private fun MyExpenseCheckBox(
    modifier : Modifier = Modifier,
    checked : Boolean,
    clicked : (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .then(modifier)
            .size(20.dp)
            .clip(CircleShape)
            .clickable {
                clicked(!checked)
            }
            .background(if (checked) primary_color else MaterialTheme.colorScheme.background)
            .border(width = 1.dp, color = primary_color, shape = CircleShape)
            .padding(3.dp)
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Check Sign",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
    
}
