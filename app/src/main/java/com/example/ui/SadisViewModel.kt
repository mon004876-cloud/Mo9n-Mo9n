package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.ActivationCodeRecord
import com.example.data.ActivationCodeRegistry
import com.example.data.ActivationCodes
import com.example.data.IraqiCurriculumData
import com.example.data.LineMasteryEntity
import com.example.data.ModelQuizScoreEntity
import com.example.data.PlanIds
import com.example.data.SadisDatabase
import com.example.data.SadisRepository
import com.example.data.StudyPlanTaskEntity
import com.example.data.SubjectType
import com.example.data.SubscriptionStatusEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    private var lastPomodoroMinutes = 25

    private val _activationResult = MutableStateFlow<ActivationResult?>(null)
    val activationResult: StateFlow<ActivationResult?> = _activationResult.asStateFlow()
    private var redeemJob: Job? = null

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

    /** Checks a printed activation code and, if it is genuine, unlocks what it was sold for. */
    fun redeemActivationCode(rawCode: String) {
        if (redeemJob?.isActive == true) return
        redeemJob = viewModelScope.launch {
            _activationResult.value = ActivationResult.Checking
            val record = withContext(Dispatchers.Default) {
                ActivationCodes.redeem(rawCode, ActivationCodeRegistry.records) ?: developerDemoRecord(rawCode)
            }
            _activationResult.value = if (record != null && repository.applyPurchase(record.planId, record.subjectId)) {
                ActivationResult.Activated(record.planId, record.subjectId)
            } else {
                ActivationResult.InvalidCode
            }
        }
    }

    fun clearActivationResult() {
        if (redeemJob?.isActive != true) {
            _activationResult.value = null
        }
    }

    /** Developer builds only: unlocks every chapter so the paid screens can be tested. */
    fun developerUnlockAllSubjects() {
        if (!BuildConfig.DEBUG) return
        viewModelScope.launch {
            repository.applyPurchase(PlanIds.ALL_SUBJECTS_ANNUAL, null)
        }
    }

    /** Developer builds only: goes back to the free first chapters. */
    fun resetToFreeChapterOneTier() {
        if (!BuildConfig.DEBUG) return
        viewModelScope.launch {
            repository.resetToFreeTier()
        }
    }

    private fun developerDemoRecord(rawCode: String): ActivationCodeRecord? =
        if (BuildConfig.DEBUG && rawCode.trim().equals(DEVELOPER_DEMO_CODE, ignoreCase = true)) {
            ActivationCodeRecord(id = "DEMO", saltHex = "", hashHex = "", planId = PlanIds.ALL_SUBJECTS_ANNUAL)
        } else {
            null
        }

    fun togglePomodoroTimer() {
        if (_isPomodoroRunning.value) {
            _isPomodoroRunning.value = false
            timerJob?.cancel()
        } else {
            if (_pomodoroSecondsLeft.value == 0) {
                _pomodoroSecondsLeft.value = lastPomodoroMinutes * 60
            }
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
        lastPomodoroMinutes = minutes
        _pomodoroSecondsLeft.value = minutes * 60
    }

    companion object {
        /** Accepted only by developer (debug) builds, never by the release app. */
        const val DEVELOPER_DEMO_CODE = "SADIS100"
    }
}

sealed interface ActivationResult {
    data object Checking : ActivationResult
    data class Activated(val planId: String, val subjectId: String?) : ActivationResult
    data object InvalidCode : ActivationResult
}
