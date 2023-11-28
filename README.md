<h1 align="center">
    Willow
</h1>

<h4 align="center">
    Offline Traditional Chinese dictionary software for the desktop.
</h4>

<img src="https://github.com/HalfAnAvocado/willow/blob/9bfb0346b97d23d17fe210d88df442f2bb14ae3f/assets/screenshot.png" alt="Screenshot">

## 🤔 What is Willow?

Willow is a Traditional Chinese to English dictionary software. Willow is designed specifically for advanced learners who are focused on mastering Traditional Chinese as it is used in Taiwan. This tool is ideal for those who prefer a monolingual approach to language learning.

## Desktop-Optimized and Offline Access

Willow is optimized for desktop use, providing a seamless and efficient user experience. It's completely offline, ensuring access to its resources without the need for an internet connection. This makes it an invaluable tool for learners regardless of their online accessibility.

## ✨ Features

- **Traditional Chinese Support:** Willow exclusively supports [Traditional Chinese characters](https://en.wikipedia.org/wiki/Traditional_Chinese_characters), making it ideal for learners focused on this script.
- **Taiwanese Mandarin Focus:** The dictionary emphasizes [Taiwanese Mandarin](https://en.wikipedia.org/wiki/Taiwanese_Mandarin), including pronunciation, meaning, and usage based on the Taiwanese standard.
- **Zhuyin (Bopomofo) Transliteration:** Instead of [Hanyu Pinyin](https://en.wikipedia.org/wiki/Hanyu_pinyin), Willow utilizes [Zhuyin](https://en.wikipedia.org/wiki/Zhuyin), the phonetic system used in Taiwan, to aid in accurate pronunciation learning.
- **Authentic Fonts:** Uses [Noto Sans Traditional Chinese](https://fonts.google.com/noto/specimen/Noto+Sans+TC) and the official standard [regular script](https://en.wikipedia.org/wiki/Regular_script) [font from the Taiwanese government](https://data.gov.tw/dataset/5961), ensuring characters are displayed in their correct [Taiwanese Mandarin](https://en.wikipedia.org/wiki/Taiwanese_Mandarin) form.
- **Rich Dictionary Sources:**
    - MoE (Ministry of Education) - Monolingual
    - LAC (Cross-Straits) - Monolingual
    - CC-CEDICT - Bilingual (English)
- **Extensive Database:** Over 250,000 unique entries sourced from the above dictionaries.
- **Deep Character Analysis:** Investigate characters that comprise each entry and explore their meanings and usages.
- **Comprehensive Word Search:** Search for words containing or related to a particular entry, not just words that start with it.
- **Contextual Learning:** Access example sentences from [Tatoeba](https://tatoeba.org/en/) to see words used in real-life contexts.
- **High-Performance Lookup:** Blazingly fast word lookup capabilities powered by [SQLite](https://www.sqlite.org/).
- **User-Generated Content:** Add your own example sentences to enrich the dictionary.
- **Anki Integration:** Seamlessly create [Anki](https://apps.ankiweb.net/) notes from dictionary data, including example sentences, via [Anki Connect](https://foosoft.net/projects/anki-connect/).
- **Efficient Navigation:** Temporary search history and shortcuts for search, navigation, copying Zhuyin, and copying headwords enhance user experience.

## 💡 Feature requests

TODO

## 🪲 Bug reports

TODO

## 🌟 Contributing

TODO

## 🗒️ Code of Conduct

TODO

## 🛠️ Building

### Dependencies

- JDK 21

### Building from source (all platforms) - Gradle

The project is built with Gradle. Run Gradle to build the project and to run the tests using the following command on Unix/macOS:

```sh
./gradlew build
```

## 🔑 License

All source code in this repository is licensed under a [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0), unless noted otherwise.

To the following third-party code, data, and files in the repository different licenses apply:

### CC-CEDICT

[CC-CEDICT](https://cc-cedict.org) is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](https://creativecommons.org/licenses/by-sa/4.0/).

### 兩岸詞典 (LAC)

[兩岸詞典](https://github.com/g0v/moedict-data-csld/blob/a1e91196f84cd2f3456570906191615f477278c8/%E5%85%A9%E5%B2%B8%E8%A9%9E%E5%85%B8.xlsx) made available by the [中華文化總會](https://www.gacc.org.tw/) is licensed under a [Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Taiwan License](https://creativecommons.org/licenses/by-nc-nd/3.0/tw/deed.en).

### 重編國語辭典修訂本 (MoE)

[重編國語辭典修訂本](https://language.moe.gov.tw/001/Upload/Files/site_content/M0001/respub/index.html) made available by the [中華民國教育部](https://www.edu.tw/) is licensed under a [Creative Commons Attribution-NoDerivs 3.0 Taiwan License](https://creativecommons.org/licenses/by-nd/3.0/tw/deed.en).

### Tatoeba

[Tatoeba example sentences](https://tatoeba.org/en/downloads) are licensed under a [Creative Commons Attribution 2.0 France License](https://creativecommons.org/licenses/by/2.0/fr/).

### Inter

[Inter](https://rsms.me/inter/) is licensed under the [SIL Open Font License, Version 1.1](http://scripts.sil.org/OFL).

Copyright (c) 2016 The Inter Project Authors (https://github.com/rsms/inter)

### Noto Sans CJK

[Noto Sans CJK](https://github.com/notofonts/noto-cjk) is licensed under the [SIL Open Font License, Version 1.1](http://scripts.sil.org/OFL).

### TW-Kai

[TW-Kai](https://data.gov.tw/dataset/5961) is licensed under the [SIL Open Font License, Version 1.1](http://scripts.sil.org/OFL).