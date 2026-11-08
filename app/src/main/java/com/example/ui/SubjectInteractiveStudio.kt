package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.TouchApp
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.SubjectType
import com.example.data.TextbookLine
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
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Interactive Subject Presence Studio:
 * Gives each of the 7 Iraqi 6th Prep subjects a distinctive, living, hands-on interactive simulator
 * right inside the textbook page so the student understands visually, enjoys, and participates actively
 * instead of passively reading narrative text blocks.
 */
@Composable
fun SubjectInteractivePresenceCard(
    subject: SubjectType,
    onOpen3DLab: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, subject.accentColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("subject_interactive_studio_${subject.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            subject.softBgColor,
                            Color.White
                        )
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Studio Header Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = subject.accentColor,
                        shape = CircleShape,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.TouchApp,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "مختبر الفهم والمشاركة الحي • ${subject.titleAr}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = subject.accentColor
                        )
                        Text(
                            text = "حرّك وجرّب بيدك لتفهم الفكرة العلمية قبل أن تحفظ السطر الوزاري",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = GoldLightContainer,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, GoldTertiary)
                ) {
                    Text(
                        text = "تفاعل حي",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnGoldLightContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Dispatch to the specialized interactive engine for each of the 7 subjects
            when (subject) {
                SubjectType.BIOLOGY -> BiologyLiveCellSimulator(subject, onOpen3DLab)
                SubjectType.PHYSICS -> PhysicsLiveCapacitorSimulator(subject, onOpen3DLab)
                SubjectType.CHEMISTRY -> ChemistryLiveThermodynamicsSimulator(subject, onOpen3DLab)
                SubjectType.MATHEMATICS -> MathLiveComplexPlaneSimulator(subject)
                SubjectType.ARABIC -> ArabicLiveGrammarDissector(subject)
                SubjectType.ENGLISH -> EnglishLiveStoryTimelineLab(subject)
                SubjectType.ISLAMIC -> IslamicLiveTajweedRadar(subject)
            }
        }
    }
}

