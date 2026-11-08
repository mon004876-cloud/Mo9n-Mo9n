package com.sadis.encyclopedia.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sadis.encyclopedia.data.IraqiCurriculumData
import com.sadis.encyclopedia.data.LineMasteryEntity
import com.sadis.encyclopedia.data.ModelQuizScoreEntity
import com.sadis.encyclopedia.data.StudyPlanTaskEntity
import com.sadis.encyclopedia.data.StudyTechniqueStep
import com.sadis.encyclopedia.data.SubjectType
import com.sadis.encyclopedia.data.TextbookPage
import com.sadis.encyclopedia.ui.theme.CoralSecondary
import com.sadis.encyclopedia.ui.theme.GoldLightContainer
import com.sadis.encyclopedia.ui.theme.GoldTertiary
import com.sadis.encyclopedia.ui.theme.OnGoldLightContainer
import com.sadis.encyclopedia.ui.theme.OnTealLightContainer
import com.sadis.encyclopedia.ui.theme.SoftMintSurface
import com.sadis.encyclopedia.ui.theme.TealLightContainer
import com.sadis.encyclopedia.ui.theme.TealPrimary

@Composable
fun StudyPlan100Screen(
    lineMasteryList: List<LineMasteryEntity>,
    modelScoresList: List<ModelQuizScoreEntity>,
    planTasksList: List<StudyPlanTaskEntity>,
    onTogglePlanStep: (Int) -> Unit,
    pomodoroSecondsLeft: Int,
    isPomodoroRunning: Boolean,
    onTogglePomodoro: () -> Unit,
    onResetPomodoro: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalLinesInCurriculum = remember {
        IraqiCurriculumData.textbookPages.sumOf { it.lines.size }
    }
    val masteredLinesCount = lineMasteryList.count { it.isMastered }
    val completedStepsCount = planTasksList.count { it.isCompleted }
    val totalPlanSteps = IraqiCurriculumData.studyPlanSteps.size

    val overallReadinessPercent = remember(masteredLinesCount, completedStepsCount, modelScoresList) {
        val lineRatio = if (totalLinesInCurriculum > 0) masteredLinesCount.toFloat() / totalLinesInCurriculum else 0f
        val stepRatio = if (totalPlanSteps > 0) completedStepsCount.toFloat() / totalPlanSteps else 0f
        val labRatio = (modelScoresList.size.toFloat() / IraqiCurriculumData.interactive4KModels.size).coerceAtMost(1f)
        ((lineRatio * 0.5f + stepRatio * 0.3f + labRatio * 0.2f) * 100).toInt().coerceIn(5, 100)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Dashboard Card: Road to 100/100
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = SoftMintSurface),
                border = BorderStroke(1.5.dp, TealPrimary),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = GoldTertiary,
                                modifier = Modifier.size(28.dp)
                            )
                            Column {
                                Text(
                                    text = "المنهجية العلمية الشاملة للدرجة الكاملة (100/100)",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = OnTealLightContainer
                                )
                                Text(
                                    text = "إتقان النص المنهجي المقرر + الأشكال 360° + أجوبة الكنترول الوزاري",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            color = TealPrimary,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "$overallReadinessPercent%",
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    LinearProgressIndicator(
                        progress = { overallReadinessPercent / 100f },
                        color = TealPrimary,
                        trackColor = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "الفقرات المتقنة: $masteredLinesCount / $totalLinesInCurriculum",
                            style = MaterialTheme.typography.labelLarge,
                            color = OnTealLightContainer
                        )
                        Text(
                            text = "مراحل الخطة المنجزة: $completedStepsCount / $totalPlanSteps",
                            style = MaterialTheme.typography.labelLarge,
                            color = OnTealLightContainer
                        )
                    }
                }
            }
        }

        // Academic Pomodoro Focus Timer Card
        item {
            PomodoroAcademicCard(
                secondsLeft = pomodoroSecondsLeft,
                isRunning = isPomodoroRunning,
                onToggle = onTogglePomodoro,
                onReset = onResetPomodoro
            )
        }

        // Section Header: The 5 Modern Learning Methods
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = CoralSecondary
                )
                Text(
                    text = "مراحل الإتقان المنهجي الخمس لطلبة السادس الإعدادي:",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Interactive Checklist of the 5 Modern Study Techniques
        items(IraqiCurriculumData.studyPlanSteps, key = { it.stepId }) { step ->
            val isDone = planTasksList.find { it.stepId == step.stepId }?.isCompleted == true
            StudyTechniqueStepCard(
                step = step,
                isCompleted = isDone,
                onToggle = { onTogglePlanStep(step.stepId) }
            )
        }
    }
}

