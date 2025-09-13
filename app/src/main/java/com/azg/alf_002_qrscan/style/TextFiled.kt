package com.azg.alf_002_qrscan.style

import android.annotation.SuppressLint
import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.darkGray
import com.azg.alf_002_qrscan.R

fun String.format(): String {
    return this.replace("\n", "").trim()
}

fun String.checkValue(): Boolean {
    return this.replace("", "").replace("\n", "").isNotEmpty()
}

fun String.maxLength(length: Int): Boolean {
    return this.replace("\n", "").length < (length)
}

fun String.minLength(length: Int): Boolean {
    return this.replace("\n", "").length >=(length)
}

fun String.isEmail(): Boolean {
    return this.isNotEmpty() &&
            Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

@Composable
fun InputValue(
    value: String,
    hint: String = "",
    maxLines: Int = 1000,
    color: Color = Color.Black,
    hintColor: Color = darkGray,
    font: Font = Font(R.font.rb_400),
    textSize: TextUnit = 16.sp,
    @SuppressLint("ModifierParameter") boxModifier: Modifier = Modifier,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (String) -> Unit,
) {
    Row(
        modifier = boxModifier.verticalScroll(rememberScrollState()),
        horizontalArrangement = horizontalArrangement, verticalAlignment = verticalAlignment
    ) {
        BasicTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            modifier = modifier,
            textStyle = TextStyle(
                color = color,
                fontSize = textSize,
                fontFamily = FontFamily(font)
            ),
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = keyboardType
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp), contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            style = TextStyle(
                                color = hintColor,
                                fontSize = textSize,
                                fontFamily = FontFamily(font)
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}



@Composable
fun InputValue(
    value: String,
    hint: String = "",
    font: Font = Font(R.font.rb_400),
    @SuppressLint("ModifierParameter") boxModifier: Modifier = Modifier,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    maxLines: Int = 1000,
    hintColor: Color = darkGray,
    color: Color = black,
    teAlignment: TextAlign = TextAlign.Start,
    textSize: TextUnit = 16.sp,
    keyboardType: KeyboardType = KeyboardType.Text,
    horizontalArrangement: Arrangement.HorizontalOrVertical = Arrangement.Center,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = boxModifier
            .fillMaxWidth(),
        horizontalArrangement = horizontalArrangement, verticalAlignment = verticalAlignment
    ) {
        BasicTextField(
            value = value,
            onValueChange = { onValueChange(it) },
            modifier = modifier,
            textStyle = TextStyle(
                color = color,
                fontSize = textSize,
                textAlign = teAlignment,
                fontFamily = FontFamily(font),
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            ),
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = keyboardType
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            maxLines = maxLines,
                            style = TextStyle(
                                color = hintColor,
                                fontSize = 14.sp,
                                fontFamily = FontFamily(font), platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
