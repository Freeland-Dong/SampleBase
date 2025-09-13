package com.azg.sample.ui.language

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.azg.sample.R
import com.azg.sample.app.PreferenceData
import com.azg.sample.base.BaseActivity
import com.azg.sample.style.AppViewTheme
import com.azg.sample.style.IconView
import com.azg.sample.style.TextView
import com.azg.sample.style.ViewParentContent
import com.azg.sample.style._12Sdp
import com.azg.sample.style._18Ssp
import com.azg.sample.style._6Sdp
import com.azg.sample.style.paddingHorizontal
import com.azg.sample.style.paddingTop
import com.azg.sample.style.transfer
import com.azg.sample.style.white
import com.azg.sample.ui.MainActivity

class LanguageActivity : BaseActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    override fun Initialize() {
    }
    @Composable
    override fun BoxScope.ContentView(innerPadding: InnerPadding, context: Context) {
        val listLanguages by remember {
            getLanguageArray()
        }
        var currentLanguage by remember {
            mutableStateOf<LanguageItem?>(null)
        }
        ViewParentContent(onBack = { finishAffinity() }) {
            Column(Modifier.fillMaxSize()) {
                LanguageScreen(
                    listLanguages,
                    currentLanguage,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = innerPadding.top + _6Sdp)
                        .weight(1f)
                ) { lang, isNext ->
                    currentLanguage = lang
                    if (isNext) {
                        PreferenceData.setLocale(
                            this@LanguageActivity,
                            currentLanguage?.code ?: "en"
                        )
                        launchActivity<MainActivity>()
                        finish()
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val listLanguages = getLanguageArray()
    AppViewTheme {
        LanguageScreen(
            listLanguages, listLanguages[0], modifier =
                Modifier.paddingTop(_6Sdp)
        ) { _, _ ->
        }
    }
}
@Composable
fun LanguageScreen(
    listLanguages: MutableList<LanguageItem>,
    currentLanguage: LanguageItem?,
    modifier: Modifier,
    callback: (LanguageItem, Boolean) -> Unit = { _, _ -> }
) {
    val lazyListState = rememberLazyListState()
    Column(modifier) {
        Row(Modifier.paddingHorizontal(_12Sdp), verticalAlignment = Alignment.CenterVertically) {
            TextView(
                text = R.string.language,
                font = R.font.rb_700,
                textSize = _18Ssp,
                color = white
            )
            Spacer(Modifier.weight(1f))
            currentLanguage?.let {
                IconView(item = Icons.Rounded.Done, icSize = 32, tint = white) {
                    callback.invoke(currentLanguage, true)
                }
            } ?: run {
                IconView(item = Icons.Default.Done, icSize = 32, tint = transfer)
            }
        }
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .paddingHorizontal(_12Sdp)
                .paddingTop(_12Sdp), state = lazyListState
        ) {
            itemsIndexed(listLanguages) { _, item ->
                LanguageItem(currentLanguage, item = item) {
                    callback.invoke(it, false)
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}