@Composable
private fun PomodoroAcademicCard(
    secondsLeft: Int,
    isRunning: Boolean,
    onToggle: () -> Unit,
    onReset: (Int) -> Unit
) {
    val minutes = secondsLeft / 60
    val seconds = secondsLeft % 60
    val formattedTime = "%02d:%02d".format(minutes, seconds)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.5.dp, GoldTertiary.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = GoldTertiary
                    )
                    Column {
                        Text(
                            text = "مؤقت تنظيم جلسات الدراسة (Pomodoro)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "25 دقيقة دراسة منهجية مركزة + 5 دقائق استراحة لتجديد النشاط الذهني",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = GoldLightContainer,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.headlineMedium,
                        color = OnGoldLightContainer,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRunning) CoralSecondary else TealPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pomodoro_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isRunning) "إيقاف مؤقت" else "بدء جلسة التركيز")
                }

                OutlinedButton(
                    onClick = { onReset(25) },
                    modifier = Modifier.testTag("pomodoro_25_btn")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("25 د")
                }

                OutlinedButton(
                    onClick = { onReset(5) },
                    modifier = Modifier.testTag("pomodoro_5_btn")
                ) {
                    Text("استراحة 5 د")
                }
            }
        }
    }
}

@Composable
private fun StudyTechniqueStepCard(
    step: StudyTechniqueStep,
    isCompleted: Boolean,
    onToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) SoftMintSurface else Color.White
        ),
        border = BorderStroke(
            1.dp,
            if (isCompleted) TealPrimary else MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .testTag("plan_step_card_${step.stepId}")
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
                Surface(
                    color = TealLightContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = step.methodBadge,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnTealLightContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "إتمام المرحلة",
                    tint = if (isCompleted) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(26.dp)
                )
            }

            Text(
                text = step.titleAr,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = step.descriptionAr,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Surface(
                color = SoftMintSurface,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FactCheck,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = step.controlAdvice,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnTealLightContainer
                    )
                }
            }

            Surface(
                color = GoldLightContainer,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "😄 ${step.wittyStudentTip}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnGoldLightContainer,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun WizariControlBankScreen(
    canAccessPage: (TextbookPage) -> Boolean,
    onOpenSubscriptionScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterSubject by remember { mutableStateOf<SubjectType?>(null) }

    val allWizariItems = remember {
        IraqiCurriculumData.textbookPages.flatMap { page ->
            page.lines.flatMap { line ->
                line.wizariAttachments.map { qa ->
                    Triple(page, line, qa)
                }
            }
        }
    }

    // الأجوبة الخاصة بالفصول المقفلة لا تُعرض إلا بعد التفعيل
    val accessibleItems = allWizariItems.filter { (page, _, _) -> canAccessPage(page) }
    val lockedCount = allWizariItems.size - accessibleItems.size

    val filteredItems = remember(searchQuery, selectedFilterSubject, accessibleItems.size) {
        accessibleItems.filter { (page, _, qa) ->
            val matchesSubject = selectedFilterSubject == null || page.subject == selectedFilterSubject
            val matchesQuery = searchQuery.isBlank() ||
                qa.questionText.contains(searchQuery, ignoreCase = true) ||
                qa.controlModelAnswer.contains(searchQuery, ignoreCase = true) ||
                qa.questionType.contains(searchQuery, ignoreCase = true)
            matchesSubject && matchesQuery
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "بنك الأجوبة النموذجية المعتمدة في الكنترول الوزاري",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "جميع الأسئلة الوزارية الواردة في السنوات السابقة مرتبة حسب سطور الكتاب المنهجي مع الأجوبة النموذجية وسلم توزيع الدرجات في الكنترول.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "بحث في الوزاريات")
                    },
                    placeholder = { Text("ابحث عن تعليل، تعريف، مقارنة، أو مصطلح منهجي...") },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wizari_search_input")
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilterSubject == null,
                            onClick = { selectedFilterSubject = null },
                            label = { Text("جميع المواد (${accessibleItems.size})") }
                        )
                    }
                    items(SubjectType.entries) { subj ->
                        FilterChip(
                            selected = selectedFilterSubject == subj,
                            onClick = { selectedFilterSubject = subj },
                            label = { Text(subj.titleAr.substringBefore(" -")) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = subj.softBgColor,
                                selectedLabelColor = subj.accentColor
                            )
                        )
                    }
                }
            }
        }

        if (lockedCount > 0) {
            item {
                OutlinedButton(
                    onClick = onOpenSubscriptionScreen,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wizari_locked_notice")
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("$lockedCount سؤالاً وزارياً من الفصول المقفلة — فعّل اشتراكك لعرضها")
                }
            }
        }

        items(filteredItems, key = { it.third.id }) { (page, line, qa) ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${page.subject.titleAr} • ص (${page.bookPageNumber}) • فقرة (${line.lineIndex})",
                        style = MaterialTheme.typography.labelLarge,
                        color = page.subject.accentColor
                    )
                    Text(
                        text = page.chapterTitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                InlineWizariControlCard(qa = qa)
            }
        }
    }
}
