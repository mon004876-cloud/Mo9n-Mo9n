package com.example.ui

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

enum class Web3DEngineType(val jsKey: String, val labelAr: String) {
    THREE_JS("THREE_JS", "محرك Three.js WebGL 360°"),
    GOOGLE_MODEL_VIEWER("MODEL_VIEWER", "محرك Google <model-viewer> 360°"),
    HYBRID_CANVAS_3D("HYBRID_CANVAS", "محرك 3D + 4K المدمج")
}

private class SadisJsBridge(
    private val onPartClicked: (String) -> Unit
) {
    private val mainHandler = Handler(Looper.getMainLooper())

    @JavascriptInterface
    fun onHotspotSelected(partId: String) {
        mainHandler.post {
            onPartClicked(partId)
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ThreeJsAndModelViewerComposable(
    modelId: String,
    engineType: Web3DEngineType,
    selectedPartId: String,
    explodeFactor: Float,
    simulationIntensity: Float,
    autoRotate360: Boolean,
    cameraPresetTrigger: Pair<Float, Float>?,
    onHotspotSelectedFromWeb: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentOnHotspotSelected by rememberUpdatedState(onHotspotSelectedFromWeb)
    var pageLoaded by remember { mutableStateOf(false) }

    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                mediaPlaybackRequiresUserGesture = false
                cacheMode = WebSettings.LOAD_DEFAULT
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            webChromeClient = WebChromeClient()
            addJavascriptInterface(
                SadisJsBridge { clickedId ->
                    currentOnHotspotSelected(clickedId)
                },
                "AndroidBridge"
            )
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    pageLoaded = true
                }
            }
            // Prevent parent LazyColumn from intercepting 360° touch orbit drags inside WebView
            setOnTouchListener { v, event ->
                v.parent?.requestDisallowInterceptTouchEvent(true)
                false
            }
            loadUrl("file:///android_asset/viewer3d.html")
        }
    }

    // Push state changes to Three.js / Google <model-viewer> whenever parameters change
    LaunchedEffect(
        pageLoaded,
        modelId,
        engineType,
        selectedPartId,
        explodeFactor,
        simulationIntensity,
        autoRotate360
    ) {
        if (pageLoaded) {
            val jsCall = "window.Sadis3DBridge && window.Sadis3DBridge.updateSceneState(" +
                "'$modelId', '${engineType.jsKey}', '$selectedPartId', $explodeFactor, $simulationIntensity, $autoRotate360);"
            webView.evaluateJavascript(jsCall, null)
        }
    }

    // Apply 360° camera preset angle when user taps 0°, 90°, 180°, 270°, Top 90°
    LaunchedEffect(pageLoaded, cameraPresetTrigger) {
        if (pageLoaded && cameraPresetTrigger != null) {
            val (azim, polar) = cameraPresetTrigger
            val jsCall = "window.Sadis3DBridge && window.Sadis3DBridge.setCameraAngle($azim, $polar);"
            webView.evaluateJavascript(jsCall, null)
        }
    }

    DisposableEffect(webView) {
        onDispose {
            webView.stopLoading()
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier.fillMaxSize()
    )
}
