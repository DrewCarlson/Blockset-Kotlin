[![](media/blockset.png)](https://blockset.com)

# Blockset Kotlin

![Bintray](https://img.shields.io/bintray/v/drewcarlson/Blockset-Kotlin/Blockset-Kotlin?color=blue)
![](https://img.shields.io/maven-metadata/v?label=artifactory&logoColor=lightgrey&metadataUrl=https%3A%2F%2Foss.jfrog.org%2Fartifactory%2Foss-snapshot-local%2Fdrewcarlson%2Fblockset%2Fblockset%2Fmaven-metadata.xml&color=lightgrey)
![](https://github.com/DrewCarlson/Blockset-Kotlin/workflows/Jvm/badge.svg)
![](https://github.com/DrewCarlson/Blockset-Kotlin/workflows/Js/badge.svg)
![](https://github.com/DrewCarlson/Blockset-Kotlin/workflows/Native/badge.svg)

Kotlin wrapper for the [Blockset API](https://blockset.com/).

## Usage

For a comprehensive list of available endpoints and to understand the returned data, see [docs.blockset.com](https://docs.blockset.com/).

Kotlin
```kotlin
val bdbService = BdbService.create(token)
val bitcoin = bdbService.getBlockchain("bitcoin-mainnet")
println(bitcoin.name) // Bitcoin
```
Swift
```swift
let bdbService = BdbServiceCompanion.create(token)
bdbService.getBlockchain("bitcoin-mainnet") { (bitcoin, error) in
  // ...
}
``` 

## Download

![](https://img.shields.io/static/v1?label=&message=Platforms&color=grey)
![](https://img.shields.io/static/v1?label=&message=Js&color=blue)
![](https://img.shields.io/static/v1?label=&message=Jvm&color=blue)
![](https://img.shields.io/static/v1?label=&message=Linux&color=blue)
![](https://img.shields.io/static/v1?label=&message=macOS&color=blue)
![](https://img.shields.io/static/v1?label=&message=Windows&color=blue)
![](https://img.shields.io/static/v1?label=&message=iOS&color=blue)
![](https://img.shields.io/static/v1?label=&message=tvOS&color=red)
![](https://img.shields.io/static/v1?label=&message=watchOS&color=red)

```kotlin
repositories {
  mavenCentral()
  // (Optional) For Snapshots:
  maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
  // Multiplatform
  implementation("org.drewcarlson:blockset:$blockset_version")
  // For JVM:
  implementation("org.drewcarlson:blockset-jvm:$blockset_version")
}
```


Note: it is required to specify a Ktor client engine implementation.
([Documentation](https://ktor.io/clients/http-client/multiplatform.html))

```kotlin
dependencies {
  // Jvm/Android
  implementation("io.ktor:ktor-client-okhttp:$ktor_version")
  implementation("io.ktor:ktor-client-android:$ktor_version")
  // iOS
  implementation("io.ktor:ktor-client-ios:$ktor_version")
  // macOS/Windows/Linux
  implementation("io.ktor:ktor-client-curl:$ktor_version")
  // Javascript/NodeJS
  implementation("io.ktor:ktor-client-js:$ktor_version")
}
``` 

## License
```
Copyright (c) 2020 Andrew Carlson

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
