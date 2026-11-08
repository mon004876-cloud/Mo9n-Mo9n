package com.example.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ArabicBurgundy
import com.example.ui.theme.ArabicSoftBg
import com.example.ui.theme.BiologyEmerald
import com.example.ui.theme.BiologySoftBg
import com.example.ui.theme.ChemistryAmber
import com.example.ui.theme.ChemistrySoftBg
import com.example.ui.theme.EnglishCyan
import com.example.ui.theme.EnglishSoftBg
import com.example.ui.theme.IslamicSoftBg
import com.example.ui.theme.IslamicTeal
import com.example.ui.theme.MathSoftBg
import com.example.ui.theme.MathViolet
import com.example.ui.theme.PhysicsRoyal
import com.example.ui.theme.PhysicsSoftBg

enum class SubjectType(
    val id: String,
    val titleAr: String,
    val subtitleAr: String,
    val accentColor: Color,
    val softBgColor: Color,
    val editionYear: String,
    val totalPagesInBook: Int,
    val singleSubjectPriceIqd: String
) {
    ISLAMIC(
        id = "islamic",
        titleAr = "التربية الإسلامية",
        subtitleAr = "الوحدة الأولى (مجانية): سورة البقرة وأحكام التلاوة | الوحدة الثانية: سورة آل عمران",
        accentColor = IslamicTeal,
        softBgColor = IslamicSoftBg,
        editionYear = "الطبعة المعتمدة - وزارة التربية العراقية",
        totalPagesInBook = 184,
        singleSubjectPriceIqd = "30,000 د.ع / للكتاب الواحد سنوياً"
    ),
    ARABIC(
        id = "arabic",
        titleAr = "اللغة العربية (القواعد والأدب)",
        subtitleAr = "الوحدة الأولى (مجانية): أسلوب الاستفهام والأدب الحديث | الوحدة الثانية: أسلوب النفي",
        accentColor = ArabicBurgundy,
        softBgColor = ArabicSoftBg,
        editionYear = "الطبعة المعتمدة - وزارة التربية العراقية",
        totalPagesInBook = 296,
        singleSubjectPriceIqd = "30,000 د.ع / للكتاب الواحد سنوياً"
    ),
    ENGLISH(
        id = "english",
        titleAr = "اللغة الإنجليزية (English)",
        subtitleAr = "Unit 1 (Free): Health & Zaid Tariq | Unit 2: Law & Safety | Literature: The Swing",
        accentColor = EnglishCyan,
        softBgColor = EnglishSoftBg,
        editionYear = "Iraq Opportunities - 6th Preparatory",
        totalPagesInBook = 220,
        singleSubjectPriceIqd = "30,000 د.ع / للكتاب الواحد سنوياً"
    ),
    MATHEMATICS(
        id = "math",
        titleAr = "الرياضيات - السادس العلمي",
        subtitleAr = "الفصل الأول (مجاني): الأعداد المركبة ودي موافر | الفصل الثاني: القطوع المخروطية | التفاضل والتكامل",
        accentColor = MathViolet,
        softBgColor = MathSoftBg,
        editionYear = "الطبعة المعتمدة - وزارة التربية العراقية",
        totalPagesInBook = 310,
        singleSubjectPriceIqd = "30,000 د.ع / للكتاب الواحد سنوياً"
    ),
    BIOLOGY(
        id = "biology",
        titleAr = "الأحياء - السادس العلمي",
        subtitleAr = "الفصل الأول (مجاني): الخلية | الفصل الثاني: الأنسجة | الفصل الثالث: التكاثر",
        accentColor = BiologyEmerald,
        softBgColor = BiologySoftBg,
        editionYear = "الطبعة المعتمدة - وزارة التربية العراقية",
        totalPagesInBook = 312,
        singleSubjectPriceIqd = "30,000 د.ع / للكتاب الواحد سنوياً"
    ),
    PHYSICS(
        id = "physics",
        titleAr = "الفيزياء - السادس العلمي",
        subtitleAr = "الفصل الأول (مجاني): المتسعات | الفصل الثاني: الحث الكهرومغناطيسي | التيار المتناوب",
        accentColor = PhysicsRoyal,
        softBgColor = PhysicsSoftBg,
        editionYear = "الطبعة المعتمدة - وزارة التربية العراقية",
        totalPagesInBook = 328,
        singleSubjectPriceIqd = "30,000 د.ع / للكتاب الواحد سنوياً"
    ),
    CHEMISTRY(
        id = "chemistry",
        titleAr = "الكيمياء - السادس العلمي",
        subtitleAr = "الفصل الأول (مجاني): الثرموداينمك | الفصل الرابع: الكيمياء الكهربائية | التوازن الأيوني",
        accentColor = ChemistryAmber,
        softBgColor = ChemistrySoftBg,
        editionYear = "الطبعة المعتمدة - وزارة التربية العراقية",
        totalPagesInBook = 304,
        singleSubjectPriceIqd = "30,000 د.ع / للكتاب الواحد سنوياً"
    )
}

data class SubscriptionPlanTier(
    val planId: String,
    val titleAr: String,
    val priceIqd: String,
    val badgeText: String,
    val features: List<String>,
    val isRecommended: Boolean = false
)

data class WizariControlQA(
    val id: String,
    val yearsBadge: String,
    val questionType: String,
    val questionText: String,
    val controlModelAnswer: String, // 100% Verbatim Control Answer from the Iraqi Ministry Textbook
    val gradeDistribution: String,
    val controlNote: String,
    val wittyHint: String // Smart, mature witty touch for 6th Prep students
)

data class TextbookLine(
    val id: String,
    val lineIndex: Int,
    val sectionHeader: String? = null,
    val verbatimText: String,
    val keyTermsToCloze: List<String>,
    val wizariAttachments: List<WizariControlQA>,
    val linked3DModelId: String? = null
)

data class TextbookFigure(
    val figureNumber: String,
    val captionVerbatim: String,
    @DrawableRes val imageRes: Int,
    val linked3DModelId: String
)

data class TextbookPage(
    val pageId: String,
    val subject: SubjectType,
    val bookPageNumber: Int,
    val chapterOrder: Int = 1, // 1 = Chapter 1 (FREE), >= 2 = Chapter 2+ (Requires Annual Subscription)
    val isFreeChapter: Boolean = true, // First chapter of every subject is 100% FREE
    val isCoverPage: Boolean = false,
    val chapterNumber: String,
    val chapterTitle: String,
    val topicTitle: String,
    val lines: List<TextbookLine>,
    val bookFigure: TextbookFigure? = null,
    val controlPageSummary: String,
    val pageWittyBoost: String
)

data class Hotspot3DPart(
    val partId: String,
    val nameAr: String,
    val nameEn: String,
    val normalizedX: Float,
    val normalizedY: Float,
    val normalizedZ: Float,
    val locationTextbook: String,
    val functionTextbook: String,
    val wizariFrequency: String,
    val controlKeyPhrase: String,
    val wittyMemoryHook: String
)

data class Interactive4KModel(
    val modelId: String,
    val subject: SubjectType,
    val titleAr: String,
    val textbookRef: String,
    @DrawableRes val render4KRes: Int,
    val descriptionAr: String,
    val simulationParamLabel: String,
    val simulationUnit: String,
    val parts: List<Hotspot3DPart>
)

data class StudyTechniqueStep(
    val stepId: Int,
    val titleAr: String,
    val methodBadge: String,
    val descriptionAr: String,
    val controlAdvice: String,
    val wittyStudentTip: String,
    val xpReward: Int
)
