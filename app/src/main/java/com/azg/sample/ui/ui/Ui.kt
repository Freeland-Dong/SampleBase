package com.azg.sample.ui.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices.PIXEL_4_XL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azg.sample.R
import com.azg.sample.style.AppViewTheme
import com.azg.sample.style.Color_f2f2f7
import com.azg.sample.style.TextView
import com.azg.sample.style._10Sdp
import com.azg.sample.style._11Ssp
import com.azg.sample.style._12Sdp
import com.azg.sample.style._12Ssp
import com.azg.sample.style._26Sdp
import com.azg.sample.style._30Sdp
import com.azg.sample.style._3Sdp
import com.azg.sample.style._8Sdp
import com.azg.sample.style.black
import com.azg.sample.style.gray
import com.azg.sample.style.onClick
import com.azg.sample.style.opacity
import com.azg.sample.style.paddingEnd
import com.azg.sample.style.paddingStart
import com.azg.sample.style.white
import com.azg.sample.ui.language.LanguageItem
import com.azg.sample.widget.grdEnd

@Preview(device = PIXEL_4_XL, showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun PreviewUnit() {
    var value by remember { mutableStateOf("Error dsjksjdksjd") }
    val isError = value.length > 10
    AppViewTheme {
        Column(Modifier.fillMaxWidth()) {
            MonthYearSelect(Modifier.fillMaxWidth()) {
            }
        }
    }
}
@Composable
fun LanguageItem(
    currentLanguage: LanguageItem? = null,
    item: LanguageItem,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    callback: (LanguageItem) -> Unit
) {
    Box(
        modifier
            .fillMaxWidth()
            .border(
                width = 1.2.dp,
                color = if (item != currentLanguage) Color_f2f2f7.opacity(0.5f) else grdEnd,
                shape = RoundedCornerShape(_8Sdp)
            )
            .background(white, shape = RoundedCornerShape(_10Sdp))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = _12Sdp, horizontal = _12Sdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(item.flagId),
                contentDescription = item.code,
                modifier = size(_26Sdp)
                    .border(1.dp, color = gray.opacity(0.2f), shape = CircleShape)
            )
            TextView(
                text = item.name,
                font = R.font.rb_400,
                textSize = _12Ssp,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            )
            Image(
                painterResource(if (currentLanguage == item) R.drawable.ic_language_selected else R.drawable.ic_language_unselected),
                contentDescription = null,
                Modifier.size(24.dp)
            )
        }
        Box(
            Modifier
                .matchParentSize()
                .onClick { callback(item) })
    }
}
@Composable
fun BoxScope.BlurView() {
    Box(
        Modifier
            .matchParentSize()
            .blur(_30Sdp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
    ) {
        Box(
            Modifier
                .matchParentSize()
                .background(
                    color = white.opacity(0.5f),
                    shape = RoundedCornerShape(_10Sdp)
                )
        )
    }
}
@SuppressLint("ResourceType")
@Composable
fun MonthYearSelect(
    modifier: Modifier = Modifier,
    isMonth: Boolean = true,
    isMonthSelect: (Boolean) -> Unit = {}
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(0.45f))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(_8Sdp))
                .padding(vertical = _8Sdp)
                .weight(0.55f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    Modifier
                        .paddingEnd(_3Sdp)
                        .weight(1f), verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(if (isMonth) R.drawable.ic_language_selected else R.drawable.ic_language_unselected),
                        contentDescription = null,
                        Modifier
                            .size(24.dp)
                            .onClick {
                                isMonthSelect.invoke(true)
                            }
                    )
                    Text(
                        text = stringResource(R.string.month_),
                        fontSize = _11Ssp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = TextStyle.Default.fontFamily(R.font.rb_500),
                        color = black,
                        modifier = Modifier
                            .weight(1f)
                            .paddingStart(_3Sdp),
                    )
                }
                Row(
                    Modifier
                        .paddingStart(_3Sdp)
                        .weight(1f), verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(if (!isMonth) R.drawable.ic_language_selected else R.drawable.ic_language_unselected),
                        contentDescription = null,
                        Modifier
                            .size(24.dp)
                            .onClick {
                                isMonthSelect.invoke(false)
                            }
                    )
                    Text(
                        text = stringResource(R.string.year),
                        fontSize = _11Ssp,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = TextStyle.Default.fontFamily(R.font.rb_500),
                        color = black,
                        modifier = Modifier
                            .weight(1f)
                            .paddingStart(_3Sdp)
                    )
                }
            }
        }
    }
}