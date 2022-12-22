package com.coder.remindme.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coder.remindme.R
import com.coder.remindme.presentation.components.RemindersList
import com.coder.remindme.presentation.navigation.Screen
import com.coder.remindme.presentation.screens.viewmodel.RemindersViewModel

@Composable
fun ReminderScreen(navController: NavController, remindersViewModel: RemindersViewModel) {
    Scaffold(
        topBar = {
            com.coder.remindme.presentation.components.AppBar()
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.ReminderEditScreen.route)
                }
            ) {
                Icon(Icons.Filled.Add, "", tint = Color(R.color.purple_700))
            }
        }
    ) {
        LaunchedEffect(key1 = true) {
            remindersViewModel.getAllReminders()
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Your Reminders:", fontWeight = FontWeight.Bold, fontSize = 30.sp)
            Spacer(modifier = Modifier.height(10.dp))
            RemindersList(remindersResource = remindersViewModel.reminderListState) {
                remindersViewModel.deleteReminder(it)
            }
        }
    }
}
