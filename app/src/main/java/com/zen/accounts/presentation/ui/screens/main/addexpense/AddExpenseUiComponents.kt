package com.zen.accounts.presentation.ui.screens.main.addexpense

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zen.accounts.presentation.ui.screens.common.GeneralEditText
import com.zen.accounts.presentation.ui.theme.Typography
import com.zen.accounts.presentation.ui.theme.generalPadding
import com.zen.accounts.presentation.ui.theme.halfGeneralPadding
import com.zen.accounts.presentation.ui.theme.primary_color

@Composable
fun UpperTitleSection(
    title : String,
    onTextChange : (String) -> Unit
) {
    Text(
        text = "Add Title",
        style = Typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground),
        modifier = Modifier
            .padding(top = halfGeneralPadding)
            .padding(vertical = halfGeneralPadding, horizontal = generalPadding)
    )

    GeneralEditText(
        text = title,
        onValueChange = onTextChange,
        modifier = Modifier.fillMaxWidth(),
        placeholderText = "Enter Title",
        showClickEffect = false
    )
}

@Composable
fun AddExpenseItemTitleSection() {
    var tempModifier : Modifier
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Add Expense Items",
            style = Typography.titleSmall.copy(color = MaterialTheme.colorScheme.onBackground),
            modifier = Modifier
                .padding(vertical = halfGeneralPadding, horizontal = generalPadding)
        )
    }

    HorizontalLineOnBackground()
}


@Composable
fun HorizontalLineOnBackground(modifier: Modifier = Modifier) {
    Spacer(
        modifier
            .fillMaxWidth()
            .height(0.2.dp)
            .background(primary_color)
    )
}
