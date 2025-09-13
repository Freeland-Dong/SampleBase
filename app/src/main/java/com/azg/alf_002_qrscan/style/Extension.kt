package com.azg.alf_002_qrscan.style

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azg.alf_002_qrscan.style.black
import com.azg.alf_002_qrscan.style.mainColor
import com.azg.alf_002_qrscan.R
import kotlinx.coroutines.launch

@Composable
fun Space(w: Int = 0, h: Int = 0) {
    Spacer(
        modifier = Modifier
            .width(w.dp)
            .height(h.dp)
    )
}
@Composable
fun SpaceH(h: Int) {
    Spacer(
        modifier = Modifier
            .height(h.dp)
    )
}
@Composable
fun SpaceW(w: Int) {
    Spacer(
        modifier = Modifier
            .width(w.dp)
    )
}

@Composable
fun ColumnScope.SpaceF(weight: Float=1f) {
    Spacer(Modifier.weight(weight))
}
@Composable
fun RowScope.SpaceF(weight: Float=1f) {
    Spacer(Modifier.weight(weight))
}
@Composable
fun Space(w: Dp = 0.dp, h: Dp = 0.dp) {
    Spacer(
        modifier = Modifier
            .width(w)
            .height(h)
    )
}
@Composable
fun SpaceH(h: Dp) {
    Spacer(
        modifier = Modifier
            .height(h)
    )
}
@Composable
fun SpaceW(w: Dp) {
    Spacer(
        modifier = Modifier
            .width(w)
    )
}

@Composable
fun ViewParentContent(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    propagateMinConstraints: Boolean = false,
    onBack: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val softwareKeyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    BackHandler(onBack = {
        softwareKeyboardController?.hide()
        coroutineScope.launch {
            onBack.invoke()
        }
    })
    Box(
        modifier = modifier
            .fillMaxSize(1f),
        contentAlignment = contentAlignment,
        propagateMinConstraints = propagateMinConstraints,
        content = content
    )
}

@Composable
fun HorScrollView(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    LaunchedEffect(key1 = keyboardHeight) {
        coroutineScope.launch {
            scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }
    Box(
        modifier
            .horizontalScroll(scrollState)
            .fillMaxSize(), content = content
    )
}
@Composable
fun VerScrollView(modifier: Modifier, content: @Composable BoxScope.() -> Unit) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    LaunchedEffect(key1 = keyboardHeight) {
        coroutineScope.launch {
            scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }
    Box(modifier.verticalScroll(scrollState)) {
        content()
    }
}
@Composable
fun CustomSwitch(
    checked: Boolean,
    thumbOn: Color = mainColor,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val switchWidth = 46.dp
    val switchHeight = 24.dp
    val thumbRadius = 12.dp
    val thumbPadding = 2.dp
    val thumbPosition = animateDpAsState(
        targetValue = if (checked) switchWidth - thumbRadius * 1.8f - thumbPadding else thumbPadding,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Box(
        modifier = modifier
            .size(switchWidth, switchHeight)
            .clip(RoundedCornerShape(switchHeight / 2))
            .background(if (checked) thumbOn else Color(0xFFB5B5B5))
            .onClick { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(thumbRadius * 1.8f)
                .padding(thumbPadding)
                .offset(x = thumbPosition.value)
                .background(Color.White, CircleShape)
        )
    }
}
val boldRegex = Regex("(?<!\\*)\\*\\*(?!\\*).*?(?<!\\*)\\*\\*(?!\\*)")
@Composable
fun TextSpanner(text: String, modifier: Modifier = Modifier, color: Color = Color(0xFF000000)) {
    var results: MatchResult? = boldRegex.find(text)
    val boldIndexes = mutableListOf<Pair<Int, Int>>()
    val keywords = mutableListOf<String>()
    var finalText = text

    while (results != null) {
        keywords.add(results.value)
        results = results.next()
    }

    keywords.forEach { keyword ->
        val indexOf = finalText.indexOf(keyword)
        val newKeyWord = keyword.removeSurrounding("**")
        finalText = finalText.replace(keyword, newKeyWord)
        boldIndexes.add(Pair(indexOf, indexOf + newKeyWord.length))
    }
    val annotatedString = buildAnnotatedString {
        append(finalText)
        boldIndexes.forEach {
            addStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontFamily = FontFamily(
                        Font(R.font.rb_400)
                    ),
                    fontSize = 15.sp
                ),
                start = it.first,
                end = it.second
            )
        }
    }

    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
        fontSize = 16.sp,
        text = annotatedString
    )
}
@Composable
fun TextView(
    text: String,
    modifier: Modifier = Modifier,
    textSize:  TextUnit= _12Ssp,
    color: Color? = null,
    font: Font? = null,
    maxLine: Int = 100,
    textAlign: TextAlign? = TextAlign.Start,
    contentAlignment: Alignment,
    onclick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .clickable(interactionSource, null) { onclick?.invoke() },
        contentAlignment = contentAlignment
    ) {
        Text(
            text = text,
            fontSize = textSize,
            textAlign = textAlign,
            maxLines = maxLine,
            color = color ?: Color.Black,
            fontFamily = FontFamily(
                font ?: Font(R.font.rb_400)
            ),
            style = TextStyle(
                platformStyle = PlatformTextStyle(
                    includeFontPadding = false
                )
            )
        )
    }
}
@Composable
fun TextView(
    text: String,
    modifier: Modifier = Modifier,
    textSize:  TextUnit= _12Ssp,
    color: Color? = null,
    font: Font? = null,
    maxLine: Int = 100,
    minLine: Int=1,
    singleLine: Boolean = false,
    textAlign: TextAlign? = TextAlign.Start,
    onclick: (() -> Unit) = {}
) {
    val interactionSource = remember { MutableInteractionSource() }

    Text(
        text = text,
        fontSize = textSize,
        textAlign = textAlign,
        maxLines = maxLine,
        minLines=minLine,
        overflow = if (singleLine) TextOverflow.Ellipsis else TextOverflow.Clip,
        color = color ?: black,
        fontFamily = FontFamily(
            font ?: Font(
                R.font.rb_400
            )
        ),
        style = TextStyle(
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            )
        ),
        modifier = modifier.clickable(interactionSource, null) {
            onclick.invoke()
        }
    )
}
@Composable
fun TextView(
    text: Int,
    modifier: Modifier = Modifier,
    textSize:  TextUnit= _12Ssp,
    color: Color = Color.Black,
    font: Int = R.font.rb_400,
    maxLine: Int = 100,
    minLine:Int=1,
    singleLine: Boolean = false,
    textAlign: TextAlign = TextAlign.Start,
    onclick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val context = LocalContext.current
    Text(
        text = context.getString(text),
        fontSize = textSize,
        textAlign = textAlign,
        maxLines = maxLine,
        minLines=minLine,
        overflow = if (singleLine) TextOverflow.Ellipsis else TextOverflow.Clip,
        color = color ?: black,
        fontFamily = FontFamily(
            Font(
                font
            )
        ),
        style = TextStyle(
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            ), textAlign = textAlign
        ),
        modifier = modifier.clickable(interactionSource, null) {
            onclick.invoke()
        }
    )
}
@Composable
fun TextView(
    text: String,
    modifier: Modifier = Modifier,
    textSize: TextUnit = _12Ssp,
    color: Color = Color.Black,
    font: Int = R.font.rb_400,
    maxLine: Int = 100,
    minLine:Int=1,
    singleLine: Boolean = false,
    txtStyle: TextStyle= TextStyle(),
    textAlign: TextAlign? = TextAlign.Start,
    onclick: () -> Unit = {}
) {
    Text(
        text = text,
        fontSize = textSize,
        textAlign = textAlign,
        maxLines = if (singleLine) 1 else maxLine,
        overflow = if (singleLine) TextOverflow.Ellipsis else TextOverflow.Clip,
        color = color,
        minLines = minLine,
        fontFamily = FontFamily(Font(font)),
        style = txtStyle.copy(
            platformStyle = PlatformTextStyle(
                includeFontPadding = false
            ),

        ),
        modifier = modifier.onClick { onclick.invoke() }
    )
}
@Composable
@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.onClick(onClick: () -> Unit): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
    )
}
@OptIn(ExperimentalFoundationApi::class)
fun Modifier.onLongClick(onLongClick: () -> Unit): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }

    this.combinedClickable(
        onClick = {
        },
        onLongClick = {
            onLongClick()
        },
        indication = null,
        interactionSource = interactionSource
    )
}