@Composable
private fun BiologyLiveCellSimulator(
    subject: SubjectType,
    onOpen3DLab: () -> Unit
) {
    var cristaeFolds by remember { mutableFloatStateOf(5f) }
    var selectedOrganelleIndex by remember { mutableIntStateOf(0) }
    val pulseTransition = rememberInfiniteTransition(label = "bio_pulse")
    val pulsePhase by pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bio_pulse_val"
    )

    val atpOutput = (cristaeFolds * 38).roundToInt()

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Interactive Canvas of the Mitochondria Cristae Folding & Live ATP Generation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(145.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF064E3B))
                .padding(12.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                // Outer membrane
                drawRoundRect(
                    color = Color(0xFF10B981),
                    topLeft = Offset(w * 0.08f, h * 0.15f),
                    size = Size(w * 0.84f, h * 0.7f),
                    cornerRadius = CornerRadius(60f, 60f),
                    style = Stroke(width = 6f)
                )
                // Inner Cristae folds responding to student slider
                val foldCount = cristaeFolds.toInt().coerceIn(2, 10)
                val stepX = (w * 0.72f) / foldCount
                for (i in 0 until foldCount) {
                    val x = w * 0.14f + i * stepX
                    drawLine(
                        color = Color(0xFF6EE7B7),
                        start = Offset(x, h * 0.16f),
                        end = Offset(x, h * (0.42f + 0.12f * pulsePhase)),
                        strokeWidth = 5f
                    )
                    drawLine(
                        color = Color(0xFF6EE7B7),
                        start = Offset(x + stepX * 0.5f, h * 0.84f),
                        end = Offset(x + stepX * 0.5f, h * (0.58f - 0.12f * pulsePhase)),
                        strokeWidth = 5f
                    )
                    // Glowing ATP energy particles
                    drawCircle(
                        color = Color(0xFFFACC15),
                        radius = 7f + 4f * pulsePhase,
                        center = Offset(x, h * 0.5f)
                    )
                }
            }

            Surface(
                color = Color(0xCC022C22),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = "طيات الأعراف: ${cristaeFolds.toInt()} • إنتاج الطاقة: $atpOutput جزيئة ATP",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFFDE047),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Text(
            text = "جرّب زيادة انثناءات الأعراف (Cristae) وشاهد كيف تزداد المساحة السطحية وإنتاج جزيئات ATP:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Slider(
            value = cristaeFolds,
            onValueChange = { cristaeFolds = it },
            valueRange = 2f..10f,
            steps = 7,
            colors = SliderDefaults.colors(
                thumbColor = subject.accentColor,
                activeTrackColor = subject.accentColor
            ),
            modifier = Modifier.testTag("bio_cristae_slider")
        )

        // Interactive Organelle Cause-and-Effect Quick Matcher
        val organelles = listOf(
            Triple("الأعراف", "الطبقة الداخلية للمايتوكوندريا", "زيادة المساحة السطحية للطبقة الداخلية"),
            Triple("الرايبوسومات", "على سطوح الشبكة البلازمية الخشنة", "بناء البروتينات في الخلية"),
            Triple("الدكتيوسوم", "جهاز كولجي في الخلية النباتية", "بناء السيليلوز وبعض مكونات الجدار الخلوي")
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            organelles.forEachIndexed { index, item ->
                FilterChip(
                    selected = selectedOrganelleIndex == index,
                    onClick = { selectedOrganelleIndex = index },
                    label = { Text(item.first, fontWeight = FontWeight.Bold) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        val currentOrg = organelles[selectedOrganelleIndex]
        Surface(
            color = SoftMintSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, subject.accentColor.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("• الموقع بالكتاب: ${currentOrg.second}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text("• الوظيفة بالكنترول: ${currentOrg.third}", style = MaterialTheme.typography.bodyMedium, color = subject.accentColor, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PhysicsLiveCapacitorSimulator(
    subject: SubjectType,
    onOpen3DLab: () -> Unit
) {
    var dielectricK by remember { mutableFloatStateOf(1f) }
    var isBatteryDisconnected by remember { mutableStateOf(true) }

    val baseV = 12f
    val baseC = 10f
    val currentC = baseC * dielectricK
    val currentV = if (isBatteryDisconnected) (baseV / dielectricK) else baseV
    val currentE = if (isBatteryDisconnected) (100f / dielectricK) else 100f

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(145.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1E1B4B))
                .padding(12.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                // Top plate (+Q)
                drawRoundRect(
                    color = Color(0xFFEF4444),
                    topLeft = Offset(w * 0.12f, h * 0.14f),
                    size = Size(w * 0.76f, 16f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                // Bottom plate (-Q)
                drawRoundRect(
                    color = Color(0xFF3B82F6),
                    topLeft = Offset(w * 0.12f, h * 0.80f),
                    size = Size(w * 0.76f, 16f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                // Dielectric slab sliding in based on k > 1
                val insertionRatio = ((dielectricK - 1f) / 5f).coerceIn(0f, 1f)
                if (insertionRatio > 0.02f) {
                    drawRoundRect(
                        color = Color(0xFFF59E0B).copy(alpha = 0.65f),
                        topLeft = Offset(w * 0.14f, h * 0.28f),
                        size = Size(w * 0.72f * insertionRatio, h * 0.46f),
                        cornerRadius = CornerRadius(12f, 12f)
                    )
                }
                // Electric field lines
                for (i in 1..5) {
                    val x = w * (0.15f + i * 0.12f)
                    drawLine(
                        color = Color(0xFF38BDF8),
                        start = Offset(x, h * 0.24f),
                        end = Offset(x, h * 0.78f),
                        strokeWidth = 3f
                    )
                }
            }

            Surface(
                color = Color(0xD90F172A),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = "ثابت العزل k=${"%.1f".format(dielectricK)} • السعة Ck=${"%.0f".format(currentC)}μF • فرق الجهد Vk=${"%.1f".format(currentV)}V",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = isBatteryDisconnected,
                onClick = { isBatteryDisconnected = true },
                label = { Text("متسعة مفصولة عن المصدر (Q ثابت)") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = !isBatteryDisconnected,
                onClick = { isBatteryDisconnected = false },
                label = { Text("متسعة متصلة بالمصدر (ΔV ثابت)") },
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "أدخل اللوح العازل الكهربائي (k) بيدك ولاحظ تأثير المجال المعاكس Ed على المجال المحصل Ek = E - Ed:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Slider(
            value = dielectricK,
            onValueChange = { dielectricK = it },
            valueRange = 1f..6f,
            colors = SliderDefaults.colors(
                thumbColor = subject.accentColor,
                activeTrackColor = subject.accentColor
            ),
            modifier = Modifier.testTag("physics_dielectric_slider")
        )

        Surface(
            color = SoftMintSurface,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, subject.accentColor.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isBatteryDisconnected) {
                    "الاستنتاج الوزاري (مفصولة): السعة تزداد (Ck = kC)، وفرق الجهد يقل (ΔVk = ΔV/k)، والمجال الكهربائي يقل بنسبة (${"%.0f".format(currentE)}%)."
                } else {
                    "الاستنتاج الوزاري (متصلة): السعة تزداد (Ck = kC)، وفرق الجهد يبقى ثابتاً (ΔVk = ΔV)، والشحنة المختزنة تزداد (Qk = kQ)."
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = OnTealLightContainer,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Composable
private fun ChemistryLiveThermodynamicsSimulator(
    subject: SubjectType,
    onOpen3DLab: () -> Unit
) {
    var isExothermic by remember { mutableStateOf(true) }
    var tempKelvin by remember { mutableFloatStateOf(298f) }

    val deltaH = if (isExothermic) -92f else +68f
    val deltaS = 0.18f // kJ/K.mol
    val deltaG = deltaH - (tempKelvin * deltaS / 3f)
    val isSpontaneous = deltaG < 0f

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = isExothermic,
                onClick = { isExothermic = true },
                label = { Text("تفاعل باعث للحرارة (ΔH سالبة)") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = !isExothermic,
                onClick = { isExothermic = false },
                label = { Text("تفاعل ماص للحرارة (ΔH موجبة)") },
                modifier = Modifier.weight(1f)
            )
        }

        Surface(
            color = if (isSpontaneous) SoftMintSurface else CoralLightContainer,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.5.dp, if (isSpontaneous) subject.accentColor else CoralSecondary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "معادلة كبس الحرة: ΔG = ΔH - TΔS  ⟵  ΔG = ${"%.1f".format(deltaG)} kJ/mol",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isSpontaneous) {
                        "✓ التفاعل تلقائي (ΔG سالبة) عند درجة حرارة ${tempKelvin.toInt()} K"
                    } else {
                        "✗ التفاعل غير تلقائي (ΔG موجبة) عند درجة حرارة ${tempKelvin.toInt()} K — ارفع درجة الحرارة T ليصبح تلقائياً!"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSpontaneous) subject.accentColor else OnCoralLightContainer
                )
            }
        }

        Text(
            text = "غيّر درجة الحرارة المطلقة (T = ${tempKelvin.toInt()} K) وراقب متى يتغلب المقدار (TΔS) على (ΔH):",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Slider(
            value = tempKelvin,
            onValueChange = { tempKelvin = it },
            valueRange = 200f..1500f,
            colors = SliderDefaults.colors(
                thumbColor = subject.accentColor,
                activeTrackColor = subject.accentColor
            )
        )
    }
}

@Composable
private fun MathLiveComplexPlaneSimulator(
    subject: SubjectType
) {
    var powerOfI by remember { mutableIntStateOf(1) }
    val remainder = ((powerOfI % 4) + 4) % 4
    val resultSymbol = when (remainder) {
        0 -> "1"
        1 -> "i"
        2 -> "-1"
        else -> "-i"
    }
    val angleDegrees = remainder * 90

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Live Argand Diagram Canvas
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E1B4B))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f
                    val r = size.width * 0.36f
                    // Axes
                    drawLine(Color.White.copy(alpha = 0.35f), Offset(0f, cy), Offset(size.width, cy), 2f)
                    drawLine(Color.White.copy(alpha = 0.35f), Offset(cx, 0f), Offset(cx, size.height), 2f)
                    // Unit circle
                    drawCircle(Color(0xFFA78BFA), radius = r, center = Offset(cx, cy), style = Stroke(3f))
                    // Vector for i^n
                    val rad = -angleDegrees * (PI / 180.0)
                    val vx = cx + (r * cos(rad)).toFloat()
                    val vy = cy + (r * sin(rad)).toFloat()
                    drawLine(Color(0xFFFACC15), Offset(cx, cy), Offset(vx, vy), strokeWidth = 6f)
                    drawCircle(Color(0xFFFACC15), radius = 10f, center = Offset(vx, vy))
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "عجلة قوى (i) ومستوى أرجاند:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = subject.accentColor
                )
                Text(
                    text = "الأس الحالي: i^$powerOfI = i^(4×${powerOfI / 4} + $remainder) = $resultSymbol",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "زاوية الدوران على المحور: $angleDegrees° (كل ضرب في i يدير المتجه 90° عكس عقارب الساعة!)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = { powerOfI += 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = subject.accentColor)
                    ) {
                        Text("اضرب في i (+90°)")
                    }
                    OutlinedButton(onClick = { powerOfI += 13 }) {
                        Text("جرّب أس كبير")
                    }
                }
            }
        }
    }
}

