package com.zen.accounts.presentation.ui.screens.common

import android.os.Bundle
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.unit.Dp

enum class LoadingState {
    IDLE,
    LOADING,
    SUCCESS,
    FAILURE
}

sealed class BackupPlan(val label : String) {
    data object Off : BackupPlan("Off")
    data object Now : BackupPlan("Now")
    data object Daily : BackupPlan("Daily")
    data object Weekly : BackupPlan("Weekly")
    data object Monthly : BackupPlan("Monthly")

    companion object {
        fun getAllBackupPlan() : List<BackupPlan> {
            return listOf(
                Off,
                Now,
                Daily,
                Weekly,
                Monthly
            )
        }
    }

}

data class ExpenseItemQuantityAmount(
    var amountType : AmountType,
    var amountPlaceholder : String,
    var quantityPlaceholder : String
)

data class AmountType(
    override var id: Long = 0L,
    override var value: String = ""
) : DropDownList()

open class DropDownList{
    open var id : Long = 0L
    open var value: String = ""
}

object dpSaver : Saver<MutableState<Dp>, Bundle> {
    override fun SaverScope.save(value: MutableState<Dp>): Bundle? {
        return Bundle().apply {
            putFloat("dpValue", value.value.value)
        }
    }

    override fun restore(value: Bundle): MutableState<Dp> {
        return mutableStateOf(Dp(value.getFloat("dpValue", 0f)))
    }

}

