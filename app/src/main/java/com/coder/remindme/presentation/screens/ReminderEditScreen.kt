package com.coder.remindme.presentation.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.coder.remindme.domain.model.RemindType
import com.coder.remindme.presentation.components.*
import com.coder.remindme.presentation.screens.viewmodel.RemindersViewModel
import java.time.LocalDateTime
import java.time.ZoneId

@Composable
fun ReminderEditScreen(
    navController: NavController,
    remindersViewModel: RemindersViewModel,
) {
    Scaffold(
        topBar = {
            com.coder.remindme.presentation.components.AppBar()
        }
    ) {
        val titleInputValue = remember { mutableStateOf(TextFieldValue()) }
        val titleError = remember { mutableStateOf("") }
        val descriptionInputValue = remember { mutableStateOf(TextFieldValue()) }
        val descriptionError = remember { mutableStateOf("") }

        var localDateTime: LocalDateTime? by remember {  mutableStateOf(null)  }

        val remindTypeState = remember { mutableStateOf(RemindType.NONE) }

        val mContext = LocalContext.current

        BoxWithLayout {
            Column(
                Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .weight(1f, false),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Set Reminder",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextFieldValidation(value = titleInputValue.value.text, onValueChange = {
                    titleInputValue.value = TextFieldValue(it)
                }, placeholder = {
                    Text(
                        text = "Enter your title"
                    )
                },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    textStyle = TextStyle(
                        color = Color.Black, fontSize = TextUnit.Unspecified,
                        fontFamily = FontFamily.SansSerif
                    ),
                    maxLines = 1,
                    singleLine = true,
                    modifier = Modifier
                        .border(5.dp, Color.Unspecified, RoundedCornerShape(10.dp))
                        .fillMaxWidth()
                        .background(
                            color = Color(0xffe2ebf0),
                            shape = CircleShape
                        ),
                    shape = CircleShape,
                    error = titleError.value
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextFieldValidation(value = descriptionInputValue.value.text,
                    onValueChange = {
                        descriptionInputValue.value = TextFieldValue(it)
                    },
                    placeholder = {
                        Text(
                            text = "Enter your subtitle"
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    textStyle = TextStyle(
                        color = Color.Black, fontSize = TextUnit.Unspecified,
                        fontFamily = FontFamily.SansSerif
                    ),
                    maxLines = 1,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xffe2ebf0),
                            shape = CircleShape
                        ),
                    shape = CircleShape,
                    error = descriptionError.value
                )

                Spacer(modifier = Modifier.height(10.dp))

                DatePicker {
                    localDateTime = it
                }
                Spacer(modifier = Modifier.height(20.dp))
                TimePicker(localDateTime) {
                    localDateTime = it
                }
                RepeatTypeDropDown {
                    remindTypeState.value = it
                }


                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = {


                    val result = validateData(
                        titleInputValue,
                        descriptionInputValue,
                        titleError,
                        descriptionError,
                        localDateTime,
                        mContext
                    )

                    if (!result) {
                        return@Button
                    }

                    remindersViewModel.createReminder(
                        titleInputValue.value.text,
                        descriptionInputValue.value.text,
                        remindTypeState.value,
                        localDateTime!!.atZone(ZoneId.systemDefault()).toInstant()
                    )
                    navController.popBackStack()
                }) {
                    Text(text = "Set Reminder")
                }
            }
        }
    }
}

private fun validateData(
    titleInputValue: MutableState<TextFieldValue>,
    descriptionInputValue: MutableState<TextFieldValue>,
    titleError: MutableState<String>,
    descriptionError: MutableState<String>,
    localDateTime: LocalDateTime?,
    mContext: Context
): Boolean {
    if (titleInputValue.value.text.isEmpty() && descriptionInputValue.value.text.isEmpty()) {
        titleError.value = "title can't be empty"
        descriptionError.value = "subtitle can't be empty"
        return false
    }

    if (titleInputValue.value.text.isEmpty()) {
        titleError.value = "title can't be empty"
        return false
    } else {
        titleError.value = ""
    }

    if (descriptionInputValue.value.text.isEmpty()) {
        descriptionError.value = "subtitle can't be empty"
        return false
    } else {
        descriptionError.value = ""
    }

    if (localDateTime == null) {
        Toast.makeText(mContext, "Select Time and Date", Toast.LENGTH_LONG).show()
        return false
    }

    return true
}
