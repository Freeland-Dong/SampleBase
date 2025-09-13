package com.azg.sample.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.azg.sample.app.PreferenceData
import com.azg.sample.style.AppViewTheme
import com.azg.sample.style.pxToDp
import com.azg.sample.style.white
import java.io.Serializable
@Suppress("DEPRECATION")
abstract class BaseActivity(private var lightStatus: Boolean = true) : AppCompatActivity() {
    data class InnerPadding(
        val top: Dp = 0.dp,
        val left: Dp = 0.dp,
        val right: Dp = 0.dp,
        val bottom: Dp = 0.dp,
        val keyboardHeight: Dp = 0.dp
    )

    data class KeyboardState(val visible: Boolean, val heightPx: Int)

    private var observer: DefaultLifecycleObserver? = null
    private val statusBarHeight: Int
        @SuppressLint("DiscouragedApi", "InternalInsetResource")
        get() = resources.getIdentifier("status_bar_height", "dimen", "android")
            .takeIf { it > 0 }?.let { resources.getDimensionPixelSize(it) } ?: 0
    @Composable
    abstract fun Initialize()
    @Composable
    abstract fun BoxScope.ContentView(innerPadding: InnerPadding, context: Context)
    @Composable
    open fun ViewDialog() {
    }
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupSystemBars()
        setContent { ComposeContent() }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        newBase?.let {
            PreferenceData.setLocale(newBase, PreferenceData.languageCode)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        observer?.let { lifecycle.removeObserver(it) }
    }
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    private fun ComposeContent() {
        Initialize()
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        val layoutDirection = LocalLayoutDirection.current
        val sysPv = WindowInsets.systemBars.asPaddingValues()
        var animatedBottom by remember { mutableIntStateOf(0) }
        var isVisible by remember { mutableStateOf(false) }
        val content = findViewById<View>(android.R.id.content)
        content.observeKeyboardState(this) { state ->
            animatedBottom = state.heightPx
            isVisible = state.visible
        }
        CompositionLocalProvider(LocalKeyboardShow provides isVisible) {
            AppViewTheme(lightStatus) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = animatedBottom.pxToDp())
                        .background(white),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectTapGestures(onTap = {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                })
                            }
                    ) {
                        val innerPadding = InnerPadding(
                            top = statusBarHeight.pxToDp(),
                            left = sysPv.calculateLeftPadding(layoutDirection),
                            right = sysPv.calculateRightPadding(layoutDirection),
                            bottom = animatedBottom.pxToDp(),
                            keyboardHeight = animatedBottom.pxToDp()
                        )
                        ContentView(innerPadding, applicationContext)
                    }
                    ViewDialog()
                }
            }
        }
    }

    fun View.observeKeyboardState(
        owner: LifecycleOwner,
        onChanged: (KeyboardState) -> Unit
    ): () -> Unit {
        val root = this
        val applyListener = { _: View, insets: WindowInsetsCompat ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val visible = insets.isVisible(WindowInsetsCompat.Type.ime())
            onChanged(KeyboardState(visible, imeInsets.bottom))
            insets
        }
        val compatListener = androidx.core.view.OnApplyWindowInsetsListener(applyListener)
        val animCallback =
            object : WindowInsetsAnimationCompat.Callback(DISPATCH_MODE_CONTINUE_ON_SUBTREE) {
                override fun onProgress(
                    insets: WindowInsetsCompat,
                    runningAnimations: MutableList<WindowInsetsAnimationCompat>
                ): WindowInsetsCompat {
                    val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
                    val visible = insets.isVisible(WindowInsetsCompat.Type.ime())
                    onChanged(KeyboardState(visible, imeInsets.bottom))
                    return insets
                }
            }
        val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            val r = Rect()
            root.getWindowVisibleDisplayFrame(r)
            val screenHeight = root.rootView.height
            val heightDiff = (screenHeight - r.bottom).coerceAtLeast(0)
            val thresholdPx = (root.resources.displayMetrics.density * 100).toInt()
            val visible = heightDiff > thresholdPx
            val height = if (visible) heightDiff else 0
            onChanged(KeyboardState(visible, height))
        }

        fun cleanup() {
            ViewCompat.setOnApplyWindowInsetsListener(root, null)
            ViewCompat.setWindowInsetsAnimationCallback(root, null)
            root.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
        }

        observer = object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) = cleanup()
        }

        ViewCompat.setOnApplyWindowInsetsListener(root, compatListener)
        ViewCompat.setWindowInsetsAnimationCallback(root, animCallback)
        root.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
        observer?.let { owner.lifecycle.addObserver(it) }

        root.post { onChanged(root.currentKeyboardState()) }
        return { cleanup() }
    }

    fun View.currentKeyboardState(): KeyboardState {
        val insets = ViewCompat.getRootWindowInsets(this) ?: return KeyboardState(
            visible = false,
            heightPx = 0
        )
        val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
        val visible = insets.isVisible(WindowInsetsCompat.Type.ime())
        return KeyboardState(visible, imeInsets.bottom)
    }

    private fun setupSystemBars() {
        window.hideSystemBar()
        window.setFullScreen()
    }

    private var activityResultCallback: ((ActivityResult) -> Unit)? = null
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            listenerResult(result)
            activityResultCallback?.invoke(result)
            activityResultCallback = null
        }

    inline fun <reified T : Any?> Activity.getData(key: String?): T? = key?.let { k ->
        intent?.extras?.takeIf { it.containsKey(k) }?.let { extras ->
            @Suppress("UNCHECKED_CAST", "DEPRECATION")
            when {
                T::class == Int::class -> intent.getIntExtra(k, 0) as T
                T::class == Boolean::class -> intent.getBooleanExtra(k, false) as T
                T::class == String::class -> intent.getStringExtra(k) as T
                T::class == Float::class -> intent.getFloatExtra(k, 0f) as T
                T::class == Long::class -> intent.getLongExtra(k, 0L) as T
                T::class == Double::class -> intent.getDoubleExtra(k, 0.0) as T
                T::class == Char::class -> intent.getCharExtra(k, Char.MIN_VALUE) as T
                T::class == CharSequence::class -> intent.getCharSequenceExtra(k) as T
                extras.get(k) is ArrayList<*> -> extras.get(k) as T
                Parcelable::class.java.isAssignableFrom(T::class.java) -> intent.getParcelableExtra(
                    k
                ) as? T
                Serializable::class.java.isAssignableFrom(T::class.java) -> intent.getSerializableExtra(
                    k
                ) as? T
                else -> throw IllegalArgumentException("Unsupported type ${T::class.java}")
            }
        }
    }

    inline fun <reified T : Any> ActivityResult.getResultData(key: String): T? =
        data?.extras?.takeIf { it.containsKey(key) }?.let { extras ->
            @Suppress("UNCHECKED_CAST", "DEPRECATION")
            when {
                T::class == Int::class -> data!!.getIntExtra(key, 0) as T
                T::class == Boolean::class -> data!!.getBooleanExtra(key, false) as T
                T::class == String::class -> data!!.getStringExtra(key) as T
                T::class == Float::class -> data!!.getFloatExtra(key, 0f) as T
                T::class == Long::class -> data!!.getLongExtra(key, 0L) as T
                T::class == Double::class -> data!!.getDoubleExtra(key, 0.0) as T
                T::class == Char::class -> data!!.getCharExtra(key, Char.MIN_VALUE) as T
                T::class == CharSequence::class -> data!!.getCharSequenceExtra(key) as T
                extras.get(key) is ArrayList<*> -> extras.get(key) as T
                Parcelable::class.java.isAssignableFrom(T::class.java) -> data!!.getParcelableExtra(
                    key
                ) as? T
                Serializable::class.java.isAssignableFrom(T::class.java) -> data!!.getSerializableExtra(
                    key
                ) as? T
                else -> throw IllegalArgumentException("Unsupported type ${T::class.java}")
            }
        }

    internal inline fun <reified T : Any> Context.launchActivity(params: Map<String, Any?> = emptyMap()) {
        Intent(this, T::class.java).apply { putExtras(params) }.let { intent ->
            if (this is Activity) startActivity(intent) else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    internal inline fun <reified T : Any> launcherForResult(
        params: Map<String, Any?> = emptyMap(),
        noinline dataResult: (ActivityResult) -> Unit = { _ -> }
    ) {
        activityResultCallback = dataResult
        Intent(this, T::class.java).apply { putExtras(params) }.let(resultLauncher::launch)
    }

    open fun listenerResult(result: ActivityResult) {}
    private fun Intent.putExtras(params: Map<String, Any?>) {
        params.forEach { (key, value) ->
            when (value) {
                null -> putExtra(key, null as Serializable?)
                is Int, is Long, is Boolean, is Float, is Double, is String, is Char, is CharSequence, is Parcelable, is Serializable ->
                    putExtra(key, value as Serializable)
                is List<*> -> when {
                    value.all { it is Parcelable } -> putParcelableArrayListExtra(
                        key,
                        ArrayList(value as List<Parcelable>)
                    )
                    value.all { it is String } -> putStringArrayListExtra(
                        key,
                        ArrayList(value as List<String>)
                    )
                    else -> throw IllegalArgumentException("Unsupported List element type for key=\"$key\"")
                }
                else -> throw IllegalArgumentException("Unsupported extra type for key=\"$key\": ${value.javaClass}")
            }
        }
    }
}

