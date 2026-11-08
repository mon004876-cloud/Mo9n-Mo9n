package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.Hotspot3DPart
import com.example.data.Interactive4KModel
import com.example.data.IraqiCurriculumData
import com.example.data.ModelQuizScoreEntity
import com.example.ui.theme.CoralLightContainer
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.GoldLightContainer
import com.example.ui.theme.GoldTertiary
import com.example.ui.theme.OnCoralLightContainer
import com.example.ui.theme.OnGoldLightContainer
import com.example.ui.theme.OnTealLightContainer
import com.example.ui.theme.SoftMintSurface
import com.example.ui.theme.TealLightContainer
import com.example.ui.theme.TealPrimary
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlinx.coroutines.delay

enum class LabInteractionMode {
    EXPLORE_3D_DISSECT,
    PIN_DROP_QUIZ,
    LIVE_SIMULATION
}

private fun normalizeAngle360(deg: Float): Float {
    val mod = deg % 360f
    return if (mod < 0f) mod + 360f else mod
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Interactive4KLabScreen(
    selectedModelId: String,
    onSelectModel: (String) -> Unit,
    modelScores: List<ModelQuizScoreEntity>,
    onRecordQuizScore: (String, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val models = IraqiCurriculumData.interactive4KModels
    val activeModel = remember(selectedModelId) {
        models.find { it.modelId == selectedModelId } ?: models.first()
    }

    var interactionMode by remember { mutableStateOf(LabInteractionMode.EXPLORE_3D_DISSECT) }
    var web3DEngineType by remember { mutableStateOf(Web3DEngineType.THREE_JS) }
    var selectedPart by remember(activeModel) { mutableStateOf(activeModel.parts.first()) }

    // Unclamped Full 360° 3D Orbit Camera State (0° .. 360°)
    var yawAngleDeg by remember(activeModel) { mutableFloatStateOf(25f) }
    var pitchAngleDeg by remember(activeModel) { mutableFloatStateOf(75f) }
    var rollAngleDeg by remember(activeModel) { mutableFloatStateOf(0f) }
    var autoOrbit360 by remember { mutableStateOf(true) }
    var cameraPresetTrigger by remember { mutableStateOf<Pair<Float, Float>?>(null) }

    var zoomScale by remember(activeModel) { mutableFloatStateOf(1.05f) }
    var explodeLayersFactor by remember(activeModel) { mutableFloatStateOf(0.25f) }
    var simulationIntensity by remember(activeModel) { mutableFloatStateOf(2.5f) }

    // Continuous 360° Auto-Turntable & Live Particle Animation Loop for Hybrid mode
    var animPhase by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(autoOrbit360) {
        while (true) {
            delay(30L)
            animPhase = (animPhase + 0.035f) % 1f
            if (autoOrbit360 && web3DEngineType == Web3DEngineType.HYBRID_CANVAS_3D) {
                yawAngleDeg = normalizeAngle360(yawAngleDeg + 1.8f)
            }
        }
    }

    // Active Pin-Drop Challenge Quiz State
    var quizTargetIndex by remember(activeModel) { mutableIntStateOf(0) }
    var quizScore by remember(activeModel) { mutableIntStateOf(0) }
    var quizCompleted by remember(activeModel) { mutableStateOf(false) }
    var quizFeedbackMessage by remember(activeModel) {
        mutableStateOf("دوّر المجسم 360° واضغط مباشرة على الدبوس الصحيح لتثبيت المعلومة بيدك!")
    }

    val savedScoreEntity = modelScores.find { it.modelId == activeModel.modelId }

    val handlePartTapped: (Hotspot3DPart) -> Unit = { tappedPart ->
        if (interactionMode == LabInteractionMode.PIN_DROP_QUIZ && !quizCompleted) {
            val expectedPart = activeModel.parts[quizTargetIndex]
            if (tappedPart.partId == expectedPart.partId) {
                val newScore = quizScore + 1
                quizScore = newScore
                selectedPart = tappedPart
                if (quizTargetIndex + 1 < activeModel.parts.size) {
                    quizTargetIndex += 1
                    val nextTarget = activeModel.parts[quizTargetIndex]
                    quizFeedbackMessage = "عاشت إيدك! جواب وزاري مضبوط. الآن أين يقع: (${nextTarget.nameAr})؟"
                } else {
                    quizCompleted = true
                    quizFeedbackMessage = "بطل الـ 100! ختمت جميع تأشيرات الرسم الوزاري (${newScore}/${activeModel.parts.size}) بنجاح!"
                    onRecordQuizScore(activeModel.modelId, newScore, activeModel.parts.size)
                }
            } else {
                quizFeedbackMessage = "اقتربت! أنت ضغطت على (${tappedPart.nameAr})، بينما المطلوب هو: (${expectedPart.nameAr}). حاول مرة أخرى!"
            }
        } else {
            selectedPart = tappedPart
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Model Selector Chips & 3D Engine Switcher (Three.js vs Google <model-viewer>)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "مختبر رسوم 3D بدوران 360° (Three.js + Model Viewer):",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Engine Selector Chips: Three.js WebGL vs Google <model-viewer> vs Hybrid 4K
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(Web3DEngineType.entries) { engine ->
                        val isSelected = engine == web3DEngineType
                        FilterChip(
                            selected = isSelected,
                            onClick = { web3DEngineType = engine },
                            label = {
                                Text(
                                    text = engine.labelAr,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ViewInAr,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = TealLightContainer,
                                selectedLabelColor = OnTealLightContainer,
                                selectedLeadingIconColor = TealPrimary
                            ),
                            modifier = Modifier.testTag("engine_chip_${engine.name}")
                        )
                    }
                }

                // Curriculum 3D Models Selector Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(models, key = { it.modelId }) { model ->
                        val isSelected = model.modelId == activeModel.modelId
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectModel(model.modelId) },
                            label = {
                                Text(
                                    text = "${model.subject.titleAr.substringBefore(" -")}: ${model.titleAr.substringAfter(": ")}",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ViewInAr,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = model.subject.softBgColor,
                                selectedLabelColor = model.subject.accentColor,
                                selectedLeadingIconColor = model.subject.accentColor
                            ),
                            modifier = Modifier.testTag("select_4k_model_${model.modelId}")
                        )
                    }
                }
            }
        }

        // Interaction Mode Switcher: Explore 360° 3D vs Pin-Drop Quiz vs Live Simulation
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LabModeButton(
                    title = "1. دوران 360° 3D",
                    icon = Icons.Default.ViewInAr,
                    selected = interactionMode == LabInteractionMode.EXPLORE_3D_DISSECT,
                    onClick = { interactionMode = LabInteractionMode.EXPLORE_3D_DISSECT },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mode_explore_3d")
                )
                LabModeButton(
                    title = "2. تحدي التأشيرات",
                    icon = Icons.Default.Quiz,
                    selected = interactionMode == LabInteractionMode.PIN_DROP_QUIZ,
                    onClick = {
                        interactionMode = LabInteractionMode.PIN_DROP_QUIZ
                        quizTargetIndex = 0
                        quizScore = 0
                        quizCompleted = false
                        quizFeedbackMessage = "دوّر المجسم 360° واضغط على الدبوس الذي يمثل: (${activeModel.parts.first().nameAr})"
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mode_quiz_3d")
                )
                LabModeButton(
                    title = "3. محاكاة حية",
                    icon = Icons.Default.PlayArrow,
                    selected = interactionMode == LabInteractionMode.LIVE_SIMULATION,
                    onClick = { interactionMode = LabInteractionMode.LIVE_SIMULATION },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("mode_sim_3d")
                )
            }
        }

        // Interactive 360° 3D Viewport Card (Three.js / Google <model-viewer> / Hybrid 4K)
        item {
            Interactive360ViewportCard(
                model = activeModel,
                interactionMode = interactionMode,
                web3DEngineType = web3DEngineType,
                selectedPart = selectedPart,
                yawAngleDeg = yawAngleDeg,
                pitchAngleDeg = pitchAngleDeg,
                rollAngleDeg = rollAngleDeg,
                autoOrbit360 = autoOrbit360,
                onToggleAutoOrbit = { autoOrbit360 = !autoOrbit360 },
                zoomScale = zoomScale,
                explodeLayersFactor = explodeLayersFactor,
                simulationIntensity = simulationIntensity,
                animPhase = animPhase,
                cameraPresetTrigger = cameraPresetTrigger,
                quizTargetPart = activeModel.parts.getOrElse(quizTargetIndex) { activeModel.parts.first() },
                quizCompleted = quizCompleted,
                onOrbitDrag = { dx, dy ->
                    yawAngleDeg = normalizeAngle360(yawAngleDeg + dx * 0.65f)
                    pitchAngleDeg = normalizeAngle360(pitchAngleDeg - dy * 0.65f)
                },
                onResetCamera = {
                    autoOrbit360 = false
                    yawAngleDeg = 0f
                    pitchAngleDeg = 75f
                    rollAngleDeg = 0f
                    zoomScale = 1.05f
                    explodeLayersFactor = 0.25f
                    cameraPresetTrigger = 0f to 75f
                },
                onSetPresetAngle = { presetAzim, presetPolar ->
                    autoOrbit360 = false
                    yawAngleDeg = presetAzim
                    pitchAngleDeg = presetPolar
                    rollAngleDeg = 0f
                    cameraPresetTrigger = presetAzim to presetPolar
                },
                onHotspotTapped = handlePartTapped,
                onHotspotIdSelectedFromWeb = { partId ->
                    val found = activeModel.parts.find { it.partId == partId }
                    if (found != null) {
                        handlePartTapped(found)
                    }
                }
            )
        }

        // Mode-Specific 360° 3D Controls
        item {
            when (interactionMode) {
                LabInteractionMode.EXPLORE_3D_DISSECT -> {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ViewInAr,
                                        contentDescription = null,
                                        tint = TealPrimary
                                    )
                                    Text(
                                        text = "التحكم بالدوران 360° وتفكيك الطبقات الثلاثية الأبعاد:",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                                Surface(
                                    color = SoftMintSurface,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "أفقي: ${yawAngleDeg.toInt()}° | عمودي: ${pitchAngleDeg.toInt()}°",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = OnTealLightContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            // 360° Horizontal Yaw Slider
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("دوران أفقي (0°-360°):", style = MaterialTheme.typography.bodyMedium)
                                Slider(
                                    value = yawAngleDeg,
                                    onValueChange = {
                                        autoOrbit360 = false
                                        yawAngleDeg = it
                                        cameraPresetTrigger = yawAngleDeg to pitchAngleDeg
                                    },
                                    valueRange = 0f..360f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = TealPrimary,
                                        activeTrackColor = TealPrimary
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("slider_yaw_360")
                                )
                            }

                            // 360° Vertical Polar/Pitch Slider
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("دوران عمودي (0°-180°):", style = MaterialTheme.typography.bodyMedium)
                                Slider(
                                    value = pitchAngleDeg.coerceIn(5f, 175f),
                                    onValueChange = {
                                        autoOrbit360 = false
                                        pitchAngleDeg = it
                                        cameraPresetTrigger = yawAngleDeg to pitchAngleDeg
                                    },
                                    valueRange = 5f..175f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = GoldTertiary,
                                        activeTrackColor = GoldTertiary
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("slider_pitch_360")
                                )
                            }

                            // 3D Layer Explosion Slider
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text("تفكيك طبقات 3D:", style = MaterialTheme.typography.bodyMedium)
                                Slider(
                                    value = explodeLayersFactor,
                                    onValueChange = { explodeLayersFactor = it },
                                    valueRange = 0f..1f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = TealPrimary,
                                        activeTrackColor = TealPrimary
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("slider_explode_3d")
                                )
                            }
                        }
                    }
                }

                LabInteractionMode.PIN_DROP_QUIZ -> {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = GoldLightContainer),
                        border = BorderStroke(1.5.dp, GoldTertiary)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = GoldTertiary
                                    )
                                    Text(
                                        text = "تحدي تثبيت التأشيرات الوزارية على مجسم 360°",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = OnGoldLightContainer
                                    )
                                }

                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = "النتيجة: $quizScore / ${activeModel.parts.size}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = OnGoldLightContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Text(
                                text = quizFeedbackMessage,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = OnGoldLightContainer
                            )

                            if (savedScoreEntity != null) {
                                Text(
                                    text = "🏆 أفضل درجة سابقة لك في هذا المجسم: ${savedScoreEntity.bestScore}/${savedScoreEntity.totalParts} (عدد مرات التدريب: ${savedScoreEntity.completedCount})",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = OnGoldLightContainer
                                )
                            }

                            if (quizCompleted) {
                                Button(
                                    onClick = {
                                        quizTargetIndex = 0
                                        quizScore = 0
                                        quizCompleted = false
                                        quizFeedbackMessage = "دوّر المجسم 360° واضغط على الدبوس الذي يمثل: (${activeModel.parts.first().nameAr})"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("إعادة تحدي الرسم الوزاري")
                                }
                            }
                        }
                    }
                }

                LabInteractionMode.LIVE_SIMULATION -> {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = SoftMintSurface),
                        border = BorderStroke(1.5.dp, TealPrimary)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "المختبر التفاعلي الحي 3D: ${activeModel.simulationParamLabel}",
                                style = MaterialTheme.typography.titleMedium,
                                color = OnTealLightContainer
                            )

                            Slider(
                                value = simulationIntensity,
                                onValueChange = { simulationIntensity = it },
                                valueRange = 1f..6f,
                                colors = SliderDefaults.colors(
                                    thumbColor = TealPrimary,
                                    activeTrackColor = TealPrimary
                                )
                            )

                            val liveReadout = when (activeModel.modelId) {
                                "model_phys_capacitor" -> {
                                    val c0 = 10f
                                    val v0 = 12f
                                    val ck = c0 * simulationIntensity
                                    val vkDisconnected = v0 / simulationIntensity
                                    "ثابت العزل (k = ${"%.1f".format(simulationIntensity)}) ← السعة تزداد إلى Ck = ${"%.1f".format(ck)} μF | فرق الجهد للمتسعة المفصولة يهبط إلى ΔVk = ${"%.2f".format(vkDisconnected)} V"
                                }
                                "model_chem_daniell" -> {
                                    val currentMa = 15f * simulationIntensity
                                    "سرعة تدفق الإلكترونات عبر السلك = ${"%.1f".format(currentMa)} mA | جهد خلية دانيال القياسي E°cell = +1.10 V (تفاعل تلقائي ΔG سالبة)"
                                }
                                else -> {
                                    val atpCount = (38 * simulationIntensity).toInt()
                                    "نشاط الأعراف والإنزيمات التنفسية = ${"%.1f".format(simulationIntensity)}x ← إنتاج طاقة: $atpCount جزيئة ATP في الثانية!"
                                }
                            }

                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = liveReadout,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnTealLightContainer,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Selected 3D Part Official Textbook Details Card (Location + Function + Humor Hook)
        item {
            Selected3DPartDetailCard(
                part = selectedPart,
                allParts = activeModel.parts,
                onSelectPartChip = { selectedPart = it }
            )
        }
    }
}

