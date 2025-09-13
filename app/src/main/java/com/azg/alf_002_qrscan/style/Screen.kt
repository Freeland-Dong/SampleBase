package com.azg.alf_002_qrscan.style

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowManager
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetricsCalculator
import kotlin.math.roundToInt
import kotlin.math.sqrt


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun getScreenWidthInDp(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp.dp
}



@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun getScreenHeightInDp(): Dp {
    val configuration = LocalConfiguration.current
    return configuration.screenHeightDp.dp
}



fun getScreenSizeInInches(context: Context): Double {
    val displayMetrics = context.resources.displayMetrics
    val widthInPixels = displayMetrics.widthPixels
    val heightInPixels = displayMetrics.heightPixels
    val widthInDp = widthInPixels / displayMetrics.density
    val heightInDp = heightInPixels / displayMetrics.density

    val screenWidthInInches = widthInDp / displayMetrics.densityDpi
    val screenHeightInInches = heightInDp / displayMetrics.densityDpi

    return sqrt(
        (screenWidthInInches * screenWidthInInches) +
                (screenHeightInInches * screenHeightInInches).toDouble()
    )
}

@Composable
fun getScreenSize(): Size {
    val context = LocalContext.current

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(context)
        val width = windowMetrics.bounds.width()
        val height = windowMetrics.bounds.height()
        Size(width, height)
    } else {
        val displayMetrics = DisplayMetrics()
        val windowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        Size(width, height)
    }
}


@Composable
fun getScreenWidth(): Int {
    return getScreenSize().width
}

@Composable
fun getScreenHeight(): Int {
    return getScreenSize().height
}

@Composable
fun getScreenWidthInPx(): Int {
    val metrics = LocalContext.current.resources.displayMetrics
    return metrics.widthPixels
}


@Composable
fun getScreenHeightInPx(): Int {
    val metrics = LocalContext.current.resources.displayMetrics
    return metrics.heightPixels
}


@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) { this@toPx.toPx() }
}


@Composable
fun Int.pxToDp(): Dp {
    return with(LocalDensity.current) { this@pxToDp.toDp() }
}

@Composable
fun Float.pxToDp(): Dp {
    return with(LocalDensity.current) { this@pxToDp.toDp() }
}


@SuppressLint("ConfigurationScreenWidthHeight")
fun Modifier.widthPercent(percent: Float): Modifier = composed {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    this.then(Modifier.width(screenWidth * (percent / 100f)))
}


@SuppressLint("ConfigurationScreenWidthHeight")
fun Modifier.heightPercent(percent: Float): Modifier = composed {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    this.then(Modifier.height(screenHeight * (percent / 100f)))
}

@SuppressLint("ConfigurationScreenWidthHeight")
fun Modifier.paddingHorizontal(values: Dp): Modifier = composed {
    this.then(Modifier.padding(horizontal = values))
}

@SuppressLint("ConfigurationScreenWidthHeight")
fun Modifier.paddingVertical(values: Dp): Modifier = composed {
    this.then(Modifier.padding(vertical = values))
}

@SuppressLint("ConfigurationScreenWidthHeight")
fun Modifier.paddingTop(values: Dp): Modifier = composed {
    this.then(Modifier.padding(top = values))
}
@SuppressLint("ConfigurationScreenWidthHeight")
fun Modifier.paddingBottom(values: Dp): Modifier = composed {
    this.then(Modifier.padding(bottom = values))
}
@SuppressLint("ConfigurationScreenWidthHeight")
fun Modifier.paddingStart(values: Dp): Modifier = composed {
    this.then(Modifier.padding(start = values))
}
@SuppressLint("ConfigurationScreenWidthHeight")
fun Modifier.paddingEnd(values: Dp): Modifier = composed {
    this.then(Modifier.padding(end = values))
}



@Composable
fun widthPercentPx(percent: Float): Int {
    val screenWidth = getScreenWidthInPx()
    return ((screenWidth * percent) / 100f).roundToInt()
}


@Composable
fun heightPercentPx(percent: Float): Int {
    val screenHeight = getScreenHeightInPx()
    return ((screenHeight * percent) / 100f).roundToInt()
}

@Composable
fun widthPercentDp(percent: Float): Dp {
    val screenWidthPx = getScreenWidthInPx()
    val widthPx = (screenWidthPx * percent / 100f)
    return with(LocalDensity.current) { widthPx.toDp() }
}

@Composable
fun heightPercentDp(percent: Float): Dp {
    val screenHeightPx = getScreenHeightInPx()
    val heightPx = (screenHeightPx * percent / 100f)
    return with(LocalDensity.current) { heightPx.toDp() }
}

