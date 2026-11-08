package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.R
import com.example.data.IraqiCurriculumData
import com.example.data.LineMasteryEntity
import com.example.data.SubjectType
import com.example.data.TextbookFigure
import com.example.data.TextbookLine
import com.example.data.TextbookPage
import com.example.data.WizariControlQA
import com.example.ui.theme.CoralLightContainer
import com.example.ui.theme.CoralSecondary
import com.example.ui.theme.GoldLightContainer
import com.example.ui.theme.GoldTertiary
import com.example.ui.theme.NightCardSurface
import com.example.ui.theme.NightInkPrimary
import com.example.ui.theme.NightPageSurface
import com.example.ui.theme.OnCoralLightContainer
import com.example.ui.theme.OnGoldLightContainer
import com.example.ui.theme.OnTealLightContainer
import com.example.ui.theme.PaperPageSurface
import com.example.ui.theme.SepiaCardSurface
import com.example.ui.theme.SepiaPageSurface
import com.example.ui.theme.SoftMintSurface
import com.example.ui.theme.TealLightContainer
import com.example.ui.theme.TealPrimary
import kotlin.math.absoluteValue
import kotlinx.coroutines.launch

enum class ReadingComfortMode(
    val labelAr: String,
    val pageSurface: Color,
    val lineCardSurface: Color,
    val primaryInk: Color
) {
    SEPIA_WARM(
        labelAr = "وضع القراءة: ورقي دافئ",
        pageSurface = SepiaPageSurface,
        lineCardSurface = SepiaCardSurface,
        primaryInk = Color(0xFF1E1B4B)
    ),
    DAY_CRISP(
        labelAr = "وضع القراءة: نهاري مشرق",
        pageSurface = PaperPageSurface,
        lineCardSurface = Color.White,
        primaryInk = Color(0xFF0F172A)
    ),
    NIGHT_CALM(
        labelAr = "وضع القراءة: ليلي هادئ",
        pageSurface = NightPageSurface,
        lineCardSurface = NightCardSurface,
        primaryInk = NightInkPrimary
    );

    fun next(): ReadingComfortMode = when (this) {
        SEPIA_WARM -> DAY_CRISP
        DAY_CRISP -> NIGHT_CALM
        NIGHT_CALM -> SEPIA_WARM
    }
}

