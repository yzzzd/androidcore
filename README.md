# AndroidCore
AndroidCore make you get the most rapid development.

[![Release](https://jitpack.io/v/crocodic-studio/AndroidCoreProject.svg)](https://jitpack.io/#crocodic-studio/AndroidCoreProject) [![](https://jitpack.io/v/crocodic-studio/AndroidCoreProject/month.svg)](https://jitpack.io/#crocodic-studio/AndroidCoreProject) 

## Wiki
To know what's inside in this repository, please read our [Wiki](https://github.com/yzzzd/androidcore/wiki)

## Changelog
Read the [CHANGELOG](https://github.com/crocodic-studio/AndroidCoreProject/blob/main/CHANGELOG.md) to find out what's included in the latest update!

## Download
Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and:

```gradle
dependencies {
    implementation 'com.github.crocodic-studio:AndroidCoreProject:{latest version}'
}
```

## Usage

### 1. Extend CoreActivity
To get DataBinding and ViewModel implementation
```kotlin
class HomeActivity : CoreActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {
  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // auto generate binding and viewModel variable
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
  }
}
```

### 2. Extend NoViewModelActivity
To get DataBinding without ViewModel implementation
```kotlin
class HomeActivity : NoViewModelActivity<ActivityHomeBinding>(R.layout.activity_home) {
  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // auto generate binding variable
        binding.lifecycleOwner = this
  }
}
```

## License
Licensed under the Apache License, Version 2.0,
