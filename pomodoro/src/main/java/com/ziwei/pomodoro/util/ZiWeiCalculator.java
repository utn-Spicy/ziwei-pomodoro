package com.ziwei.pomodoro.util;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * 紫微番茄钟 · 命理分析引擎（MVP 双场景版）
 *
 * 场景一（仅年月日）：三柱 → 日干定性格底色 + 月令判旺衰 → 番茄钟策略
 * 场景二（年月日+时辰）：三柱 + 命宫位置 + 主星校准 → 更精准策略
 *
 * 日柱计算采用 1900-01-01（甲戌日）为基准，用 Java 8+ 日期库精确推算。
 * 命宫计算：从寅宫起顺数到月，逆数到时。
 */
public class ZiWeiCalculator {

    // ========================================================================
    // 常量
    // ========================================================================
    private static final String[] STEMS   = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private static final String[] BRANCHES= {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    private static final String[] ZODIAC  = {"鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};
    private static final String[] STEM_ELEMENT  = {"木","木","火","火","土","土","金","金","水","水"};
    private static final String[] STEM_ELEMENT_FULL = {
            "甲木·阳木·参天大树","乙木·阴木·花草藤蔓",
            "丙火·阳火·太阳之火","丁火·阴火·灯烛之火",
            "戊土·阳土·高山厚土","己土·阴土·田园之土",
            "庚金·阳金·刀剑之金","辛金·阴金·珠宝之金",
            "壬水·阳水·江河之水","癸水·阴水·雨露之水"
    };

    /**
     * 日柱干支参考点：1900-01-01 → 甲戌日（stemIdx=0, branchIdx=10）
     */
    private static final LocalDate REF_DATE = LocalDate.of(1900, 1, 1);
    private static final int REF_STEM_IDX   = 0;   // 甲
    private static final int REF_BRANCH_IDX = 10;  // 戌

    /**
     * 五虎遁：年干 → 正月（寅月）天干索引
     * 甲己→丙(2), 乙庚→戊(4), 丙辛→庚(6), 丁壬→壬(8), 戊癸→甲(0)
     */
    private static final int[] MONTH_STEM_START = {2, 4, 6, 8, 0};

    // ========================================================================
    // 日主五行 → 学习风格策略（10 种）
    // ========================================================================
    private static PomodoroStrategy makeStrategy(
            int stemIdx, int month, String mainStar, boolean hasMingPalace) {

        PomodoroStrategy s = new PomodoroStrategy();
        s.dayStemName      = STEMS[stemIdx];
        s.dayStemFull      = STEM_ELEMENT_FULL[stemIdx];
        s.element          = STEM_ELEMENT[stemIdx];
        s.mainStar         = mainStar;
        s.hasAccurateMingPalace = hasMingPalace;

        // 判断日主是否得令（在月令中处于旺相）
        s.inSeason = isInSeason(stemIdx, month);

        // 根据日干定制策略
        switch (stemIdx % 10) {
            case 0: // 甲木
                s.personalityType  = "参天大树型";
                s.personalityDesc  = "目标感强、自上而下、喜欢挑战性任务。习惯先看见全貌再深入。";
                s.studyAdvice      = "适合大块时间攻坚高难度任务。先列提纲再填充细节，每完成一个大节点给自己一个阶段性奖励。";
                s.recommendDuration = s.inSeason ? 30 : 25;
                s.recommendBreak    = 5;
                s.maxBeforeLongBreak= 3;
                s.breakAdvice      = "连续3个番茄后必须长休息。不要恋战，甲木容易沉浸到忘记时间。";
                break;
            case 1: // 乙木
                s.personalityType  = "花草藤蔓型";
                s.personalityDesc  = "灵活适应、喜欢循序渐进、善于在已有框架上延伸。需要持续的正向反馈。";
                s.studyAdvice      = "适合分阶段、分模块推进。每个番茄钟设定一个明确的小目标，完成后及时记录进度。";
                s.recommendDuration = s.inSeason ? 25 : 20;
                s.recommendBreak    = 5;
                s.maxBeforeLongBreak= 4;
                s.breakAdvice      = "利用休息时间站起来活动一下。乙木需要身体舒展来保持思维灵活。";
                break;
            case 2: // 丙火
                s.personalityType  = "太阳之火型";
                s.personalityDesc  = "热情充沛、行动力强、喜欢与人互动和分享。但容易高开低走，后半程泄气。";
                s.studyAdvice      = "利用前20分钟的高能量期处理最难的任务。适合与人讨论、共同学习来维持动力。";
                s.recommendDuration = s.inSeason ? 25 : 15;
                s.recommendBreak    = 5;
                s.maxBeforeLongBreak= 3;
                s.breakAdvice      = "休息时不要刷手机。站起来走动、看看窗外，让眼睛和大脑同时休息。";
                break;
            case 3: // 丁火 ← 你的日主
                s.personalityType  = "灯烛之火型";
                s.personalityDesc  = "专注持久、深入细致、善于在安静环境中持续输出。像灯火一样温和但有穿透力。";
                s.studyAdvice      = "适合深度编码和研究型任务。25分钟为一个单元刚刚好，不需要太长的预热时间。";
                s.recommendDuration = s.inSeason ? 25 : 20;
                s.recommendBreak    = 5;
                s.maxBeforeLongBreak= 4;
                s.breakAdvice      = "丁火容易'燃尽'而不自知。注意休息时完全脱离屏幕，真正的放松才能让灯火持续。";
                break;
            case 4: // 戊土
                s.personalityType  = "高山厚土型";
                s.personalityDesc  = "稳重扎实、系统化思维、不喜欢被打断节奏。学东西一旦入门就很稳固。";
                s.studyAdvice      = "制定固定的每日学习计划和节奏，建立习惯后不要轻易打破。适合长时间、连续性的学习周期。";
                s.recommendDuration = s.inSeason ? 30 : 25;
                s.recommendBreak    = 5;
                s.maxBeforeLongBreak= 4;
                s.breakAdvice      = "戊土容易连续坐太久。设置强制番茄钟结束提醒，站起来活动筋骨。";
                break;
            case 5: // 己土
                s.personalityType  = "田园之土型";
                s.personalityDesc  = "耐心细致、温故知新、善于在实践中消化知识。不喜欢纯粹的理论空谈。";
                s.studyAdvice      = "适合边学边练的模式。每个理论知识点后立刻动手实践，用输出倒逼输入。";
                s.recommendDuration = s.inSeason ? 25 : 20;
                s.recommendBreak    = 8;
                s.maxBeforeLongBreak= 3;
                s.breakAdvice      = "休息时回顾一下刚才学的内容，己土在放松时反而容易消化吸收。";
                break;
            case 6: // 庚金
                s.personalityType  = "刀剑之金型";
                s.personalityDesc  = "果断刚锐、目标明确、喜欢解决难题和挑战。效率高但容易对简单任务失去耐心。";
                s.studyAdvice      = "任务清单驱动法最适合。每天列出最难的3件事，优先攻克。完成一个划掉一个。";
                s.recommendDuration = s.inSeason ? 20 : 15;
                s.recommendBreak    = 5;
                s.maxBeforeLongBreak= 3;
                s.breakAdvice      = "完成一个番茄后立刻休息，不要在任务中间停顿。保持快节奏的切换。";
                break;
            case 7: // 辛金
                s.personalityType  = "珠宝之金型";
                s.personalityDesc  = "精益求精、注重细节和品质、善于雕琢打磨。但有时候会因追求完美而停滞。";
                s.studyAdvice      = "设定时间盒限制，不要在一个细节上无限深入。先完成再完美，后续可以回来看。";
                s.recommendDuration = s.inSeason ? 25 : 20;
                s.recommendBreak    = 5;
                s.maxBeforeLongBreak= 3;
                s.breakAdvice      = "休息时做一些完全不相关的事情。辛金需要拉开距离才能看清整体。";
                break;
            case 8: // 壬水
                s.personalityType  = "江河之水型";
                s.personalityDesc  = "智慧流畅、兴趣广泛、善于融会贯通。但容易兴趣太多难以聚焦。";
                s.studyAdvice      = "每天固定一个主线和最多一个副线，不要超过两个方向。利用好奇心作为学习驱动力。";
                s.recommendDuration = s.inSeason ? 25 : 20;
                s.recommendBreak    = 5;
                s.maxBeforeLongBreak= 3;
                s.breakAdvice      = "换个不同类型的任务作为休息。壬水需要多样性来维持新鲜感。";
                break;
            case 9: // 癸水
                s.personalityType  = "雨露之水型";
                s.personalityDesc  = "含蓄内敛、思维深入、善于独立研究和思考。不喜欢被外界过多干扰。";
                s.studyAdvice      = "适合独立、安静的研究环境。深度学习时关闭所有通知，进入心流状态。";
                s.recommendDuration = s.inSeason ? 25 : 20;
                s.recommendBreak    = 8;
                s.maxBeforeLongBreak= 2;
                s.breakAdvice      = "癸水需要宁静的休息环境。冥想或闭目养神比刷手机更适合你。";
                break;
        }
        return s;
    }

    /**
     * 判断日主在月令是否得令（旺相）
     */
    private static boolean isInSeason(int stemIdx, int month) {
        // month: 1-12 (Jan-Dec)
        int element = stemIdx / 2; // 0木,1火,2土,3金,4水
        switch (element) {
            case 0: return month >= 1 && month <= 3;  // 木旺春令（寅卯辰）
            case 1: return month >= 4 && month <= 6;  // 火旺夏令（巳午未）
            case 2: return month >= 3 && month <= 6 || month >= 9 && month <= 12; // 土旺四季末
            case 3: return month >= 7 && month <= 9;  // 金旺秋令（申酉戌）
            case 4: return month >= 10 && month <= 12; // 水旺冬令（亥子丑）
            default: return false;
        }
    }

    // ========================================================================
    // 三柱计算
    // ========================================================================

    /** 年柱 */
    private static int getYearStemIdx(int year) {
        int idx = (year - 4) % 10;
        return idx < 0 ? idx + 10 : idx;
    }
    private static int getYearBranchIdx(int year) {
        int idx = (year - 4) % 12;
        return idx < 0 ? idx + 12 : idx;
    }

    /** 月柱：五虎遁推月干 */
    private static int getMonthStemIdx(int yearStemIdx, int month) {
        int group = yearStemIdx % 5;
        int start = MONTH_STEM_START[group];
        int monthOrderIdx = getMonthBranchOrderIdx(month);
        return (start + monthOrderIdx) % 10;
    }

    /**
     * 月支顺序索引（用于五虎遁推算月干）
     * 寅=0, 卯=1, 辰=2, 巳=3, 午=4, 未=5, 申=6, 酉=7, 戌=8, 亥=9, 子=10, 丑=11
     * 公历月近似的节气月映射（MVP简化，不考虑节气精确切换日期）
     */
    private static int getMonthBranchOrderIdx(int month) {
        if (month >= 2) return month - 2;  // Feb(2)→寅(0), Mar(3)→卯(1)...
        return 11;                           // Jan(1)→丑(11)
    }

    /** 月支在 BRANCHES 数组中的实际索引 */
    private static int getMonthBranchIdx(int month) {
        // 寅=2, 卯=3, 辰=4, 巳=5, 午=6, 未=7, 申=8, 酉=9, 戌=10, 亥=11, 子=0, 丑=1
        if (month >= 2) return month;      // Feb(2)→2(寅), Mar(3)→3(卯)...
        return 1;                            // Jan(1)→1(丑)
    }

    /** 日柱：从参考日推算 */
    private static int getDayStemIdx(LocalDate date) {
        long days = ChronoUnit.DAYS.between(REF_DATE, date);
        int idx = (int) ((REF_STEM_IDX + days) % 10);
        return idx >= 0 ? idx : idx + 10;
    }
    private static int getDayBranchIdx(LocalDate date) {
        long days = ChronoUnit.DAYS.between(REF_DATE, date);
        int idx = (int) ((REF_BRANCH_IDX + days) % 12);
        return idx >= 0 ? idx : idx + 12;
    }

    // ========================================================================
    // 命宫计算（需时辰）
    // ========================================================================

    /**
     * 定命宫地支索引
     * 规则：从寅宫起，顺数到出生月（农历月），逆数到出生时
     *
     * 注意：此处使用公历月近似计算，与真正的节气月有偏差。
     * 以每月首日作为月令起始（即公历2月≈寅月、3月≈卯月...）。
     * 对于本月下旬出生的用户，偏差约15-30天，但MVP阶段可接受。
     *
     * @param month 公历月（1-12）
     * @param hourBranchIdx 时辰索引（子=0,丑=1...亥=11）
     * @return 命宫在 BRANCHES 数组中的索引
     */
    private static int getMingPalaceBranchIdx(int month, int hourBranchIdx) {
        // 1. 从寅宫(2)起
        // 2. 顺数到出生月：寅(2) → offset 0, 卯(3) → offset 1...
        int monthOffset = getMonthBranchOrderIdx(month); // 寅月→0, 卯月→1...
        // 顺数：从寅往前数 monthOffset 步
        int step1 = (2 + monthOffset) % 12;

        // 3. 逆数到出生时
        int step2 = (step1 - hourBranchIdx) % 12;
        return step2 >= 0 ? step2 : step2 + 12;
    }

    // ========================================================================
    // 命宫主星（MVP简化版）
    // ========================================================================

    /**
     * 根据年干分组 + 命宫索引查主星
     * 年干分组：甲己=0, 乙庚=1, 丙辛=2, 丁壬=3, 戊癸=4
     */
    private static String getMainStar(int yearStemIdx, int palaceIdx) {
        int group = yearStemIdx % 5;
        String[][] table = {
                {"廉贞","天同","武曲","太阳","天机","紫微","天府","太阴","贪狼","巨门","天相","天梁"},
                {"天机","紫微","天府","太阴","贪狼","巨门","天相","天梁","廉贞","天同","武曲","太阳"},
                {"天同","武曲","太阳","天机","紫微","天府","太阴","贪狼","巨门","天相","天梁","廉贞"},
                {"武曲","太阳","天机","紫微","天府","太阴","贪狼","巨门","天相","天梁","廉贞","天同"},
                {"太阳","天机","紫微","天府","太阴","贪狼","巨门","天相","天梁","廉贞","天同","武曲"},
        };
        if (group >= 0 && group < 5 && palaceIdx >= 0 && palaceIdx < 12) {
            return table[group][palaceIdx];
        }
        return "未知";
    }

    // ========================================================================
    // 时辰转换
    // ========================================================================

    /**
     * 小时 → 时辰索引
     * 子=0,丑=1,寅=2,卯=3,辰=4,巳=5,午=6,未=7,申=8,酉=9,戌=10,亥=11
     */
    private static int hourToBranchIdx(int hour) {
        if (hour >= 23 || hour < 1)  return 0;
        if (hour < 3)   return 1;
        if (hour < 5)   return 2;
        if (hour < 7)   return 3;
        if (hour < 9)   return 4;
        if (hour < 11)  return 5;
        if (hour < 13)  return 6;
        if (hour < 15)  return 7;
        if (hour < 17)  return 8;
        if (hour < 19)  return 9;
        if (hour < 21)  return 10;
        return 11;
    }

    // ========================================================================
    // 公共输出类
    // ========================================================================

    /** 番茄钟策略 */
    @Data
    public static class PomodoroStrategy {
        public String  dayStemName;          // 日干
        public String  dayStemFull;          // 日干全称
        public String  element;              // 五行
        public String  personalityType;      // 性格类型
        public String  personalityDesc;      // 性格描述
        public String  studyAdvice;          // 学习建议
        public int     recommendDuration;    // 推荐专注时长（分钟）
        public int     recommendBreak;       // 推荐休息时长（分钟）
        public int     maxBeforeLongBreak;   // 几个番茄后长休息
        public String  breakAdvice;          // 休息建议
        public boolean inSeason;             // 是否得令
        public String  mainStar;             // 命宫主星
        public boolean hasAccurateMingPalace;// 是否有准确命宫

        @Override
        public String toString() {
            return String.format(
                    "【%s · %s】\n%s\n日主：%s\n得令：%s\n%s\n推荐节奏：%d分钟专注 · %d分钟休息 · 每%d个长休息\n%s",
                    element, personalityType, personalityDesc,
                    dayStemFull, inSeason ? "是（能量充沛）" : "否（注意养气）",
                    studyAdvice, recommendDuration, recommendBreak, maxBeforeLongBreak,
                    breakAdvice
            );
        }
    }

    /** 排盘结果 */
    @Data
    public static class ChartResult {
        public int     yearStemIdx;
        public int     yearBranchIdx;
        public String  yearStem;
        public String  yearBranch;
        public String  zodiac;
        public int     monthStemIdx;
        public int     monthBranchIdx;
        public String  monthStem;
        public String  monthBranch;
        public int     dayStemIdx;
        public int     dayBranchIdx;
        public String  dayStem;
        public String  dayBranch;
        public String  mingPalaceBranch;      // 命宫地支
        public String  mainStar;              // 命宫主星
        public boolean hasAccurateMingPalace; // 是否有时辰命宫
        public PomodoroStrategy strategy;     // 番茄钟策略

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("八字：%s%s年 · %s%s月 · %s%s日",
                    yearStem, yearBranch, monthStem, monthBranch, dayStem, dayBranch));
            if (hasAccurateMingPalace) {
                sb.append(String.format("\n命宫：%s · 主星：%s", mingPalaceBranch, mainStar));
            } else {
                sb.append("\n命宫：无时辰，无法确定");
            }
            sb.append("\n\n").append(strategy);
            return sb.toString();
        }
    }