@Composable
fun TextbookReaderScreen(
    selectedSubject: SubjectType,
    onSelectSubject: (SubjectType) -> Unit,
    isAnnualVipUnlocked: Boolean,
    onOpenSubscriptionScreen: () -> Unit,
    onQuickUnlockAnnualVip: () -> Unit,
    activeRecallEnabled: Boolean,
    onToggleActiveRecall: () -> Unit,
    revealedClozeKeys: Set<String>,
    onRevealClozeWord: (String) -> Unit,
    onHideAllClozeWords: () -> Unit,
    lineMasteryList: List<LineMasteryEntity>,
    onToggleLineMastered: (String, String) -> Unit,
    onToggleLineHighlight: (String, String) -> Unit,
    onSaveStudentNote: (String, String, String) -> Unit,
    onOpen3DModel: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val subjectPages = remember(selectedSubject) {
        IraqiCurriculumData.textbookPages
            .filter { it.subject == selectedSubject }
            .sortedBy { it.bookPageNumber }
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { subjectPages.size }
    )
    val coroutineScope = rememberCoroutineScope()
    var readingComfortMode by remember { mutableStateOf(ReadingComfortMode.SEPIA_WARM) }

    LaunchedEffect(selectedSubject) {
        pagerState.scrollToPage(0)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Refined Academic Top Control Bar with All 7 Subjects
        Surface(
            color = Color.White,
            tonalElevation = 3.dp,
            shadowElevation = 3.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Subject Chips Row (All 7 Iraqi 6th Prep Subjects)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SubjectType.entries) { subject ->
                        val isSelected = subject == selectedSubject
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSelectSubject(subject) },
                            label = {
                                Text(
                                    text = subject.titleAr,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = subject.titleAr,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = subject.softBgColor,
                                selectedLabelColor = subject.accentColor,
                                selectedLeadingIconColor = subject.accentColor
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) subject.accentColor else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.testTag("subject_chip_${subject.id}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Page Turn Controls + Free/VIP Badge + Active Recall Cloze Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (pagerState.currentPage > 0) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            },
                            enabled = pagerState.currentPage > 0,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("prev_page_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "الصفحة السابقة"
                            )
                        }

                        val currentBookPage = subjectPages.getOrNull(pagerState.currentPage)
                        val isPageFree = currentBookPage?.isFreeChapter == true || isAnnualVipUnlocked
                        Surface(
                            color = if (isPageFree) selectedSubject.softBgColor else GoldLightContainer,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isPageFree) selectedSubject.accentColor.copy(alpha = 0.45f) else GoldTertiary
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPageFree) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isPageFree) selectedSubject.accentColor else GoldTertiary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (currentBookPage?.isFreeChapter == true) {
                                        "فصل 1 مجاني • ص (${currentBookPage.bookPageNumber}) (${pagerState.currentPage + 1}/${subjectPages.size})"
                                    } else {
                                        "فصل ${currentBookPage?.chapterOrder ?: 2} • ص (${currentBookPage?.bookPageNumber ?: 1}) (${pagerState.currentPage + 1}/${subjectPages.size})"
                                    },
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isPageFree) selectedSubject.accentColor else OnGoldLightContainer
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (pagerState.currentPage < subjectPages.size - 1) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            },
                            enabled = pagerState.currentPage < subjectPages.size - 1,
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("next_page_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "الصفحة التالية"
                            )
                        }
                    }

                    // Eye-Comfort Reading Mode + Active Recall Mode Buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AssistChip(
                            onClick = { readingComfortMode = readingComfortMode.next() },
                            label = {
                                Text(
                                    text = readingComfortMode.labelAr,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = GoldLightContainer,
                                labelColor = OnGoldLightContainer
                            ),
                            border = BorderStroke(1.dp, GoldTertiary.copy(alpha = 0.6f)),
                            modifier = Modifier.testTag("toggle_reading_comfort_button")
                        )

                        AssistChip(
                            onClick = onToggleActiveRecall,
                            label = {
                                Text(
                                    text = if (activeRecallEnabled) "التسميع: مفعّل" else "إخفاء للحفظ",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (activeRecallEnabled) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "تبديل وضع الاسترجاع النشط",
                                    tint = if (activeRecallEnabled) CoralSecondary else TealPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (activeRecallEnabled) CoralLightContainer else TealLightContainer,
                                labelColor = if (activeRecallEnabled) OnCoralLightContainer else OnTealLightContainer
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (activeRecallEnabled) CoralSecondary else TealPrimary
                            ),
                            modifier = Modifier.testTag("toggle_active_recall_button")
                        )
                    }
                }
            }
        }

        // Realistic 3D Page-Flipping HorizontalPager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .testTag("textbook_horizontal_pager"),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            pageSpacing = 12.dp
        ) { pageIndex ->
            val pageData = subjectPages[pageIndex]
            val pageOffset = (
                (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
            ).absoluteValue

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val scale = lerp(1f, 0.94f, pageOffset.coerceIn(0f, 1f))
                        scaleX = scale
                        scaleY = scale
                        rotationY = lerp(0f, 10f, pageOffset.coerceIn(0f, 1f))
                        alpha = lerp(1f, 0.80f, pageOffset.coerceIn(0f, 1f))
                    }
            ) {
                TextbookPaperPageCard(
                    page = pageData,
                    readingComfortMode = readingComfortMode,
                    isAnnualVipUnlocked = isAnnualVipUnlocked,
                    onOpenSubscriptionScreen = onOpenSubscriptionScreen,
                    onQuickUnlockAnnualVip = onQuickUnlockAnnualVip,
                    activeRecallEnabled = activeRecallEnabled,
                    revealedClozeKeys = revealedClozeKeys,
                    onRevealClozeWord = onRevealClozeWord,
                    onHideAllClozeWords = onHideAllClozeWords,
                    lineMasteryList = lineMasteryList,
                    onToggleLineMastered = onToggleLineMastered,
                    onToggleLineHighlight = onToggleLineHighlight,
                    onSaveStudentNote = onSaveStudentNote,
                    onOpen3DModel = onOpen3DModel,
                    onFlipNext = {
                        if (pageIndex < subjectPages.size - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pageIndex + 1)
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun TextbookPaperPageCard(
    page: TextbookPage,
    readingComfortMode: ReadingComfortMode,
    isAnnualVipUnlocked: Boolean,
    onOpenSubscriptionScreen: () -> Unit,
    onQuickUnlockAnnualVip: () -> Unit,
    activeRecallEnabled: Boolean,
    revealedClozeKeys: Set<String>,
    onRevealClozeWord: (String) -> Unit,
    onHideAllClozeWords: () -> Unit,
    lineMasteryList: List<LineMasteryEntity>,
    onToggleLineMastered: (String, String) -> Unit,
    onToggleLineHighlight: (String, String) -> Unit,
    onSaveStudentNote: (String, String, String) -> Unit,
    onOpen3DModel: (String) -> Unit,
    onFlipNext: () -> Unit
) {
    val isLockedPage = !page.isFreeChapter && !isAnnualVipUnlocked

    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = readingComfortMode.pageSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        border = BorderStroke(1.5.dp, page.subject.accentColor.copy(alpha = 0.40f))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Prestigious Academic Textbook Page Header Ribbon
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, page.subject.accentColor.copy(alpha = 0.30f)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        page.subject.softBgColor,
                                        Color.White,
                                        page.subject.softBgColor.copy(alpha = 0.6f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = if (page.isFreeChapter) page.subject.accentColor else GoldTertiary,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = page.chapterNumber,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                                )
                            }

                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, page.subject.accentColor.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "كتاب وزارة التربية • ص (${page.bookPageNumber})",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = page.subject.accentColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = page.chapterTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = page.topicTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Sophisticated Academic Scientific Header Art on Cover Page (No cartoons/children)
            if (page.isCoverPage) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.5.dp, GoldTertiary.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box {
                            Image(
                                painter = painterResource(id = R.drawable.img_academic_cover_sadis_1794088894140),
                                contentDescription = "الموسوعة العلمية الأكاديمية للسادس الإعدادي",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(185.dp),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                color = Color(0xCC1E1B4B),
                                shape = RoundedCornerShape(topStart = 14.dp),
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = Color(0xFFFACC15),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "الفصل الأول مجاني بالكامل لكل مادة • نص حرفي + أجوبة الكنترول",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // If this page belongs to Chapter 2+ and Annual Subscription is not yet unlocked, show Paywall Card
            if (isLockedPage) {
                item {
                    AnnualSubscriptionPaywallCard(
                        subject = page.subject,
                        chapterNumber = page.chapterNumber,
                        chapterTitle = page.chapterTitle,
                        previewLine = page.lines.firstOrNull()?.verbatimText ?: "",
                        onOpenSubscriptionPlans = onOpenSubscriptionScreen,
                        onQuickUnlock = onQuickUnlockAnnualVip
                    )
                }
            } else {
                // Active Recall Instruction Banner when active
                if (activeRecallEnabled) {
                    item {
                        Surface(
                            color = CoralLightContainer,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, CoralSecondary.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "طور الاسترجاع النشط للمصطلحات الوزارية",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = OnCoralLightContainer
                                    )
                                    Text(
                                        text = "استذكر المصطلحات المنهجية المخفية في نص الكتاب ثم انقر على كل فراغ للتحقق من دقة حفظك.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = OnCoralLightContainer
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = onHideAllClozeWords,
                                    border = BorderStroke(1.dp, OnCoralLightContainer)
                                ) {
                                    Text("إخفاء الكل", color = OnCoralLightContainer)
                                }
                            }
                        }
                    }
                }

                // Live Interactive Subject Presence Simulator (Hands-On Participatory Learning per Subject)
                item {
                    SubjectInteractivePresenceCard(
                        subject = page.subject,
                        onOpen3DLab = {
                            val defaultModel = when (page.subject) {
                                SubjectType.BIOLOGY -> "model_bio_mitochondria"
                                SubjectType.PHYSICS -> "model_phys_capacitor"
                                SubjectType.CHEMISTRY -> "model_chem_daniell_cell"
                                else -> "model_bio_mitochondria"
                            }
                            onOpen3DModel(page.bookFigure?.linked3DModelId ?: defaultModel)
                        }
                    )
                }

                // Exact Textbook Figure & 4K/360° 3D Interactive Portal Card
                page.bookFigure?.let { figure ->
                    item {
                        TextbookFigure4KCard(
                            figure = figure,
                            accentColor = page.subject.accentColor,
                            onOpen3DModel = { onOpen3DModel(figure.linked3DModelId) }
                        )
                    }
                }

                // Verbatim Textbook Lines with Inline Control Ministerial Q&A on every line
                items(page.lines, key = { it.id }) { line ->
                    val masteryEntity = lineMasteryList.find { it.lineId == line.id }
                    VerbatimTextbookLineItem(
                        line = line,
                        subject = page.subject,
                        readingComfortMode = readingComfortMode,
                        activeRecallEnabled = activeRecallEnabled,
                        revealedClozeKeys = revealedClozeKeys,
                        onRevealClozeWord = onRevealClozeWord,
                        masteryEntity = masteryEntity,
                        onToggleMastered = { onToggleLineMastered(line.id, page.subject.id) },
                        onToggleHighlight = { onToggleLineHighlight(line.id, page.subject.id) },
                        onSaveNote = { note -> onSaveStudentNote(line.id, page.subject.id, note) },
                        onOpen3DModel = onOpen3DModel
                    )
                }

                // Official Control Summary + Witty Student Morale Booster Footer
                item {
                    Surface(
                        color = SoftMintSurface,
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FactCheck,
                                    contentDescription = "خلاصة الكنترول الوزاري",
                                    tint = TealPrimary
                                )
                                Text(
                                    text = "خلاصة الكنترول الوزاري لهذه الصفحة:",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = OnTealLightContainer
                                )
                            }
                            Text(
                                text = page.controlPageSummary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // Witty Student Morale Boost Box
                            Surface(
                                color = GoldLightContainer,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, GoldTertiary.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EmojiEmotions,
                                        contentDescription = null,
                                        tint = GoldTertiary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = page.pageWittyBoost,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = OnGoldLightContainer
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = onFlipNext,
                                    colors = ButtonDefaults.buttonColors(containerColor = page.subject.accentColor),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("flip_next_page_bottom_btn")
                                ) {
                                    Text("اقلب للصفحة التالية")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
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

@Composable
private fun AnnualSubscriptionPaywallCard(
    subject: SubjectType,
    chapterNumber: String,
    chapterTitle: String,
    previewLine: String,
    onOpenSubscriptionPlans: () -> Unit,
    onQuickUnlock: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        border = BorderStroke(2.dp, GoldTertiary),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("annual_paywall_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = GoldTertiary,
                    shape = CircleShape,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "الفصل الأول مجاني • باقي الفصول ضمن الاشتراك السنوي",
                        style = MaterialTheme.typography.titleMedium,
                        color = OnGoldLightContainer
                    )
                    Text(
                        text = "$chapterNumber - $chapterTitle",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Blurred / Teaser Preview of the Textbook Verbatim Content
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, GoldTertiary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "معاينة من النص المنهجي المقرر لهذا الفصل:",
                        style = MaterialTheme.typography.labelMedium,
                        color = GoldTertiary
                    )
                    Text(
                        text = previewLine.take(150) + " ... [محتوى مقفل ضمن باقة الاشتراك السنوي]",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Pricing Summary Box
            Surface(
                color = SoftMintSurface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "خيارات الاشتراك السنوي المناسبة لطالب السادس:",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnTealLightContainer
                        )
                    }
                    Text(
                        text = "• اشتراك مادة (${subject.titleAr}) كاملة لسنة دراسية: ${subject.singleSubjectPriceIqd}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "• الباقة الذهبية الشاملة لجميع المواد الـ 7 لسنة كاملة: 49,000 د.ع فقط (أقل من سعر ملزمة ورقية لكل مادة!)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onQuickUnlock,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_unlock_vip_btn")
                ) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تفعيل الاشتراك السنوي فوراً")
                }

                OutlinedButton(
                    onClick = onOpenSubscriptionPlans,
                    border = BorderStroke(1.5.dp, GoldTertiary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("view_pricing_details_btn")
                ) {
                    Text("تفاصيل الباقات", color = OnGoldLightContainer)
                }
            }
        }
    }
}

@Composable
private fun TextbookFigure4KCard(
    figure: TextbookFigure,
    accentColor: Color,
    onOpen3DModel: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, accentColor.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(215.dp)
                    .clickable { onOpen3DModel() }
            ) {
                Image(
                    painter = painterResource(id = figure.imageRes),
                    contentDescription = figure.captionVerbatim,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    color = Color(0xD91E1B4B),
                    shape = RoundedCornerShape(bottomEnd = 14.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "${figure.figureNumber} • مجسم 360° 4K",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Button(
                    onClick = onOpen3DModel,
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .testTag("enter_4k_diagram_btn_${figure.linked3DModelId}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ViewInAr,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "فتح المجسم الثلاثي الأبعاد (360° Three.js / Model Viewer)",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SoftMintSurface)
                    .padding(12.dp)
            ) {
                Text(
                    text = figure.captionVerbatim,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = OnTealLightContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VerbatimTextbookLineItem(
    line: TextbookLine,
    subject: SubjectType,
    readingComfortMode: ReadingComfortMode,
    activeRecallEnabled: Boolean,
    revealedClozeKeys: Set<String>,
    onRevealClozeWord: (String) -> Unit,
    masteryEntity: LineMasteryEntity?,
    onToggleMastered: () -> Unit,
    onToggleHighlight: () -> Unit,
    onSaveNote: (String) -> Unit,
    onOpen3DModel: (String) -> Unit
) {
    var showWizariPanel by remember { mutableStateOf(true) }
    var showNoteEditor by remember { mutableStateOf(false) }
    var noteDraft by remember(masteryEntity?.studentNote) {
        mutableStateOf(masteryEntity?.studentNote ?: "")
    }

    val isMastered = masteryEntity?.isMastered == true
    val isHighlighted = masteryEntity?.isHighlighted == true

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isHighlighted -> Color(0xFFFEF9C3)
                isMastered -> SoftMintSurface
                else -> readingComfortMode.lineCardSurface
            }
        ),
        border = BorderStroke(
            width = if (isMastered) 1.5.dp else 1.dp,
            color = if (isMastered) TealPrimary else MaterialTheme.colorScheme.outline
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("textbook_line_card_${line.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Line Section Header with Line Number Badge
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
                        modifier = Modifier.size(26.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${line.lineIndex}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (line.sectionHeader != null) {
                        Text(
                            text = line.sectionHeader,
                            style = MaterialTheme.typography.titleLarge,
                            color = if (readingComfortMode == ReadingComfortMode.NIGHT_CALM && !isHighlighted && !isMastered) {
                                Color(0xFF93C5FD)
                            } else {
                                subject.accentColor
                            }
                        )
                    } else {
                        Text(
                            text = "الفقرة المنهجية (${line.lineIndex})",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (readingComfortMode == ReadingComfortMode.NIGHT_CALM && !isHighlighted && !isMastered) {
                                Color(0xFF93C5FD)
                            } else {
                                subject.accentColor
                            }
                        )
                    }
                }
            }

            // Exact Textbook Verbatim Text OR Interactive Cloze Recall Words
            val activeTextColor = if (isHighlighted || isMastered) {
                MaterialTheme.colorScheme.onSurface
            } else {
                readingComfortMode.primaryInk
            }
            if (!activeRecallEnabled) {
                Text(
                    text = line.verbatimText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = activeTextColor
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    var maskedText = line.verbatimText
                    line.keyTermsToCloze.forEachIndexed { idx, term ->
                        val key = "${line.id}_$idx"
                        if (!revealedClozeKeys.contains(key)) {
                            maskedText = maskedText.replace(term, " [ ... مصطلح (${idx + 1}) ... ] ")
                        }
                    }
                    Text(
                        text = maskedText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = activeTextColor
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        line.keyTermsToCloze.forEachIndexed { idx, term ->
                            val key = "${line.id}_$idx"
                            val isRevealed = revealedClozeKeys.contains(key)
                            Surface(
                                color = if (isRevealed) TealLightContainer else CoralLightContainer,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isRevealed) TealPrimary else CoralSecondary
                                ),
                                modifier = Modifier
                                    .clickable { onRevealClozeWord(key) }
                                    .testTag("cloze_chip_$key")
                            ) {
                                Text(
                                    text = if (isRevealed) "✓ $term" else "كشف المصطلح (${idx + 1})",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isRevealed) OnTealLightContainer else OnCoralLightContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Hands-On Participatory Line Challenge: Let the student assemble the key blocks of this line!
            InteractiveLineParticipationChallenge(
                line = line,
                subject = subject
            )

            // Student's saved personal note if exists
            if (!masteryEntity?.studentNote.isNullOrBlank()) {
                Surface(
                    color = GoldLightContainer.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ملاحظة الطالب المنهجية: ${masteryEntity?.studentNote}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnGoldLightContainer,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            // Action Toolbar for this Line
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (line.wizariAttachments.isNotEmpty()) {
                        Surface(
                            color = if (showWizariPanel) CoralSecondary else CoralLightContainer,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .clickable { showWizariPanel = !showWizariPanel }
                                .testTag("toggle_wizari_line_${line.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QuestionAnswer,
                                    contentDescription = null,
                                    tint = if (showWizariPanel) Color.White else OnCoralLightContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "أسئلة السنوات السابقة وأجوبة الكنترول (${line.wizariAttachments.size})",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (showWizariPanel) Color.White else OnCoralLightContainer
                                )
                            }
                        }
                    }

                    line.linked3DModelId?.let { modelId ->
                        Surface(
                            color = TealLightContainer,
                            shape = RoundedCornerShape(50),
                            border = BorderStroke(1.dp, TealPrimary),
                            modifier = Modifier
                                .clickable { onOpen3DModel(modelId) }
                                .testTag("line_open_3d_${line.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ViewInAr,
                                    contentDescription = null,
                                    tint = OnTealLightContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "360° 3D",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = OnTealLightContainer
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleHighlight,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("highlight_line_${line.id}")
                    ) {
                        Icon(
                            imageVector = if (isHighlighted) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "تأشير السطر",
                            tint = if (isHighlighted) GoldTertiary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = { showNoteEditor = !showNoteEditor },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("note_line_${line.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "إضافة ملاحظة منهجية",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onToggleMastered,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("master_line_${line.id}")
                    ) {
                        Icon(
                            imageVector = if (isMastered) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "تم إتقان السطر وزارياً",
                            tint = if (isMastered) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            AnimatedVisibility(visible = showNoteEditor) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = noteDraft,
                        onValueChange = { noteDraft = it },
                        label = { Text("تدوين ملاحظة منهجية خاصة بهذا السطر") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                onSaveNote(noteDraft)
                                showNoteEditor = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                        ) {
                            Text("حفظ الملاحظة")
                        }
                    }
                }
            }

            // Inline Ministerial Control Questions & Answers attached to this exact line
            AnimatedVisibility(
                visible = showWizariPanel && line.wizariAttachments.isNotEmpty(),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    line.wizariAttachments.forEach { qa ->
                        InlineWizariControlCard(qa = qa)
                    }
                }
            }
        }
    }
}

@Composable
fun InlineWizariControlCard(
    qa: WizariControlQA,
    modifier: Modifier = Modifier
) {
    var revealControlAnswer by remember { mutableStateOf(true) }

    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.35f)),
        modifier = modifier.fillMaxWidth()
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
                Surface(
                    color = CoralLightContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = qa.questionType,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnCoralLightContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = GoldLightContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = qa.yearsBadge,
                        style = MaterialTheme.typography.labelMedium,
                        color = OnGoldLightContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Text(
                text = "السؤال الوزاري: ${qa.questionText}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedButton(
                onClick = { revealControlAnswer = !revealControlAnswer },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (revealControlAnswer) TealLightContainer else Color.White,
                    contentColor = OnTealLightContainer
                ),
                border = BorderStroke(1.dp, TealPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reveal_control_ans_${qa.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (revealControlAnswer) {
                        "إخفاء الجواب النموذجي للكنترول (لاختبار نفسك)"
                    } else {
                        "عرض الجواب النموذجي حسب الكنترول الوزاري"
                    },
                    fontWeight = FontWeight.Bold
                )
            }

            AnimatedVisibility(visible = revealControlAnswer) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SoftMintSurface)
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "الجواب النموذجي المعتمد في الكنترول الوزاري:",
                        style = MaterialTheme.typography.labelLarge,
                        color = OnTealLightContainer
                    )
                    Text(
                        text = qa.controlModelAnswer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    HorizontalDivider(color = TealPrimary.copy(alpha = 0.25f))

                    Text(
                        text = qa.gradeDistribution,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = qa.controlNote,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    // Separate Witty Touch Box (Clearly labeled so it never mixes with the official textbook text)
                    Surface(
                        color = GoldLightContainer,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, GoldTertiary.copy(alpha = 0.45f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEmotions,
                                contentDescription = null,
                                tint = GoldTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = qa.wittyHint,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = OnGoldLightContainer
                            )
                        }
                    }
                }
            }
        }
    }
}
