package com.azg.sample.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azg.sample.app.PreferenceData
import com.azg.sample.base.BaseActivity
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    @Composable
    override fun Initialize() {
        LaunchedEffect(Unit) {
//            if (isInternetAvailable()) {
//                supervisorScope {
//                    launch(Dispatchers.Main.immediate) {
//                        runCatching {
//                            NativeAdCore.preloadAd(
//                                "native_lang",
//                                this@SplashActivity,
//                                listOf(BuildConfig.N001), count = 2
//                            )
//                        }
//                    }
//                    InterAdManager.getInstance().loadAndShow(
//                        this@SplashActivity,
//                        listOf(BuildConfig.I001),
//                        adCallback = object : AdCallback() {
//                            override fun onNextAction() {
//                                super.onNextAction()
//                                goNextPage()
//                            }
//                        })
//                }
//            } else {
                delay(3000)
                goNextPage()
           // }
        }
    }

    fun goNextPage() {
        if (!PreferenceData.isFinishFirstFlow) {
            launchActivity<LanguageActivity>()
            finish()
        } else {
            launchActivity<MainActivity>()
            finish()
        }
    }
    @Composable
    override fun BoxScope.ContentView(innerPadding: InnerPadding, context: Context) {
        ViewParentContent(onBack = {}, modifier = Modifier.fillMaxSize()) {
            SplashScreen(Modifier.padding(top = innerPadding.top))
        }
    }
}
@Preview(name = "PIXEL_4_XL", device = Devices.PIXEL_4_XL, showBackground = true)
@Composable
fun SplashPreview() {
    AppViewTheme {
        SplashScreen(Modifier)
    }
}
@Composable
fun SplashScreen(modifier: Modifier) {
    ImageBackground()
    Column(
        modifier
            .fillMaxSize()
            .background(
                transparent
            ), horizontalAlignment = Alignment.Companion.CenterHorizontally
    ) {
        SpaceH(h = getScreenWidthInDp() * 0.35f)

        Image(
            painter = painterResource(R.drawable.img_logo_splash),
            contentDescription = "Logo Desc",
            contentScale = ContentScale.Companion.Crop,
            modifier = Modifier.Companion
                .size(120.dp)
                .clip(RoundedCornerShape(_12Sdp))
        )

        SpaceH(h = 16.dp)
        Text(
            text = stringResource(R.string.app_name),
            fontFamily = FontFamily(Font(R.font.rb_600)),
            color = gradientStart,
            textAlign = TextAlign.Center,
            fontSize = 22.sp
        )


        Spacer(Modifier.Companion.weight(1f))
        TextView(
            text = stringResource(R.string.this_action_may_be_constant_adverting),
            modifier = Modifier.Companion.padding(bottom = 12.dp),
            textSize = _11Ssp,
            color = gradientStart,
            font = R.font.rb_400
        )
        LinearProgressIndicator(
            modifier = Modifier.Companion.height(6.dp),
            color = gradientStart,
            trackColor = gradientStart.opacity(.5f),
            strokeCap = StrokeCap.Companion.Round
        )
        SpaceH(h = 32.dp)
    }
}