    // ========================================================================
    // 主入口
    // ========================================================================

    /**
     * 场景二：有出生时间 → 完整排盘
     */
    public static ChartResult calculate(LocalDate birthday, LocalTime birthTime) {
        ChartResult r = new ChartResult();
        int year  = birthday.getYear();
        int month = birthday.getMonthValue();

        // 1. 年柱
        r.yearStemIdx   = getYearStemIdx(year);
        r.yearBranchIdx = getYearBranchIdx(year);
        r.yearStem      = STEMS[r.yearStemIdx];
        r.yearBranch    = BRANCHES[r.yearBranchIdx];
        r.zodiac        = ZODIAC[r.yearBranchIdx];

        // 2. 月柱
        r.monthStemIdx   = getMonthStemIdx(r.yearStemIdx, month);
        r.monthBranchIdx = getMonthBranchIdx(month);
        r.monthStem      = STEMS[r.monthStemIdx];
        r.monthBranch    = BRANCHES[r.monthBranchIdx];

        // 3. 日柱
        r.dayStemIdx   = getDayStemIdx(birthday);
        r.dayBranchIdx = getDayBranchIdx(birthday);
        r.dayStem      = STEMS[r.dayStemIdx];
        r.dayBranch    = BRANCHES[r.dayBranchIdx];

        // 4. 命宫
        int hourIdx = hourToBranchIdx(birthTime.getHour());
        int mingPalaceIdx = getMingPalaceBranchIdx(month, hourIdx);
        r.mingPalaceBranch       = BRANCHES[mingPalaceIdx];
        r.mainStar               = getMainStar(r.yearStemIdx, mingPalaceIdx);
        r.hasAccurateMingPalace  = true;

        // 5. 策略
        r.strategy = makeStrategy(r.dayStemIdx, month, r.mainStar, true);

        return r;
    }