@Composable
private fun ArabicLiveGrammarDissector(
    subject: SubjectType
) {
    var selectedExampleIndex by remember { mutableIntStateOf(0) }
    val examples = listOf(
        Triple(
            "قال تعالى: ﴿وَمَا تِلْكَ بِيَمِينِكَ يَا مُوسَى﴾",
            listOf("مَا: اسم استفهام (لغير العاقل)", "تِلْكَ: اسم إشارة (معرفة) المستفهم عنه", "الحكم الإعرابي: في محل رفع مبتدأ أو خبر مقدم"),
            "القاعدة الوزارية: تلاها اسم معرفة (تلك)، فيجوز فيها الوجهان الإعرابيان!"
        ),
        Triple(
            "قال تعالى: ﴿يَسْأَلُونَكَ عَنِ السَّاعَةِ أَيَّانَ مُرْسَاهَا﴾",
            listOf("أَيَّانَ: اسم استفهام للزمان المستقبل (يفيد التهويل)", "مُرْسَاهَا: اسم معرفة (مضاف إلى الضمير ها)", "الحكم الإعرابي: في محل رفع خبر مقدم وجوباً"),
            "القاعدة الوزارية: أسماء الزمان والمكان إذا تلاها اسم معرفة تعرب خبراً مقدماً وجوباً!"
        ),
        Triple(
            "قال الشاعر: أَصَخْرَةٌ أَنَا مَا لِي لَا تُحَرِّكُنِي هَذِي الْمُدَامُ",
            listOf("أَصَخْرَةٌ: الهمزة للتصور + خصيصتها الصدارة", "مَا لِي: اسم استفهام في محل رفع مبتدأ (تلاه شبه جملة لي)", "الاستبدال بـ (هل): لا يجوز في التصور، أو نضع هل بعد الفاء"),
            "القاعدة الوزارية: انظر لما بعد اسم الاستفهام مباشرة لتحديد إعرابه بدقة!"
        )
    )

    val current = examples[selectedExampleIndex]

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "مشرحة الجملة الإعرابية التفاعلية (اضغط لتفكيك النص الوزاري خطوة بخطوة):",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            examples.forEachIndexed { idx, _ ->
                FilterChip(
                    selected = selectedExampleIndex == idx,
                    onClick = { selectedExampleIndex = idx },
                    label = { Text("شاهد وزاري (${idx + 1})") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Surface(
            color = Color(0xFFFFF1F2),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, subject.accentColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = current.first,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = subject.accentColor
                )
                current.second.forEach { token ->
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, subject.accentColor.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⟵ $token",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                Text(
                    text = "✓ ${current.third}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnTealLightContainer
                )
            }
        }
    }
}

