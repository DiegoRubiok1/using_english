# Using English - C1 Preparation App

Using English is a modern Android application designed to help prepare for the English.
The app focuses on the "Use of English" section, providing interactive exercises and progress 
tracking.

## AI disclaimer
This project is built by someone with **NO** experience in Kotlin or Android development. It is 
expected to have failures since the code itself was all generated, only being supervised by me.

## Features

- **Interactive Exercises:** Practice Multiple-choice cloze, Open cloze, Word formation, and Key word transformation.
- **Progress Tracking:** Exercises are marked as resolved once completed, allowing you to track your study progress.
- **Modern UI:** Built with Jetpack Compose and Material Design 3.
- **Customizable Themes:** Supports System Dark Mode and a specialized "Pure Black" theme for OLED screens.
- **Local Persistence:** All your progress is saved locally using Room Database.
- **Offline First:** No internet connection required to practice; all exercises are bundled within the app.

## 🛠 Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Architecture:** MVVM (Model-View-ViewModel)
- **Database:** [Room](https://developer.android.com/training/data-storage/room)
- **Asynchrony:** Kotlin Coroutines & Flow
- **Dependency Injection:** Manual Injection with ViewModel Factories
- **Navigation:** Jetpack Navigation Compose
- **Gemini:** integrated in android studio

## Architecture

The project follows clean architecture principles:
- **UI Layer:** Composable functions observing state from ViewModels.
- **Domain Layer:** Repositories handling data logic.
- **Data Layer:** Room database and JSON parsing for the initial data ingestion.

## Releases

You can find the latest stable APKs in the [Releases](https://github.com/DiegoRubiok1/using_english/releases) section of this repository.

## Development

### Prerequisites
- Android Studio Iguana or newer.
- JDK 17.

### Building
To build the project and generate a debug APK, run:
```bash
./gradlew assembleDebug
```

## 📄 License

This project is licensed under the **GNU General Public License v3.0 (GPLv3)**. 

> **Important:** This license ensures that the software remains free and open source.
If you redistribute or modify this application, you **must** also provide the source code under 
the same GPLv3 license. **Selling or reselling this application as a closed-source product is 
strictly prohibited.**

See the [LICENSE](LICENSE) file for the full text.

## Exercises
To find information about exercises source email me.
