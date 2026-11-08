package com.sadis.encyclopedia.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sadis.encyclopedia.data.IraqiCurriculumData
import com.sadis.encyclopedia.data.LineMasteryEntity
import com.sadis.encyclopedia.data.ModelQuizScoreEntity
import com.sadis.encyclopedia.data.SadisDatabase
import com.sadis.encyclopedia.data.SadisRepository
import com.sadis.encyclopedia.data.StudyPlanTaskEntity
import com.sadis.encyclopedia.data.SubjectType
import com.sadis.encyclopedia.data.SubscriptionStatusEntity
import com.sadis.encyclopedia.data.VoucherVerifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

    /** نتيجة آخر محاولة تفعيل (تُعرض للطالب ثم تُمسح). */
    private val _voucherResult = MutableStateFlow<VoucherVerifier.Result?>(null)
    val voucherResult: StateFlow<VoucherVerifier.Result?> = _voucherResult.asStateFlow()

    private val _isRedeemingVoucher = MutableStateFlow(false)
    val isRedeemingVoucher: StateFlow<Boolean> = _isRedeemingVoucher.asStateFlow()

    private var voucherRegistry: Map<String, VoucherVerifier.RegistryEntry>? = null
    private var failedVoucherAttempts = 0

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

    fun redeemVoucher(rawCode: String) {
        if (_isRedeemingVoucher.value) return
        viewModelScope.launch {
            _isRedeemingVoucher.value = true
            try {
                // إبطاء تدريجي بعد المحاولات الخاطئة لمنع التخمين المتكرر
                if (failedVoucherAttempts > 2) delay(minOf(failedVoucherAttempts * 1000L, 10_000L))
                val registry = voucherRegistry ?: loadVoucherRegistry().also { voucherRegistry = it }
                val result = VoucherVerifier.verify(rawCode, registry, System.currentTimeMillis())
                if (result is VoucherVerifier.Result.Valid) {
                    failedVoucherAttempts = 0
                    repository.activateFromVoucher(result.entry)
                } else {
                    failedVoucherAttempts++
                }
                _voucherResult.value = result
            } finally {
                _isRedeemingVoucher.value = false
            }
        }
    }

    fun clearVoucherResult() {
        _voucherResult.value = null
    }

    private suspend fun loadVoucherRegistry(): Map<String, VoucherVerifier.RegistryEntry> =
        withContext(Dispatchers.IO) {
            runCatching {
                getApplication<Application>().assets.open(VOUCHER_REGISTRY_ASSET)
                    .bufferedReader(Charsets.UTF_8)
                    .useLines { VoucherVerifier.parseRegistry(it) }
            }.getOrDefault(emptyMap())
        }

    /** للتجربة أثناء التطوير فقط (الزر مخفي في نسخة الإصدار). */
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

    companion object {
        const val VOUCHER_REGISTRY_ASSET = "vouchers/voucher_registry.txt"
    }
}
