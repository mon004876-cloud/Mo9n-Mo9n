package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "line_mastery")
data class LineMasteryEntity(
    @PrimaryKey val lineId: String,
    val subjectId: String,
    val isMastered: Boolean = false,
    val isHighlighted: Boolean = false,
    val studentNote: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "model_quiz_scores")
data class ModelQuizScoreEntity(
    @PrimaryKey val modelId: String,
    val bestScore: Int = 0,
    val totalParts: Int = 0,
    val completedCount: Int = 0
)

@Entity(tableName = "study_plan_tasks")
data class StudyPlanTaskEntity(
    @PrimaryKey val stepId: Int,
    val isCompleted: Boolean = false
)

@Entity(tableName = "subscription_status")
data class SubscriptionStatusEntity(
    @PrimaryKey val id: Int = 1,
    val isAnnualVipUnlocked: Boolean = false,
    val activePlanId: String = "FREE_CHAPTER_1",
    val activatedAt: Long = 0L
)

@Dao
interface SadisStudyDao {
    @Query("SELECT * FROM line_mastery")
    fun getAllLineMastery(): Flow<List<LineMasteryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLineMastery(entity: LineMasteryEntity)

    @Query("SELECT * FROM model_quiz_scores")
    fun getAllModelScores(): Flow<List<ModelQuizScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertModelScore(entity: ModelQuizScoreEntity)

    @Query("SELECT * FROM study_plan_tasks")
    fun getAllPlanTasks(): Flow<List<StudyPlanTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlanTask(entity: StudyPlanTaskEntity)

    @Query("SELECT * FROM subscription_status WHERE id = 1")
    fun getSubscriptionStatus(): Flow<SubscriptionStatusEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSubscriptionStatus(entity: SubscriptionStatusEntity)
}

@Database(
    entities = [
        LineMasteryEntity::class,
        ModelQuizScoreEntity::class,
        StudyPlanTaskEntity::class,
        SubscriptionStatusEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SadisDatabase : RoomDatabase() {
    abstract fun sadisDao(): SadisStudyDao

    companion object {
        @Volatile
        private var INSTANCE: SadisDatabase? = null

        fun getDatabase(context: Context): SadisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SadisDatabase::class.java,
                    "sadis_encyclopedia_db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class SadisRepository(private val dao: SadisStudyDao) {
    val allLineMastery: Flow<List<LineMasteryEntity>> = dao.getAllLineMastery()
    val allModelScores: Flow<List<ModelQuizScoreEntity>> = dao.getAllModelScores()
    val allPlanTasks: Flow<List<StudyPlanTaskEntity>> = dao.getAllPlanTasks()
    val subscriptionStatus: Flow<SubscriptionStatusEntity?> = dao.getSubscriptionStatus()

    suspend fun toggleLineMastered(lineId: String, subjectId: String, current: LineMasteryEntity?) {
        val updated = (current ?: LineMasteryEntity(lineId = lineId, subjectId = subjectId))
            .copy(isMastered = !(current?.isMastered ?: false), updatedAt = System.currentTimeMillis())
        dao.upsertLineMastery(updated)
    }

    suspend fun toggleLineHighlight(lineId: String, subjectId: String, current: LineMasteryEntity?) {
        val updated = (current ?: LineMasteryEntity(lineId = lineId, subjectId = subjectId))
            .copy(isHighlighted = !(current?.isHighlighted ?: false), updatedAt = System.currentTimeMillis())
        dao.upsertLineMastery(updated)
    }

    suspend fun saveStudentNote(lineId: String, subjectId: String, note: String, current: LineMasteryEntity?) {
        val updated = (current ?: LineMasteryEntity(lineId = lineId, subjectId = subjectId))
            .copy(studentNote = note, updatedAt = System.currentTimeMillis())
        dao.upsertLineMastery(updated)
    }

    suspend fun recordModelQuizSuccess(modelId: String, score: Int, totalParts: Int, current: ModelQuizScoreEntity?) {
        val best = maxOf(score, current?.bestScore ?: 0)
        val count = (current?.completedCount ?: 0) + 1
        dao.upsertModelScore(
            ModelQuizScoreEntity(
                modelId = modelId,
                bestScore = best,
                totalParts = totalParts,
                completedCount = count
            )
        )
    }

    suspend fun togglePlanStep(stepId: Int, isCompleted: Boolean) {
        dao.upsertPlanTask(StudyPlanTaskEntity(stepId = stepId, isCompleted = isCompleted))
    }

    suspend fun activateAnnualSubscription(planId: String) {
        dao.upsertSubscriptionStatus(
            SubscriptionStatusEntity(
                id = 1,
                isAnnualVipUnlocked = true,
                activePlanId = planId,
                activatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun resetToFreeTier() {
        dao.upsertSubscriptionStatus(
            SubscriptionStatusEntity(
                id = 1,
                isAnnualVipUnlocked = false,
                activePlanId = "FREE_CHAPTER_1",
                activatedAt = 0L
            )
        )
    }
}
