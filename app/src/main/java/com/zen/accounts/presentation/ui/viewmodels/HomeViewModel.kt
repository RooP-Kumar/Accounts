package com.zen.accounts.presentation.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zen.accounts.data.api.resource.Resource
import com.zen.accounts.data.repositoryImpl.ExpenseRepository
import com.zen.accounts.presentation.ui.screens.common.daily
import com.zen.accounts.presentation.ui.screens.common.dailyList
import com.zen.accounts.presentation.ui.screens.common.monthMap
import com.zen.accounts.presentation.ui.screens.common.monthly
import com.zen.accounts.presentation.ui.screens.common.weekly
import com.zen.accounts.presentation.ui.screens.main.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val dataStoreRepository : ExpenseRepository
) : ViewModel() {
    val allExpense get() = expenseRepository.allExpense
    val monthlyExpense get() = expenseRepository.monthlyExpense
    private var _storedFilterType : String
    val storedFilterType : String get() = _storedFilterType
    
    init {
        _storedFilterType = weekly
    }

    val homeUiState by lazy {
        HomeUiState()
    }
    
    fun getFilteredGraphData(filterTypeFromUser : String?) {
        viewModelScope.launch { 
            homeUiState.apply {
                val filterType = if (filterTypeFromUser != null) {
                    _storedFilterType = filterTypeFromUser
                    filterTypeFromUser
                } else {
                    storedFilterType
                }
                when (filterType) {
                    monthly -> {
                        showMonthlyProgressBar.value = true
                        graphData.value = Pair(null, getLastSixMonthList())
                        dataStoreRepository.dataStore.getUser()?.let { user ->
                            when(val res = expenseRepository.getFilteredGraphData(user.uid)) {
                                is Resource.SUCCESS -> {
                                    delay(500)
                                    showMonthlyProgressBar.value = false
                                    graphData.value = Pair(res.value.value, getLastSixMonthList())
                                }
                                is Resource.FAILURE -> {
                                    delay(500)
                                    showMonthlyProgressBar.value = false
                                    graphData.value = Pair(null, getLastSixMonthList())
                                }
                            }
                        }
                    }
                    weekly -> {
                        val fromDate = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1)}.timeInMillis
                        val toDate = Calendar.getInstance().timeInMillis
                        expenseRepository.getExpenseInRange(fromDate, toDate, filterType)
                            .collectLatest {
                                graphData.value = Pair(it, getWeeklyList())       
                            }
                    }
                    daily -> {
                        val fromDate = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis
                        val toDate = Calendar.getInstance().timeInMillis
                        expenseRepository.getExpenseInRange(fromDate, toDate, filterType)
                            .collectLatest {
                                graphData.value = Pair(it, dailyList)
                            }
                    }
                    
                    else -> {}
                }
            }
        }
    }
    
    private suspend fun getLastSixMonthList() : List<String> {
        val monthList = viewModelScope.async {
            val currentCal = Calendar.getInstance()
            val currentMonth = currentCal.get(Calendar.MONTH)
            val tempCal = Calendar.getInstance()
            tempCal.add(Calendar.MONTH, -5)
            val monthList = arrayListOf<String>()
            while (tempCal.get(Calendar.MONTH) != currentMonth) {
                monthList.add(monthMap[tempCal.get(Calendar.MONTH)]!!)
                tempCal.add(Calendar.MONTH, 1)
            }
            monthList.add(monthMap[currentMonth]!!)
            monthList
        }
        return monthList.await()
    }
    
    private fun getWeeklyList() : List<String> {
        val currentCal = Calendar.getInstance()
        currentCal.set(Calendar.DAY_OF_MONTH, 1)
        val listSize = currentCal.getActualMaximum(Calendar.WEEK_OF_MONTH)
        val weeklyList = ArrayList<String>()
        for (i in 1 .. listSize) {
            weeklyList.add(i.toString())
        }
        return weeklyList
    }
}
