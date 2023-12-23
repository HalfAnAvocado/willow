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
})
