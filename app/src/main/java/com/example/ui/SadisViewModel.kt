package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.IraqiCurriculumData
import com.example.data.LineMasteryEntity
import com.example.data.ModelQuizScoreEntity
import com.example.data.SadisDatabase
import com.example.data.SadisRepository
import com.example.data.StudyPlanTaskEntity
import com.example.data.SubjectType
import com.example.data.SubscriptionStatusEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainNavTab {
    TEXTBOOK_READER,
    LAB_3D_4K,
    PLAN_100,
    WIZARI_CONTROL_BANK,
    ANNUAL_SUBSCRIPTION
}

class SadisViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SadisRepository

    val lineMasteryList: StateFlow<List<LineMasteryEntity>>
    val modelScoresList: StateFlow<List<ModelQuizScoreEntity>>
    val planTasksList: StateFlow<List<StudyPlanTaskEntity>>
    val subscriptionStatus: StateFlow<SubscriptionStatusEntity?>

    private val _currentTab = MutableStateFlow(MainNavTab.TEXTBOOK_READER)
    val currentTab: StateFlow<MainNavTab> = _currentTab.asStateFlow()

    private val _selectedSubject = MutableStateFlow(SubjectType.BIOLOGY)
    val selectedSubject: StateFlow<SubjectType> = _selectedSubject.asStateFlow()

    private val _activeRecallClozeEnabled = MutableStateFlow(false)
    val activeRecallClozeEnabled: StateFlow<Boolean> = _activeRecallClozeEnabled.asStateFlow()

    private val _revealedClozeKeys = MutableStateFlow<Set<String>>(emptySet())
    val revealedClozeKeys: StateFlow<Set<String>> = _revealedClozeKeys.asStateFlow()

    private val _selected3DModelId = MutableStateFlow(IraqiCurriculumData.interactive4KModels.first().modelId)
    val selected3DModelId: StateFlow<String> = _selected3DModelId.asStateFlow()

    // Pomodoro Focus Timer State
    private val _pomodoroSecondsLeft = MutableStateFlow(25 * 60)
    val pomodoroSecondsLeft: StateFlow<Int> = _pomodoroSecondsLeft.asStateFlow()

    private val _isPomodoroRunning = MutableStateFlow(false)
    val isPomodoroRunning: StateFlow<Boolean> = _isPomodoroRunning.asStateFlow()

    private var timerJob: Job? = null

    init {
        val dao = SadisDatabase.getDatabase(application).sadisDao()
        repository = SadisRepository(dao)

        lineMasteryList = repository.allLineMastery.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        modelScoresList = repository.allModelScores.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        planTasksList = repository.allPlanTasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        subscriptionStatus = repository.subscriptionStatus.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    }

    fun selectTab(tab: MainNavTab) {
        _currentTab.value = tab
    }

    fun selectSubject(subject: SubjectType) {
        _selectedSubject.value = subject
        val matchingModel = IraqiCurriculumData.interactive4KModels.firstOrNull { it.subject == subject }
        if (matchingModel != null) {
            _selected3DModelId.value = matchingModel.modelId
        }
    }

    fun open3DModelInLab(modelId: String) {
        _selected3DModelId.value = modelId
        _currentTab.value = MainNavTab.LAB_3D_4K
    }

    fun toggleActiveRecallMode() {
        _activeRecallClozeEnabled.value = !_activeRecallClozeEnabled.value
        if (!_activeRecallClozeEnabled.value) {
            _revealedClozeKeys.value = emptySet()
        }
    }

    fun revealClozeWord(key: String) {
        _revealedClozeKeys.value = _revealedClozeKeys.value + key
    }

    fun hideAllClozeWords() {
        _revealedClozeKeys.value = emptySet()
    }

    fun toggleLineMastered(lineId: String, subjectId: String) {
        viewModelScope.launch {
            val current = lineMasteryList.value.find { it.lineId == lineId }
            repository.toggleLineMastered(lineId, subjectId, current)
        }
    }

    fun toggleLineHighlight(lineId: String, subjectId: String) {
        viewModelScope.launch {
            val current = lineMasteryList.value.find { it.lineId == lineId }
            repository.toggleLineHighlight(lineId, subjectId, current)
        }
    }

    fun saveStudentNote(lineId: String, subjectId: String, note: String) {
        viewModelScope.launch {
            val current = lineMasteryList.value.find { it.lineId == lineId }
            repository.saveStudentNote(lineId, subjectId, note, current)
        }
    }

    fun recordModelQuizScore(modelId: String, score: Int, totalParts: Int) {
        viewModelScope.launch {
            val current = modelScoresList.value.find { it.modelId == modelId }
            repository.recordModelQuizSuccess(modelId, score, totalParts, current)
        }
    }

    fun togglePlanStep(stepId: Int) {
        viewModelScope.launch {
            val current = planTasksList.value.find { it.stepId == stepId }
            val nextState = !(current?.isCompleted ?: false)
            repository.togglePlanStep(stepId, nextState)
        }
    }

    fun activateSubscriptionPlan(planId: String) {
        viewModelScope.launch {
            repository.activateAnnualSubscription(planId)
        }
    }

    fun resetToFreeChapterOneTier() {
        viewModelScope.launch {
            repository.resetToFreeTier()
        }
    }

    fun togglePomodoroTimer() {
        if (_isPomodoroRunning.value) {
            _isPomodoroRunning.value = false
            timerJob?.cancel()
        } else {
            _isPomodoroRunning.value = true
            timerJob = viewModelScope.launch {
                while (_isPomodoroRunning.value && _pomodoroSecondsLeft.value > 0) {
                    delay(1000L)
                    _pomodoroSecondsLeft.value = (_pomodoroSecondsLeft.value - 1).coerceAtLeast(0)
                }
                if (_pomodoroSecondsLeft.value == 0) {
                    _isPomodoroRunning.value = false
                }
            }
        }
    }

    fun resetPomodoroTimer(minutes: Int = 25) {
        _isPomodoroRunning.value = false
        timerJob?.cancel()
        _pomodoroSecondsLeft.value = minutes * 60
    }
}
