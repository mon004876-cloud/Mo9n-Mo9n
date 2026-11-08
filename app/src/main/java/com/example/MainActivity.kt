package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.ViewInAr
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.Interactive4KLabScreen
import com.example.ui.MainNavTab
import com.example.ui.SadisViewModel
import com.example.ui.StudyPlan100Screen
import com.example.ui.SubscriptionScreen
import com.example.ui.TextbookReaderScreen
import com.example.ui.WizariControlBankScreen
import com.example.ui.theme.GoldLightContainer
import com.example.ui.theme.GoldTertiary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OnGoldLightContainer
import com.example.ui.theme.OnTealLightContainer
import com.example.ui.theme.TealLightContainer
import com.example.ui.theme.TealPrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SadisEncyclopediaApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SadisEncyclopediaApp(
    sadisViewModel: SadisViewModel = viewModel()
) {
    val currentTab by sadisViewModel.currentTab.collectAsStateWithLifecycle()
    val selectedSubject by sadisViewModel.selectedSubject.collectAsStateWithLifecycle()
    val activeRecallEnabled by sadisViewModel.activeRecallClozeEnabled.collectAsStateWithLifecycle()
    val revealedClozeKeys by sadisViewModel.revealedClozeKeys.collectAsStateWithLifecycle()
    val selected3DModelId by sadisViewModel.selected3DModelId.collectAsStateWithLifecycle()

    val lineMasteryList by sadisViewModel.lineMasteryList.collectAsStateWithLifecycle()
    val modelScoresList by sadisViewModel.modelScoresList.collectAsStateWithLifecycle()
    val planTasksList by sadisViewModel.planTasksList.collectAsStateWithLifecycle()
    val subscriptionStatus by sadisViewModel.subscriptionStatus.collectAsStateWithLifecycle()

    val pomodoroSeconds by sadisViewModel.pomodoroSecondsLeft.collectAsStateWithLifecycle()
    val isPomodoroRunning by sadisViewModel.isPomodoroRunning.collectAsStateWithLifecycle()

    val isAnnualVipUnlocked = subscriptionStatus?.isAnnualVipUnlocked == true

    if (currentTab != MainNavTab.TEXTBOOK_READER) {
        BackHandler {
            sadisViewModel.selectTab(MainNavTab.TEXTBOOK_READER)
        }
    }

    val masteredCount = lineMasteryList.count { it.isMastered }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = stringResource(id = R.string.hero_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "7 مواد كاملة • الفصل 1 مجاني • 360° 3D + الكنترول",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        color = if (isAnnualVipUnlocked) GoldLightContainer else TealLightContainer,
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { sadisViewModel.selectTab(MainNavTab.ANNUAL_SUBSCRIPTION) }
                            .testTag("top_subscription_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isAnnualVipUnlocked) Icons.Default.Verified else Icons.Default.EmojiEvents,
                                contentDescription = "حالة الاشتراك والإتقان",
                                tint = if (isAnnualVipUnlocked) GoldTertiary else TealPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAnnualVipUnlocked) "VIP سنوي • متقن $masteredCount" else "فصل 1 مجاني • متقن $masteredCount",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isAnnualVipUnlocked) OnGoldLightContainer else OnTealLightContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == MainNavTab.TEXTBOOK_READER,
                    onClick = { sadisViewModel.selectTab(MainNavTab.TEXTBOOK_READER) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavTab.TEXTBOOK_READER) {
                                Icons.Filled.MenuBook
                            } else {
                                Icons.Outlined.MenuBook
                            },
                            contentDescription = stringResource(id = R.string.nav_book)
                        )
                    },
                    label = {
                        Text(
                            text = "الكتب (7)",
                            fontWeight = if (currentTab == MainNavTab.TEXTBOOK_READER) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnTealLightContainer,
                        selectedTextColor = TealPrimary,
                        indicatorColor = TealLightContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_book")
                )

                NavigationBarItem(
                    selected = currentTab == MainNavTab.LAB_3D_4K,
                    onClick = { sadisViewModel.selectTab(MainNavTab.LAB_3D_4K) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavTab.LAB_3D_4K) {
                                Icons.Filled.ViewInAr
                            } else {
                                Icons.Outlined.ViewInAr
                            },
                            contentDescription = stringResource(id = R.string.nav_lab_3d)
                        )
                    },
                    label = {
                        Text(
                            text = "360° 3D",
                            fontWeight = if (currentTab == MainNavTab.LAB_3D_4K) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnTealLightContainer,
                        selectedTextColor = TealPrimary,
                        indicatorColor = TealLightContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_4k_lab")
                )

                NavigationBarItem(
                    selected = currentTab == MainNavTab.PLAN_100,
                    onClick = { sadisViewModel.selectTab(MainNavTab.PLAN_100) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavTab.PLAN_100) {
                                Icons.Filled.EmojiEvents
                            } else {
                                Icons.Outlined.EmojiEvents
                            },
                            contentDescription = stringResource(id = R.string.nav_plan_100)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = R.string.nav_plan_100),
                            fontWeight = if (currentTab == MainNavTab.PLAN_100) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnTealLightContainer,
                        selectedTextColor = TealPrimary,
                        indicatorColor = TealLightContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_plan_100")
                )

                NavigationBarItem(
                    selected = currentTab == MainNavTab.WIZARI_CONTROL_BANK,
                    onClick = { sadisViewModel.selectTab(MainNavTab.WIZARI_CONTROL_BANK) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavTab.WIZARI_CONTROL_BANK) {
                                Icons.Filled.FactCheck
                            } else {
                                Icons.Outlined.FactCheck
                            },
                            contentDescription = stringResource(id = R.string.nav_wizari_bank)
                        )
                    },
                    label = {
                        Text(
                            text = "الكنترول",
                            fontWeight = if (currentTab == MainNavTab.WIZARI_CONTROL_BANK) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnTealLightContainer,
                        selectedTextColor = TealPrimary,
                        indicatorColor = TealLightContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_wizari_bank")
                )

                NavigationBarItem(
                    selected = currentTab == MainNavTab.ANNUAL_SUBSCRIPTION,
                    onClick = { sadisViewModel.selectTab(MainNavTab.ANNUAL_SUBSCRIPTION) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainNavTab.ANNUAL_SUBSCRIPTION) {
                                Icons.Filled.Star
                            } else {
                                Icons.Outlined.StarBorder
                            },
                            contentDescription = "الاشتراك السنوي"
                        )
                    },
                    label = {
                        Text(
                            text = "الاشتراك",
                            fontWeight = if (currentTab == MainNavTab.ANNUAL_SUBSCRIPTION) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OnGoldLightContainer,
                        selectedTextColor = GoldTertiary,
                        indicatorColor = GoldLightContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_subscription")
                )
            }
        }
    ) { innerPadding ->
        when (currentTab) {
            MainNavTab.TEXTBOOK_READER -> {
                TextbookReaderScreen(
                    selectedSubject = selectedSubject,
                    onSelectSubject = sadisViewModel::selectSubject,
                    isAnnualVipUnlocked = isAnnualVipUnlocked,
                    onOpenSubscriptionScreen = { sadisViewModel.selectTab(MainNavTab.ANNUAL_SUBSCRIPTION) },
                    onQuickUnlockAnnualVip = { sadisViewModel.activateSubscriptionPlan("ALL_7_SUBJECTS_VIP_ANNUAL") },
                    activeRecallEnabled = activeRecallEnabled,
                    onToggleActiveRecall = sadisViewModel::toggleActiveRecallMode,
                    revealedClozeKeys = revealedClozeKeys,
                    onRevealClozeWord = sadisViewModel::revealClozeWord,
                    onHideAllClozeWords = sadisViewModel::hideAllClozeWords,
                    lineMasteryList = lineMasteryList,
                    onToggleLineMastered = sadisViewModel::toggleLineMastered,
                    onToggleLineHighlight = sadisViewModel::toggleLineHighlight,
                    onSaveStudentNote = sadisViewModel::saveStudentNote,
                    onOpen3DModel = sadisViewModel::open3DModelInLab,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            MainNavTab.LAB_3D_4K -> {
                Interactive4KLabScreen(
                    selectedModelId = selected3DModelId,
                    onSelectModel = sadisViewModel::open3DModelInLab,
                    modelScores = modelScoresList,
                    onRecordQuizScore = sadisViewModel::recordModelQuizScore,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            MainNavTab.PLAN_100 -> {
                StudyPlan100Screen(
                    lineMasteryList = lineMasteryList,
                    modelScoresList = modelScoresList,
                    planTasksList = planTasksList,
                    onTogglePlanStep = sadisViewModel::togglePlanStep,
                    pomodoroSecondsLeft = pomodoroSeconds,
                    isPomodoroRunning = isPomodoroRunning,
                    onTogglePomodoro = sadisViewModel::togglePomodoroTimer,
                    onResetPomodoro = sadisViewModel::resetPomodoroTimer,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            MainNavTab.WIZARI_CONTROL_BANK -> {
                WizariControlBankScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }

            MainNavTab.ANNUAL_SUBSCRIPTION -> {
                SubscriptionScreen(
                    subscriptionStatus = subscriptionStatus,
                    onActivatePlan = sadisViewModel::activateSubscriptionPlan,
                    onResetToFree = sadisViewModel::resetToFreeChapterOneTier,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
