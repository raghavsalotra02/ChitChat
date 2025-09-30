package com.example.smarttalk.customState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun rememberMyUserInputState(
    hint: String,
    errorMessage: String? = null,
    initialText: String? = null,
    isReadOnly: Boolean = false,
): MyUserInputState =
    rememberSaveable(saver = MyUserInputState.Saver){
        MyUserInputState(hint = hint, initialText = initialText, errorMessage = errorMessage, isReadOnly = isReadOnly)
    }

class MyUserInputState(
    val hint: String,
    val isReadOnly: Boolean,
    private val initialText: String?,
    private val errorMessage: String?,
) {

    var text by mutableStateOf(initialText ?: "")

    var isEmpty by mutableStateOf(text.isBlank() || text == hint)
        private set

    val isHint: Boolean
        get() = text == hint

    fun updateText(newText: String){
        text = newText
        isEmpty = text.isBlank() || text == hint
    }

    var error: String? by mutableStateOf(errorMessage)
        private set

    val isError: Boolean
        get() = !(error.isNullOrBlank())

    @Composable
    fun ShowError(message: String?){
        error = message
    }

    fun showError(message: String?){
        error = message
    }

    companion object{
        val Saver: Saver<MyUserInputState, *> = listSaver(
            save = { listOf(it.hint.toString(), it.text.toString(), it.errorMessage.toString(), it.isReadOnly) },
            restore = {
                MyUserInputState(
                    hint = (it[0] as? String) ?: "",
                    initialText = (it[1] as? String) ?: "",
                    errorMessage = (it[2] as? String) ?: "",
                    isReadOnly = (it[3] as? Boolean) ?: false
                )
            }
        )
    }
}