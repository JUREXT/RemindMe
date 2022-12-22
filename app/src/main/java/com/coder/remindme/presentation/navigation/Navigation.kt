package com.coder.remindme.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coder.remindme.presentation.screens.ReminderEditScreen
import com.coder.remindme.presentation.screens.ReminderScreen
import com.coder.remindme.presentation.screens.viewmodel.RemindersViewModel

@Composable
fun Navigation() {
    val navigationController = rememberNavController()
    val remindersViewModel = hiltViewModel<RemindersViewModel>()

    NavHost(navController = navigationController, startDestination = Screen.ReminderScreen.route){
        composable(route = Screen.ReminderScreen.route){
            ReminderScreen(navigationController,remindersViewModel)
        }

        composable(route = Screen.ReminderEditScreen.route){
            ReminderEditScreen(navigationController,remindersViewModel)
        }
    }
}