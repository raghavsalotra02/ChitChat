package com.example.smarttalk.customState

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smarttalk.ui.theme.SmartTalkTheme

@Composable
fun MyUserInputField(
    state: MyUserInputState,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done,
    ),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textAlign: TextAlign = TextAlign.Start,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        color = LocalContentColor.current,
        textAlign = textAlign
    ),

    isCompulsory: Boolean = false,
    enabled: Boolean = true,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),

    maxCount: Int = Int.MAX_VALUE,
    validate: ((String) -> String?) = { null },
    onValueChanged: (String) -> Unit = {
        state.updateText(it)
        state.showError(validate(it))
                                       },
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Column(modifier = modifier) {
        AnimatedVisibility(visible = state.isError) {
            Column {
                Text(
                    text = state.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.text,
            onValueChange = {
                if (it.length <= maxCount) {
                    onValueChanged(it)
                }
            },
            label = {
                if (isCompulsory) {
                    val annotatedString = buildAnnotatedString {
                        append(state.hint)
                        withStyle(style = SpanStyle(MaterialTheme.colorScheme.error)) {
                            append("*")
                        }
                    }
                    Text(
                        text = annotatedString
                    )
                } else {
                    Text(
                        text = state.hint
                    )
                }

            },
            textStyle = textStyle,
            keyboardOptions = keyboardOptions,
            isError = state.isError,
            visualTransformation = visualTransformation,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            enabled = !state.isReadOnly,
            colors = colors,
            shape = MaterialTheme.shapes.medium,
            readOnly = !enabled
        )
    }
}

@Preview
@Composable
private fun PreviewInputState() {
    SmartTalkTheme {
        Surface {
            val state1 = rememberMyUserInputState(
                hint = "User Name",
            )
            val state2 = rememberMyUserInputState(
                hint = "Password",
                initialText = "abcd1234"
            )

            Column(modifier = Modifier.padding(10.dp)) {
                MyUserInputField(
                    state = state1,
                    isCompulsory = true,
                    validate = {
                        if (it.length < 10 && it.length>0) {
                            "Fill appropriate number"
                        } else {
                            null
                        }
                    }
                )
                Spacer(modifier = Modifier.size(20.dp))
                MyUserInputField(
                    state = state2,
                    isCompulsory = true,
                    visualTransformation = PasswordVisualTransformation()
                )

            }
        }

    }
}