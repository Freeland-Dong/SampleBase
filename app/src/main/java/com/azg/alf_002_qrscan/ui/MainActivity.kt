package com.azg.alf_002_qrscan.ui

import android.content.Context
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.azg.alf_002_qrscan.base.BaseActivity
import com.azg.alf_002_qrscan.style.AppViewTheme

class MainActivity : BaseActivity(){
    @Composable
    override fun Initialize() {

    }
    @Composable
    override fun BoxScope.ContentView(
        innerPadding: InnerPadding,
        context: Context
    ) {
        Greeting("Android")
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppViewTheme {
        Greeting("Android")
    }
}