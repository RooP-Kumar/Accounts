package com.zen.accounts.presentation.ui.screens.common

fun getQuantityAmountRelation(): List<ExpenseItemQuantityAmount> {
    return listOf(
        ExpenseItemQuantityAmount(
            AmountType(
                1,
                total_amount_value
            ),
            total_amount_value_placeholder,
            normal_qty_placeholder
        ),

        ExpenseItemQuantityAmount(
            AmountType(
                1,
                per_kg_amount_value
            ),
            per_kg_amount_value_placeholder,
            kg_qty_placeholder
        ),

        ExpenseItemQuantityAmount(
            AmountType(
                1,
                per_g_amount_value
            ),
            per_g_amount_value_placeholder,
            g_qty_placeholder
        ),

        ExpenseItemQuantityAmount(
            AmountType(
                1,
                per_piece_amount_value
            ),
            per_piece_amount_value_placeholder,
            p_qty_placeholder
        ),

        ExpenseItemQuantityAmount(
            AmountType(
                1,
                per_quantity_amount_value
            ),
            per_quantity_amount_value_placeholder,
            normal_qty_placeholder
        ),

        ExpenseItemQuantityAmount(
            AmountType(
                1,
                per_dozen_amount_value
            ),
            per_dozen_amount_value_placeholder,
            d_qty_placeholder
        )
    )
}

