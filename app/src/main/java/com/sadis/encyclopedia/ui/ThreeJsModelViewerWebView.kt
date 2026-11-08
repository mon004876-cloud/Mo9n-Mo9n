package com.sadis.encyclopedia.ui

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
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
import androidx.webkit.WebViewAssetLoader

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
                // المحتوى يُقدَّم من assets عبر WebViewAssetLoader، فلا حاجة للوصول لملفات الجهاز
                allowFileAccess = false
                allowContentAccess = false
                mediaPlaybackRequiresUserGesture = false
                cacheMode = WebSettings.LOAD_DEFAULT
                mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                setGeolocationEnabled(false)
            }
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            webChromeClient = WebChromeClient()
            addJavascriptInterface(
                SadisJsBridge { clickedId ->
                    currentOnHotspotSelected(clickedId)
                },
                "AndroidBridge"
            )
            val assetLoader = WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
                .build()
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest
                ): WebResourceResponse? {
                    // كل ما هو خارج assets المحلية يُحجب (لا اتصال بالإنترنت من داخل المختبر)
                    return assetLoader.shouldInterceptRequest(request.url)
                        ?: WebResourceResponse("text/plain", "utf-8", 403, "Blocked", emptyMap(), null)
                }

                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    // منع الانتقال لأي صفحة أخرى حتى لا يصل محتوى خارجي إلى AndroidBridge
                    return request.url.host != WebViewAssetLoader.DEFAULT_DOMAIN
                }

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
            loadUrl("https://${WebViewAssetLoader.DEFAULT_DOMAIN}/assets/viewer3d.html")
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
