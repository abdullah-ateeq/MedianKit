package com.example.mediankit

import com.example.mediankit.data.repository.MedianKitHistoryRepository
import com.example.mediankit.data.repository.SettingsRepository
import com.example.mediankit.domain.MedianCalculator
import com.example.mediankit.domain.MedianKitCalculator
import com.example.mediankit.domain.MedianKitFormatter
import com.example.mediankit.domain.MedianKitParser
import com.example.mediankit.domain.MedianKitValidator
import com.example.mediankit.domain.ModeCalculator
import com.example.mediankit.domain.ParseResult
import com.example.mediankit.domain.QuartileCalculator
import com.example.mediankit.domain.RangeCalculator
import com.example.mediankit.domain.ValidationResult
import com.example.mediankit.model.MedianKitHistory
import com.example.mediankit.model.ThemeMode
import com.example.mediankit.ui.MedianKitViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class MedianKitComprehensiveTest {

    private fun bd(s: String) = BigDecimal(s)
    private fun list(vararg s: String) = s.map { BigDecimal(it) }

    // 1. Basic median
    @Test
    fun test01_basicMedian() {
        val vals = list("10", "20", "30")
        val median = MedianCalculator.calculateMedian(vals)
        assertEquals(0, bd("20").compareTo(median))
    }

    // 2. Odd median
    @Test
    fun test02_oddMedian() {
        val vals = list("10", "20", "30", "40", "50")
        val median = MedianCalculator.calculateMedian(vals)
        assertEquals(0, bd("30").compareTo(median))
    }

    // 3. Even median
    @Test
    fun test03_evenMedian() {
        val vals = list("10", "20", "30", "40")
        val median = MedianCalculator.calculateMedian(vals)
        assertEquals(0, bd("25").compareTo(median))
    }

    // 4. Single value
    @Test
    fun test04_singleValue() {
        val res = MedianKitCalculator.calculate(list("50"))
        assertEquals(1, res.count)
        assertEquals(0, bd("50").compareTo(res.median))
        assertEquals(1, res.modes.size)
        assertEquals(0, bd("50").compareTo(res.modes[0]))
        assertEquals(0, bd("50").compareTo(res.minimum))
        assertEquals(0, bd("50").compareTo(res.maximum))
        assertEquals(0, bd("0").compareTo(res.range))
        assertEquals(0, bd("50").compareTo(res.q1))
        assertEquals(0, bd("50").compareTo(res.q3))
    }

    // 5. Two values
    @Test
    fun test05_twoValues() {
        val res = MedianKitCalculator.calculate(list("1", "2"))
        assertEquals(2, res.count)
        assertEquals(0, bd("1.5").compareTo(res.median))
        assertEquals(0, bd("1").compareTo(res.q1))
        assertEquals(0, bd("2").compareTo(res.q3))
    }

    // 6. Three values
    @Test
    fun test06_threeValues() {
        val res = MedianKitCalculator.calculate(list("1", "2", "3"))
        assertEquals(3, res.count)
        assertEquals(0, bd("2").compareTo(res.median))
        assertEquals(0, bd("1").compareTo(res.q1))
        assertEquals(0, bd("3").compareTo(res.q3))
    }

    // 7. Four values
    @Test
    fun test07_fourValues() {
        val res = MedianKitCalculator.calculate(list("10", "20", "30", "40"))
        assertEquals(4, res.count)
        assertEquals(0, bd("25").compareTo(res.median))
        assertEquals(0, bd("15").compareTo(res.q1))
        assertEquals(0, bd("35").compareTo(res.q3))
    }

    // 8. Decimal median
    @Test
    fun test08_decimalMedian() {
        val res = MedianKitCalculator.calculate(list("1.5", "2.5", "3.5", "4.5", "5.5"))
        assertEquals(0, bd("3.5").compareTo(res.median))
        assertEquals(0, bd("2.5").compareTo(res.q1))
        assertEquals(0, bd("4.5").compareTo(res.q3))
    }

    // 9. Negative values
    @Test
    fun test09_negativeValues() {
        val res = MedianKitCalculator.calculate(list("-20", "-10", "0", "10", "20"))
        assertEquals(0, bd("0").compareTo(res.median))
        assertEquals(0, bd("-20").compareTo(res.minimum))
        assertEquals(0, bd("20").compareTo(res.maximum))
        assertEquals(0, bd("40").compareTo(res.range))
        assertEquals(0, bd("-10").compareTo(res.q1))
        assertEquals(0, bd("10").compareTo(res.q3))
    }

    // 10. Mixed positive/negative values
    @Test
    fun test10_mixedPositiveNegativeValues() {
        val res = MedianKitCalculator.calculate(list("-25", "10", "50"))
        assertEquals(0, bd("-25").compareTo(res.minimum))
        assertEquals(0, bd("50").compareTo(res.maximum))
        assertEquals(0, bd("75").compareTo(res.range))
    }

    // 11. Zero
    @Test
    fun test11_zeroHandling() {
        val res = MedianKitCalculator.calculate(list("0", "0", "0"))
        assertEquals(0, bd("0").compareTo(res.median))
        assertEquals(0, bd("0").compareTo(res.range))
        assertEquals("0", MedianKitFormatter.formatNumber(res.median))
    }

    // 12. Duplicate values
    @Test
    fun test12_duplicateValues() {
        val res = MedianKitCalculator.calculate(list("10", "10", "20"))
        assertEquals(3, res.count)
        assertEquals(1, res.modes.size)
        assertEquals(0, bd("10").compareTo(res.modes[0]))
        assertEquals(2, res.modeFrequency)
    }

    // 13. All equal values
    @Test
    fun test13_allEqualValues() {
        val res = MedianKitCalculator.calculate(list("25", "25", "25", "25"))
        assertEquals(0, bd("25").compareTo(res.median))
        assertEquals(1, res.modes.size)
        assertEquals(0, bd("25").compareTo(res.modes[0]))
        assertEquals(4, res.modeFrequency)
        assertEquals(0, bd("0").compareTo(res.range))
        assertEquals(0, bd("25").compareTo(res.q1))
        assertEquals(0, bd("25").compareTo(res.q3))
    }

    // 14. Basic mode
    @Test
    fun test14_basicMode() {
        val res = ModeCalculator.calculateModes(list("10", "20", "20", "30"))
        assertEquals(1, res.modes.size)
        assertEquals(0, bd("20").compareTo(res.modes[0]))
        assertEquals(2, res.frequency)
    }

    // 15. Multiple modes
    @Test
    fun test15_multipleModes() {
        val res = ModeCalculator.calculateModes(list("10", "10", "20", "20", "30"))
        assertEquals(2, res.modes.size)
        assertEquals(0, bd("10").compareTo(res.modes[0]))
        assertEquals(0, bd("20").compareTo(res.modes[1]))
        assertEquals(2, res.frequency)
    }

    // 16. No mode
    @Test
    fun test16_noMode() {
        val res = ModeCalculator.calculateModes(list("10", "20", "30", "40"))
        assertTrue(res.modes.isEmpty())
        assertNull(res.frequency)
    }

    // 17. Mode frequency
    @Test
    fun test17_modeFrequency() {
        val res = ModeCalculator.calculateModes(list("5", "5", "5", "2", "2"))
        assertEquals(3, res.frequency)
        assertEquals(1, res.modes.size)
        assertEquals(0, bd("5").compareTo(res.modes[0]))
    }

    // 18. Decimal mode equality
    @Test
    fun test18_decimalModeEquality() {
        val res = ModeCalculator.calculateModes(list("1.5", "2", "1.50", "3"))
        assertEquals(1, res.modes.size)
        assertEquals(0, bd("1.5").compareTo(res.modes[0]))
        assertEquals(2, res.frequency)
    }

    // 19. Basic range
    @Test
    fun test19_basicRange() {
        val range = RangeCalculator.calculateRange(bd("10"), bd("50"))
        assertEquals(0, bd("40").compareTo(range))
    }

    // 20. Negative range
    @Test
    fun test20_negativeRange() {
        val range = RangeCalculator.calculateRange(bd("-25"), bd("50"))
        assertEquals(0, bd("75").compareTo(range))
    }

    // 21. Zero range
    @Test
    fun test21_zeroRange() {
        val range = RangeCalculator.calculateRange(bd("50"), bd("50"))
        assertEquals(0, bd("0").compareTo(range))
    }

    // 22. Q1
    @Test
    fun test22_q1Calculation() {
        val quartiles = QuartileCalculator.calculateQuartiles(list("10", "20", "20", "30", "40", "50"))
        assertEquals(0, bd("20").compareTo(quartiles.q1))
    }

    // 23. Q2
    @Test
    fun test23_q2Calculation() {
        val quartiles = QuartileCalculator.calculateQuartiles(list("10", "20", "20", "30", "40", "50"))
        assertEquals(0, bd("25").compareTo(quartiles.q2))
    }

    // 24. Q3
    @Test
    fun test24_q3Calculation() {
        val quartiles = QuartileCalculator.calculateQuartiles(list("10", "20", "20", "30", "40", "50"))
        assertEquals(0, bd("40").compareTo(quartiles.q3))
    }

    // 25. Odd quartiles
    @Test
    fun test25_oddQuartiles() {
        val quartiles = QuartileCalculator.calculateQuartiles(list("1", "2", "3", "4", "5", "6", "7"))
        assertEquals(0, bd("2").compareTo(quartiles.q1))
        assertEquals(0, bd("4").compareTo(quartiles.q2))
        assertEquals(0, bd("6").compareTo(quartiles.q3))
    }

    // 26. Even quartiles
    @Test
    fun test26_evenQuartiles() {
        val quartiles = QuartileCalculator.calculateQuartiles(list("1", "2", "3", "4", "5", "6", "7", "8"))
        assertEquals(0, bd("2.5").compareTo(quartiles.q1))
        assertEquals(0, bd("4.5").compareTo(quartiles.q2))
        assertEquals(0, bd("6.5").compareTo(quartiles.q3))
    }

    // 27. Small dataset quartiles
    @Test
    fun test27_smallDatasetQuartiles() {
        val q1 = QuartileCalculator.calculateQuartiles(list("5"))
        assertEquals(0, bd("5").compareTo(q1.q1))
        assertEquals(0, bd("5").compareTo(q1.q2))
        assertEquals(0, bd("5").compareTo(q1.q3))
    }

    // 28. Decimal quartiles
    @Test
    fun test28_decimalQuartiles() {
        val q = QuartileCalculator.calculateQuartiles(list("1.5", "2.5", "3.5", "4.5", "5.5"))
        assertEquals(0, bd("2.5").compareTo(q.q1))
        assertEquals(0, bd("3.5").compareTo(q.q2))
        assertEquals(0, bd("4.5").compareTo(q.q3))
    }

    // 29. Negative quartiles
    @Test
    fun test29_negativeQuartiles() {
        val q = QuartileCalculator.calculateQuartiles(list("-20", "-10", "0", "10", "20"))
        assertEquals(0, bd("-10").compareTo(q.q1))
        assertEquals(0, bd("0").compareTo(q.q2))
        assertEquals(0, bd("10").compareTo(q.q3))
    }

    // 30. Five-number summary
    @Test
    fun test30_fiveNumberSummary() {
        val res = MedianKitCalculator.calculate(list("10", "20", "20", "30", "40", "50"))
        val summary = MedianKitFormatter.formatFiveNumberSummary(res, 6)
        assertEquals("10 · 20 · 25 · 40 · 50", summary)
    }

    // 31. Sorting
    @Test
    fun test31_sorting() {
        val res = MedianKitCalculator.calculate(list("30", "10", "20"))
        assertEquals(listOf(bd("10"), bd("20"), bd("30")), res.sortedValues)
    }

    // 32. Order independence
    @Test
    fun test32_orderIndependence() {
        val res1 = MedianKitCalculator.calculate(list("10", "20", "30", "40", "50"))
        val res2 = MedianKitCalculator.calculate(list("50", "30", "10", "40", "20"))
        assertEquals(0, res1.median.compareTo(res2.median))
        assertEquals(0, res1.range.compareTo(res2.range))
        assertEquals(0, res1.q1.compareTo(res2.q1))
        assertEquals(0, res1.q3.compareTo(res2.q3))
        assertEquals(0, res1.minimum.compareTo(res2.minimum))
        assertEquals(0, res1.maximum.compareTo(res2.maximum))
    }

    // 33. Comma-separated input
    @Test
    fun test33_commaSeparatedInput() {
        val parse = MedianKitParser.parse("10, 20, 20, 30, 40, 50")
        assertTrue(parse is ParseResult.Success)
        val success = parse as ParseResult.Success
        assertEquals(6, success.values.size)
    }

    // 34. Line-separated input
    @Test
    fun test34_lineSeparatedInput() {
        val parse = MedianKitParser.parse("10\n20\n20\n30\n40\n50")
        assertTrue(parse is ParseResult.Success)
        val success = parse as ParseResult.Success
        assertEquals(6, success.values.size)
    }

    // 35. Mixed separators
    @Test
    fun test35_mixedSeparators() {
        val parse = MedianKitParser.parse("10, 20\n 20 30, \t 40\r\n 50")
        assertTrue(parse is ParseResult.Success)
        val success = parse as ParseResult.Success
        assertEquals(6, success.values.size)
    }

    // 36. Whitespace
    @Test
    fun test36_whitespace() {
        val parse = MedianKitParser.parse("   10    20   30   ")
        assertTrue(parse is ParseResult.Success)
        val success = parse as ParseResult.Success
        assertEquals(3, success.values.size)
    }

    // 37. Invalid token
    @Test
    fun test37_invalidToken() {
        val parse = MedianKitParser.parse("10\n20\nhello\n30")
        assertTrue(parse is ParseResult.Error)
        val error = parse as ParseResult.Error
        assertEquals("hello", error.invalidToken)
        assertEquals("Invalid value: \"hello\"", error.message)
    }

    // 38. Empty input
    @Test
    fun test38_emptyInput() {
        val parse = MedianKitParser.parse("    ")
        assertTrue(parse is ParseResult.Empty)
        val empty = parse as ParseResult.Empty
        assertEquals("Enter at least one number.", empty.message)
    }

    // 39. Excess decimal precision
    @Test
    fun test39_excessDecimalPrecision() {
        val bdVal = bd("3.141592653589793")
        val formatted = MedianKitFormatter.formatNumber(bdVal, 4)
        assertEquals("3.1416", formatted)
    }

    // 40. Maximum value count
    @Test
    fun test40_maximumValueCount() {
        val list1000 = (1..1000).map { BigDecimal(it) }
        val val1000 = MedianKitValidator.validate(list1000, 1000)
        assertTrue(val1000 is ValidationResult.Valid)

        val list1001 = (1..1001).map { BigDecimal(it) }
        val val1001 = MedianKitValidator.validate(list1001, 1000)
        assertTrue(val1001 is ValidationResult.Invalid)
        assertEquals("Maximum 1,000 numbers allowed.", (val1001 as ValidationResult.Invalid).message)
    }

    // 41. BigDecimal precision
    @Test
    fun test41_bigDecimalPrecision() {
        val res = MedianKitCalculator.calculate(list("0.1", "0.2", "0.3"))
        assertEquals(0, bd("0.2").compareTo(res.median))
        assertEquals("0.2", MedianKitFormatter.formatNumber(res.median))
    }

    // 42. Large numbers
    @Test
    fun test42_largeNumbers() {
        val big1 = bd("1000000000000000000000000000000")
        val big2 = bd("2000000000000000000000000000000")
        val big3 = bd("3000000000000000000000000000000")
        val res = MedianKitCalculator.calculate(listOf(big1, big2, big3))
        assertEquals(0, big2.compareTo(res.median))
        assertEquals(0, bd("2000000000000000000000000000000").compareTo(res.range))
    }

    // 43. Duplicate preservation
    @Test
    fun test43_duplicatePreservation() {
        val res = MedianKitCalculator.calculate(list("10", "10", "20"))
        assertEquals(3, res.count)
    }

    // 44. Copy formatting
    @Test
    fun test44_copyFormatting() {
        val res = MedianKitCalculator.calculate(list("10", "20", "20", "30", "40", "50"))
        val copyText = MedianKitFormatter.formatCopyText(res, 6)
        assertTrue(copyText.contains("MedianKit Result"))
        assertTrue(copyText.contains("Median: 25"))
        assertTrue(copyText.contains("Mode: 20"))
        assertTrue(copyText.contains("Range: 40"))
        assertTrue(copyText.contains("Q1: 20"))
        assertTrue(copyText.contains("Q3: 40"))
    }

    // 45. Share formatting
    @Test
    fun test45_shareFormatting() {
        val res = MedianKitCalculator.calculate(list("10", "20", "20", "30", "40", "50"))
        val shareText = MedianKitFormatter.formatShareText(res, 6)
        assertTrue(shareText.contains("MedianKit"))
        assertTrue(shareText.contains("6 values analyzed"))
        assertTrue(shareText.contains("Median: 25"))
        assertTrue(shareText.contains("Mode: 20"))
        assertTrue(shareText.contains("Range: 40"))
    }

    // 46. History save
    @Test
    fun test46_historySave() {
        val prefs = FakeSharedPreferences()
        val repo = MedianKitHistoryRepository(prefs)
        val item = MedianKitHistory(
            id = "1",
            inputValues = listOf("10", "20", "30"),
            count = 3,
            median = "20",
            modes = emptyList(),
            modeFrequency = null,
            minimum = "10",
            maximum = "30",
            range = "20",
            q1 = "10",
            q3 = "30",
            calculatedAt = 1000L
        )
        val history = repo.saveCalculation(item)
        assertEquals(1, history.size)
    }

    // 47. History load
    @Test
    fun test47_historyLoad() {
        val prefs = FakeSharedPreferences()
        val repo = MedianKitHistoryRepository(prefs)
        val item = MedianKitHistory(
            id = "test-id",
            inputValues = listOf("1", "2"),
            count = 2,
            median = "1.5",
            modes = emptyList(),
            modeFrequency = null,
            minimum = "1",
            maximum = "2",
            range = "1",
            q1 = "1",
            q3 = "2",
            calculatedAt = 2000L
        )
        repo.saveCalculation(item)
        val loaded = repo.getHistory()
        assertEquals(1, loaded.size)
        assertEquals("test-id", loaded[0].id)
        assertEquals(listOf("1", "2"), loaded[0].inputValues)
    }

    // 48. History limit
    @Test
    fun test48_historyLimit() {
        val prefs = FakeSharedPreferences()
        val repo = MedianKitHistoryRepository(prefs)
        for (i in 1..12) {
            val item = MedianKitHistory(
                id = "id-$i",
                inputValues = listOf("$i"),
                count = 1,
                median = "$i",
                modes = listOf("$i"),
                modeFrequency = 1,
                minimum = "$i",
                maximum = "$i",
                range = "0",
                q1 = "$i",
                q3 = "$i",
                calculatedAt = i.toLong()
            )
            repo.saveCalculation(item)
        }
        val history = repo.getHistory()
        assertEquals(10, history.size)
        assertEquals("id-12", history[0].id)
    }

    // 49. History deletion
    @Test
    fun test49_historyDeletion() {
        val prefs = FakeSharedPreferences()
        val repo = MedianKitHistoryRepository(prefs)
        val item1 = MedianKitHistory("1", listOf("1"), 1, "1", listOf("1"), 1, "1", "1", "0", "1", "1", 1L)
        val item2 = MedianKitHistory("2", listOf("2"), 1, "2", listOf("2"), 1, "2", "2", "0", "2", "2", 2L)
        repo.saveCalculation(item1)
        repo.saveCalculation(item2)

        val remaining = repo.deleteCalculation("1")
        assertEquals(1, remaining.size)
        assertEquals("2", remaining[0].id)
    }

    // 50. History clear
    @Test
    fun test50_historyClear() {
        val prefs = FakeSharedPreferences()
        val repo = MedianKitHistoryRepository(prefs)
        val item = MedianKitHistory("1", listOf("1"), 1, "1", listOf("1"), 1, "1", "1", "0", "1", "1", 1L)
        repo.saveCalculation(item)
        repo.clearHistory()
        assertTrue(repo.getHistory().isEmpty())
    }

    // 51. History restore
    @Test
    fun test51_historyRestore() {
        val prefs = FakeSharedPreferences()
        val historyRepo = MedianKitHistoryRepository(prefs)
        val settingsRepo = SettingsRepository(prefs)
        val vm = MedianKitViewModel(historyRepo, settingsRepo)

        val item = MedianKitHistory("1", listOf("5", "10", "15"), 3, "10", emptyList(), null, "5", "15", "10", "5", "15", 1L)
        vm.restoreHistory(item)

        val state = vm.uiState.value
        assertNotNull(state.result)
        assertEquals(0, bd("10").compareTo(state.result!!.median))
    }

    // 52. Settings persistence
    @Test
    fun test52_settingsPersistence() {
        val prefs = FakeSharedPreferences()
        val repo = SettingsRepository(prefs)
        repo.setThemeMode(ThemeMode.DARK)
        repo.setDisplayPrecision(2)
        repo.setHapticsEnabled(false)

        val reloaded = SettingsRepository(prefs)
        assertEquals(ThemeMode.DARK, reloaded.getThemeMode())
        assertEquals(2, reloaded.getDisplayPrecision())
        assertFalse(reloaded.isHapticsEnabled())
    }

    // 53. Last dataset persistence
    @Test
    fun test53_lastDatasetPersistence() {
        val prefs = FakeSharedPreferences()
        val repo = SettingsRepository(prefs)
        repo.setLastInput("99\n100")
        assertEquals("99\n100", repo.getLastInput())
    }

    // 54. Display precision
    @Test
    fun test54_displayPrecision() {
        val value = bd("25.1234567")
        assertEquals("25.12", MedianKitFormatter.formatNumber(value, 2))
        assertEquals("25.1235", MedianKitFormatter.formatNumber(value, 4))
        assertEquals("25.123457", MedianKitFormatter.formatNumber(value, 6))
    }

    // 55. Reset
    @Test
    fun test55_reset() {
        val prefs = FakeSharedPreferences()
        val historyRepo = MedianKitHistoryRepository(prefs)
        val settingsRepo = SettingsRepository(prefs)
        settingsRepo.setThemeMode(ThemeMode.DARK)
        settingsRepo.setDisplayPrecision(2)

        val vm = MedianKitViewModel(historyRepo, settingsRepo)
        vm.resetSettings()

        val settings = vm.settingsState.value
        assertEquals(ThemeMode.SYSTEM, settings.themeMode)
        assertEquals(6, settings.displayPrecision)
    }

    // 56. Rapid input protection
    @Test
    fun test56_rapidInputProtection() {
        val prefs = FakeSharedPreferences()
        val historyRepo = MedianKitHistoryRepository(prefs)
        val settingsRepo = SettingsRepository(prefs)
        val vm = MedianKitViewModel(historyRepo, settingsRepo)

        vm.onInputChanged("10")
        vm.onInputChanged("10 20")
        vm.onInputChanged("10 20 30")

        val state = vm.uiState.value
        assertNotNull(state.result)
        assertEquals(0, bd("20").compareTo(state.result!!.median))
    }

    // 57. Duplicate history protection
    @Test
    fun test57_duplicateHistoryProtection() {
        val prefs = FakeSharedPreferences()
        val repo = MedianKitHistoryRepository(prefs)
        val item1 = MedianKitHistory("1", listOf("10", "20"), 2, "15", emptyList(), null, "10", "20", "10", "10", "20", 1L)
        val item2 = MedianKitHistory("2", listOf("10", "20"), 2, "15", emptyList(), null, "10", "20", "10", "10", "20", 2L)

        repo.saveCalculation(item1)
        val afterSecond = repo.saveCalculation(item2)
        assertEquals(1, afterSecond.size)
    }

    // CRITICAL TEST — BASIC DATASET
    @Test
    fun testCritical_basicDataset() {
        val res = MedianKitCalculator.calculate(list("10", "20", "20", "30", "40", "50"))
        assertEquals(6, res.count)
        assertEquals(0, bd("25").compareTo(res.median))
        assertEquals(1, res.modes.size)
        assertEquals(0, bd("20").compareTo(res.modes[0]))
        assertEquals(0, bd("10").compareTo(res.minimum))
        assertEquals(0, bd("50").compareTo(res.maximum))
        assertEquals(0, bd("40").compareTo(res.range))
        assertEquals(0, bd("20").compareTo(res.q1))
        assertEquals(0, bd("25").compareTo(res.median)) // Q2
        assertEquals(0, bd("40").compareTo(res.q3))
    }

    // CRITICAL TEST — RANGE INVARIANT
    @Test
    fun testCritical_rangeInvariant() {
        val datasets = listOf(
            list("10", "20", "30"),
            list("-50", "0", "100"),
            list("42"),
            list("1.5", "9.75", "100.25")
        )
        for (ds in datasets) {
            val res = MedianKitCalculator.calculate(ds)
            assertEquals(0, res.maximum.subtract(res.minimum).compareTo(res.range))
        }
    }

    // CRITICAL TEST — QUARTILE INVARIANT
    @Test
    fun testCritical_quartileInvariant() {
        val datasets = listOf(
            list("1", "2", "3", "4", "5", "6", "7"),
            list("1", "2", "3", "4", "5", "6", "7", "8"),
            list("10", "20", "20", "30", "40", "50")
        )
        for (ds in datasets) {
            val res = MedianKitCalculator.calculate(ds)
            // Q2 equals median
            val quartiles = QuartileCalculator.calculateQuartiles(res.sortedValues)
            assertEquals(0, res.median.compareTo(quartiles.q2))
        }
    }
}
