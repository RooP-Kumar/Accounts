package com.zen.accounts.presentation.ui.screens.common

// <------------------ Navigation Routes ------------------>
const val splash_route = "Splash Route"
const val auth_route = "Authentication Route"
const val main_route = "Application Route"

// <------------------ Screen Routes ------------------>
const val splash_screen_route = "Splash Screen Route"
const val login_screen_route = "Login Screen Route"
const val register_screen_route = "Register Screen Route"
const val add_expense_screen_route = "Add Expense Screen Route"
const val expense_detail_screen_route = "Expense Details Screen Route"
const val my_expense_screen_route = "My Expense Screen Route"
const val home_screen_route = "Home Screen Route"
const val monthly_expense_screen_route = "Monthly Expense Screen Route"
const val setting_screen_route = "Setting Screen Route"
const val add_expense_item_screen_route = "Add Expense item Route"

// <------------------ Screen Routes Arguments Strings ------------------>
const val expense_details_argument = "expense_details_arg"
const val monthly_expense_argument = "monthly_expense_arg"

// <------------------ Screen Label Strings ------------------>
const val add_expense_item_screen_label = "Add Expense Item"
const val add_expense_screen_label = "Add Expense"
const val my_expense_screen_label = "My Expense"
const val home_screen_label = "Home"
const val monthly_expense_screen_label = "Monthly Expense"
const val setting_screen_label = "Setting"
const val expense_detail_screen_label = "Expense Details"
const val login_screen_label = "Login"
const val register_screen_label = "Register"
const val splash_screen_screen_label = "Splash Screen"

// <------------------ Placeholder Text Strings ------------------>
const val enter_title = "Title"
const val enter_amount = "Amount"
const val enter_name = "Name"
const val enter_email = "Email"
const val enter_phone = "Phone"
const val enter_pass = "Password"

// <------------------ Some Constant Strings ------------------>
const val did_not_have_account = "Didn't have an account?"
const val already_have_account = "already have an account?"

// <------------------ Data Store Strings ------------------>
const val datastore_name = "user data store"
const val user_data_store_key = "user data store key"
const val system_in_dark_mode = "system in dark mode"
const val backup_plan = "backup plan"
const val profile_pic = "profile img"

// <------------------ Buttons Label Strings ------------------>
const val login_button_label = "LOGIN"
const val logout_button_label = "LOG OUT"
const val small_logout_button_label = "Logout"
const val register_button_label = "REGISTER"
const val add_item_button_label = "ADD ITEM"

// <------------------ Rupees Sign Amount String ------------------>
fun getRupeeString(amount: Double, showZero : Boolean = false) : String {
    if (!showZero && amount == 0.0) return ""
    return "\u20B9 $amount"
}

// <------------------ Date Formatter Pattern String ------------------>
const val date_formatter_pattern_with_time = "dd LLL, yyyy hh:mm a"
const val date_formatter_pattern_without_time = "dd LLL, yyyy"

// <------------------ Account Type Value Strings ------------------>
const val total_amount_value = "At"
const val per_kg_amount_value = "A/kg"
const val per_g_amount_value = "A/g"
const val per_piece_amount_value = "A/p"
const val per_quantity_amount_value = "A/q"
const val per_dozen_amount_value = "A/d"

// <------------------ Amount placeholder String ------------------>
const val total_amount_value_placeholder = "Total amount"
const val per_kg_amount_value_placeholder = "Amount per kg"
const val per_g_amount_value_placeholder = "Amount per gram"
const val per_piece_amount_value_placeholder = "Amount per piece"
const val per_quantity_amount_value_placeholder = "Amount per quantity"
const val per_dozen_amount_value_placeholder = "Amount per dozen"

// <------------------ Quantity Placeholder String ------------------>
const val kg_qty_placeholder = "Kilo Gram"
const val g_qty_placeholder = "Gram"
const val p_qty_placeholder = "Piece"
const val normal_qty_placeholder = "Quantity"
const val d_qty_placeholder = "Dozen"

// <------------------ Work Manager String ------------------>
const val single_work_request_tag = "singleWorkRequestTag"
const val daily_work_request_tag = "dailyWorkRequestTag"
const val weekly_work_request_tag = "weeklyWorkRequestTag"
const val monthly_work_request_tag = "monthlyWorkRequestTag"
const val update_profile_work_request_tag = "updateProfileWorkRequestTag"
const val single_worker_name = "single worker name"
const val update_profile_worker_name = "update profile worker name"
const val daily_worker_name = "daily worker name"
const val weekly_worker_name = "weekly worker name"
const val monthly_worker_name = "monthly worker name"
const val work_manager_input_data = "input data tag"
const val work_manager_output_data = "output data tag"

const val create_work = "create work"
const val update_work = "update work"
const val delete_work = "delete work"

// <------------------ Message String ------------------>
const val success_register = "Successfully Register. Verify Your Email."
const val success_login = "Successfully Login to your account."
const val empty_email = "Please! Enter email"
const val empty_pass = "Please! Enter password"
const val required_field = "Required fields"
const val invalid_email = "Invalid Email"
const val invalid_pass = "Invalid Password"