    /**
     * 场景一：仅年月日 → 三柱 + 五行策略
     */
    public static ChartResult calculate(LocalDate birthday) {
        ChartResult r = new ChartResult();
        int year  = birthday.getYear();
        int month = birthday.getMonthValue();

        // 1. 年柱
        r.yearStemIdx   = getYearStemIdx(year);
        r.yearBranchIdx = getYearBranchIdx(year);
        r.yearStem      = STEMS[r.yearStemIdx];
        r.yearBranch    = BRANCHES[r.yearBranchIdx];
        r.zodiac        = ZODIAC[r.yearBranchIdx];

        // 2. 月柱
        r.monthStemIdx   = getMonthStemIdx(r.yearStemIdx, month);
        r.monthBranchIdx = getMonthBranchIdx(month);
        r.monthStem      = STEMS[r.monthStemIdx];
        r.monthBranch    = BRANCHES[r.monthBranchIdx];

        // 3. 日柱
        r.dayStemIdx   = getDayStemIdx(birthday);
        r.dayBranchIdx = getDayBranchIdx(birthday);
        r.dayStem      = STEMS[r.dayStemIdx];
        r.dayBranch    = BRANCHES[r.dayBranchIdx];

        // 4. 无时辰 → 无命宫
        r.hasAccurateMingPalace = false;
        r.mingPalaceBranch = "";
        r.mainStar         = "";

        // 5. 策略（纯日干）
        r.strategy = makeStrategy(r.dayStemIdx, month, "", false);

        return r;
    }

    // ========================================================================
    // 测试
    // ========================================================================
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════");
        System.out.println("场景一：仅年月日");
        System.out.println("═══════════════════════════════════");
        ChartResult r1 = calculate(LocalDate.of(2004, 8, 6));
        System.out.println(r1);

        System.out.println("\n═══════════════════════════════════");
        System.out.println("场景二：年月日 + 丑时");
        System.out.println("═══════════════════════════════════");
        ChartResult r2 = calculate(LocalDate.of(2004, 8, 6), LocalTime.of(1, 24));
        System.out.println(r2);
    }
}