@Composable
private fun LabModeButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (selected) TealPrimary else Color.White,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (selected) TealPrimary else MaterialTheme.colorScheme.outline),
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color.White else TealPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun Interactive360ViewportCard(
    model: Interactive4KModel,
    interactionMode: LabInteractionMode,
    web3DEngineType: Web3DEngineType,
    selectedPart: Hotspot3DPart,
    yawAngleDeg: Float,
    pitchAngleDeg: Float,
    rollAngleDeg: Float,
    autoOrbit360: Boolean,
    onToggleAutoOrbit: () -> Unit,
    zoomScale: Float,
    explodeLayersFactor: Float,
    simulationIntensity: Float,
    animPhase: Float,
    cameraPresetTrigger: Pair<Float, Float>?,
    quizTargetPart: Hotspot3DPart,
    quizCompleted: Boolean,
    onOrbitDrag: (Float, Float) -> Unit,
    onResetCamera: () -> Unit,
    onSetPresetAngle: (Float, Float) -> Unit,
    onHotspotTapped: (Hotspot3DPart) -> Unit,
    onHotspotIdSelectedFromWeb: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(2.dp, model.subject.accentColor.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top Header bar inside 4K/3D Viewport
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(model.subject.softBgColor)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.titleAr,
                        style = MaterialTheme.typography.titleMedium,
                        color = model.subject.accentColor
                    )
                    Text(
                        text = model.textbookRef,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Auto-Rotate 360° Turntable Button
                    Button(
                        onClick = onToggleAutoOrbit,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (autoOrbit360) CoralSecondary else TealPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("toggle_auto_orbit_360_btn")
                    ) {
                        Icon(
                            imageVector = if (autoOrbit360) Icons.Default.Pause else Icons.Default.Autorenew,
                            contentDescription = "دوران تلقائي 360 درجة",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (autoOrbit360) "إيقاف 360°" else "دوران 360°",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    OutlinedButton(
                        onClick = onResetCamera,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("reset_3d_camera_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CenterFocusStrong,
                            contentDescription = "إعادة ضبط زاوية 3D",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Interactive 360° 3D Viewport Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .background(Color(0xFF0B1120))
                    .testTag("interactive_4k_canvas_box")
            ) {
                if (web3DEngineType == Web3DEngineType.THREE_JS ||
                    web3DEngineType == Web3DEngineType.GOOGLE_MODEL_VIEWER
                ) {
                    // Real Three.js WebGL 360° Scene & Google <model-viewer> GLTF Engine
                    ThreeJsAndModelViewerComposable(
                        modelId = model.modelId,
                        engineType = web3DEngineType,
                        selectedPartId = selectedPart.partId,
                        explodeFactor = explodeLayersFactor,
                        simulationIntensity = simulationIntensity,
                        autoRotate360 = autoOrbit360,
                        cameraPresetTrigger = cameraPresetTrigger,
                        onHotspotSelectedFromWeb = onHotspotIdSelectedFromWeb,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Hybrid 4K + Native 360° Volumetric 3D Canvas
                    val yawRad = (yawAngleDeg * PI / 180.0).toFloat()
                    val pitchRad = (pitchAngleDeg * PI / 180.0).toFloat()
                    val facingAlpha = (0.32f + 0.28f * abs(cos(yawRad) * cos(pitchRad))).coerceIn(0.22f, 0.62f)

                    Image(
                        painter = painterResource(id = model.render4KRes),
                        contentDescription = model.titleAr,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = zoomScale * (if (cos(yawRad) < 0f) -1f else 1f)
                                scaleY = zoomScale * (if (cos(pitchRad) < 0f) -1f else 1f)
                                rotationY = yawAngleDeg
                                rotationX = pitchAngleDeg
                                rotationZ = rollAngleDeg
                                alpha = facingAlpha
                                cameraDistance = 18f * density
                            }
                    )

                    Volumetric3DSceneCanvas(
                        model = model,
                        selectedPart = selectedPart,
                        yawAngleDeg = yawAngleDeg,
                        pitchAngleDeg = pitchAngleDeg,
                        rollAngleDeg = rollAngleDeg,
                        zoomScale = zoomScale,
                        explodeLayersFactor = explodeLayersFactor,
                        simulationIntensity = simulationIntensity,
                        animPhase = animPhase,
                        showVolumetricMesh = true,
                        onOrbitDrag = onOrbitDrag,
                        onHotspotTapped = onHotspotTapped
                    )
                }

                // Bottom Instruction Overlay inside 360° Viewport
                Surface(
                    color = Color.Black.copy(alpha = 0.72f),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Text(
                        text = if (interactionMode == LabInteractionMode.PIN_DROP_QUIZ) {
                            if (quizCompleted) "أحسنت! أتممت جميع التأشيرات الوزارية"
                            else "🎯 دوّر المجسم 360° وانقر على التأشير الخاص بـ: ${quizTargetPart.nameAr}"
                        } else {
                            "اسحب بإصبعك لتدوير المجسم 360° عبر (${web3DEngineType.labelAr}) أو انقر على أي تأشير"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            // Quick 360° Camera Angle Presets Bar (0°, 90°, 180°, 270°, Top)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "زوايا 360°:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                AnglePresetChip("أمام 0°", 0f, 75f, onSetPresetAngle)
                AnglePresetChip("يمين 90°", 90f, 75f, onSetPresetAngle)
                AnglePresetChip("خلف 180°", 180f, 75f, onSetPresetAngle)
                AnglePresetChip("يسار 270°", 270f, 75f, onSetPresetAngle)
                AnglePresetChip("علوي", 0f, 15f, onSetPresetAngle)
            }
        }
    }
}

@Composable
private fun AnglePresetChip(
    label: String,
    azim: Float,
    polar: Float,
    onSelect: (Float, Float) -> Unit
) {
    Surface(
        color = TealLightContainer,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.4f)),
        modifier = Modifier.clickable { onSelect(azim, polar) }
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = OnTealLightContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun Selected3DPartDetailCard(
    part: Hotspot3DPart,
    allParts: List<Hotspot3DPart>,
    onSelectPartChip: (Hotspot3DPart) -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, TealPrimary.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "التأشيرات الوزارية المعتمدة في الرسم (اضغط للتنقل السريع):",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Quick Hotspot Selector Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(allParts, key = { it.partId }) { itemPart ->
                    val isCurrent = itemPart.partId == part.partId
                    Surface(
                        color = if (isCurrent) TealPrimary else TealLightContainer,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .clickable { onSelectPartChip(itemPart) }
                            .testTag("part_chip_${itemPart.partId}")
                    ) {
                        Text(
                            text = itemPart.nameAr,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isCurrent) Color.White else OnTealLightContainer,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Selected Part Details
            Surface(
                color = SoftMintSurface,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = part.nameAr,
                            style = MaterialTheme.typography.titleLarge,
                            color = OnTealLightContainer
                        )
                        Surface(
                            color = CoralLightContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = part.wizariFrequency,
                                style = MaterialTheme.typography.labelMedium,
                                color = OnCoralLightContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = "📍 الموقع (نص الكتاب المقرر): ${part.locationTextbook}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "⚡ الأهمية / الوظيفة (نص الكتاب المقرر): ${part.functionTextbook}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📋 الصيغة المعتمدة في الكنترول: ${part.controlKeyPhrase}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = OnTealLightContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Surface(
                        color = GoldLightContainer,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "😄 ${part.wittyMemoryHook}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = OnGoldLightContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}
