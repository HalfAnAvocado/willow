package com.marvinelsen.willow.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

private data class TestCase<T, S>(val input: T, val expected: S) : WithDataTestName {
    override fun dataTestName() = "$input -> $expected"
}

class PronunciationConverterTest : FunSpec({
    context("Zhuyin to Numbered Pinyin") {
        withData(
            TestCase("ㄗㄨㄟ", "zui1"),
            TestCase("ㄗㄨㄟˊ", "zui2"),
            TestCase("ㄗㄨㄟˇ", "zui3"),
            TestCase("ㄗㄨㄟˋ", "zui4"),
            TestCase("˙ㄗㄨㄟ", "zui5"),
            TestCase("ㄋㄩ", "nü1"),
            TestCase("ㄋㄩㄝ", "nüe1"),
            TestCase("ㄌㄩ", "lü1"),
            TestCase("ㄌㄩㄝ", "lüe1"),
            TestCase("ㄌㄩㄣ", "lün1"),
            TestCase("ㄗㄨㄟ　ㄗㄨㄟˊ", "zui1 zui2"),
            TestCase("ㄗㄨㄟˊ　ㄗㄨㄟ", "zui2 zui1"),
            TestCase("ㄗㄨㄟˇ　ㄗㄨㄟˋ", "zui3 zui4"),
            TestCase("ㄗㄨㄟˋ　ㄗㄨㄟˇ", "zui4 zui3"),
            TestCase("˙ㄗㄨㄟ　˙ㄗㄨㄟ", "zui5 zui5"),
        ) { (zhuyin, numberedPinyin) ->
            PronunciationConverter.convertToNumberedPinyin(zhuyin) shouldBe numberedPinyin
        }
    }

    context("Zhuyin to Accented Pinyin") {
        withData(
            TestCase("ㄧ", "yi\u0304"),
            TestCase("ㄧˊ", "yi\u0301"),
            TestCase("ㄧˇ", "yi\u030C"),
            TestCase("ㄧˋ", "yi\u0300"),
            TestCase("˙ㄧ", "yi"),
            TestCase("ㄋㄩˇ", "nü\u030C"),
            TestCase("ㄋㄩㄝˇ", "nüe\u030C"),
            TestCase("ㄌㄩˇ", "lü\u030C"),
            TestCase("ㄌㄩㄝˇ", "lüe\u030C"),
            TestCase("ㄌㄩㄣˇ", "lü\u030Cn"),
            TestCase("ㄞˋ", "a\u0300i"),
            TestCase("ㄠˋ", "a\u0300o"),
            TestCase("ㄟˋ", "e\u0300i"),
            TestCase("ㄐㄧㄚˋ", "jia\u0300"),
            TestCase("ㄐㄧㄠˋ", "jia\u0300o"),
            TestCase("ㄐㄧㄝˋ", "jie\u0300"),
            TestCase("ㄐㄩㄥˋ", "jio\u0300ng"),
            TestCase("ㄐㄧㄡˋ", "jiu\u0300"),
            TestCase("ㄔㄡˋ", "cho\u0300u"),
            TestCase("ㄔㄨㄚˋ", "chua\u0300"),
            TestCase("ㄔㄨㄞˋ", "chua\u0300i"),
            TestCase("ㄐㄩㄝˋ", "jue\u0300"),
            TestCase("ㄔㄨㄟˋ", "chui\u0300"),
            TestCase("ㄔㄨㄛˋ", "chuo\u0300"),
            TestCase("ㄌㄩㄝˋ", "lüe\u0300"),
        ) { (zhuyin, accentedPinyin) ->
            PronunciationConverter.convertToAccentedPinyin(zhuyin) shouldBe accentedPinyin
        }
    }
})
