package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.IraqiCurriculumData
import com.example.data.SubjectType
import com.example.data.SubscriptionPlanTier
import com.example.data.SubscriptionStatusEntity
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

@Composable
fun SubscriptionScreen(
    subscriptionStatus: SubscriptionStatusEntity?,
    onActivatePlan: (String) -> Unit,
    onResetToFree: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isVipUnlocked = subscriptionStatus?.isAnnualVipUnlocked == true
    var voucherCode by remember { mutableStateOf("") }
    var voucherFeedback by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Current Subscription Status Banner
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isVipUnlocked) GoldLightContainer else SoftMintSurface
                ),
                border = BorderStroke(
                    2.dp,
                    if (isVipUnlocked) GoldTertiary else TealPrimary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isVipUnlocked) Icons.Default.Verified else Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = if (isVipUnlocked) GoldTertiary else TealPrimary,
                                modifier = Modifier.size(30.dp)
                            )
                            Column {
                                Text(
                                    text = if (isVipUnlocked) {
                                        "الاشتراك السنوي الشامل مفعّل (جميع الفصول مفتوحة)"
                                    } else {
                                        "الباقة الحالية: الفصل الأول مجاني بالكامل لكل مادة"
                                    },
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (isVipUnlocked) OnGoldLightContainer else OnTealLightContainer
                                )
                                Text(
                                    text = if (isVipUnlocked) {
                                        "جميع فصول المواد الـ 7 ومجسمات 360° 4K وبنك الكنترول مفتوحة أمامك بالكامل."
                                    } else {
                                        "جرّب الفصل الأول مجاناً في جميع المواد الـ 7، واشترك سنوياً لفتح باقي الفصول حتى نهاية الامتحانات الوزارية."
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (isVipUnlocked) {
                        OutlinedButton(
                            onClick = onResetToFree,
                            border = BorderStroke(1.dp, OnGoldLightContainer),
                            modifier = Modifier.testTag("reset_free_tier_btn")
                        ) {
                            Text("العودة لوضع (الفصل الأول المجاني فقط) للتجربة", color = OnGoldLightContainer)
                        }
                    }
                }
            }
        }

        // Pricing Tiers Cards
        items(IraqiCurriculumData.subscriptionPlans, key = { it.planId }) { plan ->
            SubscriptionPlanCard(
                plan = plan,
                isCurrentlyActive = (isVipUnlocked && subscriptionStatus?.activePlanId == plan.planId) ||
                    (!isVipUnlocked && plan.planId == "FREE_CHAPTER_1"),
                onSelectPlan = {
                    if (plan.planId == "FREE_CHAPTER_1") {
                        onResetToFree()
                    } else {
                        onActivatePlan(plan.planId)
                    }
                }
            )
        }

        // Single Subject Pricing Breakdown Table
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "جدول تسعيرة المواد الـ 7 للسادس الإعدادي:",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "الفصل الأول / الوحدة الأولى مجانية 100% في كل مادة، والاشتراك السنوي يفتح باقي الفصول:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    SubjectType.entries.forEach { subject ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = subject.titleAr,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = subject.accentColor
                                )
                                Text(
                                    text = "الفصل الأول: مجاني • باقي الفصول (${subject.totalPagesInBook} صفحة)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = subject.softBgColor,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, subject.accentColor.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = subject.singleSubjectPriceIqd,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = subject.accentColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    }
                }
            }
        }

        // Iraqi Student Payment Methods & Library Activation Code Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, TealPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Redeem,
                            contentDescription = null,
                            tint = TealPrimary
                        )
                        Text(
                            text = "التفعيل الفوري عبر كود المكتبات أو زين كاش (ZainCash):",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "يدعم التطبيق الدفع المباشر عبر محفظة زين كاش (ZainCash)، بطاقة كي كارد / ماستركارد الرافدين، أو إدخال كود البطاقة المشتراة من المكتبات المعتمدة في بغداد والمحافظات (جرّب كتابة SADIS100 للتفعيل الفوري):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = voucherCode,
                        onValueChange = { voucherCode = it },
                        placeholder = { Text("أدخل كود التفعيل السنوي (مثال: SADIS100)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("voucher_code_input")
                    )

                    if (voucherFeedback != null) {
                        Text(
                            text = voucherFeedback!!,
                            style = MaterialTheme.typography.labelLarge,
                            color = TealPrimary
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                onActivatePlan("ALL_7_SUBJECTS_VIP_ANNUAL")
                                voucherFeedback = "✓ تم تفعيل الباقة السنوية الشاملة بنجاح! جميع فصول المواد الـ 7 مفتوحة الآن."
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("activate_voucher_btn")
                        ) {
                            Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تفعيل جميع الفصول الآن")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscriptionPlanCard(
    plan: SubscriptionPlanTier,
    isCurrentlyActive: Boolean,
    onSelectPlan: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (plan.isRecommended) Color(0xFFFFFBEB) else Color.White
        ),
        border = BorderStroke(
            width = if (plan.isRecommended || isCurrentlyActive) 2.dp else 1.dp,
            color = when {
                isCurrentlyActive -> TealPrimary
                plan.isRecommended -> GoldTertiary
                else -> MaterialTheme.colorScheme.outline
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (plan.isRecommended) 5.dp else 2.dp),
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
                Surface(
                    color = if (plan.isRecommended) GoldLightContainer else TealLightContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (plan.isRecommended) Icons.Default.Star else Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (plan.isRecommended) GoldTertiary else TealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = plan.badgeText,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (plan.isRecommended) OnGoldLightContainer else OnTealLightContainer
                        )
                    }
                }

                Text(
                    text = plan.priceIqd,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (plan.isRecommended) GoldTertiary else TealPrimary
                )
            }

            Text(
                text = plan.titleAr,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            plan.features.forEach { feature ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = TealPrimary,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp)
                    )
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Button(
                onClick = onSelectPlan,
                colors = ButtonDefaults.buttonColors(
                    containerColor = when {
                        isCurrentlyActive -> TealPrimary
                        plan.isRecommended -> GoldTertiary
                        else -> TealPrimary
                    }
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("select_plan_btn_${plan.planId}")
            ) {
                Text(
                    text = if (isCurrentlyActive) {
                        "✓ هذه الباقة مفعّلة حالياً"
                    } else if (plan.planId == "FREE_CHAPTER_1") {
                        "تفعيل الباقة التجريبية المجانية"
                    } else {
                        "اشترك الآن وافتح جميع الفصول (${plan.priceIqd})"
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