@Composable
private fun EnglishLiveStoryTimelineLab(
    subject: SubjectType
) {
    var activeStage by remember { mutableIntStateOf(0) }
    val stages = listOf(
        Triple(
            "1. Floating Peacefully (Past Continuous)",
            "While Zaid Tariq WAS FLOATING on his airbed 150m from the shore in Sharm El-Sheikh...",
            "الحدث الطويل المستمر (Past Continuous): was/were + V-ing (يأتي بعد While / As)"
        ),
        Triple(
            "2. Shark Attack! (Past Simple Interruption)",
            "...he FELT something push him, and a shark BIT his arm! He screamed and prayed to God.",
            "الحدث القصير القاطع (Past Simple): V-ed / التصريف الثاني (يأتي بعد When / and)"
        ),
        Triple(
            "3. Six Dolphins Protection",
            "Six dolphins MADE a circle around him and FRIGHTENED the shark away!",
            "سؤال وزاري مكرر: What saved Tariq? -> Six dolphins frightened the shark away."
        ),
        Triple(
            "4. Coastguard Rescue",
            "Coastguard Jameel El-Alaoui HEARD his screams, GAVE him first aid on the boat, and took him to Sharm El-Sheikh hospital.",
            "سؤال وزاري مكرر: Who rescued Tariq by boat? -> Coastguard Jameel El-Alaoui."
        )
    )

    val stage = stages[activeStage]

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "شريط أحداث القصة والقواعد التفاعلي (تتبع القصة وربط الزمن المستمر بالبسيط):",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            stages.forEachIndexed { idx, _ ->
                FilterChip(
                    selected = activeStage == idx,
                    onClick = { activeStage = idx },
                    label = { Text("مشهد ${idx + 1}") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Surface(
            color = Color(0xFFF0F9FF),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.5.dp, subject.accentColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stage.first,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = subject.accentColor
                )
                Text(
                    text = stage.second,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stage.third,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnTealLightContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun IslamicLiveTajweedRadar(
    subject: SubjectType
) {
    var selectedRuleIndex by remember { mutableIntStateOf(0) }
    val rules = listOf(
        Triple(
            "1. الإظهار الحلقي",
            "حروفه (6): (ء، هـ، ع، ح، غ، خ) — مجموعة في: «أخي هاك علماً حازه غير خاسر»",
            "مثال وزاري: ﴿مَنْ آمَنَ﴾ — السبب: مجيء الهمزة (من حروف الحلق) بعد النون الساكنة."
        ),
        Triple(
            "2. الإدغام (يرملون)",
            "بغنة (ينمو): ﴿مَنْ يَقُولُ﴾ • بدون غنة (ل، ر): ﴿مِنْ رَبِّهِمْ﴾",
            "انتباه وزاري مهم: لا يقع الإدغام في كلمة واحدة مثل (دُنْيَا، بُنْيَان، صِنْوَان، قِنْوَان) بل يسمى إظهاراً مطلقاً!"
        ),
        Triple(
            "3. الإقلاب (حرف الباء فقط)",
            "قلب النون الساكنة أو التنوين ميماً مخفاة بغنة عند ملاقاتها حرف (الباء)",
            "مثال وزاري: ﴿مِنْ بَعْدِ﴾ أو ﴿سَمِيعٌ بَصِيرٌ﴾."
        ),
        Triple(
            "4. الإخفاء الحقيقي (15 حرفاً)",
            "حروفه: أوائل كلمات «صف ذا ثنا كم جاد شخص قد سما دم طيباً زد في تقى ضع ظالماً»",
            "مثال وزاري: ﴿مَنْ ذَا الَّذِي﴾ — نطق النون الساكنة بحالة وسط بين الإظهار والإدغام مع الغنة."
        )
    )

    val currentRule = rules[selectedRuleIndex]

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "كاشف أحكام النون الساكنة والتنوين التفاعلي (اضغط على الحكم لترى الحروف والشرط الوزاري):",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("الإظهار", "الإدغام", "الإقلاب", "الإخفاء").forEachIndexed { idx, label ->
                FilterChip(
                    selected = selectedRuleIndex == idx,
                    onClick = { selectedRuleIndex = idx },
                    label = { Text(label) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Surface(
            color = SoftMintSurface,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.5.dp, subject.accentColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = currentRule.first,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = subject.accentColor
                )
                Text(
                    text = currentRule.second,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentRule.third,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnTealLightContainer
                )
            }
        }
    }
}

/**
 * Interactive "Assemble the Control Answer" Hands-On Challenge inside each textbook line!
 * Lets the student actively participate by tapping the key phrases in order to build the answer.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InteractiveLineParticipationChallenge(
    line: TextbookLine,
    subject: SubjectType,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Build interactive blocks from keyTermsToCloze or split the first Control answer into logical segments
    val targetBlocks = remember(line.id) {
        if (line.keyTermsToCloze.size >= 2) {
            line.keyTermsToCloze
        } else {
            val rawAns = line.wizariAttachments.firstOrNull()?.controlModelAnswer ?: line.verbatimText
            rawAns.split("،", "؛", ".").map { it.trim() }.filter { it.length > 4 }.take(4)
        }
    }

    val shuffledBlocks = remember(line.id) {
        targetBlocks.shuffled()
    }
    val selectedBlocks = remember(line.id) { mutableStateListOf<String>() }
    val isCompleted = selectedBlocks.size == targetBlocks.size && targetBlocks.isNotEmpty()

    Column(modifier = modifier.fillMaxWidth()) {
        Surface(
            color = if (isCompleted) SoftMintSurface else GoldLightContainer.copy(alpha = 0.7f),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                1.dp,
                if (isCompleted) subject.accentColor else GoldTertiary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .testTag("participate_line_btn_${line.id}")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.EmojiEvents else Icons.Default.Extension,
                        contentDescription = null,
                        tint = if (isCompleted) subject.accentColor else GoldTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = if (isCompleted) {
                            "أحسنت! ركّبت مفاتيح هذا السطر بيدك (+25 XP)"
                        } else {
                            "شارك بيدك: ركّب مفاتيح الجواب الوزاري لهذا السطر (${targetBlocks.size} خطوات)"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) subject.accentColor else OnGoldLightContainer
                    )
                }

                Text(
                    text = if (isExpanded) "إغلاق" else "ابدأ التحدي",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = subject.accentColor
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, subject.accentColor.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "اضغط على الركائز العلمية بالترتيب لتثبيت المعلومة في ذاكرتك النشطة:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Available blocks to tap
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        shuffledBlocks.forEachIndexed { idx, block ->
                            val isPicked = selectedBlocks.contains(block)
                            Surface(
                                color = if (isPicked) SoftMintSurface else subject.softBgColor,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isPicked) subject.accentColor else MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier.clickable {
                                    if (isPicked) {
                                        selectedBlocks.remove(block)
                                    } else {
                                        selectedBlocks.add(block)
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    if (isPicked) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = subject.accentColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = block,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (isPicked) subject.accentColor else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Assembled answer preview
                    if (selectedBlocks.isNotEmpty()) {
                        Surface(
                            color = SoftMintSurface,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "تركيبك الحالي للجواب: " + selectedBlocks.joinToString(" ⟵ "),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnTealLightContainer
                                )
                                if (isCompleted) {
                                    Text(
                                        text = "ممتاز! هكذا تحفظ العقلية الفاهمة لا الببغائية: ربطت المفهوم بيدك خطوة بخطوة.",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = subject.accentColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