fun Modifier.dashedBorder(
    strokeWidth: Dp,
    color: Color,
    cornerRadius: Dp = 0.dp,
    dashLength: Float = 10f,
    gapLength: Float = 10f
): Modifier = this.then(
    Modifier.drawBehind {
        val stroke = Stroke(
            width = strokeWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, gapLength), 0f)
        )

        val width = size.width
        val height = size.height

        val corner = cornerRadius.toPx()

        drawRoundRect(
            color = color,
            size = Size(width, height),
            style = stroke,
            cornerRadius = CornerRadius(corner, corner)
        )
    }
)

@Composable
fun DotIndicator(count: Int = 3, current: Int = 0) {
    val activeDotSize = 16.dp
    val inactiveDotSize = 8.dp
    val activeDotColor = Color.Blue
    val inactiveDotColor = Color.Gray
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        for (i in 0 until count) {
            Box(
                modifier = Modifier
                    .width(if (i == current) activeDotSize else inactiveDotSize)
                    .height(inactiveDotSize)
                    .background(
                        color = if (i == current) activeDotColor else inactiveDotColor,
                        shape = CircleShape
                    )
            )
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DotIndicator(pagerState: PagerState, count: Int = pagerState.pageCount) {
    val activeDotSize = 10.dp
    val inactiveDotSize = 10.dp
    val activeDotColor = mainColor
    val inactiveDotColor = Color(0xffC5C5C5)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        for (i in 0 until count) {
            Box(
                modifier = Modifier
                    .width(if (i == pagerState.currentPage) activeDotSize else inactiveDotSize)
                    .height(inactiveDotSize)
                    .background(
                        color = if (i == pagerState.currentPage) activeDotColor else inactiveDotColor,
                        shape = CircleShape
                    )
            )
        }
    }
}



@Composable
fun IconView(
    item: Any,
    tint: Color? = null,
    icSize: Int = 24,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    onclick: (() -> Unit)? = null
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when (item) {
            is ImageVector -> Icon(
                item,
                contentDescription = "Image Vector",
                tint = tint ?: Color.Unspecified,
                modifier = Modifier.size(icSize.dp)
            )
            is Painter -> Icon(
                painter = item,
                contentDescription = "Painter",
                modifier = Modifier.size(icSize.dp),
                tint = tint ?: Color.Unspecified
            )
            is Int ->
                Icon(
                    painter = painterResource(item),
                    contentDescription = "Painter",
                    modifier = Modifier.size(icSize.dp),
                    tint = tint ?: Color.Unspecified
                )
        }
        Box(modifier = Modifier
            .matchParentSize()
            .onClick { onclick?.invoke() })
    }
}


















































