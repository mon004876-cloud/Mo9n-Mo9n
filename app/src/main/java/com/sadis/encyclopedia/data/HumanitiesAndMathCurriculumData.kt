package com.sadis.encyclopedia.data

object HumanitiesAndMathCurriculumData {

    val pages: List<TextbookPage> = listOf(
        // ==================== 1. ISLAMIC EDUCATION (التربية الإسلامية) ====================
        TextbookPage(
            pageId = "islamic_u1_free",
            subject = SubjectType.ISLAMIC,
            bookPageNumber = 4,
            chapterOrder = 1,
            isFreeChapter = true,
            isCoverPage = true,
            chapterNumber = "الوحدة الأولى (مجانية بالكامل)",
            chapterTitle = "أحكام التلاوة (النون الساكنة والتنوين) + من سورة البقرة",
            topicTitle = "النص المنهجي الحرفي - كتاب التربية الإسلامية للسادس الإعدادي (ص 4 - 14)",
            lines = listOf(
                TextbookLine(
                    id = "isl_u1_l1",
                    lineIndex = 1,
                    sectionHeader = "أحكام النون الساكنة والتنوين: 1- الإظهار و 2- الإدغام",
                    verbatimText = "النون الساكنة: هي حرف خالٍ من الحركة يقع في وسط الكلمة أو في آخرها ويلفظ ساكناً في الوقف والوصل، والتنوين: هو نون ساكنة زائدة مسبوقة بحركة تلحق آخر الاسم وتلفظ ولا تكتب. وأحكامهما أربعة: أولاً- الإظهار: وهو إخراج النون الساكنة والتنوين من مخرجهما من غير غنة إذا جاء بعدهما أحد حروف الحلق الستة المجموعة في أوائل الكلمات (أخي هاك علماً حازه غير خاسر). ثانياً- الإدغام: وهو قلب النون الساكنة أو التنوين في آخر الكلمة الأولى إلى جنس أحد حروف (يرملون) في أول الكلمة الثانية ليصيرا حرفاً واحداً مشدداً، ويقسم إلى إدغام بغنة مع حروف (ينمو) وإدغام بلا غنة مع الحرفين (ل، ر).",
                    keyTermsToCloze = listOf("من غير غنة", "حروف الحلق الستة", "يرملون", "ينمو", "بلا غنة"),
                    wizariAttachments = listOf(
                        WizariControlQA(
                            id = "wiz_isl_tajweed_1",
                            yearsBadge = "وزاري ثابت سنوياً (2016 د1، 2018 د2، 2020 د1، 2022 د1، 2024 د1)",
                            questionType = "أحكام التلاوة (10 درجات ثابتة)",
                            questionText = "ما حكم النون الساكنة والتنوين في الآية الكريمة: ﴿صُمٌّ بُكْمٌ عُمْيٌ فَهُمْ لَا يَرْجِعُونَ﴾ وقوله تعالى: ﴿مِن رَّبِّهِمْ﴾ وقوله تعالى: ﴿عَذَابٌ أَلِيمٌ﴾ مع ذكر السبب؟",
                            controlModelAnswer = "1) (بُكْمٌ عُمْيٌ): الحكم: إظهار، السبب: لمجيء حرف العين (وهو من حروف الإظهار الحلقي) بعد التنوين.\n2) (مِن رَّبِّهِمْ): الحكم: إدغام بلا غنة، السبب: لمجيء حرف الراء بعد النون الساكنة في كلمتين.\n3) (عَذَابٌ أَلِيمٌ): الحكم: إظهار، السبب: لمجيء حرف الهمزة (من حروف الحلق الستة) بعد التنوين.",
                            gradeDistribution = "توزيع الدرجة في الكنترول: 10 درجات كاملة لسؤال أحكام التلاوة الأول (درجتان لكل استخراج: درجة للحكم + درجة للسبب).",
                            controlNote = "ملاحظة الكنترول: يشترط أن يكون الإدغام في كلمتين منفصلتين، فإذا اجتمعت النون الساكنة مع حروف (ينمو) في كلمة واحدة مثل (الدنيا، بنيان، صنوان، قنوان) فلا تدغم وتسمى إظهاراً مطلقاً.",
                            wittyHint = "لمحة فكاهية: حروف (يرملون) لا تقبل الإدغام إلا إذا كانت في كلمتين؛ أما إذا اجتمعت في كلمة واحدة مثل (الدنيا وبنيان) فكل حرف يحتفظ بشخصيته ويسمى إظهاراً مطلقاً!"
                        )
                    )
                ),
                TextbookLine(
                    id = "isl_u1_l2",
                    lineIndex = 2,
                    sectionHeader = "3- الإقلاب و 4- الإخفاء",
                    verbatimText = "ثالثاً- الإقلاب: وهو قلب النون الساكنة أو التنوين ميماً إذا جاء بعدها حرف الباء مع بقاء الغنة، مثل (مِن بَعْدِ) و (سَمِيعٌ بَصِيرٌ). رابعاً- الإخفاء: وهو النطق بالنون الساكنة أو التنوين خالية من التشديد وسطاً بين الإظهار والإدغام مع الغنة إذا جاء بعدهما أحد حروف الإخفاء الخمسة عشر المجموعة في أوائل كلمات البيت: (صف ذا ثنا كم جاد شخص قد سما .. دم طيباً زد في تقى ضع ظالماً).",
                    keyTermsToCloze = listOf("الإقلاب", "حرف الباء", "الإخفاء", "بين الإظهار والإدغام"),
                    wizariAttachments = listOf(
                        WizariControlQA(
                            id = "wiz_isl_tajweed_2",
                            yearsBadge = "وزاري مكرر (2015 د1، 2019 د1، 2021 د2، 2023 د1، 2024 د2)",
                            questionType = "عرف + استخرج الأحكام",
                            questionText = "عرف الإقلاب والإخفاء مع التمثيل لكل منهما، وعيّن الحكم في قوله تعالى: ﴿لَيُنبَذَنَّ﴾ و ﴿مَن ذَا الَّذِي﴾.",
                            controlModelAnswer = "الإقلاب: هو قلب النون الساكنة أو التنوين ميماً إذا جاء بعدها حرف الباء مع بقاء الغنة، ويقع في كلمة واحدة مثل (لَيُنبَذَنَّ) أو في كلمتين مثل (سَمِيعٌ بَصِيرٌ).\nالإخفاء: هو النطق بالنون الساكنة أو التنوين خالية من التشديد وسطاً بين الإظهار والإدغام مع الغنة إذا جاء بعدهما أحد حروف الإخفاء الخمسة عشر، مثل (مَن ذَا) لمجيء حرف الذال بعد النون الساكنة.",
                            gradeDistribution = "توزيع الدرجة في الكنترول: 10 درجات (5 درجات للتعريف مع المثال + 5 درجات للاستخراج مع السبب).",
                            controlNote = "ملاحظة الكنترول: حرف الباء هو الحرف الوحيد لحكم الإقلاب، وأي حرف لا ينتمي لحروف الحلق الستة ولا لـ (يرملون) ولا للباء فهو حتماً من حروف الإخفاء الخمسة عشر.",
                            wittyHint = "لمحة فكاهية: حرف (الباء) مدلل جداً في أحكام التلاوة؛ له حكم كامل وحده اسمه (الإقلاب) بدون أي شريك!"
                        )
                    )
                )
            ),
            controlPageSummary = "خلاصة الكنترول الوزاري للوحدة الأولى (مجانية): أحكام النون الساكنة والتنوين سؤال إجباري ثابت في مطلع ورقة أسئلة التربية الإسلامية بـ (10 درجات كاملة).",
            pageWittyBoost = "استراحة ذكية: أضمن 10 درجات في السادس الإعدادي كله هي أحكام التلاوة؛ أربعة أحكام تضعها في جيبك في 10 دقائق!"
        ),
        TextbookPage(
            pageId = "islamic_u2_locked",
            subject = SubjectType.ISLAMIC,
            bookPageNumber = 38,
            chapterOrder = 2,
            isFreeChapter = false,
            chapterNumber = "الوحدة الثانية (ضمن الاشتراك السنوي)",
            chapterTitle = "من سورة آل عمران + الحديث النبوي الشريف (في الجهاد وكرامة المجاهد)",
            topicTitle = "آيات الحفظ والمعاني والتفسير - كتاب التربية الإسلامية (ص 38 - 46)",
            lines = listOf(
                TextbookLine(
                    id = "isl_u2_l1",
                    lineIndex = 1,
                    sectionHeader = "من سورة آل عمران (آيات الحفظ: 90 - 92)",
                    verbatimText = "قال تعالى: ﴿إِنَّ الَّذِينَ كَفَرُوا بَعْدَ إِيمَانِهِمْ ثُمَّ ازْدَادُوا كُفْرًا لَّن تُقْبَلَ تَوْبَتُهُمْ وَأُولَٰئِكَ هُمُ الضَّالُّونَ * إِنَّ الَّذِينَ كَفَرُوا وَمَاتُوا وَهُمْ كُفَّارٌ فَلَن يُقْبَلَ مِنْ أَحَدِهِم مِّلْءُ الْأَرْضِ ذَهَبًا وَلَوِ افْتَدَىٰ بِهِ أُولَٰئِكَ لَهُمْ عَذَابٌ أَلِيمٌ وَمَا لَهُم مِّن نَّاصِرِينَ * لَن تَنَالُوا الْبِرَّ حَتَّىٰ تُنفِقُوا مِمَّا تُحِبُّونَ وَمَا تُنفِقُوا مِن شَيْءٍ فَإِنَّ اللَّهَ بِهِ عَلِيمٌ﴾.",
                    keyTermsToCloze = listOf("لَّن تُقْبَلَ تَوْبَتُهُمْ", "مِّلْءُ الْأَرْضِ ذَهَبًا", "لَن تَنَالُوا الْبِرَّ"),
                    wizariAttachments = listOf(
                        WizariControlQA(
                            id = "wiz_isl_imran_1",
                            yearsBadge = "وزاري (2016 د2، 2019 د1، 2022 د1، 2024 د1)",
                            questionType = "تفسير ومعاني الكلمات (15 درجة)",
                            questionText = "ما المراد بقوله تعالى: ﴿لَن تَنَالُوا الْبِرَّ حَتَّىٰ تُنفِقُوا مِمَّا تُحِبُّونَ﴾؟ وما معنى الكلمات: (ملء الأرض ذهباً، ولو افتدى به، البر)؟",
                            controlModelAnswer = "التفسير: لن تدركوا الجنة ولن تنالوا منزلة الأبرار وطاعة الله حتى تتصدقوا وتنفقوا في سبيل الله من خيار أموالكم التي تحبونها.\nالمعاني:\n- ملء الأرض ذهباً: ما يملأ الأرض من الذهب.\n- ولو افتدى به: ولو قدمه فداءً لنفسه من العذاب.\n- البر: الجنة، أو الخير والعمل الصالح.",
                            gradeDistribution = "توزيع الدرجة في الكنترول: 20 درجة لفرع الآيات القرآنية، و 15 درجة للتفسير والمعاني.",
                            controlNote = "ملاحظة الكنترول: ضبط أواخر الكلمات في النص القرآني وعدم الخلط بين خواتيم الآيات يمنح الدرجة الكاملة.",
                            wittyHint = "لمحة فكاهية: معاني الكلمات في الإسلامية هي هدية مجانية من 5 درجات؛ كلمات قصيرة وواضحة ترفع معدلك فوراً!"
                        )
                    )
                )
            ),
            controlPageSummary = "خلاصة الكنترول الوزاري للوحدة الثانية: حفظ النص القرآني من سورة آل عمران مع المعاني وأبرز ما ترشد إليه الآيات.",
            pageWittyBoost = "استراحة ذكية: الإسلامية في السادس هي رافعة المعدل الأولى؛ الـ 100 فيها تعادل ذهباً خالصاً في القبول المركزي!"
        ),

        // ==================== 2. ARABIC LANGUAGE (اللغة العربية) ====================
        TextbookPage(
            pageId = "arabic_u1_free",
            subject = SubjectType.ARABIC,
            bookPageNumber = 7,
            chapterOrder = 1,
            isFreeChapter = true,
            isCoverPage = true,
            chapterNumber = "الوحدة الأولى (مجانية بالكامل)",
            chapterTitle = "قواعد اللغة العربية: أسلوب الاستفهام + الأدب الحديث",
            topicTitle = "تعريف الاستفهام وحروفه وأسماؤه وإعرابها - كتاب اللغة العربية (ص 7 - 24)",
            lines = listOf(
                TextbookLine(
                    id = "ara_u1_l1",
                    lineIndex = 1,
                    sectionHeader = "أسلوب الاستفهام: الاستفهام الحقيقي (التصديقي والتصوري) وخصائص (هل والهمزة)",
                    verbatimText = "الاستفهام: هو طلب يراد به الجواب عن شيء مجهول في الذهن بأداة من أدوات الاستفهام، ويقسم الاستفهام الحقيقي إلى قسمين: 1- الاستفهام التصديقي (وهو الذي يجاب عنه بحرف جواب مثل: نعم، لا، بلى، أجل، إي، وأدواته هل والهمزة بدون أم المعادلة). 2- الاستفهام التصوري (وهو الذي يجاب عنه بالتعيين، وأداته الهمزة المقترنة بأم المعادلة المتصلة، وجميع أسماء الاستفهام). وتختص الهمزة عن (هل) بدخولها على الجمل المثبتة والمنفية، وتصدرها على أحرف العطف (الواو، الفاء، ثم)، ودخولها على أسلوب الشرط والحروف المشبهة بالفعل (إنَّ)، وجواز حذفها من الكلام تخفيفاً.",
                    keyTermsToCloze = listOf("التصديقي", "التصوري", "بالتعيين", "أم المعادلة", "تتصدر على أحرف العطف"),
                    wizariAttachments = listOf(
                        WizariControlQA(
                            id = "wiz_ara_istifham_1",
                            yearsBadge = "وزاري ثابت سنوياً (2014 د1، 2017 د1، 2019 د2، 2021 د1، 2023 د1، 2024 د1)",
                            questionType = "ما خصيصة الهمزة + استبدال (هل) بالهمزة",
                            questionText = "قال تعالى: ﴿أَفَلَمْ يَنظُرُوا إِلَى السَّمَاءِ فَوْقَهُمْ كَيْفَ بَنَيْنَاهَا﴾ وقال تعالى: ﴿أَوَلَمْ تُؤْمِن قَالَ بَلَىٰ﴾. ما خصيصة همزة الاستفهام في النصين الكريمين؟ وهل يجوز استبدال (هل) بالهمزة؟ ولماذا؟",
                            controlModelAnswer = "خصيصة الهمزة في النصين الكريمين:\n1) لها الصدارة في الكلام حيث سبقت حرفي العطف (الفاء في أفلم، والواو في أولم).\n2) دخلت على الكلام المنفي (لم ينظروا، لم تؤمن).\n\nحكم استبدال (هل) بالهمزة: لا يجوز استبدال (هل) بالهمزة؛ لأن (هل) لا تدخل على الجمل المنفية.",
                            gradeDistribution = "توزيع الدرجة في الكنترول: 4 درجات (درجتان لذكر الخصيصتين + درجتان لحكم الاستبدال مع السبب).",
                            controlNote = "ملاحظة الكنترول: إذا اجتمعت خصيصتان للهمزة في النص الوزاري (كالصدارة على حرف العطف والدخول على النفي) فيجب ذكرهما معاً لاستحقاق الدرجة الكاملة.",
                            wittyHint = "لمحة فكاهية: الهمزة في قواعد السادس ملكة متوجة؛ تتصدر على حروف العطف وتدخل على النفي والشرط، وتترك (هل) واقفة بالباب لا تدخل على المنفي أبداً!"
                        )
                    )
                ),
                TextbookLine(
                    id = "ara_u1_l2",
                    lineIndex = 2,
                    sectionHeader = "إعراب أسماء الاستفهام الدالة على العاقل وغير العاقل (مَنْ، ما، كم، أيّ)",
                    verbatimText = "تعرب أسماء الاستفهام الدالة على الذات العاقلة وغير العاقلة (مَنْ، مَنْ ذا، ما، ماذا، أيّ المضافة للعاقل أو غير العاقل، كم التمييزها عاقل أو غير عاقل): 1- في محل رفع مبتدأ أو خبر مقدم جوازاً إذا تلاها اسم معرفة. 2- في محل رفع مبتدأ فقط في ست حالات: إذا تلاها اسم نكرة، أو شبه جملة، أو فعل لازم، أو فعل ناقص استوفى خبره، أو فعل مبني للمجهول، أو فعل متعدٍّ استوفى مفعوله. 3- في محل نصب مفعول به مقدم وجوباً إذا تلاها فعل متعدٍّ لم يستوفِ مفعوله.",
                    keyTermsToCloze = listOf("مبتدأ أو خبر مقدم", "اسم معرفة", "مفعول به مقدم وجوباً", "لم يستوفِ مفعوله"),
                    wizariAttachments = listOf(
                        WizariControlQA(
                            id = "wiz_ara_istifham_2",
                            yearsBadge = "وزاري مكرر 18 مرة (2013 د1، 2016 د1، 2018 د2، 2020 د1، 2022 د1، 2024 د1)",
                            questionType = "أعرب اسم الاستفهام معللاً",
                            questionText = "قال تعالى: ﴿مَن ذَا الَّذِي يُقْرِضُ اللَّهَ قَرْضًا حَسَنًا﴾، وقال الشاعر: (أضاعوني وأيَّ فتىً أضاعوا .. ليوم كريهةٍ وسداد ثغرِ). ما إعراب اسمي الاستفهام (مَنْ ذا) و (أيّ)؟ وما السبب؟",
                            controlModelAnswer = "1) (مَنْ ذا): اسم استفهام مبني على السكون في محل رفع مبتدأ أو خبر مقدم جوازاً؛ السبب: تلاه اسم معرفة وهو الاسم الموصول (الذي).\n2) (أيَّ): اسم استفهام مفعول به مقدم وجوباً منصوب وعلامة نصبه الفتحة الظاهرة على آخره وهو مضاف؛ السبب: تلاه فعل متعدٍّ (أضاعوا) لم يستوفِ مفعوله.",
                            gradeDistribution = "توزيع الدرجة في الكنترول: 4 درجات (درجتان للإعراب التفصيلي + درجتان لذكر السبب المنهجي).",
                            controlNote = "ملاحظة الكنترول: اسم الاستفهام (أيّ) معرب بالحركات الثلاث (الضمة والفتحة والكسرة)، بينما سائر أسماء الاستفهام مبنية ما عدا (أيّ).",
                            wittyHint = "لمحة فكاهية: لتعرف هل الفعل المتعدي استوفى مفعوله أم لا، ضع قبله (ماذا) أو أضف له الضمير (الهاء)؛ فإن قبلها ولم تجد المفعول في الجملة فاسم الاستفهام هو المفعول به المقدم وجوباً!"
                        )
                    )
                ),
                TextbookLine(
                    id = "ara_u1_l3",
                    lineIndex = 3,
                    sectionHeader = "الأدب والنصوص: الشعر الحديث ومدرسة الإحياء ومحمد سعيد الحبوبي",
                    verbatimText = "اصطلح الباحثون على جعل حملة نابليون على مصر عام (1798م) فاصلاً بين الأدب الحديث وما قبله لما تركته من آثار في الحياة الثقافية المصرية، فنشأت مدرسة الإحياء في أواخر القرن التاسع عشر لمحاكاة النماذج الشعرية العربية القديمة في العصر العباسي، ومن أبرز روادها محمود سامي البارودي في مصر، ومحمد سعيد الحبوبي (1849 - 1915م) في العراق الذي اشتهر بموشحاته الغزلية وجهاده ضد المحتل البريطاني في الشعيبة.",
                    keyTermsToCloze = listOf("1798م", "مدرسة الإحياء", "محمود سامي البارودي", "محمد سعيد الحبوبي", "بموشحاته"),
                    wizariAttachments = listOf(
                        WizariControlQA(
                            id = "wiz_ara_adab_1",
                            yearsBadge = "وزاري (2015 د1، 2018 د1، 2021 د1، 2023 د2، 2024 د1)",
                            questionType = "أسئلة الأدب والنصوص الوزارية",
                            questionText = "1) ما الذي هدف إليه شعراء مدرسة الإحياء؟ ومن أبرز روادها في مصر والعراق؟\n2) ما الموشح؟ وبمَ يتألف؟ ولماذا سمي موشحة الحبوبي بـ (ذات القوافي)؟",
                            controlModelAnswer = "1) هدفت مدرسة الإحياء إلى إحياء التقاليد الشعرية العربية القديمة والمحافظة على الهوية الأدبية للأمة، وأبرز روادها محمود سامي البارودي وأحمد شوقي وحافظ إبراهيم في مصر، ومحمد سعيد الحبوبي والرصافي والزهاوي والجواهري في العراق.\n2) الموشح: فن شعري مستحدث نشأ في مشرق الدولة الأندلسية يتألف من مطلع وأدوار وأغصان وأقفال وخرجة، وسميت موشحة الحبوبي لتعدد قوافيها بتعدد أدوارها وأغصانها.",
                            gradeDistribution = "توزيع الدرجة في الكنترول: 5 درجات لكل فرع في قسم الأدب والنصوص (مجموع الأدب 40 درجة).",
                            controlNote = "ملاحظة الكنترول: كتابة الأبيات الشعرية المطلوبة للحفظ مضبوطة بالشكل مع عنوان القصيدة يمنح الدرجة الكاملة.",
                            wittyHint = "لمحة فكاهية: الأدب والنصوص عليه 40 درجة مضمونة من الـ 100؛ حفظ سطرين في الأدب يعادل حل معضلة إعرابية كاملة!"
                        )
                    )
                )
            ),
            controlPageSummary = "خلاصة الكنترول الوزاري للوحدة الأولى (مجانية): أسلوب الاستفهام ومدرسة الإحياء والحبوبي يشكلان أكثر من 20 درجة في الامتحان الوزاري للغة العربية.",
            pageWittyBoost = "استراحة ذكية: من يضبط الاستفهام والأدب منذ البداية يدخل قاعة امتحان العربي وهو يبتسم بثقة!"
        ),
        TextbookPage(
            pageId = "arabic_u2_locked",
            subject = SubjectType.ARABIC,
            bookPageNumber = 42,
            chapterOrder = 2,
            isFreeChapter = false,
            chapterNumber = "الوحدة الثانية (ضمن الاشتراك السنوي)",
            chapterTitle = "قواعد اللغة العربية: أسلوب النفي الظاهر والضمني",
            topicTitle = "أدوات النفي: فعل النفي (ليس) والحروف المشبهة بـ (ليس) و (لا) النافية للجنس",
            lines = listOf(
                TextbookLine(
                    id = "ara_u2_l1",
                    lineIndex = 1,
                    sectionHeader = "فعل النفي (ليس) ومعمولاه وتوكيد نفيه",
                    verbatimText = "(ليس): فعل ماضٍ ناقص جامد يفيد النفي، يدخل على الجملة الاسمية فيرفع المبتدأ اسماً له وينصب الخبر خبراً له (تأثيره الإعرابي)، وينفي اتصاف الاسم بالخبر (تأثيره المعنوي)، وهو يعمل دون قيد أو شرط حتى لو تقدم خبره على اسمه أو انتقض نفيه بـ (إلا). ويؤكد نفي (ليس) بطريقتين: إدخال حرف الجر الزائد (الباء) على خبرها المفرد، أو إدخال حرف الجر الزائد (مِنْ) على اسمها النكرة.",
                    keyTermsToCloze = listOf("يرفع المبتدأ", "وينصب الخبر", "اتصاف الاسم بالخبر", "الباء", "مِنْ"),
                    wizariAttachments = listOf(
                        WizariControlQA(
                            id = "wiz_ara_nafi_1",
                            yearsBadge = "وزاري (2015 د2، 2018 د1، 2020 د2، 2023 د1، 2024 د1)",
                            questionType = "دلّ على معمولي (ليس) + أكد النفي",
                            questionText = "قال تعالى: ﴿أَلَيْسَ اللَّهُ بِكَافٍ عَبْدَهُ﴾، وقال الشاعر: (وليسَ الصبرُ في سوحِ الرزايا .. مذلّاً للّأباةِ ولا الكرامِ). دلّ على معمولي (ليس) في النصين، ثم أكد نفي (ليس) في البيت الشعري.",
                            controlModelAnswer = "1) في الآية الكريمة: اسم ليس: لفظ الجلالة (اللهُ) مرفوع، وخبر ليس: (بكافٍ) الباء حرف جر زائد للتوكيد وكافٍ خبر ليس مجرور لفظاً منصوب محلاً.\n2) في البيت الشعري: اسم ليس: (الصبرُ)، وخبر ليس: (مذلاً).\nتوكيد نفي (ليس) في البيت: (وليسَ الصبرُ في سوحِ الرزايا بمذلٍّ).",
                            gradeDistribution = "توزيع الدرجة في الكنترول: 4 درجات (درجتان لاستخراج المعمولين + درجتان لتوكيد النفي).",
                            controlNote = "ملاحظة الكنترول: إذا كان خبر (ليس) جملة فعلية وأردنا توكيده بالباء، نحول الفعل إلى اسم مشتق مفرد ثم ندخل الباء عليه.",
                            wittyHint = "لمحة فكاهية: (ليس) أقوى أخوات كان شخصيةً؛ تعمل بلا شروط حتى لو تقدم خبرها أو جاءت (إلا) في طريقها، بينما (ما) الحجازية تبطل عملها من أول عقبة!"
                        )
                    )
                )
            ),
            controlPageSummary = "خلاصة الكنترول الوزاري للوحدة الثانية: معمولي (ليس) واستبدال (ما) بـ (ليس) و(لات) و(لا النافية للجنس) من ثوابت قواعد السادس.",
            pageWittyBoost = "استراحة ذكية: أسلوب النفي واضح المعالم؛ اضبط شروط عمل كل أداة وتأثيرها الإعرابي والمعنوي تضمن الدرجة الكاملة!"
        ),

        // ==================== 3. ENGLISH LANGUAGE (اللغة الإنجليزية) ====================
        TextbookPage(
            pageId = "english_u1_free",
            subject = SubjectType.ENGLISH,
            bookPageNumber = 6,
            chapterOrder = 1,
            isFreeChapter = true,
            isCoverPage = true,
            chapterNumber = "Unit 1 (مجاني بالكامل - Free Chapter)",
            chapterTitle = "Unit 1: The Health Service, Zaid Tariq & Grammar Rules",
            topicTitle = "Past Simple vs Past Continuous, Adjectives (-ed/-ing), Phrasal Verbs & Textbook Passages",
            lines = listOf(
                TextbookLine(
                    id = "eng_u1_l1",
                    lineIndex = 1,
                    sectionHeader = "Textbook Passage 1: It was floating when he felt something push him (Zaid Tariq - SB p. 8)",
                    verbatimText = "It was a calm morning in the beautiful seaside town of Sharm El-Sheikh, Egypt. Twenty-year-old Zaid Tariq, a tourist from Iraq, was floating peacefully 150 metres from the shore when he felt something push him. 'I didn't know what it was,' he told reporters. 'I thought perhaps my airbed had hit a piece of wood.' Then it pushed even harder and I fell into the water. That's when I saw it was a shark. While he was trying to swim away, the shark bit his arm. Six dolphins made a circle around Tariq so the shark couldn't get near him. Coastguard Jameel Elalawi heard Tariq's screams and rescued him by boat.",
                    keyTermsToCloze = listOf("Sharm El-Sheikh", "tourist from Iraq", "airbed", "Six dolphins", "Jameel Elalawi"),
                    wizariAttachments = listOf(
                        WizariControlQA(
                            id = "wiz_eng_tariq_1",
                            yearsBadge = "وزاري مكرر 17 مرة (2014 د1، 2016 د1، 2018 د2، 2020 د1، 2022 د1، 2024 د1)",
                            questionType = "Reading Comprehension Textbook Questions (10 Marks)",
                            questionText = "1) Who was Zaid Tariq? Where was he staying?\n2) What was Zaid Tariq doing when he saw the shark?\n3) What saved Tariq / frightened the shark away?\n4) Who heard Tariq's screams and rescued him by boat?",
                            controlModelAnswer = "1) He was a twenty-year-old tourist from Iraq. He was staying in Sharm El-Sheikh, Egypt.\n2) He was swimming / floating on his airbed when he saw the shark.\n3) Six dolphins made a circle around him and frightened the shark away.\n4) The coastguard Jameel Elalawi heard his screams and rescued him by boat.",
                            gradeDistribution = "توزيع الدرجة في الكنترول: درجتان لكل نقطة في السؤال الأول فرع (B) الخاص بقطع الكتاب المنهجية (مجموعه 10 درجات).",
                            controlNote = "ملاحظة الكنترول: تقبل في الكنترول الإجابة الكاملة أو المختصرة الصحيحة مثل كتابة (Six dolphins) أو (Jameel Elalawi) دون محاسبة على الأخطاء الإملائية البسيطة التي لا تغير المعنى.",
                            wittyHint = "لمحة فكاهية: زيد طارق ذهب في سفرة سياحية إلى شرم الشيخ، فصار أشهر سائح في تاريخ امتحانات السادس الإعدادي العراقي منذ 2014 إلى اليوم!"
                        )
                    )
                ),
                TextbookLine(
                    id = "eng_u1_l2",
                    lineIndex = 2,
                    sectionHeader = "Grammar (Unit 1): Past Continuous & Past Simple (When / While / As / And)",
                    verbatimText = "We can use the Past Continuous and the Past Simple in the same sentence to show that one action happened in the middle of another action. We use the Past Continuous (was/were + v-ing) for the longer action and the Past Simple (v-ed / irregular) for the shorter action: 1) While / As + Past Continuous, Past Simple. 2) When / And + Past Simple, Past Continuous. Also: Adjectives ending in (-ed) describe the person who has the feeling, while adjectives ending in (-ing) describe what or who gives them that feeling.",
                    keyTermsToCloze = listOf("While / As", "Past Continuous", "When / And", "Past Simple", "(-ed)", "(-ing)"),
                    wizariAttachments = listOf(
                        WizariControlQA(
                            id = "wiz_eng_grammar_u1",
                            yearsBadge = "وزاري ثابت سنوياً (2015 د1، 2017 د2، 2019 د1، 2021 د1، 2023 د1، 2024 د1)",
                            questionType = "Grammar & Functions (Q2 A & B - 30 Marks)",
                            questionText = "1) Khaled (play) football when he (break) his ankle. (Correct the verbs)\n2) While my baggage (go) through the X-ray machine, I walked through the metal detector. (Correct)\n3) She is not very (interested / interesting) in fashion. (Choose)",
                            controlModelAnswer = "1) Khaled was playing football when he broke his ankle.\n2) While my baggage was going through the X-ray machine...\n3) interested (because it describes a person's feeling + followed by 'in').",
                            gradeDistribution = "توزيع الدرجة في الكنترول: درجتان لكل جملة قواعد في السؤال الثاني (A) و (B).",
                            controlNote = "ملاحظة الكنترول: انتبه إلى أن الأفعال الشاذة في الماضي البسيط مثل (break → broke, see → saw, hide → hid, bite → bit) يجب حفظ تصاريفها بدقة.",
                            wittyHint = "لمحة فكاهية: قاعدة سريعة لا تخطئ: بعد (While / As) يأتي دائماً ماضٍ مستمر (was/were + ing)، وبعد (When / and) يأتي ماضٍ بسيط!"
                        )
                    )
                )
            ),
            controlPageSummary = "خلاصة الكنترول الوزاري لـ Unit 1 (مجاني): قطعة زيد طارق ومصطفى أحمد وقواعد الماضي البسيط والمستمر والأفعال المركبة وإنشاء إعلانات السجائر تضمن لك أكثر من 25 درجة في الإنكليزي.",
            pageWittyBoost = "استراحة ذكية: Unit 1 هو العمود الفقري لكتاب الإنكليزي؛ إتقانه يمنحك ربع درجة الامتحان الوزاري بسهولة!"
        ),
        TextbookPage(
            pageId = "english_u2_locked",
            subject = SubjectType.ENGLISH,
            bookPageNumber = 28,
            chapterOrder = 2,
            isFreeChapter = false,
            chapterNumber = "Unit 2 (ضمن الاشتراك السنوي)",
            chapterTitle = "Unit 2: Police Officers' Duties, Radar & Modal Verbs",
            topicTitle = "Necessity (must / have to / need to), Polite Requests, Suggestions & Expectations (should / shouldn't)",
            lines = listOf(
                TextbookLine(
                    id = "eng_u2_l1",
                    lineIndex = 1,
                    sectionHeader = "Textbook Passage: Police Officers' Duties (SB p. 17) & Expectation (should)",
                    verbatimText = "People have to follow the law so that we can all live together safely. There are a lot of different laws, so police officers have a lot of different duties. Some officers direct traffic to make sure that drivers can use the roads easily and without having accidents. They also use radar speed guns to identify people who are driving too fast. Drivers mustn't speed because speeding is one of the leading causes of death on the road.",
                    keyTermsToCloze = listOf("follow the law", "live together safely", "direct traffic", "radar speed guns", "leading causes of death"),
                    wizariAttachments = listOf(
                        WizariControlQA(
                            id = "wiz_eng_police_1",
                            yearsBadge = "وزاري مكرر (2014 د2، 2017 د1، 2020 د1، 2022 د2، 2024 د1)",
                            questionType = "Textbook Passage + Expectation Grammar",
                            questionText = "1) Why do people have to follow the law?\n2) You have always wanted to fly. I think you will like the Air Force. (Expectation: use 'should')",
                            controlModelAnswer = "1) So that we can all live together safely.\n2) You have always wanted to fly, so you should like the Air Force.",
                            gradeDistribution = "توزيع الدرجة في الكنترول: درجتان لكل فرع في الامتحان الوزاري.",
                            controlNote = "ملاحظة الكنترول: في موضوع التوقع (Expectation)، نحذف عبارة (I think) ونحذف (will) ونعوض عنها بـ (should) في الجملة المثبتة، و(shouldn't) عند وجود (I don't think) بشرط ألا يكون الفعل الرئيسي (fail أو lose).",
                            wittyHint = "لمحة فكاهية: قاعدة التوقع (Expectation) أسهل درجتين في الإنكليزي؛ احذف (I think + will) وضع مكانها (should) وخذ الدرجتين كاملة!"
                        )
                    )
                )
            ),
            controlPageSummary = "خلاصة الكنترول الوزاري لـ Unit 2: واجبات الشرطة والرادار وموضوع التوقع (Expectation) والطلب المؤدب والاقتراح وإنشاء المقابلة الوظيفية.",
            pageWittyBoost = "استراحة ذكية: قواعد Unit 2 قصيرة ومباشرة؛ كل موضوع فيها يحل بخطوة واحدة في الدفتر الامتحاني!"
        ),

        // ==================== 4. MATHEMATICS (الرياضيات) ====================
        TextbookPage(
            pageId = "math_ch1_free",
            subject = SubjectType.MATHEMATICS,
            bookPageNumber = 8,
            chapterOrder = 1,
            isFreeChapter = true,
            isCoverPage = true,
            chapterNumber = "الفصل الأول (مجاني بالكامل)",
            chapterTitle = "الأعداد المركبة (Complex Numbers) ومبرهنة دي موافر",
            topicTitle = "الصيغة العادية (a + bi)، الجذور التربيعية، المعادلة التربيعية، والصيغة القطبية ودي موافر",
            lines = listOf(
                TextbookLine(
                    id = "math_ch1_l1",
                    lineIndex = 1,
                    sectionHeader = "الوحدة التخيلية (i = √-1) والصيغة العادية (الجبريّة) للعدد المركب (z = a + bi)",
                    verbatimText = "يسمى العدد الذي يكتب بالصورة (z = a + bi) حيث (a , b ∈ R) و (i = √-1) عدداً مركباً، ويسمى (a) جزءه الحقيقي و (b) جزءه التخيلي، وتسمى الصيغة (a + bi) بالصيغة العادية أو الجبرية للعدد المركب. ومن قوى (i) الأساسية: (i² = -1)، (i³ = -i)، (i⁴ = 1). ومرافق العدد المركب (z = a + bi) هو العدد (z̄ = a - bi) وحاصل ضرب العدد المركب في مرافقه يساوي مربع الجزء الحقيقي زائد مربع الجزء التخيلي: (a + bi)(a - bi) = a² + b².",
                    keyTermsToCloze = listOf("a + bi", "i² = -1", "i³ = -i", "a - bi", "a² + b²"),
                    wizariAttachments = listOf(
                        WizariControlQA(
                            id = "wiz_math_c1_1",
                            yearsBadge = "وزاري مكرر (2015 د1، 2018 د1، 2020 د2، 2022 د1، 2024 د1)",
                            questionType = "إيجاد قيمتي (x , y ∈ R) - (10 درجات)",
                            questionText = "جد قيمتي كل من (x , y ∈ R) اللتين تحققان المعادلة: (2x + i)(y - 2i) = 2 - 9i.",
                            controlModelAnswer = "الحل النموذجي في الكنترول:\nبفك الأقواس في الطرف الأيسر:\n2xy - 4xi + yi - 2i² = 2 - 9i\nبما أن (i² = -1) فإن (-2i² = +2):\n(2xy + 2) + (-4x + y)i = 2 - 9i\nمن تساوي عددين مركبين (الحقيقي = الحقيقي، والتخيلي = التخيلي):\n1) 2xy + 2 = 2  ⇒  2xy = 0  ⇒  إما x = 0 أو y = 0\n2) -4x + y = -9\n- عندما x = 0 نعوض في (2) فنحصل على: y = -9\n- عندما y = 0 نعوض في (2) فنحصل على: -4x = -9  ⇒  x = 9/4.",
                            gradeDistribution = "توزيع الدرجة في الكنترول: 10 درجات كاملة (3 درجات للتبسيط بالصيغة العادية + 3 درجات لتكوين المعادلتين + 4 درجات لإيجاد قيم x و y).",
                            controlNote = "ملاحظة الكنترول: عند تساوي عددين مركبين يجب وضع كل طرف بالصيغة العادية (a + bi) قبل مساواة الحقيقي بالحقيقي والتخيلي بالتخيلي (بدون كتابة i في معادلة التخيلي).",
                            wittyHint = "لمحة فكاهية: إياك أن تأخذ الحرف (i) معك عندما تساوي التخيلي بالتخيلي! (i) مجرد بطاقة تعريف للجزء التخيلي تنتهي مهمتها عند تكوين المعادلة!"
                        )
                    )
                ),
                TextbookLine(
                    id = "math_ch1_l2",
                    lineIndex = 2,
                    sectionHeader = "الصيغة القطبية ومبرهنة دي موافر (De Moivre's Theorem)",
                    verbatimText = "إذا كان (z = x + yi) عدداً مركباً فإن مقياسه (Modulus) هو: r = |z| = √(x² + y²)، وسعته الأساسية (Argument θ) تحقق: cos θ = x / r و sin θ = y / r، وتكتب الصيغة القطبية للعدد المركب بالصورة: z = r (cos θ + i sin θ). وتنص مبرهنة دي موافر لكل (n ∈ Z) على أن: [r (cos θ + i sin θ)]ⁿ = rⁿ (cos nθ + i sin nθ)، أما نتيجة مبرهنة دي موافر للجذور النونية فهي: z^(1/n) = r^(1/n) [cos((θ + 2kπ)/n) + i sin((θ + 2kπ)/n)] حيث k = 0, 1, 2, ..., n-1.",
                    keyTermsToCloze = listOf("r = |z| = √(x² + y²)", "cos θ = x / r", "r (cos θ + i sin θ)", "cos nθ + i sin nθ", "(θ + 2kπ)/n"),
                    wizariAttachments = listOf(
                        WizariControlQA(
                            id = "wiz_math_demoivre",
                            yearsBadge = "وزاري ثابت سنوياً (2014 د1، 2017 د1، 2019 د1، 2021 د1، 2023 د1، 2024 د1)",
                            questionType = "مبرهنة ونتيجة دي موافر (10 درجات ثابتة)",
                            questionText = "احسب باستخدام مبرهنة دي موافر (أو التعميم): (1 - i)⁷.",
                            controlModelAnswer = "الحل النموذجي في الكنترول:\nنضع العدد بالصيغة الديكارتية: z = 1 - i = (1, -1)\nالمقياس: r = √(1² + (-1)²) = √2\ncos θ = 1/√2 ، sin θ = -1/√2 (تقع الزاوية في الربع الرابع وزاوية الإسناد π/4)\nإذن القيمة الأساسية للسعة: θ = 2π - π/4 = 7π/4\nالصيغة القطبية: z = √2 (cos 7π/4 + i sin 7π/4)\nبتطبيق مبرهنة دي موافر للأس (7):\nz⁷ = (√2)⁷ [cos(49π/4) + i sin(49π/4)]\nبما أن 49π/4 بعد طرح الدورات الزوجية (48π/4 = 12π) تكافئ (π/4):\nz⁷ = 8√2 (cos π/4 + i sin π/4) = 8√2 (1/√2 + (1/√2)i) = 8 + 8i.",
                            gradeDistribution = "توزيع الدرجة في الكنترول: 10 درجات (3 درجات لإيجاد المقياس والسعة والصيغة القطبية + 4 درجات لتطبيق مبرهنة دي موافر + 3 درجات للتبسيط النهائي).",
                            controlNote = "ملاحظة الكنترول: يجب أن تكون الصيغة القطبية قياسية (cos θ في الجزء الحقيقي و sin θ في الجزء التخيلي وبينهما إشارة موجبة) قبل توزيع الأس.",
                            wittyHint = "لمحة فكاهية: مبرهنة دي موافر أوفى صديق لطالب السادس في الرياضيات؛ مضمونة الحضور كل سنة بـ 10 درجات كاملة في الورقة الامتحانية!"
                        )
                    )
                )
            ),
            controlPageSummary = "خلاصة الكنترول الوزاري للفصل الأول (مجاني): الفصل الأول في الرياضيات يحصد 20 درجة كاملة في الوزاري (فرع على قيم x,y أو المعادلة التربيعية + فرع ثابت على مبرهنة أو نتيجة دي موافر).",
            pageWittyBoost = "استراحة ذكية: 20 درجة في الرياضيات من الفصل الأول وحده؛ اضبط المقياس والسعة ودي موافر وتفوق بثقة!"
        ),
        TextbookPage(
            pageId = "math_ch2_locked",
            subject = SubjectType.MATHEMATICS,
            bookPageNumber = 54,
            chapterOrder = 2,
            isFreeChapter = false,
            chapterNumber = "الفصل الثاني (ضمن الاشتراك السنوي)",
            chapterTitle = "القطوع المخروطية (Conic Sections): المكافئ والناقص والزائد",
            topicTitle = "معادلات القطع المكافئ والقطع الناقص والقطع الزائد والعلاقة المساعدة (c² = a² ∓ b²)",
            lines = listOf(
                TextbookLine(
                    id = "math_ch2_l1",
                    lineIndex = 1,
                    sectionHeader = "القطع الناقص (Ellipse) والقطع الزائد (Hyperbola)",
                    verbatimText = "في القطع الناقص: مجموع بعدي أية نقطة عليه عن البؤرتين يساوي عدداً ثابتاً هو طول المحور الكبير (2a)، ويكون (a > b) و (a > c)، والعلاقة بين الثوابت هي: c² = a² - b²، واختلافه المركزي أقل من الواحد (e = c/a < 1). أما في القطع الزائد: القيمة المطلقة لفرق بعدي أية نقطة عليه عن البؤرتين يساوي عدداً ثابتاً هو طول المحور الحقيقي (2a)، وتكون (c > a) و (c > b)، والعلاقة بين الثوابت هي: c² = a² + b²، واختلافه المركزي أكبر من الواحد (e = c/a > 1).",
                    keyTermsToCloze = listOf("طول المحور الكبير (2a)", "c² = a² - b²", "e = c/a < 1", "c² = a² + b²", "e = c/a > 1"),
                    wizariAttachments = listOf(
                        WizariControlQA(
                            id = "wiz_math_conics_1",
                            yearsBadge = "وزاري ثابت سنوياً (2015 د1، 2018 د2، 2021 د1، 2023 د1، 2024 د1)",
                            questionType = "الربط بين القطوع المخروطية (10 درجات)",
                            questionText = "جد معادلة القطع الناقص الذي مركزه نقطة الأصل وإحدى بؤرتيه هي بؤرة القطع المكافئ (y² - 16x = 0) وطول محوره الكبير يساوي (10) وحدات.",
                            controlModelAnswer = "الحل النموذجي في الكنترول:\nمن معادلة القطع المكافئ: y² = 16x بالمقارنة مع y² = 4px نجد أن: 4p = 16 ⇒ p = 4\nإذن بؤرة المكافئ هي F(4, 0) وهي نفسها إحدى بؤرتي القطع الناقص ⇒ c = 4 (البؤرتان على محور السينات).\nطول المحور الكبير: 2a = 10 ⇒ a = 5 ⇒ a² = 25.\nمن العلاقة في القطع الناقص (c² = a² - b²): 16 = 25 - b² ⇒ b² = 9.\nمعادلة القطع الناقص السيني: x²/25 + y²/9 = 1.",
                            gradeDistribution = "توزيع الدرجة في الكنترول: 10 درجات (3 درجات لإيجاد بؤرة المكافئ c + 3 درجات لإيجاد a و b + 4 درجات لكتابة المعادلة القياسية).",
                            controlNote = "ملاحظة الكنترول: انتبه إلى أن إشارة العلاقة المساعدة (c² = a² ∓ b²) توافق اسم القطع (ناقص في القطع الناقص، وزائد في القطع الزائد) بينما إشارة المعادلة القياسية عكس اسم القطع.",
                            wittyHint = "لمحة فكاهية: حيلة ذكية لا تُنسى: المعادلة القياسية تخالف اسم القطع في الإشارة، أما العلاقة المساعدة (c²) فتطيع اسم القطع حرفياً!"
                        )
                    )
                )
            ),
            controlPageSummary = "خلاصة الكنترول الوزاري للفصل الثاني: مسائل الربط بين القطوع الثلاثة (المكافئ والناقص والزائد) تحصد 20 درجة ثابتة في الامتحان الوزاري.",
            pageWittyBoost = "استراحة ذكية: القطوع المخروطية أشبه بلعبة ألغاز ممتعة؛ كل معلومة تعطيك أحد الثوابت (a أو b أو c)!"
        )
    )
}
